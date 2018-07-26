package de.hhu.stups.plues.dataeditor.ui.components.datavisualization;

import de.hhu.stups.plues.dataeditor.ui.database.DataService;
import de.hhu.stups.plues.dataeditor.ui.database.DbService;
import de.hhu.stups.plues.dataeditor.ui.database.events.DataChangeEvent;
import de.hhu.stups.plues.dataeditor.ui.entities.AbstractUnit;
import de.hhu.stups.plues.dataeditor.ui.entities.AbstractUnitWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.Course;
import de.hhu.stups.plues.dataeditor.ui.entities.CourseWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.EntityType;
import de.hhu.stups.plues.dataeditor.ui.entities.EntityWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.GroupWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.Level;
import de.hhu.stups.plues.dataeditor.ui.entities.LevelWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.Module;
import de.hhu.stups.plues.dataeditor.ui.entities.ModuleWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.SessionWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.SubRootWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.Unit;
import de.hhu.stups.plues.dataeditor.ui.entities.UnitWrapper;
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
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import org.controlsfx.control.textfield.CustomTextField;
import org.fxmisc.easybind.EasyBind;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@Component
public class DataTreeView extends VBox implements Initializable {

  private static final String MINORS = "minors";
  private final DataService dataService;
  private final ExtendedDataContextMenu dataContextMenu;
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
                      final DataService dataService) {
    this.dataService = dataService;
    this.dataContextMenu = new ExtendedDataContextMenu(dataService);
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
    treeTableView.setOnDragDetected(test -> {
      if (treeTableView.getSelectionModel().getSelectedItem() == null) {
        return;
      }
      EntityWrapper wrapper = treeTableView.getSelectionModel().getSelectedItem().getValue();
      dataService.draggedEntityProperty().set(wrapper);
      Dragboard db = treeTableView.startDragAndDrop(TransferMode.COPY);
      // an empty clipboard, we use the DataService to pass the object
      ClipboardContent clipboardContent = new ClipboardContent();
      clipboardContent.putString("");
      db.setContent(clipboardContent);
    });
    EasyBind.subscribe(txtQuery.textProperty(), this::filterDataTree);
  }

  private void filterDataTree(String filter) {
    if (filter == null || filter.length() == 0) {
      treeTableView.setRoot(treeTableRoot);
      return;
    }
    TreeItem<EntityWrapper> newTreeTableRoot = new TreeItem<>();
    treeTableRoot.getChildren().forEach(treeItem ->
          addFilteredCourse(filter.toLowerCase(), (CourseWrapper) treeItem.getValue(),
                newTreeTableRoot, treeItem));
    treeTableView.setRoot(newTreeTableRoot);
  }

  private boolean addFilteredCourse(final String filter, CourseWrapper courseWrapper,
                                    final TreeItem<EntityWrapper> parentNode,
                                    final TreeItem<EntityWrapper> currentNode) {
    final SimpleBooleanProperty childAdded = new SimpleBooleanProperty(false);
    final TreeItem<EntityWrapper> currentParentNode = new TreeItem<>(courseWrapper);
    currentNode.getChildren().forEach(child -> {
      if (child.getValue().getEntityType() == null) {
        TreeItem<EntityWrapper> minorsSubRoot = new TreeItem<>(
              new SubRootWrapper(resources.getString(MINORS)));
        child.getChildren().forEach(minor ->
              childAdded.set(addFilteredCourse(filter,
                    ((CourseWrapper) minor.getValue()), minorsSubRoot, minor)
                    || childAdded.get()));
        if (childAdded.get()) {
          currentParentNode.getChildren().add(minorsSubRoot);
        }
      } else {
        childAdded.set(addFilteredLevel(filter,
              ((LevelWrapper) child.getValue()), currentParentNode, child)
              || childAdded.get());
      }
    });
    if (childAdded.get()) {
      parentNode.getChildren().add(currentParentNode);
      return true;
    }
    if (courseWrapper.getLongName().toLowerCase().contains(filter)
          || courseWrapper.getShortName().toLowerCase().contains(filter)
          || courseWrapper.getKey().toLowerCase().contains(filter)) {
      parentNode.getChildren().add(currentParentNode);
      return true;
    }
    return false;
  }

  private boolean addFilteredLevel(final String filter, LevelWrapper levelWrapper,
                                   final TreeItem<EntityWrapper> parentNode,
                                   final TreeItem<EntityWrapper> currentNode) {
    final SimpleBooleanProperty childAdded = new SimpleBooleanProperty(false);
    final TreeItem<EntityWrapper> currentParentNode = new TreeItem<>(levelWrapper);
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
    if (levelWrapper.getName().toLowerCase().contains(filter)) {
      parentNode.getChildren().add(currentParentNode);
      return true;
    }
    return false;
  }

  private boolean addFilteredModule(final String filter, ModuleWrapper moduleWrapper,
                                    final TreeItem<EntityWrapper> parentNode,
                                    final TreeItem<EntityWrapper> currentNode) {
    final SimpleBooleanProperty childAdded = new SimpleBooleanProperty(false);
    final TreeItem<EntityWrapper> currentParentNode = new TreeItem<>(moduleWrapper);
    currentNode.getChildren().forEach(child -> childAdded.set(addFilteredAbstractUnit(filter,
          ((AbstractUnitWrapper) child.getValue()), currentParentNode, child)
          || childAdded.get()));
    if (childAdded.get()) {
      parentNode.getChildren().add(currentParentNode);
      return true;
    }
    if (moduleWrapper.getKey().toLowerCase().contains(filter)
          || moduleWrapper.getTitle().toLowerCase().contains(filter)) {
      parentNode.getChildren().add(currentParentNode);
      return true;
    }
    return false;
  }

  private boolean addFilteredAbstractUnit(final String filter,
                                          final AbstractUnitWrapper abstractUnitWrapper,
                                          final TreeItem<EntityWrapper> parentNode,
                                          final TreeItem<EntityWrapper> currentNode) {
    final SimpleBooleanProperty childAdded = new SimpleBooleanProperty(false);
    final TreeItem<EntityWrapper> currentParentNode = new TreeItem<>(abstractUnitWrapper);
    currentNode.getChildren().forEach(child -> childAdded.set(addFilteredUnit(filter,
          ((UnitWrapper) child.getValue()), currentParentNode, child)
          || childAdded.get()));
    if (childAdded.get()) {
      parentNode.getChildren().add(currentParentNode);
      return true;
    }
    if (abstractUnitWrapper.getKey().toLowerCase().contains(filter)
          || abstractUnitWrapper.getTitle().toLowerCase().contains(filter)) {
      parentNode.getChildren().add(currentParentNode);
      return true;
    }
    return false;
  }

  private boolean addFilteredUnit(final String filter, UnitWrapper unitWrapper,
                                  final TreeItem<EntityWrapper> parentNode,
                                  final TreeItem<EntityWrapper> currentNode) {
    final SimpleBooleanProperty childAdded = new SimpleBooleanProperty(false);
    final TreeItem<EntityWrapper> currentParentNode = new TreeItem<>(unitWrapper);
    currentNode.getChildren().forEach(child -> childAdded.set(addFilteredGroup(filter,
          ((GroupWrapper) child.getValue()), currentParentNode, child)
          || childAdded.get()));
    if (childAdded.get()) {
      parentNode.getChildren().add(currentParentNode);
      return true;
    }
    if (unitWrapper.getKey().toLowerCase().contains(filter)
          || unitWrapper.getTitle().toLowerCase().contains(filter)) {
      parentNode.getChildren().add(currentParentNode);
      return true;
    }
    return false;
  }

  private boolean addFilteredGroup(final String filter, GroupWrapper groupWrapper,
                                   final TreeItem<EntityWrapper> parentNode,
                                   final TreeItem<EntityWrapper> currentNode) {
    final SimpleBooleanProperty childAdded = new SimpleBooleanProperty(false);
    final TreeItem<EntityWrapper> currentParentNode = new TreeItem<>(groupWrapper);
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

  private boolean addFilteredSession(final String filter, final SessionWrapper sessionWrapper,
                                     final TreeItem<EntityWrapper> parentNode) {
    final TreeItem<EntityWrapper> currentParentNode = new TreeItem<>(sessionWrapper);

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
      case STORE_ENTITY:
        updateSingleEntity(dataChangeEvent.getChangedEntity());
        break;
      case INSERT_NEW_ENTITY:
        insertNewEntity(dataChangeEvent.getChangedEntity());
        break;
      case DELETE_ENTITY:
        deleteEntity(dataChangeEvent.getChangedEntity());
        break;
      default:
        break;
    }
  }

  private void deleteEntity(EntityWrapper wrapper) {
    List<TreeItem<EntityWrapper>> list = getTreeItemForEntityWrapperRecursive(wrapper);
    for (TreeItem<EntityWrapper> child : list) {
      if (child.getParent() != null && child.getParent().getChildren() != null) {
        child.getParent().getChildren().remove(child);
      }
    }
  }

  private void insertNewEntity(EntityWrapper newEntity) {
    switch (newEntity.getEntityType()) {
      case COURSE:
        insertNewCourse((CourseWrapper) newEntity);
        break;
      case LEVEL:
        insertNewLevel((LevelWrapper) newEntity);
        break;
      case MODULE:
        insertNewModule((ModuleWrapper) newEntity);
        break;
      case ABSTRACT_UNIT:
        insertNewAbstractUnit((AbstractUnitWrapper) newEntity);
        break;
      case UNIT:
        insertNewUnit((UnitWrapper) newEntity);
        break;
      case GROUP:
        insertNewGroup((GroupWrapper) newEntity);
        break;
      case SESSION:
        insertNewSession((SessionWrapper) newEntity);
        break;
      default:
    }
  }

  private void insertNewLevel(LevelWrapper newEntity) {
    if (newEntity.getParent() == null) {
      CourseWrapper parentCourseWrapper = newEntity.getCourseWrapper();
      List<TreeItem<EntityWrapper>> courseTreeItemList =
            getTreeItemForEntityWrapperRecursive(parentCourseWrapper);
      for (TreeItem<EntityWrapper> courseTreeItem : courseTreeItemList) {
        if (courseTreeItem.getParent() == treeTableRoot) {
          courseTreeItem.getChildren().add(new TreeItem<>(newEntity));
        }
      }
    } else {
      List<TreeItem<EntityWrapper>> levelTreeItemList =
            getTreeItemForEntityWrapperRecursive(newEntity.getParent());
      for (TreeItem<EntityWrapper> levelTreeItem : levelTreeItemList) {
        levelTreeItem.getChildren().add(new TreeItem<>(newEntity));
      }
    }
  }

  private void insertNewModule(ModuleWrapper newEntity) {
    LevelWrapper lv = newEntity.getLevel();
    TreeItem<EntityWrapper> moduleTreeItem = new TreeItem<>(newEntity);
    List<TreeItem<EntityWrapper>> levelTreeItemList =
            getTreeItemForEntityWrapperRecursive(lv);
    for (TreeItem<EntityWrapper> levelTreeItem : levelTreeItemList) {
      levelTreeItem.getChildren().add(moduleTreeItem);
    }
    addAbstractUnitsToModule(newEntity, moduleTreeItem);
  }

  private void insertNewAbstractUnit(AbstractUnitWrapper newEntity) {
    newEntity.getModules().forEach(mw -> {
      List<TreeItem<EntityWrapper>> mwTreeItemList =
            getTreeItemForEntityWrapperRecursive(mw);
      for (TreeItem<EntityWrapper> mwTreeItem : mwTreeItemList) {
        mwTreeItem.getChildren().add(new TreeItem<>(newEntity));
      }
    });
  }

  private void insertNewUnit(UnitWrapper newEntity) {
    newEntity.getAbstractUnits().forEach(auw -> {
      List<TreeItem<EntityWrapper>> auTreeItemList =
            getTreeItemForEntityWrapperRecursive(auw);
      for (TreeItem<EntityWrapper> auTreeItem : auTreeItemList) {
        auTreeItem.getChildren().add(new TreeItem<>(newEntity));
      }
    });
  }

  private void insertNewGroup(GroupWrapper newEntity) {
    UnitWrapper unitWrapper = newEntity.getUnit();
    List<TreeItem<EntityWrapper>> unitTreeItemList =
          getTreeItemForEntityWrapperRecursive(unitWrapper);
    for (TreeItem<EntityWrapper> unitTreeItem : unitTreeItemList) {
      unitTreeItem.getChildren().add(new TreeItem<>(newEntity));
    }
  }

  private void insertNewSession(SessionWrapper newEntity) {
    GroupWrapper parent = new GroupWrapper(newEntity.getSession().getGroup());
    List<TreeItem<EntityWrapper>> parentTreeItemList =
          getTreeItemForEntityWrapperRecursive(parent);
    for (TreeItem<EntityWrapper> parentTreeItem : parentTreeItemList) {
      parentTreeItem.getChildren().add(new TreeItem<>(newEntity));
    }
  }

  private List<TreeItem<EntityWrapper>> getTreeItemForEntityWrapperRecursive(
        final EntityWrapper entityWrapper) {
    return getTreeItemForEntityWrapperRecursive(entityWrapper, treeTableRoot.getChildren());
  }

  private List<TreeItem<EntityWrapper>> getTreeItemForEntityWrapperRecursive(
        final EntityWrapper entityWrapper,
        final List<TreeItem<EntityWrapper>> nodes,
        List<TreeItem<EntityWrapper>> result) {

    if (result == null) {
      return new ArrayList<>();
    }

    for (final TreeItem<EntityWrapper> node : nodes) {
      if (node != null) {
        if (node.getValue().equals(entityWrapper)) {
          result.add(node);
        }
        result = getTreeItemForEntityWrapperRecursive(entityWrapper, node.getChildren(), result);

      }
    }
    return result;
  }

  private List<TreeItem<EntityWrapper>> getTreeItemForEntityWrapperRecursive(
        final EntityWrapper entityWrapper,
        final List<TreeItem<EntityWrapper>> nodes) {
    return getTreeItemForEntityWrapperRecursive(entityWrapper, nodes,
          new ArrayList<>());
  }

  private void reloadData() {
    dataService.courseWrappersProperty().values().stream().filter(
        courseWrapper -> courseWrapper.getCourse().isMajor()).forEach(this::addCourseToTreeView);
    dataService.courseWrappersProperty().values().stream().filter(
        courseWrapper -> courseWrapper.getCourse().isMinor()).forEach(this::addCourseToTreeView);
  }

  private void updateSingleEntity(final EntityWrapper changedEntity) {
    List<TreeItem<EntityWrapper>> current = getTreeItemForEntityWrapperRecursive(changedEntity);
    if (current == null || current.isEmpty()) {
      return;
    }
    TreeItem<EntityWrapper> bestChild = current.get(0);
    for (TreeItem<EntityWrapper> child : current) {
      if (child.getChildren().size() > bestChild.getChildren().size()) {
        bestChild = child;
      }
      child.getParent().getChildren().remove(child);
    }
    switch (changedEntity.getEntityType()) {
      case COURSE:
        addSimpleCourse((CourseWrapper) changedEntity, bestChild);
        break;
      case LEVEL:
        addSimpleLevel((LevelWrapper) changedEntity, bestChild);
        break;
      case MODULE:
        addSimpleModule((ModuleWrapper) changedEntity, bestChild);
        break;
      case ABSTRACT_UNIT:
        addSimpleAbstractUnit((AbstractUnitWrapper) changedEntity, bestChild);
        break;
      case UNIT:
        addSimpleUnit((UnitWrapper) changedEntity, bestChild);
        break;
      case SESSION:
        addSimpleSession((SessionWrapper) changedEntity, bestChild);
        break;
      case GROUP:
        addSimpleGroup((GroupWrapper) changedEntity, bestChild);
        break;
      default:
        break;
    }
  }

  private void addSimpleModule(ModuleWrapper moduleWrapper, TreeItem<EntityWrapper> bestChild) {
    List<TreeItem<EntityWrapper>> parentList =
          getTreeItemForEntityWrapperRecursive(dataService.getLevelWrappers().get(
                moduleWrapper.getLevel().getId()));
    for (TreeItem<EntityWrapper> parent : parentList) {
      parent.getChildren().add(bestChild);
    }

    bestChild.getChildren().clear();

    addAbstractUnitsToModule(moduleWrapper, bestChild);
  }

  private void addAbstractUnitsToModule(ModuleWrapper moduleWrapper,
                                        TreeItem<EntityWrapper> moduleTreeItem) {
    moduleWrapper.getAbstractUnits().forEach(auw -> {
      TreeItem<EntityWrapper> auTreeItem =
            getTreeItemWithMostChildren(auw);
      if (auTreeItem != null) {
        auTreeItem.getParent().getChildren().remove(auTreeItem);
      } else {
        auTreeItem = new TreeItem<>(auw);
      }
      moduleTreeItem.getChildren().add(auTreeItem);
    });
  }

  private void addSimpleAbstractUnit(AbstractUnitWrapper abstractUnitWrapper,
                                     TreeItem<EntityWrapper> bestChild) {
    abstractUnitWrapper.getModules().forEach(moduleWrapper -> {
      List<TreeItem<EntityWrapper>> auparentList =
            getTreeItemForEntityWrapperRecursive(moduleWrapper);
      for (TreeItem<EntityWrapper> auparent : auparentList) {
        auparent.getChildren().add(bestChild);
      }
    });
  }

  private void addSimpleUnit(UnitWrapper unitWrapper, TreeItem<EntityWrapper> bestChild) {
    unitWrapper.getAbstractUnits().forEach(abstractUnitWrapper -> {
      List<TreeItem<EntityWrapper>> uparentList =
            getTreeItemForEntityWrapperRecursive(abstractUnitWrapper);
      for (TreeItem<EntityWrapper> uparent : uparentList) {
        uparent.getChildren().add(bestChild);
      }
    });
  }

  private void addSimpleSession(SessionWrapper sessionWrapper, TreeItem<EntityWrapper> bestChild) {
    List<TreeItem<EntityWrapper>> parentList = getTreeItemForEntityWrapperRecursive(
          dataService.getGroupWrappers().get(
                sessionWrapper.getGroup().getId()));
    for (TreeItem<EntityWrapper> parent : parentList) {
      parent.getChildren().add(bestChild);
    }
  }

  private void addSimpleGroup(GroupWrapper groupWrapper, TreeItem<EntityWrapper> bestChild) {
    List<TreeItem<EntityWrapper>> parentList = getTreeItemForEntityWrapperRecursive(
          dataService.getGroupWrappers().get(groupWrapper.getUnit().getId()));
    for (TreeItem<EntityWrapper> parent : parentList) {
      parent.getChildren().add(bestChild);
    }
  }

  private void insertNewCourse(CourseWrapper newEntity) {
    final Course course = newEntity.getCourse();
    final TreeItem<EntityWrapper> treeItemCourse = new TreeItem<>(newEntity);
    treeTableRoot.getChildren().add(treeItemCourse);
    if (course.isMajor()) {
      final TreeItem<EntityWrapper> minorsSubRoot =
            new TreeItem<>(new SubRootWrapper(resources.getString(MINORS)));
      treeItemCourse.getChildren().add(minorsSubRoot);
      course.getMinorCourses().forEach(minorCourse ->
            minorsSubRoot.getChildren().add(new TreeItem<>(dataService.courseWrappersProperty()
                  .get(minorCourse.getId()))));
    } else {
      addCourseNested(newEntity);
    }
  }

  private void addSimpleCourse(final CourseWrapper wrapper,
                               final TreeItem<EntityWrapper> bestChild) {
    treeTableRoot.getChildren().add(bestChild);
    addCourseNested(wrapper);
  }

  private TreeItem<EntityWrapper> getTreeItemWithMostChildren(EntityWrapper wrapper) {
    List<TreeItem<EntityWrapper>> allTreeItems = getTreeItemForEntityWrapperRecursive(wrapper);
    if (allTreeItems.isEmpty()) {
      return null;
    }
    TreeItem<EntityWrapper> bestTreeItem = allTreeItems.get(0);
    for (TreeItem<EntityWrapper> treeItem : allTreeItems) {
      if (treeItem.getChildren().size() > bestTreeItem.getChildren().size()) {
        bestTreeItem = treeItem;
      }
    }
    return bestTreeItem;
  }

  private TreeItem<EntityWrapper> getMinorSubRoot(TreeItem<EntityWrapper> treeItem) {
    for (TreeItem<EntityWrapper> item : treeItem.getChildren()) {
      if (item.getValue().getEntityType() == null) {
        return item;
      }
    }
    return null;
  }


  /**
   * Adds or updates all Major / Minor courses for given CourseWrapper.
   */
  private void addCourseNested(final CourseWrapper wrapper) {

    final TreeItem<EntityWrapper> courseWrapperTree = getTreeItemWithMostChildren(wrapper);
    if (courseWrapperTree == null) {
      return;
    }
    TreeItem<EntityWrapper> minorSubRoot = getMinorSubRoot(courseWrapperTree);
    if (wrapper.getCourse().isMinor()) {
      courseWrapperTree.getChildren().remove(minorSubRoot);

      wrapper.getMajorCourseWrappers().forEach(courseWrapper -> {
        TreeItem<EntityWrapper> parentTreeItem = getTreeItemWithMostChildren(courseWrapper);
        if (parentTreeItem != null) {
          TreeItem<EntityWrapper> parentMinorSubRoot = getMinorSubRoot(parentTreeItem);
          if (parentMinorSubRoot != null) {
            parentMinorSubRoot.getChildren().add(new TreeItem<>(wrapper));
          }
        }
      });
    } else {
      if (minorSubRoot == null) {
        minorSubRoot = new TreeItem<>(new SubRootWrapper(resources.getString(MINORS)));
        courseWrapperTree.getChildren().add(minorSubRoot);
      }
      minorSubRoot.getChildren().clear();
      for (CourseWrapper courseWrapper : wrapper.getMinorCourseWrappers()) {
        minorSubRoot.getChildren().add(new TreeItem<>(courseWrapper));
      }
    }
  }

  private void addSimpleLevel(LevelWrapper wrapper, TreeItem<EntityWrapper> bestChild) {
    if (wrapper.getParent() == null) {
      List<TreeItem<EntityWrapper>> parentList = getTreeItemForEntityWrapperRecursive(
            wrapper.getCourseWrapper());
      for (TreeItem<EntityWrapper> parent : parentList) {
        parent.getChildren().add(bestChild);
      }
    } else {
      List<TreeItem<EntityWrapper>> parentList = getTreeItemForEntityWrapperRecursive(
            wrapper.getParent());
      for (TreeItem<EntityWrapper> parent : parentList) {
        parent.getChildren().add(bestChild);
      }
    }
  }

  private void addCourseToTreeView(final CourseWrapper courseWrapper) {
    final Course course = courseWrapper.getCourse();
    final TreeItem<EntityWrapper> treeItemCourse = new TreeItem<>(courseWrapper);
    treeTableRoot.getChildren().add(treeItemCourse);
    if (course.isMajor()) {
      final TreeItem<EntityWrapper> minorsSubRoot =
            new TreeItem<>(new SubRootWrapper(resources.getString(MINORS)));
      treeItemCourse.getChildren().add(minorsSubRoot);
      course.getMinorCourses().forEach(minorCourse ->
            minorsSubRoot.getChildren().add(new TreeItem<>(dataService.courseWrappersProperty()
                  .get(minorCourse.getId()))));
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
          new TreeItem<>(dataService.getModuleWrappers().get(module.getId()));
    treeItemParent.getChildren().add(moduleTreeItem);
    module.getAbstractUnits().forEach(abstractUnit ->
          addAbstractUnitToTreeItem(moduleTreeItem, abstractUnit));
  }

  private void addAbstractUnitToTreeItem(final TreeItem<EntityWrapper> treeItemParent,
                                         final AbstractUnit abstractUnit) {
    final TreeItem<EntityWrapper> abstractUnitTreeItem =
          new TreeItem<>(dataService.getAbstractUnitWrappers().get(abstractUnit.getId()));
    treeItemParent.getChildren().add(abstractUnitTreeItem);
    abstractUnit.getUnits().forEach(unit -> addUnitToTreeItem(abstractUnitTreeItem, unit));
  }

  private void addUnitToTreeItem(final TreeItem<EntityWrapper> treeItemParent,
                                 final Unit unit) {
    final TreeItem<EntityWrapper> unitTreeItem = new TreeItem<>(dataService.unitWrappersProperty()
          .get(unit.getId()));
    treeItemParent.getChildren().add(unitTreeItem);
    unit.getGroups().forEach(group -> {
      final TreeItem<EntityWrapper> treeItemGroup = new TreeItem<>(
            dataService.groupWrappersProperty().get(group.getId()));
      unitTreeItem.getChildren().add(treeItemGroup);
      group.getSessions().forEach(session -> treeItemGroup.getChildren().add(
            new TreeItem<>(dataService.sessionWrappersProperty()
                  .get(session.getId()))));
    });
  }
}
