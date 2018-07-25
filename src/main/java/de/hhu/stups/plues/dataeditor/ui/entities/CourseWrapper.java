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

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
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

  /**
   * Initialize the property bindings according to the given {@link Course}.
   */
  public CourseWrapper(final Course course) {
    assert course != null;
    keyProperty = new SimpleStringProperty(course.getKey());
    poProperty = new SimpleIntegerProperty(course.getPo());
    creditPointsProperty = new SimpleIntegerProperty(course.getCreditPoints());
    shortNameProperty = new SimpleStringProperty(course.getShortName());
    longNameProperty = new SimpleStringProperty(course.getLongName());
    degreeProperty = new SimpleObjectProperty<>(
        CourseDegree.getDegreeFromString(course.getDegree()));
    kzfaProperty = new SimpleObjectProperty<>(CourseKzfa.getKzfaFromString(course.getKzfa()));
    courseProperty = new SimpleObjectProperty<>(course);
    idProperty = new SimpleIntegerProperty(course.getId());
    majorCourseWrapperProperty = new SimpleSetProperty<>(FXCollections.observableSet());
    minorCourseWrapperProperty = new SimpleSetProperty<>(FXCollections.observableSet());
    setPropertyListener();
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
    course.setMinorCourses(new HashSet<>());
    course.setMajorCourses(new HashSet<>());
    return new CourseWrapper(course);
  }

  private void setPropertyListener() {
    creditPointsProperty.addListener((observable, oldValue, newValue) ->
        courseProperty.get().setCreditPoints(newValue.intValue()));
    shortNameProperty.addListener((observable, oldValue, newValue) ->
        courseProperty.get().setShortName(newValue));
    keyProperty.addListener((observable, oldValue, newValue) ->
        courseProperty.get().setKey(newValue));
    kzfaProperty.addListener((observable, oldValue, newValue) -> {
      final Course course = courseProperty.get();
      if (newValue != null) {
        course.setKzfa(CourseKzfa.toString(newValue));
        return;
      }
      course.setKzfa("");
    });
    poProperty.addListener((observable, oldValue, newValue) -> {
      if (newValue != null) {
        courseProperty.get().setPo(newValue.intValue());
        return;
      }
      courseProperty.get().setPo(-1);
    });
    degreeProperty.addListener((observable, oldValue, newValue) -> {
      final Course course = courseProperty.get();
      if (newValue != null) {
        course.setDegree(newValue.toString().toLowerCase());
        return;
      }
      course.setDegree("");
    });
    longNameProperty.addListener((observable, oldValue, newValue) ->
        courseProperty.get().setLongName(newValue));
    idProperty.addListener((observable, oldValue, newValue) -> {
      final Course course = courseProperty.get();
      if (newValue != null) {
        course.setId(newValue.intValue());
        return;
      }
      course.setId(-1);
    });
    majorCourseWrapperProperty.addListener((observable, oldValue, newValue) ->
        addOrRemoveCourse(courseProperty.get().getMajorCourses(), oldValue, newValue));
    minorCourseWrapperProperty.addListener((observable, oldValue, newValue) ->
        addOrRemoveCourse(courseProperty.get().getMinorCourses(), oldValue, newValue));
  }

  /**
   * Add or remove a major or minor course from the underlying course entity.
   *
   * @param courses  The set of major or minor courses from the underlying
   * {@link this#courseProperty course entity}.
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
  }

  public StringProperty keyProperty() {
    return keyProperty;
  }

  public int getPo() {
    return poProperty.get();
  }

  public void setPo(final int po) {
    this.poProperty.set(po);
  }

  public IntegerProperty poProperty() {
    return poProperty;
  }

  public int getCreditPoints() {
    return creditPointsProperty.get();
  }

  public void setCreditPoints(final int creditPoints) {
    this.creditPointsProperty.set(creditPoints);
  }

  public IntegerProperty creditPointsProperty() {
    return creditPointsProperty;
  }

  public String getShortName() {
    return shortNameProperty.get();
  }

  public void setShortName(final String shortName) {
    this.shortNameProperty.set(shortName);
  }

  public StringProperty shortNameProperty() {
    return shortNameProperty;
  }

  public String getLongName() {
    return longNameProperty.get();
  }

  public void setLongName(final String longName) {
    this.longNameProperty.set(longName);
  }

  public StringProperty longNameProperty() {
    return longNameProperty;
  }

  public CourseDegree getDegree() {
    return degreeProperty.get();
  }

  public void setDegree(final CourseDegree degree) {
    this.degreeProperty.set(degree);
  }

  public ObjectProperty<CourseDegree> degreeProperty() {
    return degreeProperty;
  }

  public CourseKzfa getKzfa() {
    return kzfaProperty.get();
  }

  public void setKzfa(final CourseKzfa kzfa) {
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
}
