package de.hhu.stups.plues.dataeditor.ui.entities;

import de.hhu.stups.plues.data.entities.Unit;
import javafx.beans.property.SetProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

import java.util.stream.Collectors;

public class UnitWrapper implements EntityWrapper {


  private final StringProperty keyProperty;
  private final StringProperty titleProperty;
  private final SetProperty<Integer> semestersProperty;
  private final SetProperty<AbstractUnitWrapper> abstractUnitsProperty;
  // TODO: groups?

  /**
   * Initialize the property bindings according to the given unit.
   */
  public UnitWrapper(final Unit unit) {
    keyProperty = new SimpleStringProperty(unit.getKey());
    titleProperty = new SimpleStringProperty(unit.getTitle());
    semestersProperty = new SimpleSetProperty<>(FXCollections.observableSet(unit.getSemesters()));
    abstractUnitsProperty = new SimpleSetProperty<>(
        FXCollections.observableSet(unit.getAbstractUnits().stream()
            .map(AbstractUnitWrapper::new).collect(Collectors.toSet())));
  }

  public String getKeyProperty() {
    return keyProperty.get();
  }

  public void setKeyProperty(String keyProperty) {
    this.keyProperty.set(keyProperty);
  }

  public StringProperty keyPropertyProperty() {
    return keyProperty;
  }

  public String getTitleProperty() {
    return titleProperty.get();
  }

  public void setTitleProperty(String titleProperty) {
    this.titleProperty.set(titleProperty);
  }

  public StringProperty titlePropertyProperty() {
    return titleProperty;
  }

  public ObservableSet<Integer> getSemestersProperty() {
    return semestersProperty.get();
  }

  public void setSemestersProperty(ObservableSet<Integer> semestersProperty) {
    this.semestersProperty.set(semestersProperty);
  }

  public SetProperty<Integer> semestersPropertyProperty() {
    return semestersProperty;
  }

  public ObservableSet<AbstractUnitWrapper> getAbstractUnitsProperty() {
    return abstractUnitsProperty.get();
  }

  public void setAbstractUnitsProperty(ObservableSet<AbstractUnitWrapper> abstractUnitsProperty) {
    this.abstractUnitsProperty.set(abstractUnitsProperty);
  }

  public SetProperty<AbstractUnitWrapper> abstractUnitsPropertyProperty() {
    return abstractUnitsProperty;
  }

}
