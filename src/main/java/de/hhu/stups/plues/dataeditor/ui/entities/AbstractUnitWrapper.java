package de.hhu.stups.plues.dataeditor.ui.entities;

import de.hhu.stups.plues.data.entities.AbstractUnit;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SetProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

public class AbstractUnitWrapper implements EntityWrapper {

  private final StringProperty keyProperty;
  private final StringProperty titleProperty;
  private final SetProperty<UnitWrapper> unitsProperty;
  private final SetProperty<ModuleWrapper> modulesProperty;
  private final ObjectProperty<AbstractUnit> abstractUnitProperty;

  /**
   * Initialize the property bindings according to the given abstract unit.
   */
  public AbstractUnitWrapper(final AbstractUnit abstractUnit) {
    assert abstractUnit != null;
    keyProperty = new SimpleStringProperty(abstractUnit.getKey());
    titleProperty = new SimpleStringProperty(abstractUnit.getTitle());
    unitsProperty = new SimpleSetProperty<>(FXCollections.observableSet());
    modulesProperty = new SimpleSetProperty<>(FXCollections.observableSet());
    abstractUnitProperty = new SimpleObjectProperty<>(abstractUnit);
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

  public ObservableSet<UnitWrapper> getUnitsProperty() {
    return unitsProperty.get();
  }

  public void setUnitsProperty(ObservableSet<UnitWrapper> unitsProperty) {
    this.unitsProperty.set(unitsProperty);
  }

  public SetProperty<UnitWrapper> unitsProperty() {
    return unitsProperty;
  }

  public ObservableSet<ModuleWrapper> getModulesProperty() {
    return modulesProperty.get();
  }

  public void setModulesProperty(ObservableSet<ModuleWrapper> modulesProperty) {
    this.modulesProperty.set(modulesProperty);
  }

  public SetProperty<ModuleWrapper> modulesProperty() {
    return modulesProperty;
  }

  public ObjectProperty<AbstractUnit> abstractUnitProperty() {
    return abstractUnitProperty;
  }

  public AbstractUnit getAbstractUnit() {
    return abstractUnitProperty.get();
  }

  @Override
  public String toString() {
    if (abstractUnitProperty.get() == null) {
      return "";
    }
    return abstractUnitProperty.get().getTitle();
  }
}
