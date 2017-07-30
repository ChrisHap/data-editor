package de.hhu.stups.plues.dataeditor.ui.database;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.hhu.stups.plues.data.SqliteStore;
import de.hhu.stups.plues.data.StoreException;
import de.hhu.stups.plues.dataeditor.ui.database.events.DbEvent;
import de.hhu.stups.plues.dataeditor.ui.database.events.LoadDbEvent;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.reactfx.EventSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

@Singleton
public class DbService {

  private final Logger logger = LoggerFactory.getLogger(getClass());
  private final EventSource<DbEvent> dbEventSource;
  private final ObjectProperty<SqliteStore> storeProperty;
  private final ObjectProperty<File> dbFileProperty;

  /**
   * The database service to load and modify a .sqlite3 database.
   */
  @Inject
  public DbService() {
    dbEventSource = new EventSource<>();
    dbEventSource.subscribe(this::handleDbEvent);
    storeProperty = new SimpleObjectProperty<>();
    dbFileProperty = new SimpleObjectProperty<>();
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
      if (storeProperty.get() != null) {
        storeProperty.get().close();
      }
      try {
        storeProperty.set(new SqliteStore(dbFile.getPath()));
        dbFileProperty.set(dbFile);
      } catch (final StoreException storeException) {
        logger.error("Error when loading the sqlite database " + dbFile, storeException);
      }
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
}
