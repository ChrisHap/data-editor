package de.hhu.stups.plues.dataeditor.ui.database.events;

public enum DataChangeType {
  CHANGE_ENTITY, NEW_ENTITY, STORE_ENTITY, DELETE_ENTITY, RELOAD_DB;

  public boolean reloadDb() {
    return this.equals(RELOAD_DB);
  }

  public boolean changeEntity() {
    return this.equals(CHANGE_ENTITY);
  }

  public boolean addEntity() {
    return this.equals(NEW_ENTITY);
  }

  public boolean storeEntity() {
    return this.equals(STORE_ENTITY);
  }

  public boolean deleteEntity() {
    return this.equals(DELETE_ENTITY);
  }
}
