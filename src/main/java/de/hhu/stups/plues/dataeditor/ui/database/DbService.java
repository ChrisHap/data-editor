package de.hhu.stups.plues.dataeditor.ui.database;

import de.hhu.stups.plues.data.SqliteStore;
import de.hhu.stups.plues.data.StoreException;
import de.hhu.stups.plues.dataeditor.ui.database.events.DbEvent;
import de.hhu.stups.plues.dataeditor.ui.database.events.LoadDbEvent;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.reactfx.EventSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.File;

@Component
public class DbService {

  private final Logger logger = LoggerFactory.getLogger(getClass());
  private final EventSource<DbEvent> dbEventSource;
  private final ObjectProperty<SqliteStore> storeProperty;
  private final ObjectProperty<DataSource> dataSourceProperty;
  private final ObjectProperty<File> dbFileProperty;
  private final ConfigurableApplicationContext context;
  private DataSource dataSource;

  /**
   * The database service to load and modify a .sqlite3 database.
   */
  @Autowired
  public DbService(ConfigurableApplicationContext context) {
    dbEventSource = new EventSource<>();
    dbEventSource.subscribe(this::handleDbEvent);
    storeProperty = new SimpleObjectProperty<>();
    dataSourceProperty = new SimpleObjectProperty<>();
    dbFileProperty = new SimpleObjectProperty<>();
    this.context=context;
    DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
    dataSourceBuilder.type(org.sqlite.SQLiteDataSource.class);
    dataSourceBuilder.driverClassName("org.sqlite.JDBC");
    dataSourceBuilder.url("jdbc:sqlite:cs.sqlite3");
    dataSource = dataSourceBuilder.build();
  }

  private void handleDbEvent(final DbEvent dbEvent) {
    switch (dbEvent.getEventType()) {
      case LOAD_DB:
        final Thread loadDbThread =
            new Thread(getLoadDbRunnable(((LoadDbEvent) dbEvent).getDbFile()));
        loadDbThread.setDaemon(true);
        loadDbThread.start();
        break;
      case UPDATE_DB:
        // TODO: update entity using ((UpdateDbEvent) dbEvent).getEntityWrapper()
        break;
      case CLOSE_DB:
        if (storeProperty.get() == null) {
          break;
        }
        storeProperty.get().close();
        break;
      default:
        break;
    }
  }

  private Runnable getLoadDbRunnable(final File dbFile) {
    return () -> {
      // close old store if another database has been loaded
      if (context.getBean(DataSource.class)!=null) {
        //TODO vielleicht schließen?
      }
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.type(org.sqlite.SQLiteDataSource.class);
        dataSourceBuilder.driverClassName("org.sqlite.JDBC");
        dataSourceBuilder.url("jdbc:sqlite:"+dbFile.getAbsolutePath());
        DataSource dataSource = dataSourceBuilder.build();
        setDataSource(dataSource);
        //TODO lauffähig machen
        //context.refresh();

        dataSourceProperty.set(dataSource);
        //storeProperty.set(new SqliteStore(dbFile.getPath()));
        dbFileProperty.set(dbFile);
    };
  }

  public EventSource<DbEvent> dbEventSource() {
    return dbEventSource;
  }

  public File getDbFile() {
    return dbFileProperty.get();
  }

  public ObjectProperty<SqliteStore> storeProperty() {
    return storeProperty;
  }

  public ObjectProperty<DataSource> dataSourceProperty() {
    return dataSourceProperty;
  }

  public DataSource getDataSource(){
    return dataSource;
  }

  private void setDataSource(DataSource dataSource){
    this.dataSource=dataSource;
  }
}
