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

public class UnitWrapper implements EntityWrapper {

  private final IntegerProperty idProperty;
  private final StringProperty keyProperty;
  private final StringProperty titleProperty;
  private final SetProperty<Integer> semestersProperty;
  private final SetProperty<AbstractUnitWrapper> abstractUnitsProperty;
  private final ObjectProperty<Unit> unitProperty;
  private final SetProperty<GroupWrapper> groupsProperty;

  /**
   * Initialize the property bindings according to the given {@link Unit}.
   */
  public UnitWrapper(final Unit unit) {
    assert unit != null;
    keyProperty = new SimpleStringProperty(unit.getKey());
    titleProperty = new SimpleStringProperty(unit.getTitle());
    semestersProperty = new SimpleSetProperty<>(FXCollections.observableSet(unit.getSemesters()));
    abstractUnitsProperty = new SimpleSetProperty<>(FXCollections.observableSet());
    unitProperty = new SimpleObjectProperty<>(unit);
    idProperty = new SimpleIntegerProperty(unit.getId());
    groupsProperty = new SimpleSetProperty<>(FXCollections.observableSet());
    setPropertyListener();
  }

  private void setPropertyListener() {
    keyProperty.addListener((observable, oldValue, newValue) ->
          unitProperty.get().setKey(newValue));
    titleProperty.addListener((observable, oldValue, newValue) ->
          unitProperty.get().setTitle(newValue));
    semestersProperty.addListener((observable, oldValue, newValue) ->
          addOrRemoveSemester(newValue));
    abstractUnitsProperty.addListener((observable, oldValue, newValue) ->
          addOrRemoveAbstractUnit(newValue));
    groupsProperty.addListener((observable, oldValue, newValue) ->
          addOrRemoveGroup(newValue));

    idProperty.addListener((observable, oldValue, newValue) -> {
      final Unit unit = unitProperty.get();
      if (newValue != null) {
        unit.setId(newValue.intValue());
        return;
      }
      unit.setId(-1);
    });
  }

  private void addOrRemoveSemester(final ObservableSet<Integer> newValue) {
    unitProperty.get().setSemesters(newValue);
  }

  private void addOrRemoveAbstractUnit(final ObservableSet<AbstractUnitWrapper> newValue) {
    unitProperty.get().setAbstractUnits(newValue.stream().map(
          AbstractUnitWrapper::getAbstractUnit).collect(Collectors.toSet()));
  }

  private void addOrRemoveGroup(final ObservableSet<GroupWrapper> newValue) {
    unitProperty.get().setGroups(newValue.stream().map(
          GroupWrapper::getGroup).collect(Collectors.toSet()));
  }

  public ObservableSet<GroupWrapper> getGroups() {
    return groupsProperty.get();
  }

  public SetProperty<GroupWrapper> groupsProperty() {
    return groupsProperty;
  }

  public void setGroups(final ObservableSet<GroupWrapper> groups) {
    this.groupsProperty.set(groups);
  }

  public int getId() {
    return idProperty.get();
  }

  public void setId(final int id) {
    this.idProperty.set(id);
  }

  public IntegerProperty idProperty() {
    return idProperty;
  }

  public String getKey() {
    return keyProperty.get();
  }

  public void setKey(final String key) {
    this.keyProperty.set(key);
  }

  public StringProperty keyProperty() {
    return keyProperty;
  }

  public String getTitle() {
    return titleProperty.get();
  }

  public void setTitle(final String title) {
    this.titleProperty.set(title);
  }

  public StringProperty titleProperty() {
    return titleProperty;
  }

  public ObservableSet<Integer> getSemesters() {
    return semestersProperty.get();
  }

  public void setSemesters(final ObservableSet<Integer> semesters) {
    this.semestersProperty.set(semesters);
  }

  public SetProperty<Integer> semestersProperty() {
    return semestersProperty;
  }

  public ObservableSet<AbstractUnitWrapper> getAbstractUnits() {
    return abstractUnitsProperty.get();
  }

  public void setAbstractUnits(final ObservableSet<AbstractUnitWrapper> abstractUnits) {
    this.abstractUnitsProperty.set(abstractUnits);
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

  /**
   * Creates an empty UnitWrapper.
   * @return the empty UnitWrapper.
   */
  public static UnitWrapper createEmptyUnitWrapper() {
    Unit unit = new Unit();
    unit.setSemesters(new HashSet<>());
    return new UnitWrapper(unit);
  }
}
