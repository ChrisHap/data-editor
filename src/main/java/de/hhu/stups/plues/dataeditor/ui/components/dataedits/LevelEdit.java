package de.hhu.stups.plues.dataeditor.ui.components.dataedits;

import com.google.inject.Inject;

import de.hhu.stups.plues.dataeditor.ui.components.LabeledTextField;
import de.hhu.stups.plues.dataeditor.ui.database.DataService;
import de.hhu.stups.plues.dataeditor.ui.entities.EntityWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.LevelWrapper;
import de.hhu.stups.plues.dataeditor.ui.layout.Inflater;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class LevelEdit extends GridPane implements Initializable {

  private final DataService dataService;

  private LevelWrapper levelWrapper;
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
  private RadioButton rbParentCourse;
  @FXML
  @SuppressWarnings("unused")
  private RadioButton rbParentLevel;
  @FXML
  @SuppressWarnings("unused")
  private VBox cbBox;
  @FXML
  @SuppressWarnings("unused")
  private ComboBox<EntityWrapper> cbParentLevel;
  @FXML
  @SuppressWarnings("unused")
  private ComboBox<EntityWrapper> cbParentCourse;
  @FXML
  @SuppressWarnings("unused")
  private Button persistChanges;

  @Inject
  public LevelEdit(final Inflater inflater,
                   final DataService dataService) {
    this.dataService = dataService;
    inflater.inflate("components/dataedits/level_edit", this, this, "level_edit");
  }

  @Override
  public void initialize(final URL location, final ResourceBundle resources) {
    this.resources = resources;
    initializeInputFields();
    cbBox.getChildren().remove(cbParentCourse);
    rbParentLevel.selectedProperty().addListener((observable, oldValue, newValue) ->
        showParentLevels(newValue));
    rbParentCourse.selectedProperty().addListener((observable, oldValue, newValue) ->
        showParentCourses(newValue));
  }

  private void showParentCourses(final boolean show) {
    if (!show || cbBox.getChildren().contains(cbParentCourse)) {
      return;
    }
    cbBox.getChildren().remove(cbParentLevel);
    cbBox.getChildren().add(cbParentCourse);
  }

  private void showParentLevels(final boolean show) {
    if (!show || cbBox.getChildren().contains(cbParentLevel)) {
      return;
    }
    cbBox.getChildren().remove(cbParentCourse);
    cbBox.getChildren().add(cbParentLevel);
  }

  private void initializeComboBoxes() {
    cbParentLevel.getItems().clear();
    cbParentLevel.getItems().addAll(dataService.getLevelWrappers().values());
    cbParentLevel.getSelectionModel().select(levelWrapper.getParent());
    cbParentCourse.getItems().clear();
    cbParentCourse.getItems().addAll(dataService.getCourseWrappers().values());
    cbParentCourse.getSelectionModel().select(levelWrapper.getCourseWrapper());
  }

  @FXML
  public void persistChanges() {

  }

  private void initializeInputFields() {
    txtLevel.setLabelText(resources.getString("level"));
    txtMinCp.setLabelText(resources.getString("minCp"));
    txtMaxCp.setLabelText(resources.getString("maxCp"));
  }

  public void setLevelWrapper(final LevelWrapper levelWrapper) {
    this.levelWrapper = levelWrapper;
    loadLevelData();
  }

  private void loadLevelData() {
    txtLevel.textProperty().bind(levelWrapper.nameProperty());
    txtMaxCp.textProperty().bind(Bindings.createStringBinding(
        () -> String.valueOf(levelWrapper.maxCreditsProperty().get())));
    txtMinCp.textProperty().bind(Bindings.createStringBinding(
        () -> String.valueOf(levelWrapper.minCreditsProperty().get())));
    initializeComboBoxes();
    if (levelWrapper.getParent() != null) {
      rbParentLevel.setSelected(true);
    } else {
      rbParentCourse.setSelected(true);
    }
  }
}
