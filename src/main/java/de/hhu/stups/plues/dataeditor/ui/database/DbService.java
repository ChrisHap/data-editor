package de.hhu.stups.plues.dataeditor.ui.database;

import de.hhu.stups.plues.dataeditor.ui.database.events.DbEvent;
import de.hhu.stups.plues.dataeditor.ui.database.events.LoadDbEvent;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
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

@Component
public class DbService {

  private final Logger logger = LoggerFactory.getLogger(getClass());
  private final EventSource<DbEvent> dbEventSource;
  private final ObjectProperty<DataSource> dataSourceProperty;
  private final ObjectProperty<File> dbFileProperty;
  private final ObjectProperty<Task<Void>> dbTaskProperty;
  private DataSource dataSource;
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
    dataSource = dataSourceBuilder.build();
    abstractRoutingDataSource = new AbstractRoutingDataSource() {
      @Override
      protected Object determineCurrentLookupKey() {
        return null;
      }
    };
    dbTaskProperty = new SimpleObjectProperty<>();
  }

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
            setDataSource(newDataSource);
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

  public EventSource<DbEvent> dbEventSource() {
    return dbEventSource;
  }

  public File getDbFile() {
    return dbFileProperty.get();
  }

  public ObjectProperty<DataSource> dataSourceProperty() {
    return dataSourceProperty;
  }

  public DataSource getDataSource() {
    return dataSource;
  }

  private void setDataSource(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public ObjectProperty<Task<Void>> dbTaskProperty() {
    return dbTaskProperty;
  }
}
