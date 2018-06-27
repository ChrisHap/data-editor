package de.hhu.stups.plues.dataeditor.ui.components.dataedits;

import de.hhu.stups.plues.dataeditor.ui.components.LabeledTextField;
import de.hhu.stups.plues.dataeditor.ui.database.DataService;
import de.hhu.stups.plues.dataeditor.ui.database.events.DataChangeEvent;
import de.hhu.stups.plues.dataeditor.ui.database.events.DataChangeType;
import de.hhu.stups.plues.dataeditor.ui.entities.CourseDegree;
import de.hhu.stups.plues.dataeditor.ui.entities.CourseWrapper;
import de.hhu.stups.plues.dataeditor.ui.layout.Inflater;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;
import org.fxmisc.easybind.EasyBind;

import java.net.URL;
import java.util.ResourceBundle;

public class CourseEdit extends GridPane implements Initializable {

  private final DataService dataService;
  private final BooleanProperty dataChangedProperty;

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
  private Button btPersistChanges;
  @FXML
  @SuppressWarnings("unused")
  private Label lbMajorsOrMinors;
  @FXML
  @SuppressWarnings("unused")
  private ListView<CourseWrapper> listViewMajorsOrMinors;

  /**
   * Inject the {@link DataService}.
   */
  CourseEdit(final Inflater inflater,
                    final DataService dataService,
                    final CourseWrapper courseWrapper) {
    this.dataService = dataService;
    this.courseWrapper = courseWrapper;
    dataChangedProperty = new SimpleBooleanProperty(false);
    inflater.inflate("components/dataedits/course_edit", this, this, "course_edit");
  }

  @Override
  public void initialize(final URL location, final ResourceBundle resources) {
    this.resources = resources;
    btPersistChanges.disableProperty().bind(dataChangedProperty.not());
    initializeCbDegree();
    initializeInputFields();
    lbMajorsOrMinors.textProperty().bind(Bindings.when(rbMajorCourse.selectedProperty())
        .then(resources.getString("minors")).otherwise(resources.getString("majors")));
    setDataListener();
    loadCourseData();
  }

  /**
   * Update data if the wrapper has changed.
   */
  private void setDataListener() {
    EasyBind.subscribe(courseWrapper.longNameProperty(), s -> setFullName());
    EasyBind.subscribe(courseWrapper.shortNameProperty(), s -> setShortName());
    EasyBind.subscribe(courseWrapper.creditPointsProperty(), number -> setCreditPoints());
    EasyBind.subscribe(courseWrapper.poProperty(), number -> setPversion());
    EasyBind.subscribe(courseWrapper.courseProperty(), course -> selectMajorOrMinor());
    EasyBind.subscribe(courseWrapper.degreeProperty(), course -> selectCourseDegree());
    EasyBind.subscribe(rbMajorCourse.selectedProperty(), aBoolean -> loadMajorsOrMinors());
    EasyBind.subscribe(rbMinorCourse.selectedProperty(), aBoolean -> loadMajorsOrMinors());
    updateDataChanged();
  }

  private void selectCourseDegree() {
    cbCourseDegree.getSelectionModel().select(courseWrapper.getDegree());
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

  private void updateDataChanged() {
    EasyBind.subscribe(txtFullName.textProperty(), s -> dataChangedProperty.set(true));
    EasyBind.subscribe(txtShortName.textProperty(), s -> dataChangedProperty.set(true));
    EasyBind.subscribe(txtPVersion.textProperty(), s -> dataChangedProperty.set(true));
    EasyBind.subscribe(txtCreditPoints.textProperty(), s -> dataChangedProperty.set(true));
    EasyBind.subscribe(listViewMajorsOrMinors.itemsProperty(), s -> dataChangedProperty.set(true));
    EasyBind.subscribe(rbMajorCourse.selectedProperty(), s -> dataChangedProperty.set(true));
    EasyBind.subscribe(rbMinorCourse.selectedProperty(), s -> dataChangedProperty.set(true));
    EasyBind.subscribe(cbCourseDegree.selectionModelProperty(), s -> dataChangedProperty.set(true));
  }

  private void loadCourseData() {
    setFullName();
    setShortName();
    setCreditPoints();
    setPversion();
    selectMajorOrMinor();
    dataChangedProperty.set(false);
  }

  private void selectMajorOrMinor() {
    if (courseWrapper.getCourse().isMajor()) {
      rbMajorCourse.setSelected(true);
    } else {
      rbMinorCourse.setSelected(true);
    }
  }

  private void setPversion() {
    txtPVersion.setText(String.valueOf(courseWrapper.poProperty().get()));

  }

  private void setCreditPoints() {
    txtCreditPoints.setText(String.valueOf(courseWrapper.creditPointsProperty().get()));
  }

  private void setShortName() {
    txtShortName.setText(courseWrapper.getShortName());
  }

  private void setFullName() {
    txtFullName.setText(courseWrapper.getLongName());
  }

  /**
   * Push the {@link #courseWrapper} to the {@link #dataService} and set
   * {@link #dataChangedProperty} to false.
   */
  @FXML
  @SuppressWarnings("unused")
  public void persistChanges() {
    if (cbCourseDegree.getValue() == null) {
      Alert alert = new Alert(Alert.AlertType.ERROR, "Bitte degree auswählen", ButtonType.OK);
      alert.showAndWait();
      return;
    }
    courseWrapper.getCourse().setDegree(cbCourseDegree.getValue().toString());
    courseWrapper.getCourse().setLongName(txtFullName.textProperty().getValue());
    courseWrapper.getCourse().setShortName(txtShortName.textProperty().getValue());
    try {
      courseWrapper.getCourse().setPo(Integer.parseInt(txtPVersion.textProperty().get()));
    } catch (NumberFormatException exeption){
      new Alert(Alert.AlertType.ERROR, "Prüfungsordnung muss Zahl sein", ButtonType.OK).showAndWait();
      return;
    }
    try {
      courseWrapper.getCourse().setCreditPoints(Integer.parseInt(
            txtCreditPoints.textProperty().getValue()));
    } catch (NumberFormatException exeption) {
      new Alert(Alert.AlertType.ERROR, "Credit Points muss Zahl sein", ButtonType.OK).showAndWait();
      return;
    }
    if (rbMajorCourse.isSelected()) {
      courseWrapper.getCourse().setKzfa("H");
    } else {
      courseWrapper.getCourse().setKzfa("N");
    }

    dataService.dataChangeEventSource().push(
        new DataChangeEvent(DataChangeType.STORE_ENTITY, courseWrapper));
    dataChangedProperty.set(false);
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
}
