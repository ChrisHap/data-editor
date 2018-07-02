package de.hhu.stups.plues.dataeditor.ui.database.events;

import de.hhu.stups.plues.dataeditor.ui.entities.EntityType;
import de.hhu.stups.plues.dataeditor.ui.entities.EntityWrapper;

public class DataChangeEvent {

  private final DataChangeType dataChangeType;
  private final EntityWrapper changedEntity;
  private final EntityType changedType;
  private final EntityWrapper parent;

  /**
   * Create an event referring to the whole database state like {@link DataChangeType#RELOAD_DB}.
   */
  public DataChangeEvent(final DataChangeType dataChangeType) {
    this.dataChangeType = dataChangeType;
    changedEntity = null;
    changedType = null;
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
    changedType = null;
    parent = null;
  }

  /**
   * Create an event to change a specific database entity for a given {@link EntityWrapper}.
   */
  public DataChangeEvent(final DataChangeType dataChangeType,
                         final EntityWrapper changedEntity,
                         final EntityType changedType) {
    assert dataChangeType.changeEntity();
    this.dataChangeType = dataChangeType;
    this.changedEntity = changedEntity;
    this.changedType = changedType;
    parent = null;
  }

  /**
   * Create an event to change a specific database entity for a given {@link EntityWrapper}
   * with a given parent.
   */
  public DataChangeEvent(final DataChangeType dataChangeType,
                         final EntityWrapper changedEntity,
                         final EntityType changedType,
                         final EntityWrapper parent) {
    assert dataChangeType.changeEntity();
    this.dataChangeType = dataChangeType;
    this.changedEntity = changedEntity;
    this.changedType = changedType;
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

  public EntityType getChangedType() {
    return changedType;
  }
}
