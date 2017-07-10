package de.hhu.stups.plues.dataeditor.ui.database;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.hhu.stups.plues.data.SqliteStore;
import de.hhu.stups.plues.dataeditor.ui.database.events.DataChangeEvent;
import de.hhu.stups.plues.dataeditor.ui.database.events.DataChangeType;
import de.hhu.stups.plues.dataeditor.ui.entities.AbstractUnitWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.CourseWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.LevelWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.ModuleWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.SessionWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.UnitWrapper;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import org.fxmisc.easybind.EasyBind;
import org.reactfx.EventSource;

import java.util.stream.Collectors;

/**
 * A simple data service providing several map properties to manage the database entity wrapper.
 */
@Singleton
public class DataService {

  private final EventSource<DataChangeEvent> dataChangeEventSource;
  private final MapProperty<String, CourseWrapper> courseWrappersProperty;
  private final MapProperty<String, LevelWrapper> levelWrappersProperty;
  private final MapProperty<String, ModuleWrapper> moduleWrappersProperty;
  private final MapProperty<String, AbstractUnitWrapper> abstractUnitWrappersProperty;
  private final MapProperty<String, UnitWrapper> unitWrappersProperty;
  private final MapProperty<String, SessionWrapper> sessionWrappersProperty;

  /**
   * Initialize the map properties to store and manage the database entity wrapper and subscribe to
   * {@link DbService}.
   */
  @Inject
  public DataService(final DbService dbService) {
    courseWrappersProperty = new SimpleMapProperty<>(FXCollections.observableHashMap());
    levelWrappersProperty = new SimpleMapProperty<>(FXCollections.observableHashMap());
    moduleWrappersProperty = new SimpleMapProperty<>(FXCollections.observableHashMap());
    abstractUnitWrappersProperty = new SimpleMapProperty<>(FXCollections.observableHashMap());
    unitWrappersProperty = new SimpleMapProperty<>(FXCollections.observableHashMap());
    sessionWrappersProperty = new SimpleMapProperty<>(FXCollections.observableHashMap());
    dataChangeEventSource = new EventSource<>();

    EasyBind.subscribe(dbService.storeProperty(), this::loadData);
  }

  private void loadData(final SqliteStore sqliteStore) {
    if (sqliteStore == null) {
      return;
    }
    clear();
    initializeEntitiesFlat(sqliteStore);
    initializeEntitiesNested();
    dataChangeEventSource.push(new DataChangeEvent(DataChangeType.RELOAD_DB));
  }

  /**
   * Initialize all map properties on the first level.
   */
  private void initializeEntitiesFlat(final SqliteStore sqliteStore) {
    sqliteStore.getCourses().forEach(course ->
        courseWrappersProperty.put(course.getKey(), new CourseWrapper(course)));
    sqliteStore.getLevels().forEach(level ->
        levelWrappersProperty.put(level.getId().toString(), new LevelWrapper(level)));
    sqliteStore.getModules().forEach(module ->
        moduleWrappersProperty.put(module.getKey(), new ModuleWrapper(module)));
    sqliteStore.getAbstractUnits().forEach(abstractUnit ->
        abstractUnitWrappersProperty.put(abstractUnit.getKey(),
            new AbstractUnitWrapper(abstractUnit)));
    sqliteStore.getUnits().forEach(unit ->
        unitWrappersProperty.put(unit.getKey(), new UnitWrapper(unit)));
    sqliteStore.getSessions().forEach(session ->
        sessionWrappersProperty.put(String.valueOf(session.getId()), new SessionWrapper(session)));
  }

  /**
   * Initialize the nested {@link de.hhu.stups.plues.dataeditor.ui.entities.EntityWrapper} after
   * calling {@link #initializeEntitiesFlat(SqliteStore)} since we need to use the wrapper defined
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
    unitWrappersProperty.values().forEach(unitWrapper ->
        unitWrapper.abstractUnitsProperty().addAll(
            unitWrapper.getUnit().getAbstractUnits().stream()
                .map(abstractUnit -> abstractUnitWrappersProperty.get(abstractUnit.getKey()))
                .collect(Collectors.toSet())));
  }

  private void clear() {
    courseWrappersProperty.clear();
    levelWrappersProperty.clear();
    moduleWrappersProperty.clear();
    abstractUnitWrappersProperty.clear();
    unitWrappersProperty.clear();
    sessionWrappersProperty.clear();
  }

  public ObservableMap<String, CourseWrapper> getCourseWrappers() {
    return courseWrappersProperty.get();
  }

  public MapProperty<String, CourseWrapper> courseWrappersProperty() {
    return courseWrappersProperty;
  }

  public ObservableMap<String, LevelWrapper> getLevelWrappers() {
    return levelWrappersProperty.get();
  }

  public MapProperty<String, LevelWrapper> levelWrappersProperty() {
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
