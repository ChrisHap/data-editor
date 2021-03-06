package de.hhu.stups.plues.dataeditor.ui.components.dataedits;

import de.hhu.stups.plues.dataeditor.ui.components.LabeledTextField;
import de.hhu.stups.plues.dataeditor.ui.database.DataService;
import de.hhu.stups.plues.dataeditor.ui.database.events.DataChangeEvent;
import de.hhu.stups.plues.dataeditor.ui.database.events.DataChangeType;
import de.hhu.stups.plues.dataeditor.ui.entities.AbstractUnitWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.EntityType;
import de.hhu.stups.plues.dataeditor.ui.entities.EntityWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.ModuleWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.UnitWrapper;
import de.hhu.stups.plues.dataeditor.ui.layout.Inflater;
import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.fxmisc.easybind.EasyBind;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class AbstractUnitEdit extends GridPane implements Initializable {

  private final DataService dataService;
  private final AbstractUnitWrapper abstractUnitWrapper;
  private final BooleanProperty dataChangedProperty;
  private final EntityListViewContextMenu moduleListViewContextMenu;
  private final EntityListViewContextMenu unitListViewContextMenu;

  private ResourceBundle resources;

  @FXML
  @SuppressWarnings("unused")
  private LabeledTextField txtAbstractUnit;
  @FXML
  @SuppressWarnings("unused")
  private LabeledTextField txtKey;
  @FXML
  @SuppressWarnings("unused")
  private Button persistChanges;
  @FXML
  @SuppressWarnings("unused")
  private ToggleButton btAbstractUnits;
  @FXML
  @SuppressWarnings("unused")
  private ToggleButton btModuleLevels;
  @FXML
  @SuppressWarnings("unused")
  private ListView<ModuleWrapper> listViewModules;
  @FXML
  @SuppressWarnings("unused")
  private ListView<UnitWrapper> listViewUnits;
  @FXML
  @SuppressWarnings("unused")
  private VBox referencedEntitiesBox;
  @FXML
  @SuppressWarnings("unused")
  private Button btPersistChanges;

  /**
   * Initialize abstract unit edit.
   */
  AbstractUnitEdit(final Inflater inflater,
                   final DataService dataService,
                   final AbstractUnitWrapper abstractUnitWrapper) {
    this.dataService = dataService;
    this.abstractUnitWrapper = abstractUnitWrapper;
    dataChangedProperty = new SimpleBooleanProperty(false);
    this.moduleListViewContextMenu = new EntityListViewContextMenu();
    this.unitListViewContextMenu = new EntityListViewContextMenu();
    inflater.inflate("components/dataedits/abstract_unit_edit", this, this, "abstract_unit_edit");
  }

  @Override
  public void initialize(final URL location, final ResourceBundle resources) {
    this.resources = resources;
    referencedEntitiesBox.getChildren().remove(listViewUnits);
    btPersistChanges.disableProperty().bind(dataChangedProperty.not());
    initializeInputFields();
    loadAbstractUnitData();
    setDataListener();

    setListViewContextMenus();
    setListViewDragListeners();
  }

  private void setListViewContextMenus() {
    this.moduleListViewContextMenu.setParent(listViewModules);
    this.unitListViewContextMenu.setParent(listViewUnits);

    listViewModules.getItems().addListener((InvalidationListener) observable ->
          dataChangedProperty.set(true));
    listViewModules.setOnMouseClicked(event -> {
      moduleListViewContextMenu.hide();
      final ModuleWrapper selectedItem =
            listViewModules.getSelectionModel().getSelectedItem();
      if (selectedItem != null && MouseButton.SECONDARY.equals(event.getButton())) {
        moduleListViewContextMenu.show(listViewModules,
              event.getScreenX(), event.getScreenY());
      }
    });

    listViewUnits.getItems().addListener((InvalidationListener) observable ->
          dataChangedProperty.set(true));
    listViewUnits.setOnMouseClicked(event -> {
      unitListViewContextMenu.hide();
      final UnitWrapper selectedItem =
            listViewUnits.getSelectionModel().getSelectedItem();
      if (selectedItem != null && MouseButton.SECONDARY.equals(event.getButton())) {
        unitListViewContextMenu.show(listViewUnits,
              event.getScreenX(), event.getScreenY());
      }
    });
  }

  private void setListViewDragListeners() {
    dataService.draggedEntityProperty().addListener((observable, oldValue, newValue) ->
          listViewModules.requestFocus());
    listViewModules.setOnDragOver(event -> {
      event.acceptTransferModes(TransferMode.COPY);
      event.consume();
    });
    listViewModules.setOnDragDropped(event -> {
      event.setDropCompleted(true);
      final EntityWrapper draggedWrapper = dataService.draggedEntityProperty().get();
      //noinspection SuspiciousMethodCalls
      if (EntityType.MODULE.equals(draggedWrapper.getEntityType())
            && !listViewModules.getItems().contains(draggedWrapper)) {
        listViewModules.getItems().add(((ModuleWrapper) draggedWrapper));
        dataChangedProperty.set(true);
      }
      event.consume();
    });

    dataService.draggedEntityProperty().addListener((observable, oldValue, newValue) ->
          listViewUnits.requestFocus());
    listViewUnits.setOnDragOver(event -> {
      event.acceptTransferModes(TransferMode.COPY);
      event.consume();
    });
    listViewUnits.setOnDragDropped(event -> {
      event.setDropCompleted(true);
      final EntityWrapper draggedWrapper = dataService.draggedEntityProperty().get();
      //noinspection SuspiciousMethodCalls
      if (EntityType.UNIT.equals(draggedWrapper.getEntityType())
            && (!listViewUnits.getItems().contains(draggedWrapper))) {
        listViewUnits.getItems().add(((UnitWrapper) draggedWrapper));
        dataChangedProperty.set(true);
      }
      event.consume();
    });
  }

  private void updateDataChanged() {
    EasyBind.subscribe(txtKey.textProperty(), s -> dataChangedProperty.set(true));
    EasyBind.subscribe(txtAbstractUnit.textProperty(), s -> dataChangedProperty.set(true));
    EasyBind.subscribe(listViewUnits.itemsProperty(), s -> dataChangedProperty.set(true));
    EasyBind.subscribe(listViewModules.itemsProperty(), s -> dataChangedProperty.set(true));
  }

  /**
   * Update data if the wrapper has changed.
   */
  private void setDataListener() {
    EasyBind.subscribe(abstractUnitWrapper.idProperty(), number -> setId());
    EasyBind.subscribe(abstractUnitWrapper.titleProperty(), s -> setTitle());
    EasyBind.subscribe(abstractUnitWrapper.unitsProperty(), unitWrappers -> setUnits());
    EasyBind.subscribe(abstractUnitWrapper.modulesProperty(), moduleWrappers -> setModules());
    updateDataChanged();
  }

  private void initializeInputFields() {
    txtAbstractUnit.setLabelText(resources.getString("abstract_unit"));
    txtKey.setLabelText(resources.getString("id"));
  }

  @FXML
  @SuppressWarnings("unused")
  public void showModules() {
    referencedEntitiesBox.getChildren().remove(listViewUnits);
    referencedEntitiesBox.getChildren().add(listViewModules);
  }

  @FXML
  @SuppressWarnings("unused")
  public void showUnits() {
    referencedEntitiesBox.getChildren().remove(listViewModules);
    referencedEntitiesBox.getChildren().add(listViewUnits);
  }

  private void loadAbstractUnitData() {
    setTitle();
    setId();
    setModules();
    setUnits();
    dataChangedProperty.set(false);
  }

  private void setTitle() {
    txtAbstractUnit.setText(abstractUnitWrapper.getTitle());
  }

  private void setId() {
    txtKey.setText(abstractUnitWrapper.getKey());
  }

  private void setModules() {
    listViewModules.getItems().addAll(abstractUnitWrapper.getAbstractUnit()
          .getModules().stream().map(module -> dataService.getModuleWrappers().get(module.getId()))
          .collect(Collectors.toSet()));
  }

  private void setUnits() {
    listViewUnits.getItems().addAll(abstractUnitWrapper.getAbstractUnit()
          .getUnits().stream().map(unit -> dataService.getUnitWrappers().get(unit.getId()))
          .collect(Collectors.toSet()));
  }

  /**
   * Push the {@link #abstractUnitWrapper} to the {@link #dataService} and set
   * {@link #dataChangedProperty} to false.
   */
  @FXML
  @SuppressWarnings("unused")
  public void persistChanges() {
    abstractUnitWrapper.setTitle(txtAbstractUnit.textProperty().get());
    abstractUnitWrapper.setKey(txtKey.textProperty().get());
    abstractUnitWrapper.getModules().clear();
    listViewModules.getItems().forEach(moduleWrapper -> {
      abstractUnitWrapper.getModules().add(moduleWrapper);
      moduleWrapper.getAbstractUnits().add(abstractUnitWrapper);
    });
    abstractUnitWrapper.getUnits().clear();
    listViewUnits.getItems().forEach(unitWrapper -> {
      abstractUnitWrapper.getUnits().add(unitWrapper);
      unitWrapper.getAbstractUnits().add(abstractUnitWrapper);
    });

    dataService.dataChangeEventSource().push(
          new DataChangeEvent(DataChangeType.STORE_ENTITY, abstractUnitWrapper));

    dataChangedProperty.set(false);
  }
}
