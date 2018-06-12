package de.hhu.stups.plues.dataeditor.ui.database;

import de.hhu.stups.plues.data.SqliteStore;
import de.hhu.stups.plues.dataeditor.ui.database.events.DataChangeEvent;
import de.hhu.stups.plues.dataeditor.ui.database.events.DataChangeType;
import de.hhu.stups.plues.dataeditor.ui.entities.*;
import de.hhu.stups.plues.dataeditor.ui.entities.repositories.*;
import javafx.beans.property.ListProperty;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import org.fxmisc.easybind.EasyBind;
import org.reactfx.EventSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.stream.Collectors;

/**
 * A simple data service providing several map properties to manage the database entity wrapper.
 */
@Component
public class DataService {

  private final EventSource<DataChangeEvent> dataChangeEventSource;
  private final MapProperty<String, CourseWrapper> courseWrappersProperty;
  private final ListProperty<CourseWrapper> majorCourseWrappersProperty;
  private final ListProperty<CourseWrapper> minorCourseWrappersProperty;
  private final MapProperty<Integer, LevelWrapper> levelWrappersProperty;
  private final MapProperty<String, ModuleWrapper> moduleWrappersProperty;
  private final MapProperty<String, AbstractUnitWrapper> abstractUnitWrappersProperty;
  private final MapProperty<String, UnitWrapper> unitWrappersProperty;
  private final MapProperty<String, SessionWrapper> sessionWrappersProperty;
  private final MapProperty<String, GroupWrapper> groupWrappersProperty;
  @Autowired
  private CourseRepository courseRepository;
  @Autowired
  private LevelRepository levelRepository;
  @Autowired
  private ModuleRepository moduleRepository;
  @Autowired
  private AbstractUnitRepository abstractUnitRepository;
  @Autowired
  private UnitRepository unitRepository;
  @Autowired
  private GroupRepository groupRepository;
  @Autowired
  private SessionRepository sessionRepository;

  private ConfigurableApplicationContext context;

  /**
   * Initialize the map properties to store and manage the database entity wrapper and subscribe to
   * {@link DbService}.
   */

  @Autowired
  public DataService(final DbService dbService, ConfigurableApplicationContext context) {
    courseWrappersProperty = new SimpleMapProperty<>(FXCollections.observableHashMap());
    majorCourseWrappersProperty = new SimpleListProperty<>(FXCollections.observableArrayList());
    minorCourseWrappersProperty = new SimpleListProperty<>(FXCollections.observableArrayList());
    levelWrappersProperty = new SimpleMapProperty<>(FXCollections.observableHashMap());
    moduleWrappersProperty = new SimpleMapProperty<>(FXCollections.observableHashMap());
    abstractUnitWrappersProperty = new SimpleMapProperty<>(FXCollections.observableHashMap());
    unitWrappersProperty = new SimpleMapProperty<>(FXCollections.observableHashMap());
    sessionWrappersProperty = new SimpleMapProperty<>(FXCollections.observableHashMap());
    groupWrappersProperty = new SimpleMapProperty<>(FXCollections.observableHashMap());
    dataChangeEventSource = new EventSource<>();
    this.context=context;

    EasyBind.subscribe(dbService.dataSourceProperty(), this::loadData);
    dataChangeEventSource.subscribe(this::persistData);
  }

  private void persistData(DataChangeEvent dataChangeEvent){
    if(!dataChangeEvent.getDataChangeType().storeEntity()){
      return;
    }
    if(dataChangeEvent.getChangedEntity().getEntityType()==EntityType.COURSE){
      courseRepository.save(((CourseWrapper)dataChangeEvent.getChangedEntity()).getCourse());
    }
  }

  private void loadData(final DataSource dataSource) {
    if (dataSource == null) {
      return;
    }
    clear();
    initializeEntitiesFlat();
    initializeEntitiesNested();
    dataChangeEventSource.push(new DataChangeEvent(DataChangeType.RELOAD_DB));
  }

  /**
   * Initialize all map properties on the first level.
   */
  private void initializeEntitiesFlat() {
    courseRepository.findAll().forEach(course -> {
      final CourseWrapper courseWrapper = new CourseWrapper(course);
      courseWrappersProperty.put(course.getKey(), courseWrapper);
      if (course.isMajor()) {
        majorCourseWrappersProperty.add(courseWrapper);
      } else {
        minorCourseWrappersProperty.add(courseWrapper);
      }
    });
    levelRepository.findAll().forEach(level ->
        levelWrappersProperty.put(level.getId(), new LevelWrapper(level)));
    moduleRepository.findAll().forEach(module ->
        moduleWrappersProperty.put(module.getKey(), new ModuleWrapper(module)));
    abstractUnitRepository.findAll().forEach(abstractUnit ->
        abstractUnitWrappersProperty.put(abstractUnit.getKey(),
            new AbstractUnitWrapper(abstractUnit)));
    unitRepository.findAll().forEach(unit ->
        unitWrappersProperty.put(unit.getKey(), new UnitWrapper(unit)));
    groupRepository.findAll().forEach(group ->
        groupWrappersProperty.put(String.valueOf(group.getId()), new GroupWrapper(group)));
    sessionRepository.findAll().forEach(session ->
        sessionWrappersProperty.put(String.valueOf(session.getId()), new SessionWrapper(session)));
  }

  /**
   * Initialize the nested {@link de.hhu.stups.plues.dataeditor.ui.entities.EntityWrapper} after
   * calling {@link #initializeEntitiesFlat()} since we need to use the wrapper defined
   * there.
   */
  public void initializeEntitiesNested() {
    abstractUnitWrappersProperty.values().forEach(abstractUnitWrapper -> {
      abstractUnitWrapper.modulesProperty().addAll(
          abstractUnitWrapper.getAbstractUnit().getModules().stream()
              .map(module -> moduleWrappersProperty.get(module.getKey()))
              .collect(Collectors.toSet()));
      abstractUnitWrapper.unitsProperty().addAll(
          abstractUnitWrapper.getAbstractUnit().getUnits().stream()
              .map(unit -> unitWrappersProperty.get(unit.getKey())).collect(Collectors.toSet()));
    });
    moduleWrappersProperty.values().forEach(moduleWrapper -> {
      moduleWrapper.abstractUnitsProperty().addAll(
          moduleWrapper.getModule().getAbstractUnits().stream()
              .map(abstractUnit -> abstractUnitWrappersProperty.get(abstractUnit.getKey()))
              .collect(Collectors.toSet()));
      moduleWrapper.coursesProperty().addAll(
          moduleWrapper.getModule().getCourses().stream()
              .map(course -> courseWrappersProperty.get(course.getKey()))
              .collect(Collectors.toSet()));
    });
    unitWrappersProperty.values().forEach(unitWrapper -> {
      unitWrapper.abstractUnitsProperty().addAll(
          unitWrapper.getUnit().getAbstractUnits().stream()
              .map(abstractUnit -> abstractUnitWrappersProperty.get(abstractUnit.getKey()))
              .collect(Collectors.toSet()));
      unitWrapper.groupsProperty().addAll(
          unitWrapper.getUnit().getGroups().stream()
              .map(group -> groupWrappersProperty.get(String.valueOf(group.getId())))
              .collect(Collectors.toSet()));
    });
    // add majors and minors to course wrappers
    courseWrappersProperty.values().forEach(courseWrapper -> {
      courseWrapper.majorCourseWrapperProperty().addAll(
          courseWrapper.getCourse().getMajorCourses().stream()
              .map(course -> courseWrappersProperty().get(course.getKey()))
              .collect(Collectors.toSet()));
      courseWrapper.minorCourseWrapperProperty().addAll(
          courseWrapper.getCourse().getMinorCourses().stream()
              .map(course -> courseWrappersProperty().get(course.getKey()))
              .collect(Collectors.toSet()));
    });
    levelWrappersProperty.values().forEach(levelWrapper -> {
      if (levelWrapper.getLevel().getParent() != null) {
        levelWrapper.setParent(levelWrappersProperty.get(levelWrapper.getLevel().getParent()
            .getId()));
      }
      if (levelWrapper.getLevel().getCourse() != null) {
        levelWrapper.setCourseProperty(courseWrappersProperty().get(levelWrapper.getLevel()
            .getCourse().getKey()));
      }
    });
    groupWrappersProperty.values().forEach(groupWrapper -> {
      groupWrapper.setUnit(unitWrappersProperty.get(groupWrapper.getGroup().getUnit().getKey()));
      groupWrapper.sessionsProperty().addAll(
          groupWrapper.getGroup().getSessions().stream()
              .map(session -> sessionWrappersProperty.get(String.valueOf(session.getId())))
              .collect(Collectors.toSet()));
    });
  }

  private void clear() {
    courseWrappersProperty.clear();
    majorCourseWrappersProperty.clear();
    minorCourseWrappersProperty.clear();
    levelWrappersProperty.clear();
    moduleWrappersProperty.clear();
    abstractUnitWrappersProperty.clear();
    unitWrappersProperty.clear();
    sessionWrappersProperty.clear();
    groupWrappersProperty.clear();
  }

  public ObservableMap<String, CourseWrapper> getCourseWrappers() {
    return courseWrappersProperty.get();
  }

  public MapProperty<String, CourseWrapper> courseWrappersProperty() {
    return courseWrappersProperty;
  }

  public MapProperty<String, GroupWrapper> groupWrappersProperty() {
    return groupWrappersProperty;
  }

  public ObservableMap<String, GroupWrapper> getGroupWrappers() {
    return groupWrappersProperty.get();
  }

  public ListProperty<CourseWrapper> majorCourseWrappersProperty() {
    return majorCourseWrappersProperty;
  }

  public ListProperty<CourseWrapper> minorCourseWrappersProperty() {
    return minorCourseWrappersProperty;
  }

  public ObservableMap<Integer, LevelWrapper> getLevelWrappers() {
    return levelWrappersProperty.get();
  }

  public MapProperty<Integer, LevelWrapper> levelWrappersProperty() {
    return levelWrappersProperty;
  }

  public ObservableMap<String, ModuleWrapper> getModuleWrappers() {
    return moduleWrappersProperty.get();
  }

  public MapProperty<String, ModuleWrapper> moduleWrappersProperty() {
    return moduleWrappersProperty;
  }

  public ObservableMap<String, AbstractUnitWrapper> getAbstractUnitWrappers() {
    return abstractUnitWrappersProperty.get();
  }

  public MapProperty<String, AbstractUnitWrapper> abstractUnitWrappersProperty() {
    return abstractUnitWrappersProperty;
  }

  public ObservableMap<String, UnitWrapper> getUnitWrappers() {
    return unitWrappersProperty.get();
  }

  public MapProperty<String, UnitWrapper> unitWrappersProperty() {
    return unitWrappersProperty;
  }

  public ObservableMap<String, SessionWrapper> getSessionWrappers() {
    return sessionWrappersProperty.get();
  }

  public MapProperty<String, SessionWrapper> sessionWrappersProperty() {
    return sessionWrappersProperty;
  }

  public EventSource<DataChangeEvent> dataChangeEventSource() {
    return dataChangeEventSource;
  }
}
