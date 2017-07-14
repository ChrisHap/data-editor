package de.hhu.stups.plues.dataeditor.ui.entities;

import de.hhu.stups.plues.data.entities.Level;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SetProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableSet;

public class LevelWrapper implements EntityWrapper {

  private final StringProperty nameProperty;
  private final IntegerProperty minCreditsProperty;
  private final IntegerProperty maxCreditsProperty;
  private final ObjectProperty<LevelWrapper> parentProperty;
  private final ObjectProperty<CourseWrapper> courseProperty;
  private final SetProperty<LevelWrapper> childrenProperty;
  private final ObjectProperty<Level> levelProperty;

  /**
   * Initialize the property bindings according to the given level.
   */
  public LevelWrapper(final Level level) {
    assert level != null;
    nameProperty = new SimpleStringProperty(level.getName());
    minCreditsProperty = new SimpleIntegerProperty(level.getMinCreditPoints());
    maxCreditsProperty = new SimpleIntegerProperty(level.getMaxCreditPoints());
    parentProperty = new SimpleObjectProperty<>();
    childrenProperty = new SimpleSetProperty<>();
    levelProperty = new SimpleObjectProperty<>(level);
    courseProperty = new SimpleObjectProperty<>();
  }

  public String getNameProperty() {
    return nameProperty.get();
  }

  public void setNameProperty(String nameProperty) {
    this.nameProperty.set(nameProperty);
  }

  public StringProperty nameProperty() {
    return nameProperty;
  }

  public int getMinCreditsProperty() {
    return minCreditsProperty.get();
  }

  public void setMinCreditsProperty(int minCreditsProperty) {
    this.minCreditsProperty.set(minCreditsProperty);
  }

  public IntegerProperty minCreditsProperty() {
    return minCreditsProperty;
  }

  public int getMaxCreditsProperty() {
    return maxCreditsProperty.get();
  }

  public void setMaxCreditsProperty(int maxCreditsProperty) {
    this.maxCreditsProperty.set(maxCreditsProperty);
  }

  public IntegerProperty maxCreditsProperty() {
    return maxCreditsProperty;
  }

  public LevelWrapper getParent() {
    return parentProperty.get();
  }

  public void setParentProperty(LevelWrapper parentProperty) {
    this.parentProperty.set(parentProperty);
  }

  public ObjectProperty<LevelWrapper> parentProperty() {
    return parentProperty;
  }

  public ObjectProperty<CourseWrapper> courseProperty() {
    return courseProperty;
  }

  public void setCourseProperty(final CourseWrapper courseWrapper) {
    courseProperty.set(courseWrapper);
  }

  public ObservableSet<LevelWrapper> getChildrenProperty() {
    return childrenProperty.get();
  }

  public void setChildrenProperty(ObservableSet<LevelWrapper> childrenProperty) {
    this.childrenProperty.set(childrenProperty);
  }

  public SetProperty<LevelWrapper> childrenProperty() {
    return childrenProperty;
  }

  public ObjectProperty<Level> levelProperty() {
    return levelProperty;
  }

  @Override
  public String toString() {
    if (levelProperty.get() == null) {
      return "";
    }
    return levelProperty.get().getName();
  }

  @Override
  public EntityType getEntityType() {
    return EntityType.LEVEL;
  }

  public Level getLevel() {
    return levelProperty.get();
  }

  public CourseWrapper getCourseWrapper() {
    return courseProperty.get();
  }
}
