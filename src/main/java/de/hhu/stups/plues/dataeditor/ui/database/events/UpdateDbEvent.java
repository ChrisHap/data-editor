package de.hhu.stups.plues.dataeditor.ui.database.events;

import de.hhu.stups.plues.dataeditor.ui.entities.EntityWrapper;

public class UpdateDbEvent implements DbEvent {

  private final EntityWrapper entityWrapper;
  private static final DbEventType eventType = DbEventType.UPDATE_DB;

  public UpdateDbEvent(final EntityWrapper entityWrapper) {
    this.entityWrapper = entityWrapper;
  }

  public EntityWrapper getEntityWrapper() {
    return entityWrapper;
  }

  @Override
  public DbEventType getEventType() {
    return eventType;
  }
}
