package de.hhu.stups.plues.dataeditor.ui.entities;

import de.hhu.stups.plues.data.entities.Group;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SetProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

public class GroupWrapper implements EntityWrapper {

  private final IntegerProperty idProperty;
  private final IntegerProperty halfSemesterProperty;
  private final SetProperty<SessionWrapper> sessionsProperty;
  private final ObjectProperty<UnitWrapper> unitProperty;
  private final ObjectProperty<Group> groupProperty;

  /**
   * Initialize the property bindings according to the given {@link Group}.
   */
  public GroupWrapper(final Group group) {
    assert group != null;
    idProperty = new SimpleIntegerProperty(group.getId());
    halfSemesterProperty = new SimpleIntegerProperty(group.getHalfSemester());
    sessionsProperty = new SimpleSetProperty<>(FXCollections.observableSet());
    unitProperty = new SimpleObjectProperty<>();
    groupProperty = new SimpleObjectProperty<>(group);
  }

  public int getId() {
    return idProperty.get();
  }

  public void setId(int id) {
    this.idProperty.set(id);
  }

  public IntegerProperty idProperty() {
    return idProperty;
  }

  public int getHalfSemester() {
    return halfSemesterProperty.get();
  }

  public void setHalfSemester(int halfSemester) {
    this.halfSemesterProperty.set(halfSemester);
  }

  public IntegerProperty halfSemesterProperty() {
    return halfSemesterProperty;
  }

  public ObservableSet<SessionWrapper> getSessions() {
    return sessionsProperty.get();
  }

  public void setSessions(ObservableSet<SessionWrapper> sessions) {
    this.sessionsProperty.set(sessions);
  }

  public SetProperty<SessionWrapper> sessionsProperty() {
    return sessionsProperty;
  }

  public UnitWrapper getUnit() {
    return unitProperty.get();
  }

  public void setUnit(final UnitWrapper unit) {
    this.unitProperty.set(unit);
  }

  public ObjectProperty<UnitWrapper> unitProperty() {
    return unitProperty;
  }

  public Group getGroup() {
    return groupProperty.get();
  }

  public ObjectProperty<Group> groupProperty() {
    return groupProperty;
  }

  @Override
  public String toString() {
    if (groupProperty.get() == null) {
      return "";
    }
    return "G" + getId();
  }

  @Override
  public EntityType getEntityType() {
    return EntityType.GROUP;
  }
}
