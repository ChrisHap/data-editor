package de.hhu.stups.plues.dataeditor.ui.entities;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SessionWrapper implements EntityWrapper {

  private final IntegerProperty idProperty;
  private final StringProperty dayProperty;
  private final IntegerProperty timeProperty;
  private final IntegerProperty rhythmProperty;
  private final IntegerProperty durationProperty;
  private final BooleanProperty tentativeProperty;
  private final ObjectProperty<Group> groupProperty;
  private final ObjectProperty<Session> sessionProperty;

  /**
   * Initialize the property bindings according to the given {@link Session}.
   */
  public SessionWrapper(final Session session) {
    assert session != null;
    dayProperty = new SimpleStringProperty(session.getDay());
    timeProperty = new SimpleIntegerProperty(session.getTime());
    rhythmProperty = new SimpleIntegerProperty(session.getRhythm());
    durationProperty = new SimpleIntegerProperty(session.getDuration());
    tentativeProperty = new SimpleBooleanProperty(session.isTentative());
    groupProperty = new SimpleObjectProperty<>(session.getGroup());
    sessionProperty = new SimpleObjectProperty<>(session);
    idProperty = new SimpleIntegerProperty(session.getId());
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

  public String getDay() {
    return dayProperty.get();
  }

  public void setDayProperty(String dayProperty) {
    this.dayProperty.set(dayProperty);
  }

  public StringProperty dayProperty() {
    return dayProperty;
  }

  public int getTime() {
    return timeProperty.get();
  }

  public void setTimeProperty(int timeProperty) {
    this.timeProperty.set(timeProperty);
  }

  public IntegerProperty timeProperty() {
    return timeProperty;
  }

  public int getRhythm() {
    return rhythmProperty.get();
  }

  public void setRhythmProperty(int rhythmProperty) {
    this.rhythmProperty.set(rhythmProperty);
  }

  public IntegerProperty rhythmProperty() {
    return rhythmProperty;
  }

  public int getDuration() {
    return durationProperty.get();
  }

  public void setDurationProperty(int durationProperty) {
    this.durationProperty.set(durationProperty);
  }

  public IntegerProperty durationProperty() {
    return durationProperty;
  }

  public boolean isTentativeProperty() {
    return tentativeProperty.get();
  }

  public void setTentativeProperty(boolean tentativeProperty) {
    this.tentativeProperty.set(tentativeProperty);
  }

  public boolean isTentative() {
    return tentativeProperty.get();
  }

  public BooleanProperty tentativeProperty() {
    return tentativeProperty;
  }

  public Group getGroup() {
    return groupProperty.get();
  }

  public void setGroup(Group group) {
    this.groupProperty.set(group);
  }

  public ObjectProperty<Group> groupProperty() {
    return groupProperty;
  }

  public ObjectProperty<Session> sessionProperty() {
    return sessionProperty;
  }

  public Session getSession() {
    return sessionProperty().get();
  }

  @Override
  public String toString() {
    if (sessionProperty.get() == null) {
      return "";
    }
    return sessionProperty.get().toString();
  }

  @Override
  public EntityType getEntityType() {
    return EntityType.SESSION;
  }
}
