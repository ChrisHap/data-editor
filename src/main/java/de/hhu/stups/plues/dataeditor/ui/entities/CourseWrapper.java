package de.hhu.stups.plues.dataeditor.ui.entities;

import de.hhu.stups.plues.data.entities.Course;
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

public class CourseWrapper implements EntityWrapper {

  private final IntegerProperty idProperty;
  private final StringProperty keyProperty;
  private final IntegerProperty poProperty;
  private final IntegerProperty creditPointsProperty;
  private final StringProperty shortNameProperty;
  private final StringProperty longNameProperty;
  private final ObjectProperty<CourseDegree> degreeProperty;
  private final ObjectProperty<CourseKzfa> kzfaProperty;
  private final SetProperty<CourseWrapper> majorCourseWrapperProperty;
  private final SetProperty<CourseWrapper> minorCourseWrapperProperty;
  private final ObjectProperty<Course> courseProperty;

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
  public EntityType getEntityType() {
    return EntityType.COURSE;
  }

  public ObservableSet<CourseWrapper> getMajorCourseWrappers() {
    return majorCourseWrapperProperty.get();
  }

  public ObservableSet<CourseWrapper> getMinorCourseWrappers() {
    return minorCourseWrapperProperty.get();
  }
}
