package de.hhu.stups.plues.dataeditor.ui.entities;

import de.hhu.stups.plues.data.entities.Module;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SetProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

public class ModuleWrapper implements EntityWrapper {

  private final IntegerProperty idProperty;
  private final StringProperty keyProperty;
  private final StringProperty titleProperty;
  private final IntegerProperty pordnrProperty;
  private final IntegerProperty electiveUnitsProperty;
  private final BooleanProperty bundledProperty;
  private final SetProperty<AbstractUnitWrapper> abstractUnitsProperty;
  private final SetProperty<CourseWrapper> coursesProperty;
  private final ObjectProperty<Module> moduleProperty;

  /**
   * Initialize the property bindings according to the given {@link Module}.
   */
  public ModuleWrapper(final Module module) {
    assert module != null;
    keyProperty = new SimpleStringProperty(module.getKey());
    titleProperty = new SimpleStringProperty(module.getTitle());
    pordnrProperty = new SimpleIntegerProperty(module.getPordnr());
    bundledProperty = new SimpleBooleanProperty(module.getBundled());
    electiveUnitsProperty = new SimpleIntegerProperty(module.getElectiveUnits());
    abstractUnitsProperty = new SimpleSetProperty<>(FXCollections.observableSet());
    coursesProperty = new SimpleSetProperty<>(FXCollections.observableSet());
    moduleProperty = new SimpleObjectProperty<>(module);
    idProperty = new SimpleIntegerProperty(module.getId());
  }

  public int getId() {
    return idProperty.get();
  }

  public IntegerProperty idProperty() {
    return idProperty;
  }

  public void setId(int id) {
    this.idProperty.set(id);
  }

  public String getKey() {
    return keyProperty.get();
  }

  public void setKeyProperty(String keyProperty) {
    this.keyProperty.set(keyProperty);
  }

  public StringProperty keyProperty() {
    return keyProperty;
  }

  public String getTitle() {
    return titleProperty.get();
  }

  public void setTitleProperty(String titleProperty) {
    this.titleProperty.set(titleProperty);
  }

  public StringProperty titleProperty() {
    return titleProperty;
  }

  public int getPordnr() {
    return pordnrProperty.get();
  }

  public void setPordnrProperty(int pordnrProperty) {
    this.pordnrProperty.set(pordnrProperty);
  }

  public IntegerProperty pordnrProperty() {
    return pordnrProperty;
  }

  public BooleanProperty bundledProperty() {
    return bundledProperty;
  }

  public int getElectiveUnits() {
    return electiveUnitsProperty.get();
  }

  public void setElectiveUnitsProperty(int electiveUnitsProperty) {
    this.electiveUnitsProperty.set(electiveUnitsProperty);
  }

  public IntegerProperty electiveUnitsProperty() {
    return electiveUnitsProperty;
  }

  public ObservableSet<AbstractUnitWrapper> getAbstractUnits() {
    return abstractUnitsProperty.get();
  }

  public void setAbstractUnitsProperty(ObservableSet<AbstractUnitWrapper> abstractUnitsProperty) {
    this.abstractUnitsProperty.set(abstractUnitsProperty);
  }

  public SetProperty<AbstractUnitWrapper> abstractUnitsProperty() {
    return abstractUnitsProperty;
  }

  public ObservableSet<CourseWrapper> getCourses() {
    return coursesProperty.get();
  }

  public void setCoursesProperty(final ObservableSet<CourseWrapper> coursesProperty) {
    this.coursesProperty.set(coursesProperty);
  }

  public SetProperty<CourseWrapper> coursesProperty() {
    return coursesProperty;
  }

  public ObjectProperty<Module> moduleProperty() {
    return moduleProperty;
  }

  public Module getModule() {
    return moduleProperty.get();
  }

  @Override
  public String toString() {
    if (moduleProperty.get() == null) {
      return "";
    }
    return moduleProperty.get().getTitle();
  }

  @Override
  public EntityType getEntityType() {
    return EntityType.MODULE;
  }
}
