package de.hhu.stups.plues.dataeditor.ui.components.dataedits;

import de.hhu.stups.plues.dataeditor.ui.components.LabeledTextField;
import de.hhu.stups.plues.dataeditor.ui.database.DataService;
import de.hhu.stups.plues.dataeditor.ui.database.events.DataChangeEvent;
import de.hhu.stups.plues.dataeditor.ui.database.events.DataChangeType;
import de.hhu.stups.plues.dataeditor.ui.entities.AbstractUnitWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.EntityType;
import de.hhu.stups.plues.dataeditor.ui.entities.EntityWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.LevelWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.ModuleLevel;
import de.hhu.stups.plues.dataeditor.ui.entities.ModuleWrapper;
import de.hhu.stups.plues.dataeditor.ui.layout.Inflater;
import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.fxmisc.easybind.EasyBind;

import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

public class ModuleEdit extends GridPane implements Initializable {

  private final ModuleWrapper moduleWrapper;
  private final BooleanProperty dataChangedProperty;
  private final DataService dataService;
  private final EntityListViewContextMenu entityListViewContextMenu;

  private ResourceBundle resources;

  @FXML
  @SuppressWarnings("unused")
  private LabeledTextField txtModule;
  @FXML
  @SuppressWarnings("unused")
  private LabeledTextField txtKey;
  @FXML
  @SuppressWarnings("unused")
  private LabeledTextField txtPordnr;
  @FXML
  @SuppressWarnings("unused")
  private CheckBox cbBundled;
  @FXML
  @SuppressWarnings("unused")
  private Button btPersistChanges;
  @FXML
  @SuppressWarnings("unused")
  private VBox referenceBox;
  @FXML
  @SuppressWarnings("unused")
  private VBox cbBox;
  @FXML
  @SuppressWarnings("unused")
  private ListView<AbstractUnitWrapper> listViewAbstractUnits;

  @FXML
  @SuppressWarnings("unused")
  private ComboBox<LevelWrapper> cbParentLevel;

  /**
   * Initialize module edit.
   */
  ModuleEdit(final Inflater inflater,
                    final DataService dataService,
                    final ModuleWrapper moduleWrapper) {
    this.dataService = dataService;
    this.moduleWrapper = moduleWrapper;
    dataChangedProperty = new SimpleBooleanProperty(false);
    this.entityListViewContextMenu = new EntityListViewContextMenu();
    inflater.inflate("components/dataedits/module_edit", this, this, "module_edit");
  }

  @Override
  public void initialize(final URL location, final ResourceBundle resources) {
    this.resources = resources;
    btPersistChanges.disableProperty().bind(dataChangedProperty.not());
    initializeInputFields();
    setDataListener();
    loadModuleData();

    setListViewContextMenu();
    setListViewDragListeners();
  }

  private void setListViewContextMenu() {
    this.entityListViewContextMenu.setParent(listViewAbstractUnits);
    listViewAbstractUnits.getItems().addListener((InvalidationListener) observable ->
          dataChangedProperty.set(true));
    listViewAbstractUnits.setOnMouseClicked(event -> {
      entityListViewContextMenu.hide();
      final AbstractUnitWrapper selectedItem =
            listViewAbstractUnits.getSelectionModel().getSelectedItem();
      if (selectedItem != null && MouseButton.SECONDARY.equals(event.getButton())) {
        entityListViewContextMenu.show(listViewAbstractUnits,
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
            && (!listViewAbstractUnits.getItems().contains(draggedWrapper))) {
        listViewAbstractUnits.getItems().add(((AbstractUnitWrapper) draggedWrapper));
        dataChangedProperty.set(true);
      }
      event.consume();
    });
  }

  private void updateDataChanged() {
    EasyBind.subscribe(txtModule.textProperty(), s -> dataChangedProperty.set(true));
    EasyBind.subscribe(txtKey.textProperty(), s -> dataChangedProperty.set(true));
    EasyBind.subscribe(txtPordnr.textProperty(), s -> dataChangedProperty.set(true));
    EasyBind.subscribe(listViewAbstractUnits.itemsProperty(), s -> dataChangedProperty.set(true));
    EasyBind.subscribe(cbBundled.selectedProperty(), s -> dataChangedProperty.set(true));
    EasyBind.subscribe(cbParentLevel.getSelectionModel().selectedItemProperty(), entityWrapper ->
          dataChangedProperty.set(true));
  }

  /**
   * Update data if the wrapper has changed.
   */
  private void setDataListener() {
    EasyBind.subscribe(moduleWrapper.moduleProperty(), module -> setModule());
    EasyBind.subscribe(moduleWrapper.keyProperty(), module -> setKey());
    EasyBind.subscribe(moduleWrapper.pordnrProperty(), module -> setPordnr());
    EasyBind.subscribe(moduleWrapper.abstractUnitsProperty(), module -> setAbstractUnits());
    EasyBind.subscribe(moduleWrapper.bundledProperty(), module -> setBundled());
    updateDataChanged();
  }

  private void initializeInputFields() {
    txtModule.setLabelText(resources.getString("module"));
    txtKey.setLabelText(resources.getString("id"));
    txtPordnr.setLabelText(resources.getString("pordnr"));
  }

  private void loadModuleData() {
    setModule();
    setKey();
    setPordnr();
    setAbstractUnits();
    setBundled();
    setParentLevels();
    dataChangedProperty.set(false);
  }

  private void setParentLevels() {
    cbParentLevel.getItems().clear();
    cbParentLevel.getItems().addAll(dataService.getLevelWrappers().values());
    cbParentLevel.getSelectionModel().select(moduleWrapper.getLevel());
  }

  private void setBundled() {
    cbBundled.setSelected(moduleWrapper.bundledProperty().get());
  }

  private void setAbstractUnits() {
    listViewAbstractUnits.getItems().addAll(moduleWrapper.getAbstractUnits());
  }

  private void setPordnr() {
    txtPordnr.setText(String.valueOf(moduleWrapper.pordnrProperty().get()));
  }

  private void setKey() {
    txtKey.setText(String.valueOf(moduleWrapper.keyProperty().get()));
  }

  private void setModule() {
    txtModule.setText(moduleWrapper.getTitle());
  }

  /**
   * Push the {@link #moduleWrapper} to the {@link #dataService} and set
   * {@link #dataChangedProperty} to false.
   */
  @FXML
  @SuppressWarnings("unused")
  public void persistChanges() {
    int pordNr;
    try {
      pordNr = Integer.parseInt(txtPordnr.textProperty().get());
    } catch (NumberFormatException exception) {
      new Alert(Alert.AlertType.ERROR,
            resources.getString("pordnrError"), ButtonType.OK).showAndWait();
      return;
    }
    if (cbParentLevel.getValue() == null) {
      new Alert(Alert.AlertType.ERROR,
            resources.getString("parentLevelError"), ButtonType.OK).showAndWait();
      return;
    }
    LevelWrapper parentLevel = cbParentLevel.getValue();
    LevelWrapper topParent = parentLevel;
    while (topParent.getParent() != null) {
      topParent = topParent.getParent();
    }
    if (topParent.getCourseWrapper() == null) {
      new Alert(Alert.AlertType.ERROR,
            resources.getString("parentLevelCourseError"), ButtonType.OK).showAndWait();
      return;
    }
    moduleWrapper.setTitleProperty(txtModule.textProperty().get());
    moduleWrapper.setPordnrProperty(pordNr);

    moduleWrapper.setLevel(parentLevel);
    parentLevel.getLevel().getModules().add(moduleWrapper.getModule());
    moduleWrapper.setKeyProperty(txtKey.textProperty().get().toUpperCase());
    moduleWrapper.getModule().setKey(txtKey.textProperty().get().toUpperCase());

    ModuleLevel moduleLevel = new ModuleLevel();
    moduleLevel.setLevel(parentLevel.getLevel());
    moduleLevel.setModule(moduleWrapper.getModule());
    moduleLevel.setCourse(topParent.getCourseWrapper().getCourse());
    Set<ModuleLevel> moduleLevels = new HashSet<>();
    moduleLevels.add(moduleLevel);
    moduleWrapper.getModule().setModuleLevels(moduleLevels);

    moduleWrapper.getAbstractUnits().clear();
    listViewAbstractUnits.getItems().forEach(abstractUnitWrapper -> {
      Set<ModuleWrapper> modules = abstractUnitWrapper.getModules();
      modules.clear();
      modules.add(moduleWrapper);
      moduleWrapper.getAbstractUnits().add(abstractUnitWrapper);
    });

    dataService.dataChangeEventSource().push(
          new DataChangeEvent(DataChangeType.STORE_ENTITY, moduleWrapper));

    dataChangedProperty.set(false);
  }
}
