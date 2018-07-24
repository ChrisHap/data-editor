package de.hhu.stups.plues.dataeditor.ui.entities;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SetProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableSet;

import java.util.Set;
import java.util.stream.Collectors;

public class LevelWrapper implements EntityWrapper {

  private final IntegerProperty idProperty;
  private final StringProperty nameProperty;
  private final IntegerProperty minCreditsProperty;
  private final IntegerProperty maxCreditsProperty;
  private final ObjectProperty<LevelWrapper> parentProperty;
  private final ObjectProperty<CourseWrapper> courseProperty;
  private final SetProperty<LevelWrapper> childrenProperty;
  private final ObjectProperty<Level> levelProperty;

  /**
   * Initialize the property bindings according to the given {@link Level}.
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
    idProperty = new SimpleIntegerProperty(level.getId());
    setPropertyListener();
  }

  private void setPropertyListener() {
    nameProperty.addListener((observable, oldValue, newValue) ->
          levelProperty.get().setName(newValue));
    minCreditsProperty.addListener((observable, oldValue, newValue) ->
          levelProperty.get().setMinCreditPoints(newValue.intValue()));
    maxCreditsProperty.addListener((observable, oldValue, newValue) ->
          levelProperty.get().setMaxCreditPoints(newValue.intValue()));

    parentProperty.addListener((observable, oldValue, newValue) -> {
      if (newValue != null) {
        levelProperty.get().setParent(newValue.getLevel());
      } else {
        levelProperty.get().setParent(null);
      }
    });

    courseProperty.addListener((observable, oldValue, newValue) -> {
      if (newValue != null) {
        levelProperty.get().setCourse(newValue.getCourse());
      } else {
        levelProperty.get().setCourse(null);
      }
    });

    childrenProperty.addListener((observable, oldValue, newValue) ->
          addOrRemoveLevel(levelProperty.get().getChildren(), oldValue, newValue));

    idProperty.addListener((observable, oldValue, newValue) -> {
      final Level level = levelProperty.get();
      if (newValue != null) {
        level.setId(newValue.intValue());
        return;
      }
      level.setId(-1);
    });
  }

  private void addOrRemoveLevel(final Set<Level> levels,
                                 final ObservableSet<LevelWrapper> oldValue,
                                 final ObservableSet<LevelWrapper> newValue) {
    if (newValue.size() > oldValue.size()) {
      newValue.removeAll(oldValue);
      levels.addAll(newValue.stream().map(LevelWrapper::getLevel).collect(Collectors.toSet()));
      return;
    }
    if (newValue.size() < oldValue.size()) {
      oldValue.removeAll(newValue);
      levels.removeAll(
            oldValue.stream().map(LevelWrapper::getLevel).collect(Collectors.toSet()));
    }
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

  public String getNameProperty() {
    return nameProperty.get();
  }

  public void setNameProperty(final String name) {
    this.nameProperty.set(name);
  }

  public StringProperty nameProperty() {
    return nameProperty;
  }

  public String getName() {
    return nameProperty.get();
  }

  public int getMinCreditsProperty() {
    return minCreditsProperty.get();
  }

  public void setMinCredits(final int minCredits) {
    this.minCreditsProperty.set(minCredits);
  }

  public IntegerProperty minCreditsProperty() {
    return minCreditsProperty;
  }

  public int getMaxCreditsProperty() {
    return maxCreditsProperty.get();
  }

  public void setMaxCredits(final int maxCredits) {
    this.maxCreditsProperty.set(maxCredits);
  }

  public IntegerProperty maxCreditsProperty() {
    return maxCreditsProperty;
  }

  public LevelWrapper getParent() {
    return parentProperty.get();
  }

  public void setParent(final LevelWrapper parent) {
    this.parentProperty.set(parent);
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

  public void setChildrenProperty(final ObservableSet<LevelWrapper> children) {
    this.childrenProperty.set(children);
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

  /**
   * Creates an empty LewelWrapper.
   * @return the created LevelWraper.
   */
  public static LevelWrapper createEmptyLevelWrapper() {
    Level level = new Level();
    return new LevelWrapper(level);
  }
}
