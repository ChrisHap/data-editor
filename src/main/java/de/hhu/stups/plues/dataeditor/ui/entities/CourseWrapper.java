package de.hhu.stups.plues.dataeditor.ui.entities;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SetProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

import javax.persistence.Transient;

public class CourseWrapper implements EntityWrapper {

  private IntegerProperty idProperty;
  private StringProperty keyProperty;
  private IntegerProperty poProperty;
  private IntegerProperty creditPointsProperty;
  private StringProperty shortNameProperty;
  private StringProperty longNameProperty;
  private ObjectProperty<CourseDegree> degreeProperty;
  private ObjectProperty<CourseKzfa> kzfaProperty;
  private SetProperty<CourseWrapper> majorCourseWrapperProperty;
  private SetProperty<CourseWrapper> minorCourseWrapperProperty;
  private ObjectProperty<Course> courseProperty;

  protected CourseWrapper() {
    keyProperty = new SimpleStringProperty();
    poProperty = new SimpleIntegerProperty();
    creditPointsProperty = new SimpleIntegerProperty();
    shortNameProperty = new SimpleStringProperty();
    longNameProperty = new SimpleStringProperty();
    degreeProperty = new SimpleObjectProperty<>();
    kzfaProperty = new SimpleObjectProperty<>();
    courseProperty = new SimpleObjectProperty<>();
    majorCourseWrapperProperty = new SimpleSetProperty<>(FXCollections.observableSet());
    minorCourseWrapperProperty = new SimpleSetProperty<>(FXCollections.observableSet());
    idProperty = new SimpleIntegerProperty();
  }
  /**
   * Initialize the property bindings according to the given {@link Course}.
   */

  public CourseWrapper(final Course course) {
    keyProperty = new SimpleStringProperty(course.getKey());
    poProperty = new SimpleIntegerProperty(course.getPo());
    creditPointsProperty = new SimpleIntegerProperty(course.getCreditPoints());
    shortNameProperty = new SimpleStringProperty(course.getShortName());
    longNameProperty = new SimpleStringProperty(course.getLongName());
    degreeProperty = new SimpleObjectProperty<>(
        CourseDegree.getDegreeFromString(course.getDegree()));
    kzfaProperty = new SimpleObjectProperty<>(CourseKzfa.getKzfaFromString(course.getKzfa()));
    courseProperty = new SimpleObjectProperty<>(course);
    majorCourseWrapperProperty = new SimpleSetProperty<>(FXCollections.observableSet());
    minorCourseWrapperProperty = new SimpleSetProperty<>(FXCollections.observableSet());
    idProperty = new SimpleIntegerProperty(course.getId());
  }

  public int getId() {
    return idProperty.get();
  }

  public void setId(int id) {
    this.idProperty.set(id);
  }

  public IntegerProperty idProperty() {
    return idProperty;
  }

  public String getKey() {
    return keyProperty.get();
  }

  public void setKey(String key) {
    this.keyProperty.set(key);
  }

  public StringProperty keyProperty() {
    return keyProperty;
  }

  public int getPo() {
    return poProperty.get();
  }

  public void setPo(int po) {
    this.poProperty.set(po);
  }

  public IntegerProperty poProperty() {
    return poProperty;
  }

  public int getCreditPoints() {
    return creditPointsProperty.get();
  }

  public void setCreditPoints(int creditPoints) {
    this.creditPointsProperty.set(creditPoints);
  }

  public IntegerProperty creditPointsProperty() {
    return creditPointsProperty;
  }

  public String getShortName() {
    return shortNameProperty.get();
  }

  public void setShortName(String shortName) {
    this.shortNameProperty.set(shortName);
  }

  public StringProperty shortNameProperty() {
    return shortNameProperty;
  }

  public String getLongName() {
    return longNameProperty.get();
  }

  public void setLongName(String longName) {
    this.longNameProperty.set(longName);
  }

  public StringProperty longNameProperty() {
    return longNameProperty;
  }

  public CourseDegree getDegree() {
    return degreeProperty.get();
  }

  public void setDegree(CourseDegree degreeProperty) {
    this.degreeProperty.set(degreeProperty);
  }

  public ObjectProperty<CourseDegree> degreeProperty() {
    return degreeProperty;
  }

  public CourseKzfa getKzfa() {
    return kzfaProperty.get();
  }

  public void setKzfa(CourseKzfa kzfa) {
    this.kzfaProperty.set(kzfa);
  }

  public ObjectProperty<CourseKzfa> kzfaProperty() {
    return kzfaProperty;
  }

  public ObjectProperty<Course> courseProperty() {
    return courseProperty;
  }

  public SetProperty<CourseWrapper> majorCourseWrapperProperty() {
    return majorCourseWrapperProperty;
  }

  public SetProperty<CourseWrapper> minorCourseWrapperProperty() {
    return minorCourseWrapperProperty;
  }

  @Transient
  public Course getCourse() {
    return courseProperty.get();
  }

  @Override
  public String toString() {
    if (courseProperty.get() == null) {
      return "";
    }
    return courseProperty.get().toString();
  }

  @Override
  @Transient
  public EntityType getEntityType() {
    return EntityType.COURSE;
  }

  @Transient
  public ObservableSet<CourseWrapper> getMajorCourseWrappers() {
    return majorCourseWrapperProperty.get();
  }

  @Transient
  public ObservableSet<CourseWrapper> getMinorCourseWrappers() {
    return minorCourseWrapperProperty.get();
  }

  /**
   * Create an empty CourseWrapper.
   * @return the created CourseWrapper.
   */
  public static CourseWrapper createEmptyCourseWrapper() {
    Course course = new Course();
    course.setCreditPoints(5);
    course.setDegree("bk");
    course.setPo(2016);
    return new CourseWrapper(course);
  }
}
