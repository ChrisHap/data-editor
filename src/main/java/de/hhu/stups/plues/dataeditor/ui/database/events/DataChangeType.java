package de.hhu.stups.plues.dataeditor.ui.database.events;

public enum DataChangeType {
  CHANGE_ENTITY, RELOAD_DB;

  public boolean reloadDb() {
    return this.equals(RELOAD_DB);
  }

  public boolean changeEntity() {
    return this.equals(CHANGE_ENTITY);
  }
}
