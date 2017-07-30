package de.hhu.stups.plues.dataeditor.ui.components.dataedits;

import de.hhu.stups.plues.dataeditor.ui.entities.AbstractUnitWrapper;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class AbstractUnitTableCell {

  private final ObjectProperty<AbstractUnitWrapper> abstractUnitWrapperProperty;
  private final StringProperty titleProperty;
  private final BooleanProperty considerAbstractUnitProperty;

  /**
   * Initialize the properties.
   */
  AbstractUnitTableCell(final AbstractUnitWrapper abstractUnitWrapper,
                        final boolean considerAbstractUnit) {
    abstractUnitWrapperProperty = new SimpleObjectProperty<>(abstractUnitWrapper);
    titleProperty = new SimpleStringProperty();
    titleProperty.bind(abstractUnitWrapper.titleProperty());
    considerAbstractUnitProperty = new SimpleBooleanProperty(considerAbstractUnit);
  }

  public ObjectProperty<AbstractUnitWrapper> abstractUnitWrapperProperty() {
    return abstractUnitWrapperProperty;
  }

  StringProperty titleProperty() {
    return titleProperty;
  }

  BooleanProperty considerAbstractUnitProperty() {
    return considerAbstractUnitProperty;
  }

  public String getTitle() {
    return titleProperty.get();
  }

  public void setTitle(final String title) {
    titleProperty.set(title);
  }

  public boolean getConsiderAbstractUnit() {
    return considerAbstractUnitProperty.get();
  }

  public void setConsiderAbstractUnit(final boolean considerAbstractUnit) {
    considerAbstractUnitProperty.set(considerAbstractUnit);
  }

}
