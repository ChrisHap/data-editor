package de.hhu.stups.plues.dataeditor.ui.components.datavisualization;

import de.hhu.stups.plues.dataeditor.ui.database.DataService;
import de.hhu.stups.plues.dataeditor.ui.database.DbService;
import de.hhu.stups.plues.dataeditor.ui.database.events.DataChangeEvent;
import de.hhu.stups.plues.dataeditor.ui.entities.*;
import de.hhu.stups.plues.dataeditor.ui.layout.Inflater;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.utils.FontAwesomeIconFactory;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import org.controlsfx.control.textfield.CustomTextField;
import org.fxmisc.easybind.EasyBind;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class DataTreeView extends VBox implements Initializable {

  private final DataService dataService;
  private final DataContextMenu dataContextMenu;

  private ResourceBundle resources;

  @FXML
  @SuppressWarnings("unused")
  private CustomTextField txtQuery;
  @FXML
  @SuppressWarnings("unused")
  private TreeTableView<EntityWrapper> treeTableView;
  @FXML
  @SuppressWarnings("unused")
  private TreeTableColumn<EntityWrapper, String> treeTableColumnName;

  private TreeItem<EntityWrapper> treeTableRoot;

  /**
   * Initialize the {@link DataService} and context menu provider.
   */
  @Autowired
  public DataTreeView(final Inflater inflater,
                      final DataService dataService,
                      final DataContextMenu dataContextMenu) {
    this.dataService = dataService;
    this.dataContextMenu = dataContextMenu;
    inflater.inflate("components/datavisualization/data_tree_view", this, this, "data_view");
  }

  @Override
  public void initialize(final URL location, final ResourceBundle resources) {
    this.resources = resources;
    treeTableRoot = new TreeItem<>();
    treeTableView.setRoot(treeTableRoot);
    treeTableColumnName.setCellValueFactory(param -> {
      if (param.getValue() == null) {
        return new SimpleStringProperty("");
      }
      return new SimpleStringProperty(param.getValue().getValue().toString());
    });
    txtQuery.setLeft(FontAwesomeIconFactory.get().createIcon(FontAwesomeIcon.SEARCH, "12"));
    dataService.dataChangeEventSource().subscribe(this::updateDataTree);
    treeTableView.prefWidthProperty().bind(widthProperty());
    txtQuery.prefWidthProperty().bind(widthProperty());
    treeTableView.setOnMouseClicked(event -> {
      dataContextMenu.hide();
      final TreeItem<EntityWrapper> selectedTreeItem =
          treeTableView.getSelectionModel().getSelectedItem();
      if (MouseButton.SECONDARY.equals(event.getButton()) && selectedTreeItem != null
          && selectedTreeItem.getValue().getEntityType() != null) {
        dataContextMenu.setEntityWrapper(selectedTreeItem.getValue());
        dataContextMenu.show(this, event.getScreenX(), event.getScreenY());
      }
    });
    EasyBind.subscribe(txtQuery.textProperty(),this::filterDataTree);
  }

  private void filterDataTree(String filter) {
    if (filter == null || filter.length() == 0) {
      treeTableView.setRoot(treeTableRoot);
      return;
    }
    TreeItem<EntityWrapper> newTreeTableRoot = new TreeItem<>();
    treeTableRoot.getChildren().forEach(treeItem ->
        addFilteredCourse(filter, (CourseWrapper)treeItem.getValue(), newTreeTableRoot, treeItem));
    treeTableView.setRoot(newTreeTableRoot);
  }

  private boolean addFilteredCourse(String filter, CourseWrapper courseWrapper,
                                    TreeItem parentNode, TreeItem<EntityWrapper> currentNode) {
    SimpleBooleanProperty childAdded = new SimpleBooleanProperty(false);
    TreeItem<EntityWrapper> currentParentNode = new TreeItem<>(courseWrapper);
    currentNode.getChildren().forEach(child -> {
      if (courseWrapper.getKzfa().equals(CourseKzfa.MAJOR)) {
        if (child.getValue().getEntityType() == null) {
          TreeItem<EntityWrapper> minorsSubRoot = new TreeItem<>(
                new SubRootWrapper(resources.getString("minors")));
          child.getChildren().forEach(minor ->
              childAdded.set(addFilteredCourse(filter,
                      ((CourseWrapper)minor.getValue()), minorsSubRoot, minor)
                  || childAdded.get()));
          if (childAdded.get()) {
            currentParentNode.getChildren().add(minorsSubRoot);
          }
        } else {
          childAdded.set(addFilteredLevel(filter,
                    ((LevelWrapper)child.getValue()),currentParentNode, child)
              || childAdded.get());
        }
      }
    });
    if (childAdded.get()) {
      parentNode.getChildren().add(currentParentNode);
      return true;
    }
    if (courseWrapper.getLongName().contains(filter)
          || courseWrapper.getShortName().contains(filter)
          || courseWrapper.getKey().contains(filter)) {
      parentNode.getChildren().add(currentParentNode);
      return true;
    }
    return false;
  }

  private boolean addFilteredLevel(String filter, LevelWrapper levelWrapper,
                                   TreeItem parentNode, TreeItem<EntityWrapper> currentNode) {
    SimpleBooleanProperty childAdded = new SimpleBooleanProperty(false);
    TreeItem<EntityWrapper> currentParentNode = new TreeItem<>(levelWrapper);
    currentNode.getChildren().forEach(child -> {
      if (child.getValue().getEntityType() == EntityType.LEVEL) {
        childAdded.set(addFilteredLevel(filter,
              ((LevelWrapper) child.getValue()), currentParentNode, child)
              || childAdded.get());
      } else {
        childAdded.set(addFilteredModule(filter,
              ((ModuleWrapper) child.getValue()), currentParentNode, child)
              || childAdded.get());
      }
    });
    if (childAdded.get()) {
      parentNode.getChildren().add(currentParentNode);
      return true;
    }
    if (levelWrapper.getName().contains(filter)) {
      parentNode.getChildren().add(currentParentNode);
      return true;
    }
    return false;
  }

  private boolean addFilteredModule(String filter, ModuleWrapper moduleWrapper,
                                    TreeItem parentNode, TreeItem<EntityWrapper> currentNode) {
    SimpleBooleanProperty childAdded = new SimpleBooleanProperty(false);
    TreeItem<EntityWrapper> currentParentNode = new TreeItem<>(moduleWrapper);
    currentNode.getChildren().forEach(child -> childAdded.set(addFilteredAbstractUnit(filter,
              ((AbstractUnitWrapper) child.getValue()), currentParentNode, child)
              || childAdded.get()));
    if (childAdded.get()) {
      parentNode.getChildren().add(currentParentNode);
      return true;
    }
    if (moduleWrapper.getKey().contains(filter)
          || moduleWrapper.getTitle().contains(filter)) {
      parentNode.getChildren().add(currentParentNode);
      return true;
    }
    return false;
  }

  private boolean addFilteredAbstractUnit(String filter, AbstractUnitWrapper abstractUnitWrapper,
                                         TreeItem parentNode, TreeItem<EntityWrapper> currentNode) {
    SimpleBooleanProperty childAdded = new SimpleBooleanProperty(false);
    TreeItem<EntityWrapper> currentParentNode = new TreeItem<>(abstractUnitWrapper);
    currentNode.getChildren().forEach(child -> childAdded.set(addFilteredUnit(filter,
          ((UnitWrapper) child.getValue()), currentParentNode, child)
          || childAdded.get()));
    if (childAdded.get()) {
      parentNode.getChildren().add(currentParentNode);
      return true;
    }
    if (abstractUnitWrapper.getKey().contains(filter)
          || abstractUnitWrapper.getTitle().contains(filter)) {
      parentNode.getChildren().add(currentParentNode);
      return true;
    }
    return false;
  }

  private boolean addFilteredUnit(String filter, UnitWrapper unitWrapper,
                                 TreeItem parentNode, TreeItem<EntityWrapper> currentNode) {
    SimpleBooleanProperty childAdded = new SimpleBooleanProperty(false);
    TreeItem<EntityWrapper> currentParentNode = new TreeItem<>(unitWrapper);
    currentNode.getChildren().forEach(child -> childAdded.set(addFilteredGroup(filter,
          ((GroupWrapper) child.getValue()), currentParentNode, child)
          || childAdded.get()));
    if (childAdded.get()) {
      parentNode.getChildren().add(currentParentNode);
      return true;
    }
    if (unitWrapper.getKey().contains(filter)
          || unitWrapper.getTitle().contains(filter)) {
      parentNode.getChildren().add(currentParentNode);
      return true;
    }
    return false;
  }

  private boolean addFilteredGroup(String filter, GroupWrapper groupWrapper,
                                   TreeItem parentNode, TreeItem<EntityWrapper> currentNode) {
    SimpleBooleanProperty childAdded = new SimpleBooleanProperty(false);
    TreeItem<EntityWrapper> currentParentNode = new TreeItem<>(groupWrapper);
    currentNode.getChildren().forEach(child -> childAdded.set(addFilteredSession(filter,
          ((SessionWrapper) child.getValue()), currentParentNode)
          || childAdded.get()));
    if (childAdded.get()) {
      parentNode.getChildren().add(currentParentNode);
      return true;
    }
    if (String.valueOf(groupWrapper.getId()).equals(filter)) {
      parentNode.getChildren().add(currentParentNode);
      return true;
    }
    return false;
  }

  private boolean addFilteredSession(String filter, SessionWrapper sessionWrapper,
                                   TreeItem parentNode) {
    TreeItem<EntityWrapper> currentParentNode = new TreeItem<>(sessionWrapper);

    if (String.valueOf(sessionWrapper.getId()).equals(filter)) {
      parentNode.getChildren().add(currentParentNode);
      return true;
    }
    return false;
  }

  /**
   * Load the data in {@link #treeTableView} when {@link DbService#dataSourceProperty} has changed.
   */
  private void updateDataTree(final DataChangeEvent dataChangeEvent) {
    switch (dataChangeEvent.getDataChangeType()) {
      case RELOAD_DB:
        treeTableRoot.getChildren().clear();
        reloadData();
        break;
      case CHANGE_ENTITY:
        updateSingleEntity(dataChangeEvent.getChangedEntity());
        break;
      case NEW_ENTITY:
        // TODO:
        break;
      default:
        break;
    }
  }

  private void reloadData() {
    final Map<String, Set<CourseWrapper>> majorMinorCourses =
        dataService.courseWrappersProperty().values().stream().collect(Collectors
            .groupingBy(courseWrapper -> courseWrapper.getCourse().getKzfa(), Collectors.toSet()));
    majorMinorCourses.get("H").forEach(this::addCourseToTreeView);
    majorMinorCourses.get("N").forEach(this::addCourseToTreeView);
  }

  private void updateSingleEntity(final EntityWrapper changedEntity) {
    // TODO: update single entity wrapper, i.e., either add or remove the entity,
    //       the attributes itself are updated via property bindings
  }

  private void addCourseToTreeView(final CourseWrapper courseWrapper) {
    final Course course = courseWrapper.getCourse();
    final TreeItem<EntityWrapper> treeItemCourse = new TreeItem<>(courseWrapper);
    treeTableRoot.getChildren().add(treeItemCourse);
    if (course.isMajor()) {
      final TreeItem<EntityWrapper> minorsSubRoot =
          new TreeItem<>(new SubRootWrapper(resources.getString("minors")));
      treeItemCourse.getChildren().add(minorsSubRoot);
      course.getMinorCourses().forEach(minorCourse ->
          minorsSubRoot.getChildren().add(new TreeItem<>(dataService.courseWrappersProperty()
              .get(minorCourse.getKey()))));
    }
    course.getLevels().forEach(level -> addLevelToTreeItem(treeItemCourse, level));
  }

  /**
   * Load a specific level from the {@link DataService} and add it as a children of the given tree
   * item.
   */
  private void addLevelToTreeItem(final TreeItem<EntityWrapper> treeItemParent,
                                  final Level level) {
    final LevelWrapper levelWrapper = dataService.getLevelWrappers().get(level.getId());
    final TreeItem<EntityWrapper> treeItemLevel = new TreeItem<>(levelWrapper);
    treeItemParent.getChildren().add(treeItemLevel);
    level.getChildren().forEach(subLevel -> addLevelToTreeItem(treeItemLevel, subLevel));
    level.getModules().forEach(module -> addModuleToTreeItem(treeItemLevel, module));
  }

  private void addModuleToTreeItem(final TreeItem<EntityWrapper> treeItemParent,
                                   final Module module) {
    final TreeItem<EntityWrapper> moduleTreeItem =
        new TreeItem<>(dataService.getModuleWrappers().get(module.getKey()));
    treeItemParent.getChildren().add(moduleTreeItem);
    module.getAbstractUnits().forEach(abstractUnit ->
        addAbstractUnitToTreeItem(moduleTreeItem, abstractUnit));
  }

  private void addAbstractUnitToTreeItem(final TreeItem<EntityWrapper> treeItemParent,
                                         final AbstractUnit abstractUnit) {
    final TreeItem<EntityWrapper> abstractUnitTreeItem =
        new TreeItem<>(dataService.getAbstractUnitWrappers().get(abstractUnit.getKey()));
    treeItemParent.getChildren().add(abstractUnitTreeItem);
    abstractUnit.getUnits().forEach(unit -> addUnitToTreeItem(abstractUnitTreeItem, unit));
  }

  private void addUnitToTreeItem(final TreeItem<EntityWrapper> treeItemParent,
                                 final Unit unit) {
    final TreeItem<EntityWrapper> unitTreeItem = new TreeItem<>(dataService.unitWrappersProperty()
        .get(unit.getKey()));
    treeItemParent.getChildren().add(unitTreeItem);
    unit.getGroups().forEach(group -> {
      final TreeItem<EntityWrapper> treeItemGroup = new TreeItem<>(
          dataService.groupWrappersProperty().get(String.valueOf(group.getId())));
      unitTreeItem.getChildren().add(treeItemGroup);
      group.getSessions().forEach(session -> treeItemGroup.getChildren().add(
          new TreeItem<>(dataService.sessionWrappersProperty()
              .get(String.valueOf(session.getId())))));
    });
  }
}
