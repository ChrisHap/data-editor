package de.hhu.stups.plues.dataeditor.ui.entities;

import de.hhu.stups.plues.data.entities.Module;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SetProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

import java.util.stream.Collectors;

public class ModuleWrapper implements EntityWrapper {

  private final StringProperty keyProperty;
  private final StringProperty titleProperty;
  private final IntegerProperty pordnrProperty;
  private final IntegerProperty electiveUnitsProperty;
  private final SetProperty<AbstractUnitWrapper> abstractUnitsProperty;
  private final SetProperty<CourseWrapper> coursesProperty;

  /**
   * Initialize the property bindings according to the given module.
   */
  public ModuleWrapper(final Module module) {
    keyProperty = new SimpleStringProperty(module.getKey());
    titleProperty = new SimpleStringProperty(module.getTitle());
    pordnrProperty = new SimpleIntegerProperty(module.getPordnr());
    electiveUnitsProperty = new SimpleIntegerProperty(module.getElectiveUnits());
    abstractUnitsProperty = new SimpleSetProperty<>(
        FXCollections.observableSet(module.getAbstractUnits().stream()
            .map(AbstractUnitWrapper::new).collect(Collectors.toSet())));
    coursesProperty = new SimpleSetProperty<>(
        FXCollections.observableSet(module.getCourses().stream()
            .map(CourseWrapper::new).collect(Collectors.toSet())));
  }

  public String getKeyProperty() {
    return keyProperty.get();
  }

  public void setKeyProperty(String keyProperty) {
    this.keyProperty.set(keyProperty);
  }

  public StringProperty keyProperty() {
    return keyProperty;
  }

  public String getTitleProperty() {
    return titleProperty.get();
  }

  public void setTitleProperty(String titleProperty) {
    this.titleProperty.set(titleProperty);
  }

  public StringProperty titleProperty() {
    return titleProperty;
  }

  public int getPordnrProperty() {
    return pordnrProperty.get();
  }

  public void setPordnrProperty(int pordnrProperty) {
    this.pordnrProperty.set(pordnrProperty);
  }

  public IntegerProperty pordnrProperty() {
    return pordnrProperty;
  }

  public int getElectiveUnitsProperty() {
    return electiveUnitsProperty.get();
  }

  public void setElectiveUnitsProperty(int electiveUnitsProperty) {
    this.electiveUnitsProperty.set(electiveUnitsProperty);
  }

  public IntegerProperty electiveUnitsProperty() {
    return electiveUnitsProperty;
  }

  public ObservableSet<AbstractUnitWrapper> getAbstractUnitsProperty() {
    return abstractUnitsProperty.get();
  }

  public void setAbstractUnitsProperty(ObservableSet<AbstractUnitWrapper> abstractUnitsProperty) {
    this.abstractUnitsProperty.set(abstractUnitsProperty);
  }

  public SetProperty<AbstractUnitWrapper> abstractUnitsProperty() {
    return abstractUnitsProperty;
  }

  public ObservableSet<CourseWrapper> getCoursesProperty() {
    return coursesProperty.get();
  }

  public void setCoursesProperty(ObservableSet<CourseWrapper> coursesProperty) {
    this.coursesProperty.set(coursesProperty);
  }

  public SetProperty<CourseWrapper> coursesProperty() {
    return coursesProperty;
  }
}
