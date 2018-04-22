package de.hhu.stups.plues.dataeditor.ui.components.datavisualization;

import de.hhu.stups.plues.data.entities.AbstractUnit;
import de.hhu.stups.plues.data.entities.Course;
import de.hhu.stups.plues.data.entities.Level;
import de.hhu.stups.plues.data.entities.Module;
import de.hhu.stups.plues.data.entities.Unit;
import de.hhu.stups.plues.dataeditor.ui.database.DataService;
import de.hhu.stups.plues.dataeditor.ui.database.DbService;
import de.hhu.stups.plues.dataeditor.ui.database.events.DataChangeEvent;
import de.hhu.stups.plues.dataeditor.ui.entities.CourseWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.EntityWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.LevelWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.SubRootWrapper;
import de.hhu.stups.plues.dataeditor.ui.layout.Inflater;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.utils.FontAwesomeIconFactory;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import org.controlsfx.control.textfield.CustomTextField;
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
  }

  /**
   * Load the data in {@link #treeTableView} when {@link DbService#storeProperty} has changed.
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
