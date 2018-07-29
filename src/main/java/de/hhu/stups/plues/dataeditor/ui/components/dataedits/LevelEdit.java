package de.hhu.stups.plues.dataeditor.ui.components.dataedits;

import de.hhu.stups.plues.dataeditor.ui.components.LabeledTextField;
import de.hhu.stups.plues.dataeditor.ui.database.DataService;
import de.hhu.stups.plues.dataeditor.ui.database.events.DataChangeEvent;
import de.hhu.stups.plues.dataeditor.ui.database.events.DataChangeType;
import de.hhu.stups.plues.dataeditor.ui.entities.CourseWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.EntityWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.LevelWrapper;
import de.hhu.stups.plues.dataeditor.ui.layout.Inflater;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.fxmisc.easybind.EasyBind;

import java.net.URL;
import java.util.ResourceBundle;

public class LevelEdit extends GridPane implements Initializable {

  private final DataService dataService;
  private final BooleanProperty dataChangedProperty;
  private final LevelWrapper levelWrapper;

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
  private Button btPersistChanges;

  /**
   * Initialize level edit.
   */
  LevelEdit(final Inflater inflater,
                   final DataService dataService,
                   final LevelWrapper levelWrapper) {
    this.dataService = dataService;
    this.levelWrapper = levelWrapper;
    dataChangedProperty = new SimpleBooleanProperty(false);
    inflater.inflate("components/dataedits/level_edit", this, this, "level_edit");
  }

  @Override
  public void initialize(final URL location, final ResourceBundle resources) {
    this.resources = resources;
    btPersistChanges.disableProperty().bind(dataChangedProperty.not());
    initializeInputFields();
    cbBox.getChildren().remove(cbParentCourse);
    setDataListener();
    loadLevelData();
  }

  private void updateDataChanged() {
    EasyBind.subscribe(txtLevel.textProperty(), s -> dataChangedProperty.set(true));
    EasyBind.subscribe(txtMaxCp.textProperty(), s -> dataChangedProperty.set(true));
    EasyBind.subscribe(txtMinCp.textProperty(), s -> dataChangedProperty.set(true));
    EasyBind.subscribe(rbParentLevel.selectedProperty(), aBoolean -> dataChangedProperty.set(true));
    EasyBind.subscribe(rbParentCourse.selectedProperty(), aBoolean ->
          dataChangedProperty.set(true));
    EasyBind.subscribe(cbParentLevel.getSelectionModel().selectedItemProperty(), entityWrapper ->
          dataChangedProperty.set(true));
    EasyBind.subscribe(cbParentCourse.getSelectionModel().selectedItemProperty(), entityWrapper ->
          dataChangedProperty.set(true));
  }

  /**
   * Update data if the wrapper has changed.
   */
  private void setDataListener() {
    EasyBind.subscribe(levelWrapper.levelProperty(), level -> setLevel());
    EasyBind.subscribe(levelWrapper.maxCreditsProperty(), number -> setMaxCp());
    EasyBind.subscribe(levelWrapper.minCreditsProperty(), number -> setMinCp());
    EasyBind.subscribe(levelWrapper.parentProperty(), levelWrapper1 -> selectParent());
    EasyBind.subscribe(levelWrapper.courseProperty(), courseWrapper -> selectParent());
    EasyBind.subscribe(rbParentLevel.selectedProperty(), this::showParentLevels);
    EasyBind.subscribe(rbParentCourse.selectedProperty(), this::showParentCourses);
    updateDataChanged();
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

  private void initializeInputFields() {
    txtLevel.setLabelText(resources.getString("level"));
    txtMinCp.setLabelText(resources.getString("minCp"));
    txtMaxCp.setLabelText(resources.getString("maxCp"));
  }

  private void loadLevelData() {
    setLevel();
    setMaxCp();
    setMinCp();
    selectParent();
    setParentLevels();
    setParentCourses();
    dataChangedProperty.set(false);
  }

  private void setParentCourses() {
    cbParentCourse.getItems().clear();
    cbParentCourse.getItems().addAll(dataService.getCourseWrappers().values());
    cbParentCourse.getSelectionModel().select(levelWrapper.getCourseWrapper());
  }

  private void setParentLevels() {
    cbParentLevel.getItems().clear();
    cbParentLevel.getItems().addAll(dataService.getLevelWrappers().values());
    cbParentLevel.getSelectionModel().select(levelWrapper.getParent());
  }

  private void selectParent() {
    if (levelWrapper.getParent() != null) {
      rbParentLevel.setSelected(true);
    } else {
      rbParentCourse.setSelected(true);
    }
  }

  private void setMinCp() {
    txtMinCp.setText(String.valueOf(levelWrapper.minCreditsProperty().get()));
  }

  private void setMaxCp() {
    txtMaxCp.setText(String.valueOf(levelWrapper.maxCreditsProperty().get()));
  }

  private void setLevel() {
    txtLevel.setText(levelWrapper.getNameProperty());
  }

  /**
   * Push the {@link #levelWrapper} to the {@link #dataService} and set
   * {@link #dataChangedProperty} to false.
   */
  @FXML
  @SuppressWarnings("unused")
  public void persistChanges() {
    levelWrapper.setNameProperty(txtLevel.textProperty().get());
    try {
      levelWrapper.setMaxCredits(Integer.parseInt(txtMaxCp.textProperty().get()));
      levelWrapper.setMinCredits(Integer.parseInt(txtMinCp.textProperty().get()));
    } catch (NumberFormatException exception) {
      new Alert(Alert.AlertType.ERROR, resources.getString("creditsError"),
            ButtonType.OK).showAndWait();
      return;
    }
    if (rbParentLevel.isSelected()) {
      levelWrapper.setParent((LevelWrapper)cbParentLevel.getValue());
      levelWrapper.setCourseProperty(null);
    } else {
      levelWrapper.setCourseProperty((CourseWrapper)cbParentCourse.getValue());
      levelWrapper.setParent(null);
    }

    boolean isNew = levelWrapper.getLevel().getId() == 0;
    //Insert Level in Database.
    dataService.dataChangeEventSource().push(
          new DataChangeEvent(DataChangeType.STORE_ENTITY, levelWrapper));

    //Insert Level in DataTreeView and DataListView.
    if (isNew) {
      dataService.dataChangeEventSource().push(
            new DataChangeEvent(DataChangeType.INSERT_NEW_ENTITY, levelWrapper));
    }
    dataChangedProperty.set(false);
  }
}
