package de.hhu.stups.plues.dataeditor.ui.database;

import de.hhu.stups.plues.dataeditor.ui.database.events.DataChangeEvent;
import de.hhu.stups.plues.dataeditor.ui.database.events.DataChangeType;
import de.hhu.stups.plues.dataeditor.ui.entities.AbstractUnitWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.Course;
import de.hhu.stups.plues.dataeditor.ui.entities.CourseKzfa;
import de.hhu.stups.plues.dataeditor.ui.entities.CourseWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.EntityType;
import de.hhu.stups.plues.dataeditor.ui.entities.EntityWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.GroupWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.Level;
import de.hhu.stups.plues.dataeditor.ui.entities.LevelWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.Module;
import de.hhu.stups.plues.dataeditor.ui.entities.ModuleWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.SessionWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.UnitWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.repositories.AbstractUnitRepository;
import de.hhu.stups.plues.dataeditor.ui.entities.repositories.CourseRepository;
import de.hhu.stups.plues.dataeditor.ui.entities.repositories.GroupRepository;
import de.hhu.stups.plues.dataeditor.ui.entities.repositories.LevelRepository;
import de.hhu.stups.plues.dataeditor.ui.entities.repositories.ModuleRepository;
import de.hhu.stups.plues.dataeditor.ui.entities.repositories.RepositoryFactory;
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
  private final MapProperty<Integer, CourseWrapper> courseWrappersProperty;
  private final ListProperty<CourseWrapper> majorCourseWrappersProperty;
  private final ListProperty<CourseWrapper> minorCourseWrappersProperty;
  private final MapProperty<Integer, LevelWrapper> levelWrappersProperty;
  private final MapProperty<Integer, ModuleWrapper> moduleWrappersProperty;
  private final MapProperty<Integer, AbstractUnitWrapper> abstractUnitWrappersProperty;
  private final MapProperty<Integer, UnitWrapper> unitWrappersProperty;
  private final MapProperty<Integer, SessionWrapper> sessionWrappersProperty;
  private final MapProperty<Integer, GroupWrapper> groupWrappersProperty;

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

  @Autowired
  public DataService(final DbService dbService, RepositoryFactory repositoryFactory) {
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

    this.courseRepository = repositoryFactory.getCourseRepository();
    this.levelRepository = repositoryFactory.getLevelRepository();
    this.moduleRepository = repositoryFactory.getModuleRepository();
    this.abstractUnitRepository = repositoryFactory.getAbstractUnitRepository();
    this.unitRepository = repositoryFactory.getUnitRepository();
    this.groupRepository = repositoryFactory.getGroupRepository();
    this.sessionRepository = repositoryFactory.getSessionRepository();

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
        saveCourse((CourseWrapper) changedEntity);
        break;
      case LEVEL:
        saveLevel((LevelWrapper) changedEntity);
        break;
      case MODULE:
        saveModule((ModuleWrapper) changedEntity);
        break;
      case ABSTRACT_UNIT:
        saveAbstractUnit((AbstractUnitWrapper) changedEntity);
        break;
      case UNIT:
        unitRepository.save(((UnitWrapper) changedEntity).getUnit());
        break;
      case GROUP:
        groupRepository.save(((GroupWrapper) changedEntity).getGroup());
        break;
      case SESSION:
        sessionRepository.save(((SessionWrapper) changedEntity).getSession());
        break;
      default:
    }
  }

  private void saveAbstractUnit(AbstractUnitWrapper abstractUnitWrapper) {
    abstractUnitRepository.save(abstractUnitWrapper.getAbstractUnit());
    abstractUnitRepository.deleteModuleAbstractUnitTypeByAbstractUnit(
          abstractUnitWrapper.getId());
    abstractUnitRepository.deleteModuleAbstractUnitSemesterByAbstractUnit(
          abstractUnitWrapper.getId());

    abstractUnitWrapper.getModules().forEach(moduleWrapper -> {
      abstractUnitRepository.insertSimpleModuleAbstractUnitType(
            moduleWrapper.getId(), abstractUnitWrapper.getId());
      abstractUnitRepository.insertSimpleModuleAbstractUnitSemester(
            moduleWrapper.getId(), abstractUnitWrapper.getId());
    });
    abstractUnitRepository.deleteUnitAbstractUnitByAbstractUnit(abstractUnitWrapper.getId());
    abstractUnitWrapper.getUnits().forEach(unitWrapper ->
          abstractUnitRepository.insertSimpleUnitAbstractUnit(unitWrapper.getId(),
                abstractUnitWrapper.getId()));
  }

  private void saveNewEntity(EntityType changedType, EntityWrapper changedEntity) {
    final int maxId;
    switch (changedType) {
      case COURSE:
        saveNewCourse((CourseWrapper) changedEntity);
        break;
      case LEVEL:
        saveNewLevel((LevelWrapper) changedEntity);
        break;
      case MODULE:
        saveNewModule((ModuleWrapper) changedEntity);
        break;
      case ABSTRACT_UNIT:
        saveNewAbstractUnit((AbstractUnitWrapper) changedEntity);
        break;
      case UNIT:
        maxId = unitRepository.getMaxId() + 1;
        ((UnitWrapper) changedEntity).setId(maxId);
        unitWrappersProperty.put(changedEntity.getId(),
            ((UnitWrapper) changedEntity));
        unitRepository.save(((UnitWrapper) changedEntity).getUnit());
        break;
      case GROUP:
        maxId = groupRepository.getMaxId() + 1;
        ((GroupWrapper) changedEntity).setId(maxId);
        groupWrappersProperty.put(changedEntity.getId(),
            ((GroupWrapper) changedEntity));
        groupRepository.save(((GroupWrapper) changedEntity).getGroup());
        break;
      case SESSION:
        maxId = sessionRepository.getMaxId() + 1;
        ((SessionWrapper) changedEntity).setId(maxId);
        sessionWrappersProperty.put(changedEntity.getId(),
            ((SessionWrapper) changedEntity));
        sessionRepository.save(((SessionWrapper) changedEntity).getSession());
        break;
      default:
    }
  }

  private void saveNewLevel(final LevelWrapper levelWrapper) {
    final int maxId = levelRepository.getMaxId();
    levelWrapper.setId(maxId + 1);
    final Level lvl = levelWrapper.getLevel();
    levelRepository.insertSimpleLevel(lvl.getId(), lvl.getName(), lvl.getTm(), lvl.getArt(),
        lvl.getMin(), lvl.getMax(), lvl.getMinCreditPoints(), lvl.getMaxCreditPoints(),
        lvl.getParent() == null ? null : lvl.getParent().getId());
    if (lvl.getParent() == null && lvl.getCourse() != null) {
      levelRepository.insertCourseLevel(lvl.getCourse().getId(), lvl.getId());
    }
    levelWrappersProperty.put(levelWrapper.getId(), levelWrapper);
  }

  private void saveNewAbstractUnit(AbstractUnitWrapper abstractUnitWrapper) {
    abstractUnitWrapper.setId(abstractUnitRepository.getMaxId() + 1);
    abstractUnitWrappersProperty.put(abstractUnitWrapper.getId(), abstractUnitWrapper);
    abstractUnitRepository.save(abstractUnitWrapper.getAbstractUnit());
    abstractUnitWrapper.getModules().forEach(moduleWrapper -> {
      abstractUnitRepository.insertSimpleModuleAbstractUnitType(
            moduleWrapper.getId(), abstractUnitWrapper.getId());
      abstractUnitRepository.insertSimpleModuleAbstractUnitSemester(
            moduleWrapper.getId(), abstractUnitWrapper.getId());
    });
    abstractUnitWrapper.getUnits().forEach(unitWrapper ->
          abstractUnitRepository.insertSimpleUnitAbstractUnit(unitWrapper.getId(),
          abstractUnitWrapper.getId()));
  }

  private void saveLevel(final LevelWrapper levelWrapper) {
    levelRepository.deleteCourseLevel(levelWrapper.getId());
    final Level lvl = levelWrapper.getLevel();
    levelRepository.updateSimpleLevel(lvl.getId(), lvl.getName(), lvl.getTm(), lvl.getArt(),
        lvl.getMin(), lvl.getMax(), lvl.getMinCreditPoints(), lvl.getMaxCreditPoints(),
        lvl.getParent() == null ? null : lvl.getParent().getId());
    if (lvl.getParent() == null && lvl.getCourse() != null) {
      levelRepository.insertCourseLevel(lvl.getCourse().getId(), lvl.getId());
    }
  }

  private void saveModule(ModuleWrapper moduleWrapper) {
    moduleRepository.deleteModuleLevel(moduleWrapper.getId());
    final Module mod = moduleWrapper.getModule();
    moduleRepository.updateSimpleModule(mod.getId(), mod.getKey(), mod.getTitle(),
        mod.getPordnr(), mod.getElectiveUnits(), mod.getBundled());
    final Level lvl = mod.getLevel();
    Level dummyLevel = lvl;
    while (dummyLevel.getParent() != null) {
      dummyLevel = dummyLevel.getParent();
    }
    final Course course = dummyLevel.getCourse();
    //TODO mandatory einbauen
    if (course != null) {
      moduleRepository.insertModuleLevel(mod.getId(), lvl.getId(), course.getId(),
          mod.getTitle(), false);
    }

    abstractUnitRepository.deleteModuleAbstractUnitSemesterByModule(mod.getId());
    abstractUnitRepository.deleteModuleAbstractUnitTypeByModule(mod.getId());
    moduleWrapper.getAbstractUnits().forEach(abstractUnitWrapper -> {
      abstractUnitRepository.deleteModuleAbstractUnitSemesterByAbstractUnit(
            abstractUnitWrapper.getId());
      abstractUnitRepository.insertSimpleModuleAbstractUnitSemester(
            mod.getId(), abstractUnitWrapper.getId());
      abstractUnitRepository.deleteModuleAbstractUnitTypeByAbstractUnit(
            abstractUnitWrapper.getId());
      abstractUnitRepository.insertSimpleModuleAbstractUnitType(
            mod.getId(), abstractUnitWrapper.getId());
    });
  }

  private void saveNewModule(final ModuleWrapper moduleWrapper) {
    final int maxId = levelRepository.getMaxId();
    moduleWrapper.setId(maxId + 1);
    final Module mod = moduleWrapper.getModule();
    moduleRepository.insertSimpleModule(mod.getId(), mod.getKey(), mod.getTitle(), mod.getPordnr(),
        mod.getElectiveUnits(), mod.getBundled());
    final Level lvl = mod.getLevel();
    Level dummyLevel = lvl;
    while (dummyLevel.getParent() != null) {
      dummyLevel = dummyLevel.getParent();
    }
    final Course course = dummyLevel.getCourse();
    //TODO mandatory einbauen
    if (course != null) {
      moduleRepository.insertModuleLevel(mod.getId(), lvl.getId(), course.getId(),
          mod.getTitle(), false);
    }
    moduleWrapper.getAbstractUnits().forEach(abstractUnitWrapper -> {
      abstractUnitRepository.deleteModuleAbstractUnitSemesterByAbstractUnit(
            abstractUnitWrapper.getId());
      abstractUnitRepository.insertSimpleModuleAbstractUnitSemester(
            mod.getId(), abstractUnitWrapper.getId());
      abstractUnitRepository.deleteModuleAbstractUnitTypeByAbstractUnit(
            abstractUnitWrapper.getId());
      abstractUnitRepository.insertSimpleModuleAbstractUnitType(
            mod.getId(), abstractUnitWrapper.getId());
    });

    moduleWrappersProperty.put(mod.getId(), moduleWrapper);
  }

  private void saveCourse(final CourseWrapper courseWrapper) {
    courseRepository.deleteMinor(courseWrapper.getId());
    final Course co = courseWrapper.getCourse();
    courseRepository.updateSimpleCourse(co.getId(), co.getKey(), co.getDegree(),
        co.getShortName(), co.getLongName(), CourseKzfa.toString(courseWrapper.getKzfa()),
          co.getPo(), co.getCreditPoints());
    if (co.isMinor()) {
      co.getMajorCourses().forEach(course ->
          courseRepository.insertMinor(co.getId(), course.getId()));
    } else {
      co.getMinorCourses().forEach(course ->
          courseRepository.insertMinor(course.getId(), co.getId()));
    }
  }

  private void saveNewCourse(final CourseWrapper courseWrapper) {
    final int maxId = courseRepository.getMaxId();
    courseWrapper.setId(maxId + 1);
    courseWrappersProperty.put(courseWrapper.getId(), courseWrapper);
    final Course co = courseWrapper.getCourse();
    courseRepository.insertSimpleCourse(co.getId(), co.getKey(), co.getDegree(), co.getShortName(),
        co.getLongName(), CourseKzfa.toString(courseWrapper.getKzfa()), co.getPo(),
          co.getCreditPoints());
    if (courseWrapper.getCourse().isMajor()) {
      majorCourseWrappersProperty.add(courseWrapper);
      co.getMinorCourses().forEach(course ->
          courseRepository.insertMinor(course.getId(), co.getId()));
    } else {
      minorCourseWrappersProperty.add(courseWrapper);
      co.getMajorCourses().forEach(course ->
          courseRepository.insertMinor(co.getId(), course.getId()));
    }
  }

  private void deleteEntity(final EntityType changedType, final EntityWrapper changedEntity) {
    switch (changedType) {
      case COURSE:
        courseRepository.delete(((CourseWrapper) changedEntity).getCourse());
        courseWrappersProperty.get().values().remove(changedEntity);
        majorCourseWrappersProperty.remove(changedEntity);
        minorCourseWrappersProperty.remove(changedEntity);
        break;
      case LEVEL:
        levelRepository.delete(((LevelWrapper) changedEntity).getLevel());
        levelWrappersProperty.get().values().remove(changedEntity);
        break;
      case MODULE:
        moduleRepository.delete(((ModuleWrapper) changedEntity).getModule());
        moduleWrappersProperty.get().values().remove(changedEntity);
        break;
      case ABSTRACT_UNIT:
        abstractUnitRepository.delete(((AbstractUnitWrapper) changedEntity).getAbstractUnit());
        abstractUnitWrappersProperty.get().values().remove(changedEntity);
        break;
      case UNIT:
        unitRepository.delete(((UnitWrapper) changedEntity).getUnit());
        unitWrappersProperty.get().values().remove(changedEntity);
        break;
      case GROUP:
        groupRepository.delete(((GroupWrapper) changedEntity).getGroup());
        groupWrappersProperty.get().values().remove(changedEntity);
        break;
      case SESSION:
        sessionRepository.delete(((SessionWrapper) changedEntity).getSession());
        sessionWrappersProperty.get().values().remove(changedEntity);
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
      courseWrappersProperty.put(course.getId(), courseWrapper);
      if (course.isMajor()) {
        majorCourseWrappersProperty.add(courseWrapper);
      } else {
        minorCourseWrappersProperty.add(courseWrapper);
      }
    });
    levelRepository.findAll().forEach(level ->
        levelWrappersProperty.put(level.getId(),
            new LevelWrapper(level)));
    moduleRepository.findAll().forEach(module ->
        moduleWrappersProperty.put(module.getId(),
            new ModuleWrapper(module)));
    abstractUnitRepository.findAll().forEach(abstractUnit ->
        abstractUnitWrappersProperty.put(abstractUnit.getId(),
            new AbstractUnitWrapper(abstractUnit)));
    unitRepository.findAll().forEach(unit ->
        unitWrappersProperty.put(unit.getId(),
            new UnitWrapper(unit)));
    groupRepository.findAll().forEach(group ->
        groupWrappersProperty.put(group.getId(),
            new GroupWrapper(group)));
    sessionRepository.findAll().forEach(session ->
        sessionWrappersProperty.put(session.getId(),
            new SessionWrapper(session)));
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
              .map(module -> moduleWrappersProperty.get(module.getId()))
              .collect(Collectors.toSet()));
      abstractUnitWrapper.unitsProperty().addAll(
          abstractUnitWrapper.getAbstractUnit().getUnits().stream()
              .map(unit -> unitWrappersProperty.get(unit.getId())).collect(Collectors.toSet()));
    });
    moduleWrappersProperty.values().forEach(moduleWrapper -> {
      moduleWrapper.abstractUnitsProperty().addAll(
          moduleWrapper.getModule().getAbstractUnits().stream()
              .map(abstractUnit -> abstractUnitWrappersProperty.get(abstractUnit.getId()))
              .collect(Collectors.toSet()));
      moduleWrapper.coursesProperty().addAll(
          moduleWrapper.getModule().getCourses().stream()
              .map(course -> courseWrappersProperty.get(course.getId()))
              .collect(Collectors.toSet()));
      if (moduleWrapper.getModule().getLevel() != null) {
        moduleWrapper.setLevel(levelWrappersProperty.get(
              moduleWrapper.getModule().getLevel().getId()));
      }
    });
    unitWrappersProperty.values().forEach(unitWrapper -> {
      unitWrapper.abstractUnitsProperty().addAll(
          unitWrapper.getUnit().getAbstractUnits().stream()
              .map(abstractUnit -> abstractUnitWrappersProperty.get(abstractUnit.getId()))
              .collect(Collectors.toSet()));
      unitWrapper.groupsProperty().addAll(
          unitWrapper.getUnit().getGroups().stream()
              .map(group -> groupWrappersProperty.get(group.getId()))
              .collect(Collectors.toSet()));
    });
    // add majors and minors to course wrappers
    courseWrappersProperty.values().forEach(courseWrapper -> {
      courseWrapper.majorCourseWrapperProperty().addAll(
          courseWrapper.getCourse().getMajorCourses().stream()
              .map(course -> courseWrappersProperty().get(course.getId()))
              .collect(Collectors.toSet()));
      courseWrapper.minorCourseWrapperProperty().addAll(
          courseWrapper.getCourse().getMinorCourses().stream()
              .map(course -> courseWrappersProperty().get(course.getId()))
              .collect(Collectors.toSet()));
    });
    levelWrappersProperty.values().forEach(levelWrapper -> {
      if (levelWrapper.getLevel().getParent() != null) {
        levelWrapper.setParent(levelWrappersProperty.get(levelWrapper.getLevel().getParent()
            .getId()));
      }
      if (levelWrapper.getLevel().getCourse() != null) {
        levelWrapper.setCourseProperty(courseWrappersProperty().get(levelWrapper.getLevel()
            .getCourse().getId()));
      }
    });
    groupWrappersProperty.values().forEach(groupWrapper -> {
      groupWrapper.setUnit(unitWrappersProperty.get(groupWrapper.getGroup().getUnit().getId()));
      groupWrapper.sessionsProperty().addAll(
          groupWrapper.getGroup().getSessions().stream()
              .map(session -> sessionWrappersProperty.get(session.getId()))
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

  public ObservableMap<Integer, CourseWrapper> getCourseWrappers() {
    return courseWrappersProperty.get();
  }

  public MapProperty<Integer, CourseWrapper> courseWrappersProperty() {
    return courseWrappersProperty;
  }

  public MapProperty<Integer, GroupWrapper> groupWrappersProperty() {
    return groupWrappersProperty;
  }

  public ObservableMap<Integer, GroupWrapper> getGroupWrappers() {
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

  public ObservableMap<Integer, ModuleWrapper> getModuleWrappers() {
    return moduleWrappersProperty.get();
  }

  public MapProperty<Integer, ModuleWrapper> moduleWrappersProperty() {
    return moduleWrappersProperty;
  }

  public ObservableMap<Integer, AbstractUnitWrapper> getAbstractUnitWrappers() {
    return abstractUnitWrappersProperty.get();
  }

  public MapProperty<Integer, AbstractUnitWrapper> abstractUnitWrappersProperty() {
    return abstractUnitWrappersProperty;
  }

  public ObservableMap<Integer, UnitWrapper> getUnitWrappers() {
    return unitWrappersProperty.get();
  }

  public MapProperty<Integer, UnitWrapper> unitWrappersProperty() {
    return unitWrappersProperty;
  }

  public ObservableMap<Integer, SessionWrapper> getSessionWrappers() {
    return sessionWrappersProperty.get();
  }

  public MapProperty<Integer, SessionWrapper> sessionWrappersProperty() {
    return sessionWrappersProperty;
  }

  public EventSource<DataChangeEvent> dataChangeEventSource() {
    return dataChangeEventSource;
  }

  public ObjectProperty<EntityWrapper> draggedEntityProperty() {
    return draggedEntityProperty;
  }
}
