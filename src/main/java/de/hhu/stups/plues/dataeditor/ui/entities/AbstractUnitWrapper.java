package de.hhu.stups.plues.dataeditor.ui.entities;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SetProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

import java.util.HashSet;
import java.util.stream.Collectors;

public class AbstractUnitWrapper implements EntityWrapper {

  private final IntegerProperty idProperty;
  private final StringProperty keyProperty;
  private final StringProperty titleProperty;
  private final SetProperty<UnitWrapper> unitsProperty;
  private final SetProperty<ModuleWrapper> modulesProperty;
  private final ObjectProperty<AbstractUnit> abstractUnitProperty;

  /**
   * Initialize the property bindings according to the given {@link AbstractUnit}.
   */
  public AbstractUnitWrapper(final AbstractUnit abstractUnit) {
    assert abstractUnit != null;
    keyProperty = new SimpleStringProperty(abstractUnit.getKey());
    titleProperty = new SimpleStringProperty(abstractUnit.getTitle());
    unitsProperty = new SimpleSetProperty<>(FXCollections.observableSet());
    modulesProperty = new SimpleSetProperty<>(FXCollections.observableSet());
    abstractUnitProperty = new SimpleObjectProperty<>(abstractUnit);
    idProperty = new SimpleIntegerProperty(abstractUnit.getId());
    setPropertyListener();
  }

  private void setPropertyListener() {
    keyProperty.addListener((observable, oldValue, newValue) ->
          abstractUnitProperty.get().setKey(newValue));
    titleProperty.addListener((observable, oldValue, newValue) ->
          abstractUnitProperty.get().setTitle(newValue));
    unitsProperty.addListener((observable, oldValue, newValue) ->
          addOrRemoveUnit(newValue));
    modulesProperty.addListener((observable, oldValue, newValue) ->
          addOrRemoveModule(newValue));

    idProperty.addListener((observable, oldValue, newValue) -> {
      final AbstractUnit abstractUnit = abstractUnitProperty.get();
      if (newValue != null) {
        abstractUnit.setId(newValue.intValue());
        return;
      }
      abstractUnit.setId(-1);
    });
  }

  private void addOrRemoveUnit(final ObservableSet<UnitWrapper> newValue) {
    abstractUnitProperty.get().setUnits(newValue.stream().map(
          UnitWrapper::getUnit).collect(Collectors.toSet()));
  }

  private void addOrRemoveModule(final ObservableSet<ModuleWrapper> newValue) {
    abstractUnitProperty.get().setModules(newValue.stream().map(
          ModuleWrapper::getModule).collect(Collectors.toSet()));
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

  public String getKey() {
    return keyProperty.get();
  }

  public void setKey(String keyProperty) {
    this.keyProperty.set(keyProperty);
  }

  public StringProperty keyProperty() {
    return keyProperty;
  }

  public String getTitle() {
    return titleProperty.get();
  }

  public void setTitle(String titleProperty) {
    this.titleProperty.set(titleProperty);
  }

  public StringProperty titleProperty() {
    return titleProperty;
  }

  public ObservableSet<UnitWrapper> getUnits() {
    return unitsProperty.get();
  }

  public void setUnits(ObservableSet<UnitWrapper> unitsProperty) {
    this.unitsProperty.set(unitsProperty);
  }

  public SetProperty<UnitWrapper> unitsProperty() {
    return unitsProperty;
  }

  public ObservableSet<ModuleWrapper> getModules() {
    return modulesProperty.get();
  }

  public void setModules(ObservableSet<ModuleWrapper> modulesProperty) {
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

  @Override
  public EntityType getEntityType() {
    return EntityType.ABSTRACT_UNIT;
  }

  /**
   * Creates an empty AbstractUnitWrapper.
   * @return the empty AbstractUnitWrapper.
   */
  public static AbstractUnitWrapper createEmptyAbstractUnitWrapper() {
    AbstractUnit abstractUnit = new AbstractUnit();
    abstractUnit.setTitle("");
    abstractUnit.setId(0);
    abstractUnit.setModules(new HashSet<>());
    abstractUnit.setUnits(new HashSet<>());
    return new AbstractUnitWrapper(abstractUnit);
  }
}
