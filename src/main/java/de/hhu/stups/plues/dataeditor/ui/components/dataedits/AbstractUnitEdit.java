package de.hhu.stups.plues.dataeditor.ui.components.dataedits;

import com.google.inject.Inject;

import de.hhu.stups.plues.dataeditor.ui.components.LabeledTextField;
import de.hhu.stups.plues.dataeditor.ui.entities.AbstractUnitWrapper;
import de.hhu.stups.plues.dataeditor.ui.layout.Inflater;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ResourceBundle;

public class AbstractUnitEdit extends GridPane implements Initializable {

  private AbstractUnitWrapper abstractUnitWrapper;
  private ResourceBundle resources;

  @FXML
  @SuppressWarnings("unused")
  private LabeledTextField txtAbstractUnit;
  @FXML
  @SuppressWarnings("unused")
  private LabeledTextField txtId;
  @FXML
  @SuppressWarnings("unused")
  private LabeledTextField txtType;
  @FXML
  @SuppressWarnings("unused")
  private LabeledTextField txtSemester;
  @FXML
  @SuppressWarnings("unused")
  private Button persistChanges;

  @Inject
  public AbstractUnitEdit(final Inflater inflater) {
    inflater.inflate("components/dataedits/abstract_unit_edit", this, this, "abstract_unit_edit");
  }

  @Override
  public void initialize(final URL location, final ResourceBundle resources) {
    this.resources = resources;
    initializeInputFields();
  }

  private void initializeInputFields() {
    txtAbstractUnit.setLabelText(resources.getString("abstract_unit"));
    txtId.setLabelText(resources.getString("id"));
    txtType.setLabelText(resources.getString("type"));
    txtSemester.setLabelText(resources.getString("semester"));
  }

  @FXML
  public void persistChanges() {

  }

  public void setAbstractUnitWrapper(final AbstractUnitWrapper abstractUnitWrapper) {
    this.abstractUnitWrapper = abstractUnitWrapper;
    loadData();
  }

  private void loadData() {
    // TODO
  }
}
