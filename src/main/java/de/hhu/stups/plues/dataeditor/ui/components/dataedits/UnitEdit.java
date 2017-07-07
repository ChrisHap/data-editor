package de.hhu.stups.plues.dataeditor.ui.components.dataedits;

import com.google.inject.Inject;

import de.hhu.stups.plues.dataeditor.ui.components.LabeledTextField;
import de.hhu.stups.plues.dataeditor.ui.entities.EntityWrapper;
import de.hhu.stups.plues.dataeditor.ui.layout.Inflater;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.binding.Bindings;
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

  @Inject
  public UnitEdit(final Inflater inflater) {
    inflater.inflate("components/dataedits/unit_edit", this, this, "unit_edit");
  }

  @Override
  public void initialize(final URL location, final ResourceBundle resources) {
    this.resources = resources;
    initializeInputFields();
    initializeButtons();
  }

  private void initializeButtons() {
    final FontAwesomeIconView iconEdit = new FontAwesomeIconView(FontAwesomeIcon.EDIT);
    iconEdit.setGlyphSize(12);
    final FontAwesomeIconView iconAdd = new FontAwesomeIconView(FontAwesomeIcon.PLUS);
    iconAdd.setGlyphSize(12);
    final FontAwesomeIconView iconRemove = new FontAwesomeIconView(FontAwesomeIcon.MINUS);
    iconRemove.setGlyphSize(12);
    btAddSession.graphicProperty().bind(Bindings.createObjectBinding(() -> iconAdd));
    btEditSession.graphicProperty().bind(Bindings.createObjectBinding(() -> iconEdit));
    btRemoveSession.graphicProperty().bind(Bindings.createObjectBinding(() -> iconRemove));
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
}
