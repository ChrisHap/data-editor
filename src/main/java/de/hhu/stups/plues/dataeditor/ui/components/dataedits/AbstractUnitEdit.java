package de.hhu.stups.plues.dataeditor.ui.components.dataedits;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import de.hhu.stups.plues.dataeditor.ui.components.LabeledTextField;
import de.hhu.stups.plues.dataeditor.ui.database.DataService;
import de.hhu.stups.plues.dataeditor.ui.entities.AbstractUnitWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.ModuleWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.UnitWrapper;
import de.hhu.stups.plues.dataeditor.ui.layout.Inflater;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class AbstractUnitEdit extends GridPane implements Initializable {

  private final DataService dataService;
  private final AbstractUnitWrapper abstractUnitWrapper;
  private ResourceBundle resources;

  @FXML
  @SuppressWarnings("unused")
  private LabeledTextField txtAbstractUnit;
  @FXML
  @SuppressWarnings("unused")
  private LabeledTextField txtId;
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

  /**
   * Initialize abstract unit edit.
   */
  @Inject
  public AbstractUnitEdit(final Inflater inflater,
                          final DataService dataService,
                          @Assisted final AbstractUnitWrapper abstractUnitWrapper) {
    this.dataService = dataService;
    this.abstractUnitWrapper = abstractUnitWrapper;
    inflater.inflate("components/dataedits/abstract_unit_edit", this, this, "abstract_unit_edit");
  }

  @Override
  public void initialize(final URL location, final ResourceBundle resources) {
    this.resources = resources;
    referencedEntitiesBox.getChildren().remove(listViewUnits);
    initializeInputFields();
    loadData();
  }

  private void initializeInputFields() {
    txtAbstractUnit.setLabelText(resources.getString("abstract_unit"));
    txtId.setLabelText(resources.getString("id"));
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

  @FXML
  @SuppressWarnings("unused")
  public void persistChanges() {
    // TODO
  }

  private void loadData() {
    txtAbstractUnit.textProperty().bind(abstractUnitWrapper.titleProperty());
    txtId.textProperty().bind(abstractUnitWrapper.keyProperty());
    listViewModules.getItems().addAll(abstractUnitWrapper.getAbstractUnit()
        .getModules().stream().map(module -> dataService.getModuleWrappers().get(module.getKey()))
        .collect(Collectors.toSet()));
    listViewUnits.getItems().addAll(abstractUnitWrapper.getAbstractUnit()
        .getUnits().stream().map(unit -> dataService.getUnitWrappers().get(unit.getKey()))
        .collect(Collectors.toSet()));
  }
}
