package de.hhu.stups.plues.dataeditor.ui.entities;

import de.hhu.stups.plues.data.entities.Unit;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SetProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

public class UnitWrapper implements EntityWrapper {

  private final StringProperty keyProperty;
  private final StringProperty titleProperty;
  private final SetProperty<Integer> semestersProperty;
  private final SetProperty<AbstractUnitWrapper> abstractUnitsProperty;
  private final ObjectProperty<Unit> unitProperty;
  // TODO: groups?

  /**
   * Initialize the property bindings according to the given unit.
   */
  public UnitWrapper(final Unit unit) {
    assert unit != null;
    keyProperty = new SimpleStringProperty(unit.getKey());
    titleProperty = new SimpleStringProperty(unit.getTitle());
    semestersProperty = new SimpleSetProperty<>(FXCollections.observableSet(unit.getSemesters()));
    abstractUnitsProperty = new SimpleSetProperty<>(FXCollections.observableSet());
    unitProperty = new SimpleObjectProperty<>(unit);
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

  public ObservableSet<Integer> getSemestersProperty() {
    return semestersProperty.get();
  }

  public void setSemestersProperty(ObservableSet<Integer> semestersProperty) {
    this.semestersProperty.set(semestersProperty);
  }

  public SetProperty<Integer> semestersProperty() {
    return semestersProperty;
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

  public ObjectProperty<Unit> unitProperty() {
    return unitProperty;
  }

  public Unit getUnit() {
    return unitProperty.get();
  }

  @Override
  public String toString() {
    if (unitProperty.get() == null) {
      return "";
    }
    return unitProperty.get().getTitle();
  }

  @Override
  public EntityType getEntityType() {
    return EntityType.UNIT;
  }
}
