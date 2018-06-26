package de.hhu.stups.plues.dataeditor.ui.database;

import de.hhu.stups.plues.dataeditor.ui.database.events.DbEvent;
import de.hhu.stups.plues.dataeditor.ui.database.events.LoadDbEvent;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import org.reactfx.EventSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Component;

import java.io.File;
import javax.sql.DataSource;

/**
 * The DbService establishes and manages the connection to the database.
 */
@Component
public class DbService {

  private final Logger logger = LoggerFactory.getLogger(getClass());
  private final EventSource<DbEvent> dbEventSource;
  private final ObjectProperty<DataSource> dataSourceProperty;
  private final ObjectProperty<File> dbFileProperty;
  private final ObjectProperty<Task<Void>> dbTaskProperty;
  private AbstractRoutingDataSource abstractRoutingDataSource;

  /**
   * The database service to load and modify a .sqlite3 database.
   */
  @Autowired
  public DbService() {
    dbEventSource = new EventSource<>();
    dbEventSource.subscribe(this::handleDbEvent);
    dataSourceProperty = new SimpleObjectProperty<>();
    dbFileProperty = new SimpleObjectProperty<>();
    DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
    dataSourceBuilder.type(org.sqlite.SQLiteDataSource.class);
    dataSourceBuilder.driverClassName("org.sqlite.JDBC");
    dataSourceBuilder.url("jdbc:sqlite:cs.sqlite3");
    abstractRoutingDataSource = new AbstractRoutingDataSource() {
      @Override
      protected Object determineCurrentLookupKey() {
        return null;
      }
    };
    dbTaskProperty = new SimpleObjectProperty<>();
    dataSourceProperty.set(dataSourceBuilder.build());
  }

  /**
   * This message reacts to user input from the MainMenu.
   *
   * @param dbEvent the specific event thrown by the MainMenu.
   */
  private void handleDbEvent(final DbEvent dbEvent) {
    switch (dbEvent.getEventType()) {
      case LOAD_DB:
        final Task<Void> loadDbTask = new Task<Void>() {
          @Override
          protected Void call() {
            final File dbFile = ((LoadDbEvent) dbEvent).getDbFile();
            // close old store if another database has been loaded
            DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
            dataSourceBuilder.type(org.sqlite.SQLiteDataSource.class);
            dataSourceBuilder.driverClassName("org.sqlite.JDBC");
            dataSourceBuilder.url("jdbc:sqlite:" + dbFile.getAbsolutePath());
            DataSource newDataSource = dataSourceBuilder.build();
            //TODO DataSource ändern vielleich abstract routing datasource
            dataSourceProperty.set(newDataSource);
            dbFileProperty.set(dbFile);
            return null;
          }
        };
        dbTaskProperty.set(loadDbTask);
        new Thread(loadDbTask).start();
        break;
      case UPDATE_DB:
        // TODO: update entity using ((UpdateDbEvent) dbEvent).getEntityWrapper()
        break;
      case CLOSE_DB:
        break;
      default:
        break;
    }
  }

  /**
   * Getter for the database event source.
   *
   * @return the event source for communicating opening, closing, updating and saving the database.
   */
  public EventSource<DbEvent> dbEventSource() {
    return dbEventSource;
  }

  /**
   * Getter for the database file.
   *
   * @return the file where the current database is stored.
   */
  public File getDbFile() {
    return dbFileProperty.get();
  }

  /**
   * Getter for the dataSourceProperty.
   *
   * @return property for handling the datasource. Used to reload data if changed.
   */
  public ObjectProperty<DataSource> dataSourceProperty() {
    return dataSourceProperty;
  }

  public ObjectProperty<Task<Void>> dbTaskProperty() {
    return dbTaskProperty;
  }
}
