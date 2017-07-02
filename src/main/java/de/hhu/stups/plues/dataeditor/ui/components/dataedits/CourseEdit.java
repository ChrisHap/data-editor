package de.hhu.stups.plues.dataeditor.ui.components.dataedits;

import com.google.inject.Inject;

import com.google.inject.assistedinject.Assisted;

import de.hhu.stups.plues.data.entities.Course;
import de.hhu.stups.plues.dataeditor.ui.components.LabeledTextField;
import de.hhu.stups.plues.dataeditor.ui.layout.Inflater;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;

import java.net.URL;
import java.util.ResourceBundle;

public class CourseEdit extends GridPane implements Initializable {

  // TODO: use wrapper for the entities providing bindings for each instance attribute
  private final Course course;
  private ResourceBundle resources;

  @FXML
  @SuppressWarnings("unused")
  private ComboBox<CourseDegree> cbCourseDegree;
  @FXML
  @SuppressWarnings("unused")
  private LabeledTextField txtCourse;
  @FXML
  @SuppressWarnings("unused")
  private LabeledTextField txtStg;
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

  @Inject
  public CourseEdit(final Inflater inflater,
                    @Assisted final Course course) {
    this.course = course;
    inflater.inflate("components/dataedits/course_edit", this, this, "course_edit");
  }

  @Override
  public void initialize(final URL location, final ResourceBundle resources) {
    this.resources = resources;
    initializeCbDegree();
    initializeInputFields();
  }

  private void initializeInputFields() {
    txtCourse.setLabelText(resources.getString("course"));
    txtCreditPoints.setLabelText(resources.getString("credits"));
    txtPVersion.setLabelText(resources.getString("pversion"));
    txtStg.setLabelText(resources.getString("stg"));
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
}
