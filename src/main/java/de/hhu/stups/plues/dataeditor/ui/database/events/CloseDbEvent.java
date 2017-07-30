package de.hhu.stups.plues.dataeditor.ui.database.events;


public class CloseDbEvent implements DbEvent {

  private static final DbEventType eventType = DbEventType.CLOSE_DB;

  /**
   * Close the database.
   */
  public CloseDbEvent() {
    //
  }

  @Override
  public DbEventType getEventType() {
    return eventType;
  }
}
