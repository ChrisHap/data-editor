package de.hhu.stups.plues.dataeditor.ui.entities;

import de.hhu.stups.plues.data.entities.ModuleLevel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ModuleLevelWrapper implements EntityWrapper {

  private final StringProperty nameProperty;
  private final ObjectProperty<LevelWrapper> levelProperty;
  private final ObjectProperty<ModuleWrapper> moduleProperty;
  private final ObjectProperty<CourseWrapper> courseProperty;
  private final IntegerProperty creditsProperty;
  private final BooleanProperty mandatoryProperty;
  private final ObjectProperty<ModuleLevel> moduleLevelProperty;

  /**
   * Initialize the property bindings according to the given level.
   */
  public ModuleLevelWrapper(final ModuleLevel moduleLevel) {
    assert moduleLevel != null;
    nameProperty = new SimpleStringProperty(moduleLevel.getName());
    levelProperty = new SimpleObjectProperty<>();
    moduleProperty = new SimpleObjectProperty<>();
    creditsProperty = new SimpleIntegerProperty();
    courseProperty = new SimpleObjectProperty<>();
    mandatoryProperty = new SimpleBooleanProperty(moduleLevel.getMandatory());
    moduleLevelProperty = new SimpleObjectProperty<>(moduleLevel);
  }

  @Override
  public EntityType getEntityType() {
    return EntityType.MODULE_LEVEL;
  }

  @Override
  public String toString() {
    if (moduleLevelProperty.get() == null) {
      return "";
    }
    return moduleLevelProperty.get().getName();
  }

  public String getName() {
    return nameProperty.get();
  }

  public StringProperty nameProperty() {
    return nameProperty;
  }

  public void setNameProperty(String nameProperty) {
    this.nameProperty.set(nameProperty);
  }

  public LevelWrapper getLevel() {
    return levelProperty.get();
  }

  public ObjectProperty<LevelWrapper> levelProperty() {
    return levelProperty;
  }

  public void setLevelProperty(LevelWrapper levelProperty) {
    this.levelProperty.set(levelProperty);
  }

  public ModuleWrapper getModule() {
    return moduleProperty.get();
  }

  public ObjectProperty<ModuleWrapper> moduleProperty() {
    return moduleProperty;
  }

  public void setModuleProperty(ModuleWrapper moduleProperty) {
    this.moduleProperty.set(moduleProperty);
  }

  public CourseWrapper getCourse() {
    return courseProperty.get();
  }

  public ObjectProperty<CourseWrapper> courseProperty() {
    return courseProperty;
  }

  public void setCourseProperty(CourseWrapper courseProperty) {
    this.courseProperty.set(courseProperty);
  }

  public int getCredits() {
    return creditsProperty.get();
  }

  public IntegerProperty creditsProperty() {
    return creditsProperty;
  }

  public void setCreditsProperty(int creditsProperty) {
    this.creditsProperty.set(creditsProperty);
  }

  public boolean isMandatory() {
    return mandatoryProperty.get();
  }

  public BooleanProperty mandatoryProperty() {
    return mandatoryProperty;
  }

  public void setMandatoryProperty(final boolean mandatoryProperty) {
    this.mandatoryProperty.set(mandatoryProperty);
  }

  public ModuleLevel getModuleLevel() {
    return moduleLevelProperty.get();
  }
}
