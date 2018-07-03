package de.hhu.stups.plues.dataeditor.ui.database.events;

import de.hhu.stups.plues.dataeditor.ui.entities.EntityWrapper;

public class DataChangeEvent {

  private final DataChangeType dataChangeType;
  private final EntityWrapper changedEntity;
  private final EntityWrapper parent;

  /**
   * Create an event referring to the whole database state like {@link DataChangeType#RELOAD_DB}.
   */
  public DataChangeEvent(final DataChangeType dataChangeType) {
    this.dataChangeType = dataChangeType;
    changedEntity = null;
    parent = null;
  }

  /**
   * Create an event to change a specific database entity for a given {@link EntityWrapper}.
   */
  public DataChangeEvent(final DataChangeType dataChangeType,
                         final EntityWrapper changedEntity) {
    assert dataChangeType.changeEntity();
    this.dataChangeType = dataChangeType;
    this.changedEntity = changedEntity;
    parent = null;
  }

  /**
   * Create an event to change a specific database entity for a given {@link EntityWrapper}
   * with a given parent.
   */
  public DataChangeEvent(final DataChangeType dataChangeType,
                         final EntityWrapper changedEntity,
                         final EntityWrapper parent) {
    assert dataChangeType.changeEntity();
    this.dataChangeType = dataChangeType;
    this.changedEntity = changedEntity;
    this.parent = parent;
  }

  public DataChangeType getDataChangeType() {
    return dataChangeType;
  }

  public EntityWrapper getChangedEntity() {
    return changedEntity;
  }

  public EntityWrapper getParent() {
    return parent;
  }

}
