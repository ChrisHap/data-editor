package de.hhu.stups.plues.dataeditor.ui.database;

import de.hhu.stups.plues.dataeditor.ui.database.events.DataChangeEvent;
import de.hhu.stups.plues.dataeditor.ui.database.events.DataChangeType;
import de.hhu.stups.plues.dataeditor.ui.entities.AbstractUnitWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.CourseWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.EntityType;
import de.hhu.stups.plues.dataeditor.ui.entities.EntityWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.GroupWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.Level;
import de.hhu.stups.plues.dataeditor.ui.entities.LevelWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.ModuleWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.SessionWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.UnitWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.repositories.AbstractUnitRepository;
import de.hhu.stups.plues.dataeditor.ui.entities.repositories.CourseRepository;
import de.hhu.stups.plues.dataeditor.ui.entities.repositories.GroupRepository;
import de.hhu.stups.plues.dataeditor.ui.entities.repositories.LevelRepository;
import de.hhu.stups.plues.dataeditor.ui.entities.repositories.ModuleRepository;
import de.hhu.stups.plues.dataeditor.ui.entities.repositories.SessionRepository;
import de.hhu.stups.plues.dataeditor.ui.entities.repositories.UnitRepository;
import javafx.beans.property.ListProperty;
import javafx.beans.property.MapProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import org.fxmisc.easybind.EasyBind;
import org.reactfx.EventSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;
import javax.sql.DataSource;


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

  private final CourseRepository courseRepository;
  private final LevelRepository levelRepository;
  private final ModuleRepository moduleRepository;
  private final AbstractUnitRepository abstractUnitRepository;
  private final UnitRepository unitRepository;
  private final GroupRepository groupRepository;
  private final SessionRepository sessionRepository;

  private ObjectProperty<EntityWrapper> draggedEntityProperty;

  /**
   * Initialize the map properties to store and manage the database entity wrapper and subscribe to
   * {@link DbService}.
   */

  //TODO Factory verwenden
  @Autowired
  public DataService(final DbService dbService, CourseRepository courseRepository,
                     LevelRepository levelRepository,
                     ModuleRepository moduleRepository,
                     AbstractUnitRepository abstractUnitRepository,
                     UnitRepository unitRepository, GroupRepository groupRepository,
                     SessionRepository sessionRepository) {
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
    draggedEntityProperty = new SimpleObjectProperty<>();

    this.courseRepository = courseRepository;
    this.levelRepository = levelRepository;
    this.moduleRepository = moduleRepository;
    this.abstractUnitRepository = abstractUnitRepository;
    this.unitRepository = unitRepository;
    this.groupRepository = groupRepository;
    this.sessionRepository = sessionRepository;

    EasyBind.subscribe(dbService.dataSourceProperty(), this::loadData);
    dataChangeEventSource.subscribe(this::persistData);
  }

  private void persistData(DataChangeEvent dataChangeEvent) {
    if (dataChangeEvent.getDataChangeType().storeEntity()) {
      if (dataChangeEvent.getChangedEntity().getId() == 0) {
        saveNewEntity(dataChangeEvent.getChangedEntity().getEntityType(),
            dataChangeEvent.getChangedEntity());
      } else {
        saveEntity(dataChangeEvent.getChangedEntity().getEntityType(),
            dataChangeEvent.getChangedEntity());
      }
    } else if (dataChangeEvent.getDataChangeType().deleteEntity()) {
      deleteEntity(dataChangeEvent.getChangedEntity().getEntityType(),
          dataChangeEvent.getChangedEntity());
    }
  }

  private void saveEntity(EntityType changedType, EntityWrapper changedEntity) {
    switch (changedType) {
      case COURSE:
        courseRepository.save(((CourseWrapper) changedEntity).getCourse());
        break;
      case LEVEL:
        saveLevel((LevelWrapper)changedEntity);
        break;
      case ABSTRACT_UNIT:
        abstractUnitRepository.save(((AbstractUnitWrapper) changedEntity).getAbstractUnit());
        break;
      case UNIT:
        unitRepository.save(((UnitWrapper) changedEntity).getUnit());
        break;
      case GROUP:
        groupRepository.save(((GroupWrapper) changedEntity).getGroup());
        break;
      case MODULE:
        moduleRepository.save(((ModuleWrapper) changedEntity).getModule());
        break;
      case SESSION:
        sessionRepository.save(((SessionWrapper) changedEntity).getSession());
        break;
      default:
    }
  }

  private void saveNewEntity(EntityType changedType, EntityWrapper changedEntity) {
    int maxId;
    switch (changedType) {
      case COURSE:
        maxId = courseRepository.getMaxId();
        ((CourseWrapper) changedEntity).getCourse().setId(maxId + 1);
        ((CourseWrapper) changedEntity).setId(maxId + 1);
        break;
      case LEVEL:
        saveNewLevel((LevelWrapper)changedEntity);
        break;
      case ABSTRACT_UNIT:
        ((AbstractUnitWrapper) changedEntity).getAbstractUnit().setId(
            abstractUnitRepository.getMaxId() + 1);
        break;
      case UNIT:
        ((UnitWrapper) changedEntity).getUnit().setId(
            unitRepository.getMaxId() + 1);
        break;
      case GROUP:
        ((GroupWrapper) changedEntity).getGroup().setId(
            groupRepository.getMaxId() + 1);
        break;
      case MODULE:
        ((ModuleWrapper) changedEntity).getModule().setId(
            moduleRepository.getMaxId() + 1);
        break;
      case SESSION:
        ((SessionWrapper) changedEntity).getSession().setId(
            sessionRepository.getMaxId() + 1);
        break;
      default:
    }
    //saveEntity(changedType, changedEntity);
  }

  private void saveNewLevel(LevelWrapper levelWrapper) {
    int maxId = levelRepository.getMaxId();
    levelWrapper.getLevel().setId(maxId + 1);
    levelWrapper.setId(maxId + 1);
    Level lvl = levelWrapper.getLevel();
    levelRepository.insertSimpleLevel(lvl.getId(),lvl.getName(),lvl.getTm(), lvl.getArt(),
          lvl.getMin(),lvl.getMax(),lvl.getMinCreditPoints(),lvl.getMaxCreditPoints(),
          lvl.getParent() == null ? null : lvl.getParent().getId());
    if (lvl.getParent() == null) {
      levelRepository.insertCourseLevel(lvl.getCourse().getId(), lvl.getId());
    }
  }

  private void saveLevel(LevelWrapper levelWrapper) {
    levelRepository.deleteCourseLevel(levelWrapper.getId());
    Level lvl = levelWrapper.getLevel();
    levelRepository.updateSimpleLevel(lvl.getId(),lvl.getName(),lvl.getTm(), lvl.getArt(),
          lvl.getMin(),lvl.getMax(),lvl.getMinCreditPoints(),lvl.getMaxCreditPoints(),
          lvl.getParent() == null ? null : lvl.getParent().getId());
    if (lvl.getParent() == null) {
      levelRepository.insertCourseLevel(lvl.getCourse().getId(), lvl.getId());
    }
  }

  private void deleteEntity(EntityType changedType, EntityWrapper changedEntity) {
    switch (changedType) {
      case COURSE:
        courseRepository.delete(((CourseWrapper) changedEntity).getCourse());
        break;
      case LEVEL:
        levelRepository.delete(((LevelWrapper) changedEntity).getLevel());
        break;
      case MODULE:
        moduleRepository.delete(((ModuleWrapper) changedEntity).getModule());
        break;
      case ABSTRACT_UNIT:
        abstractUnitRepository.delete(((AbstractUnitWrapper) changedEntity).getAbstractUnit());
        break;
      case UNIT:
        unitRepository.delete(((UnitWrapper) changedEntity).getUnit());
        break;
      case GROUP:
        groupRepository.delete(((GroupWrapper) changedEntity).getGroup());
        break;
      case SESSION:
        sessionRepository.delete(((SessionWrapper) changedEntity).getSession());
        break;
      default:
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
  private void initializeEntitiesNested() {
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

  public ObjectProperty<EntityWrapper> draggedEntityProperty() {
    return draggedEntityProperty;
  }
}
