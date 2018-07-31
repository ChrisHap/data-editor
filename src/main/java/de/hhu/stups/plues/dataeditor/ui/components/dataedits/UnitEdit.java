package de.hhu.stups.plues.dataeditor.ui.components.dataedits;

import de.hhu.stups.plues.dataeditor.ui.components.LabeledTextField;
import de.hhu.stups.plues.dataeditor.ui.database.DataService;
import de.hhu.stups.plues.dataeditor.ui.database.events.DataChangeEvent;
import de.hhu.stups.plues.dataeditor.ui.database.events.DataChangeType;
import de.hhu.stups.plues.dataeditor.ui.entities.AbstractUnitWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.EntityType;
import de.hhu.stups.plues.dataeditor.ui.entities.EntityWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.GroupWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.UnitWrapper;
import de.hhu.stups.plues.dataeditor.ui.layout.Inflater;
import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import org.fxmisc.easybind.EasyBind;

import java.net.URL;
import java.util.ResourceBundle;

public class UnitEdit extends GridPane implements Initializable {

  private final UnitWrapper unitWrapper;
  private final BooleanProperty dataChangedProperty;
  private final DataService dataService;
  private final EntityListViewContextMenu abstractUnitListViewContextMenu;
  private final EntityListViewContextMenu groupListViewContextMenu;

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

  /**
   * Initialize unit edit.
   */
  UnitEdit(final Inflater inflater,
                  final DataService dataService,
                  final UnitWrapper unitWrapper) {
    this.dataService = dataService;
    this.unitWrapper = unitWrapper;
    dataChangedProperty = new SimpleBooleanProperty(false);
    this.abstractUnitListViewContextMenu = new EntityListViewContextMenu();
    this.groupListViewContextMenu = new EntityListViewContextMenu();
    inflater.inflate("components/dataedits/unit_edit", this, this, "unit_edit");
  }

  @Override
  public void initialize(final URL location, final ResourceBundle resources) {
    this.resources = resources;
    btPersistChanges.disableProperty().bind(dataChangedProperty.not());
    initializeInputFields();
    setDataListener();
    loadUnitData();

    setListViewContextMenus();
    setListViewDragListeners();
  }

  private void setListViewContextMenus() {
    this.abstractUnitListViewContextMenu.setParent(listViewAbstractUnits);
    this.groupListViewContextMenu.setParent(listViewGroups);

    listViewAbstractUnits.getItems().addListener((InvalidationListener) observable ->
          dataChangedProperty.set(true));
    listViewAbstractUnits.setOnMouseClicked(event -> {
      abstractUnitListViewContextMenu.hide();
      final AbstractUnitWrapper selectedItem =
            listViewAbstractUnits.getSelectionModel().getSelectedItem();
      if (selectedItem != null && MouseButton.SECONDARY.equals(event.getButton())) {
        abstractUnitListViewContextMenu.show(listViewAbstractUnits,
              event.getScreenX(), event.getScreenY());
      }
    });

    listViewGroups.getItems().addListener((InvalidationListener) observable ->
          dataChangedProperty.set(true));
    listViewGroups.setOnMouseClicked(event -> {
      groupListViewContextMenu.hide();
      final GroupWrapper selectedItem =
            listViewGroups.getSelectionModel().getSelectedItem();
      if (selectedItem != null && MouseButton.SECONDARY.equals(event.getButton())) {
        groupListViewContextMenu.show(listViewGroups,
              event.getScreenX(), event.getScreenY());
      }
    });
  }

  private void setListViewDragListeners() {
    dataService.draggedEntityProperty().addListener((observable, oldValue, newValue) ->
          listViewAbstractUnits.requestFocus());
    listViewAbstractUnits.setOnDragOver(event -> {
      event.acceptTransferModes(TransferMode.COPY);
      event.consume();
    });
    listViewAbstractUnits.setOnDragDropped(event -> {
      event.setDropCompleted(true);
      final EntityWrapper draggedWrapper = dataService.draggedEntityProperty().get();
      //noinspection SuspiciousMethodCalls
      if (EntityType.ABSTRACT_UNIT.equals(draggedWrapper.getEntityType())
            && !listViewAbstractUnits.getItems().contains(draggedWrapper)) {
        listViewAbstractUnits.getItems().add(((AbstractUnitWrapper) draggedWrapper));
        dataChangedProperty.set(true);
      }
      event.consume();
    });

    dataService.draggedEntityProperty().addListener((observable, oldValue, newValue) ->
          listViewGroups.requestFocus());
    listViewGroups.setOnDragOver(event -> {
      event.acceptTransferModes(TransferMode.COPY);
      event.consume();
    });
    listViewGroups.setOnDragDropped(event -> {
      event.setDropCompleted(true);
      final EntityWrapper draggedWrapper = dataService.draggedEntityProperty().get();
      //noinspection SuspiciousMethodCalls
      if (EntityType.GROUP.equals(draggedWrapper.getEntityType())
            && (!listViewGroups.getItems().contains(draggedWrapper))) {
        listViewGroups.getItems().add(((GroupWrapper) draggedWrapper));
        dataChangedProperty.set(true);
      }
      event.consume();
    });
  }

  private void updateDataChanged() {
    EasyBind.subscribe(txtId.textProperty(), s -> dataChangedProperty.set(true));
    EasyBind.subscribe(txtSemester.textProperty(), s -> dataChangedProperty.set(true));
    EasyBind.subscribe(txtUnit.textProperty(), s -> dataChangedProperty.set(true));
    EasyBind.subscribe(listViewAbstractUnits.itemsProperty(), s -> dataChangedProperty.set(true));
    EasyBind.subscribe(listViewGroups.itemsProperty(), s -> dataChangedProperty.set(true));
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

  private void loadGroups() {
    listViewGroups.getItems().clear();
    listViewGroups.getItems().addAll(unitWrapper.getGroups());
  }

  private void loadAbstractUnits() {
    listViewAbstractUnits.getItems().clear();
    listViewAbstractUnits.getItems().addAll(unitWrapper.getAbstractUnits());
  }


  private void initializeInputFields() {
    txtUnit.setLabelText(resources.getString("unit"));
    txtId.setLabelText(resources.getString("id"));
    txtSemester.setLabelText(resources.getString("semester"));
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

  /**
   * Push the {@link #unitWrapper} to the {@link #dataService} and set
   * {@link #dataChangedProperty} to false.
   */
  @FXML
  @SuppressWarnings("unused")
  public void persistChanges() {
    unitWrapper.setTitle(txtUnit.textProperty().get());
    listViewAbstractUnits.getItems().forEach(parent -> {
      unitWrapper.getAbstractUnits().add(parent);
      parent.getUnits().add(unitWrapper);
    });

    dataService.dataChangeEventSource().push(
          new DataChangeEvent(DataChangeType.STORE_ENTITY, unitWrapper));

    dataChangedProperty.set(false);
  }
}
