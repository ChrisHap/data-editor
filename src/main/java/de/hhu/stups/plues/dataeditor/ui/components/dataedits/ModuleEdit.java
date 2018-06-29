package de.hhu.stups.plues.dataeditor.ui.components.dataedits;

import de.hhu.stups.plues.dataeditor.ui.components.LabeledTextField;
import de.hhu.stups.plues.dataeditor.ui.database.DataService;
import de.hhu.stups.plues.dataeditor.ui.database.events.DataChangeEvent;
import de.hhu.stups.plues.dataeditor.ui.database.events.DataChangeType;
import de.hhu.stups.plues.dataeditor.ui.entities.*;
import de.hhu.stups.plues.dataeditor.ui.layout.Inflater;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.fxmisc.easybind.EasyBind;

import java.net.URL;
import java.util.ResourceBundle;

public class ModuleEdit extends GridPane implements Initializable {

  private final ModuleWrapper moduleWrapper;
  private final BooleanProperty dataChangedProperty;
  private final DataService dataService;
  private EntityWrapper parent;

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
  private ListView<AbstractUnitWrapper> listViewAbstractUnits;

  /**
   * Initialize module edit.
   */
  ModuleEdit(final Inflater inflater,
                    final DataService dataService,
                    final ModuleWrapper moduleWrapper) {
    this.dataService = dataService;
    this.moduleWrapper = moduleWrapper;
    dataChangedProperty = new SimpleBooleanProperty(false);
    inflater.inflate("components/dataedits/module_edit", this, this, "module_edit");
  }

  @Override
  public void initialize(final URL location, final ResourceBundle resources) {
    this.resources = resources;
    btPersistChanges.disableProperty().bind(dataChangedProperty.not());
    initializeInputFields();
    setDataListener();
    loadModuleData();
  }

  /**
   * Push the {@link #moduleWrapper} to the {@link #dataService} and set
   * {@link #dataChangedProperty} to false.
   */
  @FXML
  @SuppressWarnings("unused")
  public void persistChanges() {
    moduleWrapper.getModule().setTitle(txtModule.textProperty().get());
    try {
      moduleWrapper.getModule().setPordnr(Integer.parseInt(txtPordnr.textProperty().get()));
    } catch (NumberFormatException exception) {
      new Alert(Alert.AlertType.ERROR,
            "PordNr muss Zahl Sein", ButtonType.OK).showAndWait();
      return;
    }
        if (parent != null) {
          ModuleLevel parentModuleLevel = new ModuleLevel();
          parentModuleLevel.setModule(moduleWrapper.getModule());
          parentModuleLevel.setLevel(((LevelWrapper) parent).getLevel());
          moduleWrapper.getModule().getModuleLevels().add(parentModuleLevel);
          ((LevelWrapper) parent).getLevel().getModules().add(moduleWrapper.getModule());
          dataService.dataChangeEventSource().push(
                new DataChangeEvent(DataChangeType.STORE_ENTITY, moduleWrapper));
          dataService.dataChangeEventSource().push(
                new DataChangeEvent(DataChangeType.STORE_ENTITY, parent));
    }
    dataService.dataChangeEventSource().push(
          new DataChangeEvent(DataChangeType.STORE_ENTITY, moduleWrapper));

    dataChangedProperty.set(false);
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

  private void updateDataChanged() {
    EasyBind.subscribe(txtModule.textProperty(), s -> dataChangedProperty.set(true));
    EasyBind.subscribe(txtKey.textProperty(), s -> dataChangedProperty.set(true));
    EasyBind.subscribe(txtPordnr.textProperty(), s -> dataChangedProperty.set(true));
    EasyBind.subscribe(listViewAbstractUnits.itemsProperty(), s -> dataChangedProperty.set(true));
    EasyBind.subscribe(cbBundled.selectedProperty(), s -> dataChangedProperty.set(true));
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
    dataChangedProperty.set(false);
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

  public void setParentEntityWrapper(EntityWrapper parent) {
    this.parent = parent;
  }
}
