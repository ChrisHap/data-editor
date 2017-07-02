package de.hhu.stups.plues.dataeditor.ui.database.events;

import java.io.File;

public class LoadDbEvent implements DbEvent {

  private final File dbFile;
  private final DbEventType eventType = DbEventType.LOAD_DB;

  public LoadDbEvent(final File dbFile) {
    this.dbFile = dbFile;
  }

  public File getDbFile() {
    return dbFile;
  }

  @Override
  public DbEventType getEventType() {
    return eventType;
  }
}
