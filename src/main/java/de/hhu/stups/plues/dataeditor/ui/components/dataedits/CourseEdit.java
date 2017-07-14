package de.hhu.stups.plues.dataeditor.ui.components.dataedits;

import com.google.inject.Inject;

import de.hhu.stups.plues.dataeditor.ui.components.LabeledTextField;
import de.hhu.stups.plues.dataeditor.ui.database.DataService;
import de.hhu.stups.plues.dataeditor.ui.database.events.DataChangeEvent;
import de.hhu.stups.plues.dataeditor.ui.database.events.DataChangeType;
import de.hhu.stups.plues.dataeditor.ui.entities.CourseDegree;
import de.hhu.stups.plues.dataeditor.ui.entities.CourseWrapper;
import de.hhu.stups.plues.dataeditor.ui.layout.Inflater;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;

import java.net.URL;
import java.util.ResourceBundle;

public class CourseEdit extends GridPane implements Initializable {

  private final DataService dataService;

  private CourseWrapper courseWrapper;
  private ResourceBundle resources;

  @FXML
  @SuppressWarnings("unused")
  private ComboBox<CourseDegree> cbCourseDegree;
  @FXML
  @SuppressWarnings("unused")
  private LabeledTextField txtFullName;
  @FXML
  @SuppressWarnings("unused")
  private LabeledTextField txtShortName;
  @FXML
  @SuppressWarnings("unused")
  private LabeledTextField txtPVersion;
  @FXML
  @SuppressWarnings("unused")
  private LabeledTextField txtCreditPoints;
  @FXML
  @SuppressWarnings("unused")
  private RadioButton rbMajorCourse;
  @FXML
  @SuppressWarnings("unused")
  private RadioButton rbMinorCourse;
  @FXML
  @SuppressWarnings("unused")
  private Button persistChanges;
  @FXML
  @SuppressWarnings("unused")
  private Label lbMajorsOrMinors;
  @FXML
  @SuppressWarnings("unused")
  private ListView<CourseWrapper> listViewMajorsOrMinors;

  /**
   * Inject the {@link DataService}.
   */
  @Inject
  public CourseEdit(final Inflater inflater,
                    final DataService dataService) {
    this.dataService = dataService;
    inflater.inflate("components/dataedits/course_edit", this, this, "course_edit");
  }

  @Override
  public void initialize(final URL location, final ResourceBundle resources) {
    this.resources = resources;
    initializeCbDegree();
    initializeInputFields();
    lbMajorsOrMinors.textProperty().bind(Bindings.when(rbMajorCourse.selectedProperty())
        .then(resources.getString("minors")).otherwise(resources.getString("majors")));
    rbMajorCourse.selectedProperty().addListener((observable, oldValue, newValue) ->
        loadMajorsOrMinors());
    rbMinorCourse.selectedProperty().addListener((observable, oldValue, newValue) ->
        loadMajorsOrMinors());
  }

  private void loadMajorsOrMinors() {
    if (courseWrapper == null) {
      return;
    }
    listViewMajorsOrMinors.getItems().clear();
    if (rbMajorCourse.isSelected()) {
      listViewMajorsOrMinors.getItems()
          .addAll(courseWrapper.getMinorCourseWrappers());
    } else {
      listViewMajorsOrMinors.getItems()
          .addAll(courseWrapper.getMajorCourseWrappers());
    }
  }

  private void loadCourseData() {
    txtFullName.textProperty().bind(courseWrapper.longNameProperty());
    txtShortName.textProperty().bind(courseWrapper.shortNameProperty());
    txtCreditPoints.textProperty().bind(Bindings.createStringBinding(
        () -> String.valueOf(courseWrapper.creditPointsProperty().get())));
    txtPVersion.textProperty().bind(Bindings.createStringBinding(
        () -> String.valueOf(courseWrapper.poProperty().get())));
    if (courseWrapper.getCourse().isMajor()) {
      rbMajorCourse.setSelected(true);
    } else {
      rbMinorCourse.setSelected(true);
    }
  }

  @FXML
  @SuppressWarnings("unused")
  public void persistChanges() {
    dataService.dataChangeEventSource().push(
        new DataChangeEvent(DataChangeType.STORE_ENTITY, courseWrapper));
  }

  private void initializeInputFields() {
    txtFullName.setLabelText(resources.getString("course"));
    txtShortName.setLabelText(resources.getString("stg"));
    txtCreditPoints.setLabelText(resources.getString("credits"));
    txtPVersion.setLabelText(resources.getString("pversion"));
  }

  private void initializeCbDegree() {
    cbCourseDegree.setConverter(new StringConverter<CourseDegree>() {
      @Override
      public String toString(final CourseDegree courseDegree) {
        return resources.getString(courseDegree.toString().toLowerCase());
      }

      @Override
      public CourseDegree fromString(final String string) {
        return null;
      }
    });
    cbCourseDegree.getSelectionModel().selectFirst();
  }

  public CourseWrapper getCourseWrapper() {
    return courseWrapper;
  }

  /**
   * Set the {@link #courseWrapper} and load the course data.
   */
  public void setCourseWrapper(final CourseWrapper courseWrapper) {
    this.courseWrapper = courseWrapper;
    loadCourseData();
  }
}
