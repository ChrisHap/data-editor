package de.hhu.stups.plues.dataeditor.ui.components.dataedits;

import com.google.inject.Inject;

import de.hhu.stups.plues.dataeditor.ui.components.LabeledTextField;
import de.hhu.stups.plues.dataeditor.ui.entities.EntityWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.UnitWrapper;
import de.hhu.stups.plues.dataeditor.ui.layout.Inflater;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ResourceBundle;

public class UnitEdit extends GridPane implements Initializable {

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
  private ComboBox<EntityWrapper> cbAbstractUnit;
  @FXML
  @SuppressWarnings("unused")
  private Button persistChanges;
  @FXML
  @SuppressWarnings("unused")
  private ListView<EntityWrapper> listViewSessions;
  @FXML
  @SuppressWarnings("unused")
  private Button btAddSession;
  @FXML
  @SuppressWarnings("unused")
  private Button btEditSession;
  @FXML
  @SuppressWarnings("unused")
  private Button btRemoveSession;
  private UnitWrapper unitWrapper;

  @Inject
  public UnitEdit(final Inflater inflater) {
    inflater.inflate("components/dataedits/unit_edit", this, this, "unit_edit");
  }

  @Override
  public void initialize(final URL location, final ResourceBundle resources) {
    this.resources = resources;
    initializeInputFields();
  }

  @FXML
  public void persistChanges() {

  }

  @FXML
  public void addSession() {

  }

  @FXML
  public void editSession() {

  }

  @FXML
  public void removeSession() {

  }


  private void initializeInputFields() {
    txtUnit.setLabelText(resources.getString("unit"));
    txtId.setLabelText(resources.getString("id"));
    txtSemester.setLabelText(resources.getString("semester"));
  }

  public void setUnitWrapper(final UnitWrapper unitWrapper) {
    this.unitWrapper = unitWrapper;
    loadData();
  }

  private void loadData() {
    // TODO
  }
}
