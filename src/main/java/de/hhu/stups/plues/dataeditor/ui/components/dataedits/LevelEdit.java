package de.hhu.stups.plues.dataeditor.ui.components.dataedits;

import com.google.inject.Inject;

import de.hhu.stups.plues.data.entities.Level;
import de.hhu.stups.plues.dataeditor.ui.components.LabeledTextField;
import de.hhu.stups.plues.dataeditor.ui.entities.EntityWrapper;
import de.hhu.stups.plues.dataeditor.ui.layout.Inflater;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ResourceBundle;

public class LevelEdit extends GridPane implements Initializable {

  private final ObjectProperty<Level> levelProperty;

  private ResourceBundle resources;

  @FXML
  @SuppressWarnings("unused")
  private LabeledTextField txtLevel;
  @FXML
  @SuppressWarnings("unused")
  private LabeledTextField txtMinCp;
  @FXML
  @SuppressWarnings("unused")
  private LabeledTextField txtMaxCp;
  @FXML
  @SuppressWarnings("unused")
  private ComboBox<EntityWrapper> cbParent;
  @FXML
  @SuppressWarnings("unused")
  private Button persistChanges;

  @Inject
  public LevelEdit(final Inflater inflater) {
    levelProperty = new SimpleObjectProperty<>();
    inflater.inflate("components/dataedits/level_edit", this, this, "level_edit");
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
    txtLevel.setLabelText(resources.getString("level"));
    txtMinCp.setLabelText(resources.getString("minCp"));
    txtMaxCp.setLabelText(resources.getString("maxCp"));
  }

  public ObjectProperty<Level> levelProperty() {
    return levelProperty;
  }
}
