package de.hhu.stups.plues.dataeditor.ui.components.dataedits;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import de.hhu.stups.plues.dataeditor.ui.components.LabeledTextField;
import de.hhu.stups.plues.dataeditor.ui.entities.AbstractUnitWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.ModuleWrapper;
import de.hhu.stups.plues.dataeditor.ui.layout.Inflater;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class ModuleEdit extends GridPane implements Initializable {

  private ModuleWrapper moduleWrapper;
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
  private Button persistChanges;
  @FXML
  @SuppressWarnings("unused")
  private VBox referenceBox;
  @FXML
  @SuppressWarnings("unused")
  private ListView<AbstractUnitWrapper> listViewAbstractUnits;

  @Inject
  public ModuleEdit(final Inflater inflater,
                    @Assisted final ModuleWrapper moduleWrapper) {
    this.moduleWrapper = moduleWrapper;
    inflater.inflate("components/dataedits/module_edit", this, this, "module_edit");
  }

  @Override
  public void initialize(final URL location, final ResourceBundle resources) {
    this.resources = resources;
    initializeInputFields();
    loadData();
  }

  @FXML
  @SuppressWarnings("unused")
  public void persistChanges() {
    //
  }

  private void initializeInputFields() {
    txtModule.setLabelText(resources.getString("module"));
    txtKey.setLabelText(resources.getString("id"));
    txtPordnr.setLabelText(resources.getString("pordnr"));
  }

  private void loadData() {
    txtModule.textProperty().bind(moduleWrapper.titleProperty());
    txtKey.textProperty().bind(Bindings.createStringBinding(
        () -> String.valueOf(moduleWrapper.keyProperty().get())));
    txtPordnr.textProperty().bind(Bindings.createStringBinding(
        () -> String.valueOf(moduleWrapper.pordnrProperty().get())));
    listViewAbstractUnits.getItems().addAll(moduleWrapper.getAbstractUnits());
    cbBundled.setSelected(moduleWrapper.bundledProperty().get());
  }
}
