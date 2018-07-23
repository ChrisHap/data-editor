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
import java.util.List;
import java.util.ResourceBundle;

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
            new SubRootWrapper(resources.getString("minors")));
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
    TreeItem<EntityWrapper> child = getTreeItemForEntityWrapperRecursive(wrapper,
          treeTableRoot.getChildren());
    while (child != null) {
      child.getParent().getChildren().remove(child);
      child = getTreeItemForEntityWrapperRecursive(wrapper,
            treeTableRoot.getChildren());
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

  private void insertNewCourse(CourseWrapper newEntity) {
    TreeItem<EntityWrapper> newCourse = new TreeItem<>(newEntity);
    treeTableRoot.getChildren().add(newCourse);
    if (newEntity.getCourse().isMinor()) {
      newEntity.getMajorCourseWrappers().forEach(courseWrapper -> {
        TreeItem<EntityWrapper> majorCourseTreeItem =
            getTreeItemForEntityWrapperRecursive(courseWrapper, treeTableRoot.getChildren());
        if (majorCourseTreeItem != null) {
          majorCourseTreeItem.getChildren().forEach(item -> {
            if (item.getValue().getEntityType() == null) {
              item.getChildren().add(new TreeItem<>(newEntity));
            }
          });
        }
      });
    }
  }

  private void insertNewLevel(LevelWrapper newEntity) {
    if (newEntity.getParent() == null) {
      CourseWrapper parentCourseWrapper = newEntity.getCourseWrapper();
      TreeItem<EntityWrapper> courseTreeItem =
          getTreeItemForEntityWrapperRecursive(parentCourseWrapper,
              treeTableRoot.getChildren());
      if (courseTreeItem != null) {
        courseTreeItem.getChildren().add(new TreeItem<>(newEntity));
      }
    } else {
      TreeItem<EntityWrapper> levelTreeItem =
          getTreeItemForEntityWrapperRecursive(newEntity.getParent(),
              treeTableRoot.getChildren());
      if (levelTreeItem != null) {
        levelTreeItem.getChildren().add(new TreeItem<>(newEntity));
      }
    }
  }

  private void insertNewModule(ModuleWrapper newEntity) {
    newEntity.getModule().getModuleLevels().forEach(ml -> {
      LevelWrapper lv = new LevelWrapper(ml.getLevel());
      TreeItem<EntityWrapper> levelTreeItem =
          getTreeItemForEntityWrapperRecursive(lv, treeTableRoot.getChildren());
      if (levelTreeItem != null) {
        levelTreeItem.getChildren().add(new TreeItem<>(newEntity));
      }
    });
  }

  private void insertNewAbstractUnit(AbstractUnitWrapper newEntity) {
    newEntity.getModules().forEach(mw -> {
      TreeItem<EntityWrapper> mwTreeItem =
          getTreeItemForEntityWrapperRecursive(mw, treeTableRoot.getChildren());
      if (mwTreeItem != null) {
        mwTreeItem.getChildren().add(new TreeItem<>(newEntity));
      }
    });
  }

  private void insertNewUnit(UnitWrapper newEntity) {
    newEntity.getAbstractUnits().forEach(auw -> {
      TreeItem<EntityWrapper> auTreeItem =
          getTreeItemForEntityWrapperRecursive(auw, treeTableRoot.getChildren());
      if (auTreeItem != null) {
        auTreeItem.getChildren().add(new TreeItem<>(newEntity));
      }
    });
  }

  private void insertNewGroup(GroupWrapper newEntity) {
    UnitWrapper unitWrapper = newEntity.getUnit();
    TreeItem<EntityWrapper> unitTreeItem =
        getTreeItemForEntityWrapperRecursive(unitWrapper, treeTableRoot.getChildren());
    if (unitTreeItem != null) {
      unitTreeItem.getChildren().add(new TreeItem<>(newEntity));
    }
  }

  private void insertNewSession(SessionWrapper newEntity) {
    GroupWrapper parent = new GroupWrapper(newEntity.getSession().getGroup());
    TreeItem<EntityWrapper> parentTreeItem =
        getTreeItemForEntityWrapperRecursive(parent, treeTableRoot.getChildren());
    if (parentTreeItem != null) {
      parentTreeItem.getChildren().add(new TreeItem<>(newEntity));
    }
  }

  private TreeItem<EntityWrapper> getTreeItemForEntityWrapperRecursive(
      EntityWrapper entityWrapper, List<TreeItem<EntityWrapper>> nodes) {
    for (TreeItem<EntityWrapper> node : nodes) {
      if (node.getValue().equals(entityWrapper)) {
        return node;
      }
      TreeItem<EntityWrapper> rec =
          getTreeItemForEntityWrapperRecursive(entityWrapper, node.getChildren());
      if (rec != null) {
        return rec;
      }
    }
    return null;
  }

  private void reloadData() {
    dataService.courseWrappersProperty().values().stream().filter(
        courseWrapper -> courseWrapper.getCourse().isMajor()).forEach(this::addCourseToTreeView);
    dataService.courseWrappersProperty().values().stream().filter(
        courseWrapper -> courseWrapper.getCourse().isMinor()).forEach(this::addCourseToTreeView);
  }

  private void updateSingleEntity(final EntityWrapper changedEntity) {
    TreeItem<EntityWrapper> current = getTreeItemForEntityWrapperRecursive(changedEntity,
          treeTableRoot.getChildren());
    TreeItem<EntityWrapper> child = current;
    while (child != null) {
      if (child.getChildren().size() > current.getChildren().size()) {
        current = child;
      }
      child.getParent().getChildren().remove(child);
      child = getTreeItemForEntityWrapperRecursive(changedEntity,
            treeTableRoot.getChildren());
    }
    final TreeItem<EntityWrapper> bestChild = current;
    switch (changedEntity.getEntityType()) {
      case COURSE:
        addSimpleCourse((CourseWrapper)changedEntity, bestChild);
        break;
      case LEVEL:
        addSimpleLevel((LevelWrapper) changedEntity, bestChild);
        break;
      case MODULE:
        getTreeItemForEntityWrapperRecursive(dataService.getLevelWrappers().get(
              ((ModuleWrapper)changedEntity).getLevel().getId()),
              treeTableRoot.getChildren()).getChildren().add(bestChild);
        break;
      case ABSTRACT_UNIT:
        ((AbstractUnitWrapper)changedEntity).getModules().forEach( moduleWrapper ->
              getTreeItemForEntityWrapperRecursive(moduleWrapper,
              treeTableRoot.getChildren()).getChildren().add(bestChild));
        break;
      case UNIT:
        ((UnitWrapper)changedEntity).getAbstractUnits().forEach( abstractUnitWrapper ->
              getTreeItemForEntityWrapperRecursive(abstractUnitWrapper,
              treeTableRoot.getChildren()).getChildren().add(bestChild));
        break;
      case SESSION:
        getTreeItemForEntityWrapperRecursive(dataService.getGroupWrappers().get(
              String.valueOf(((SessionWrapper)changedEntity).getGroup().getId())),
              treeTableRoot.getChildren()).getChildren().add(bestChild);
        break;
      case GROUP:
        TreeItem<EntityWrapper> parent =  getTreeItemForEntityWrapperRecursive(
              dataService.getGroupWrappers().get(String.valueOf(
                    ((GroupWrapper)changedEntity).getUnit().getId())),
              treeTableRoot.getChildren());
        if (parent != null) {
          parent.getChildren().add(bestChild);
        }
        break;
      default:
        break;
    }
  }

  private void addSimpleCourse(CourseWrapper wrapper, TreeItem<EntityWrapper> bestChild) {
    treeTableRoot.getChildren().add(bestChild);
    wrapper.getMajorCourseWrappers().forEach(courseWrapper -> {
      TreeItem<EntityWrapper> parentCourse = getTreeItemForEntityWrapperRecursive(courseWrapper,
            treeTableRoot.getChildren());
      if (parentCourse != null) {
        parentCourse.getChildren().forEach(courseChild -> {
          if (courseChild.getValue().getEntityType() == null) {
            courseChild.getChildren().add(new TreeItem<>(wrapper));
          }
        });
      }
    });
  }

  private void addSimpleLevel(LevelWrapper wrapper, TreeItem bestChild) {
    if (wrapper.getParent() == null) {
      getTreeItemForEntityWrapperRecursive(wrapper.getCourseWrapper(),
            treeTableRoot.getChildren()).getChildren().add(bestChild);
    } else {
      getTreeItemForEntityWrapperRecursive(wrapper.getParent(),
            treeTableRoot.getChildren()).getChildren().add(bestChild);
    }
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
