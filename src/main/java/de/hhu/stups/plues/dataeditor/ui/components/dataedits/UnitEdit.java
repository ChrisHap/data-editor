package de.hhu.stups.plues.dataeditor.ui.components.dataedits;

import de.hhu.stups.plues.dataeditor.ui.components.LabeledTextField;
import de.hhu.stups.plues.dataeditor.ui.database.DataService;
import de.hhu.stups.plues.dataeditor.ui.database.events.DataChangeEvent;
import de.hhu.stups.plues.dataeditor.ui.database.events.DataChangeType;
import de.hhu.stups.plues.dataeditor.ui.entities.AbstractUnitWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.EntityWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.GroupWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.UnitWrapper;
import de.hhu.stups.plues.dataeditor.ui.layout.Inflater;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import org.fxmisc.easybind.EasyBind;

import java.net.URL;
import java.util.ResourceBundle;

public class UnitEdit extends GridPane implements Initializable {

  private final UnitWrapper unitWrapper;
  private final BooleanProperty dataChangedProperty;
  private final DataService dataService;

  private ResourceBundle resources;

  @FXML
  @SuppressWarnings("unused")
  private LabeledTextField txtUnit;
  @FXML
  @SuppressWarnings("unused")
  private LabeledTextField txtId;
  @FXML
  @SuppressWarnings("unused")
  private LabeledTextField txtSemester;
  @FXML
  @SuppressWarnings("unused")
  private ListView<AbstractUnitWrapper> listViewAbstractUnits;
  @FXML
  @SuppressWarnings("unused")
  private ListView<GroupWrapper> listViewGroups;
  @FXML
  @SuppressWarnings("unused")
  private Button btPersistChanges;

  private EntityWrapper parent;

  /**
   * Initialize unit edit.
   */
  UnitEdit(final Inflater inflater,
                  final DataService dataService,
                  final UnitWrapper unitWrapper) {
    this.dataService = dataService;
    this.unitWrapper = unitWrapper;
    dataChangedProperty = new SimpleBooleanProperty(false);
    inflater.inflate("components/dataedits/unit_edit", this, this, "unit_edit");
  }

  @Override
  public void initialize(final URL location, final ResourceBundle resources) {
    this.resources = resources;
    btPersistChanges.disableProperty().bind(dataChangedProperty.not());
    initializeInputFields();
    setDataListener();
    loadUnitData();
  }

  private void updateDataChanged() {
    EasyBind.subscribe(txtId.textProperty(), s -> dataChangedProperty.set(true));
    EasyBind.subscribe(txtSemester.textProperty(), s -> dataChangedProperty.set(true));
    EasyBind.subscribe(txtUnit.textProperty(), s -> dataChangedProperty.set(true));
    EasyBind.subscribe(listViewAbstractUnits.itemsProperty(), s -> dataChangedProperty.set(true));
    EasyBind.subscribe(listViewGroups.itemsProperty(), s -> dataChangedProperty.set(true));
  }

  private void loadGroups() {
    listViewGroups.getItems().clear();
    listViewGroups.getItems().addAll(unitWrapper.getGroups());
  }

  private void loadAbstractUnits() {
    listViewAbstractUnits.getItems().clear();
    listViewAbstractUnits.getItems().addAll(unitWrapper.getAbstractUnits());
  }

  /**
   * Push the {@link #unitWrapper} to the {@link #dataService} and set
   * {@link #dataChangedProperty} to false.
   */
  @FXML
  @SuppressWarnings("unused")
  public void persistChanges() {
    unitWrapper.getUnit().setTitle(txtUnit.textProperty().get());
    if (parent != null) {
      unitWrapper.getUnit().getAbstractUnits().add(
            ((AbstractUnitWrapper) parent).getAbstractUnit());
      ((AbstractUnitWrapper) parent).getAbstractUnit().getUnits().add(unitWrapper.getUnit());
      dataService.dataChangeEventSource().push(
            new DataChangeEvent(DataChangeType.STORE_ENTITY, parent));
    }
    dataService.dataChangeEventSource().push(
        new DataChangeEvent(DataChangeType.STORE_ENTITY, unitWrapper));
    dataChangedProperty.set(false);
  }


  private void initializeInputFields() {
    txtUnit.setLabelText(resources.getString("unit"));
    txtId.setLabelText(resources.getString("id"));
    txtSemester.setLabelText(resources.getString("semester"));
  }

  /**
   * Update data if the wrapper has changed.
   */
  private void setDataListener() {
    EasyBind.subscribe(unitWrapper.unitProperty(), unit -> setUnit());
    EasyBind.subscribe(unitWrapper.semestersProperty(), unit -> setSemesters());
    EasyBind.subscribe(unitWrapper.idProperty(), unit -> setUnitId());
    EasyBind.subscribe(unitWrapper.groupsProperty(), unit -> loadGroups());
    EasyBind.subscribe(unitWrapper.abstractUnitsProperty(), unit -> loadAbstractUnits());
    EasyBind.subscribe(unitWrapper.groupsProperty(), abstractUnitWrapperMap -> loadGroups());
    EasyBind.subscribe(unitWrapper.abstractUnitsProperty(),
        abstractUnitWrapperMap -> loadAbstractUnits());
    updateDataChanged();
  }

  private void loadUnitData() {
    setUnit();
    setSemesters();
    setUnitId();
    loadGroups();
    loadAbstractUnits();
    dataChangedProperty.set(false);
  }

  private void setUnitId() {
    txtId.setText(unitWrapper.getKey());
  }

  private void setSemesters() {
    txtSemester.setText(unitWrapper.getSemesters().toString());
  }

  private void setUnit() {
    txtUnit.setText(unitWrapper.getTitle());
  }
}
