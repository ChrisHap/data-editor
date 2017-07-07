package de.hhu.stups.plues.dataeditor.ui.components.dataedits;

import com.google.inject.Inject;

import de.hhu.stups.plues.data.entities.Module;
import de.hhu.stups.plues.dataeditor.ui.components.LabeledTextField;
import de.hhu.stups.plues.dataeditor.ui.entities.EntityWrapper;
import de.hhu.stups.plues.dataeditor.ui.layout.Inflater;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ResourceBundle;

public class ModuleEdit extends GridPane implements Initializable {

  private final ObjectProperty<Module> moduleProperty;

  private ResourceBundle resources;

  @FXML
  @SuppressWarnings("unused")
  private LabeledTextField txtModule;
  @FXML
  @SuppressWarnings("unused")
  private LabeledTextField txtId;
  @FXML
  @SuppressWarnings("unused")
  private LabeledTextField txtPordnr;
  @FXML
  @SuppressWarnings("unused")
  private LabeledTextField txtCp;
  @FXML
  @SuppressWarnings("unused")
  private ComboBox<EntityWrapper> cbParent;
  @FXML
  @SuppressWarnings("unused")
  private CheckBox cbMandatory;
  @FXML
  @SuppressWarnings("unused")
  private CheckBox cbBundled;
  @FXML
  @SuppressWarnings("unused")
  private Button persistChanges;

  @Inject
  public ModuleEdit(final Inflater inflater) {
    moduleProperty = new SimpleObjectProperty<>();
    inflater.inflate("components/dataedits/module_edit", this, this, "module_edit");
  }

  @Override
  public void initialize(final URL location, final ResourceBundle resources) {
    this.resources = resources;
    initializeInputFields();
  }

  @FXML
  public void persistChanges() {

  }

  private void initializeInputFields() {
    txtModule.setLabelText(resources.getString("module"));
    txtId.setLabelText(resources.getString("id"));
    txtPordnr.setLabelText(resources.getString("pordnr"));
    txtCp.setLabelText(resources.getString("credits"));
  }

  public ObjectProperty<Module> moduleProperty() {
    return moduleProperty;
  }
}
