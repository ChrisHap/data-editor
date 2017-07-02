package de.hhu.stups.plues.dataeditor.ui.database;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.hhu.stups.plues.dataeditor.ui.database.events.DbEvent;
import de.hhu.stups.plues.dataeditor.ui.database.events.LoadDbEvent;
import de.hhu.stups.plues.dataeditor.ui.database.events.UpdateDbEvent;
import org.reactfx.EventSource;

@Singleton
public class DbService {

  private final EventSource<DbEvent> dbEventSource;

  /**
   * The database service to load and modify a .sqlite3 database.
   */
  @Inject
  public DbService() {
    dbEventSource = new EventSource<>();
    dbEventSource.subscribe(this::handleDbEvent);
  }

  private void handleDbEvent(final DbEvent dbEvent) {
    switch (dbEvent.getEventType()) {
      case LOAD_DB:
        System.out.println("load database: " + ((LoadDbEvent) dbEvent).getDbFile());
        break;
      case UPDATE_DB:
        System.out.println("update entity: " + ((UpdateDbEvent) dbEvent).getEntityWrapper());
        break;
      default:
        break;
    }
  }

  public EventSource<DbEvent> dbEventSource() {
    return dbEventSource;
  }
}
