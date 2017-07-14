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
   * Initialize the property bindings according to the given course.
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
  }

  public String getKeyProperty() {
    return keyProperty.get();
  }

  public void setKeyProperty(String keyProperty) {
    this.keyProperty.set(keyProperty);
  }

  public StringProperty keyProperty() {
    return keyProperty;
  }

  public int getPoProperty() {
    return poProperty.get();
  }

  public void setPoProperty(int poProperty) {
    this.poProperty.set(poProperty);
  }

  public IntegerProperty poProperty() {
    return poProperty;
  }

  public int getCreditPointsProperty() {
    return creditPointsProperty.get();
  }

  public void setCreditPointsProperty(int creditPointsProperty) {
    this.creditPointsProperty.set(creditPointsProperty);
  }

  public IntegerProperty creditPointsProperty() {
    return creditPointsProperty;
  }

  public String getShortNameProperty() {
    return shortNameProperty.get();
  }

  public void setShortNameProperty(String shortNameProperty) {
    this.shortNameProperty.set(shortNameProperty);
  }

  public StringProperty shortNameProperty() {
    return shortNameProperty;
  }

  public String getLongNameProperty() {
    return longNameProperty.get();
  }

  public void setLongNameProperty(String longNameProperty) {
    this.longNameProperty.set(longNameProperty);
  }

  public StringProperty longNameProperty() {
    return longNameProperty;
  }

  public CourseDegree getDegreeProperty() {
    return degreeProperty.get();
  }

  public void setDegreeProperty(CourseDegree degreeProperty) {
    this.degreeProperty.set(degreeProperty);
  }

  public ObjectProperty<CourseDegree> degreeProperty() {
    return degreeProperty;
  }

  public CourseKzfa getKzfaProperty() {
    return kzfaProperty.get();
  }

  public void setKzfaProperty(CourseKzfa kzfaProperty) {
    this.kzfaProperty.set(kzfaProperty);
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
