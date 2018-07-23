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
import java.util.Set;
import java.util.stream.Collectors;

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
    majorCourseWrapperProperty.addListener((observable, oldValue, newValue) ->
        addOrRemoveCourse(courseProperty.get().getMajorCourses(), oldValue, newValue));
    minorCourseWrapperProperty.addListener((observable, oldValue, newValue) ->
        addOrRemoveCourse(courseProperty.get().getMinorCourses(), oldValue, newValue));
  }

  /**
   * Create an empty CourseWrapper.
   *
   * @return Return an empty {@link CourseWrapper}.
   */
  public static CourseWrapper createEmptyCourseWrapper() {
    Course course = new Course();
    course.setCreditPoints(5);
    course.setDegree("bk");
    course.setPo(2016);
    return new CourseWrapper(course);
  }

  /**
   * Add or remove a major or minor course from the underlying course entity.
   *
   * @param courses  The set of major or minor courses from the underlying {@link this#courseProperty course entity}.
   * @param oldValue The old value of the major or minor course property.
   * @param newValue The new value of the major or minor course property.
   */
  private void addOrRemoveCourse(final Set<Course> courses,
                                 final ObservableSet<CourseWrapper> oldValue,
                                 final ObservableSet<CourseWrapper> newValue) {
    if (newValue.size() > oldValue.size()) {
      newValue.removeAll(oldValue);
      courses.addAll(newValue.stream().map(CourseWrapper::getCourse).collect(Collectors.toSet()));
      return;
    }
    if (newValue.size() < oldValue.size()) {
      oldValue.removeAll(newValue);
      courses.removeAll(
          oldValue.stream().map(CourseWrapper::getCourse).collect(Collectors.toSet()));
    }
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

  public void setKey(final String key) {
    this.keyProperty.set(key);
    this.courseProperty.get().setShortName(key);
  }

  public StringProperty keyProperty() {
    return keyProperty;
  }

  public int getPo() {
    return poProperty.get();
  }

  public void setPo(final int po) {
    this.poProperty.set(po);
    this.courseProperty.get().setPo(po);
  }

  public IntegerProperty poProperty() {
    return poProperty;
  }

  public int getCreditPoints() {
    return creditPointsProperty.get();
  }

  public void setCreditPoints(final int creditPoints) {
    this.creditPointsProperty.set(creditPoints);
    this.courseProperty.get().setCreditPoints(creditPoints);
  }

  public IntegerProperty creditPointsProperty() {
    return creditPointsProperty;
  }

  public String getShortName() {
    return shortNameProperty.get();
  }

  public void setShortName(final String shortName) {
    this.shortNameProperty.set(shortName);
    this.courseProperty.get().setShortName(shortName);
  }

  public StringProperty shortNameProperty() {
    return shortNameProperty;
  }

  public String getLongName() {
    return longNameProperty.get();
  }

  public void setLongName(final String longName) {
    this.longNameProperty.set(longName);
    this.courseProperty.get().setLongName(longName);
  }

  public StringProperty longNameProperty() {
    return longNameProperty;
  }

  public CourseDegree getDegree() {
    return degreeProperty.get();
  }

  public void setDegree(final CourseDegree degree) {
    this.degreeProperty.set(degree);
    this.courseProperty.get().setDegree(degree.toString().toLowerCase());
  }

  public ObjectProperty<CourseDegree> degreeProperty() {
    return degreeProperty;
  }

  public CourseKzfa getKzfa() {
    return kzfaProperty.get();
  }

  public void setKzfa(final CourseKzfa kzfa) {
    this.kzfaProperty.set(kzfa);
    this.courseProperty.get().setKzfa(kzfa.toString());
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
}
