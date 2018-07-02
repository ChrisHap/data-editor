package de.hhu.stups.plues.dataeditor.ui.components.datavisualization;

import de.hhu.stups.plues.dataeditor.ui.database.DataService;
import de.hhu.stups.plues.dataeditor.ui.database.events.DataChangeEvent;
import de.hhu.stups.plues.dataeditor.ui.database.events.DataChangeType;
import de.hhu.stups.plues.dataeditor.ui.entities.AbstractUnitWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.CourseWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.EntityType;
import de.hhu.stups.plues.dataeditor.ui.entities.EntityWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.GroupWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.LevelWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.ModuleWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.SessionWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.UnitWrapper;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.ResourceBundle;

@Component
public class DataContextMenu extends ContextMenu {

  private final ObjectProperty<EntityWrapper> entityWrapperProperty;
  private final ResourceBundle resources;
  private DataService dataService;

  /**
   * Initialize the menu items and {@link #entityWrapperProperty}.
   */
  @Autowired
  public DataContextMenu(final DataService dataService) {
    this.dataService = dataService;
    resources = ResourceBundle.getBundle("lang.data_context_menu");
    entityWrapperProperty = new SimpleObjectProperty<>();
    entityWrapperProperty.addListener((observable, oldValue, newValue) ->
        initializeContextMenu(newValue));
  }

  private void initializeContextMenu(final EntityWrapper entityWrapper) {
    getItems().clear();
    final MenuItem menuItemEditEntity = new MenuItem(resources.getString("editEntity"));
    menuItemEditEntity.setOnAction(event -> dataService.dataChangeEventSource().push(
        new DataChangeEvent(DataChangeType.CHANGE_ENTITY, entityWrapper)));
    getItems().add(menuItemEditEntity);
    switch (entityWrapper.getEntityType()) {
      case COURSE:
        MenuItem menuItemAddCourse = new MenuItem(resources.getString("addCourse"));
        menuItemAddCourse.setOnAction(event -> dataService.dataChangeEventSource().push(
              new DataChangeEvent(DataChangeType.NEW_ENTITY,
                    CourseWrapper.createEmptyCourseWrapper(), EntityType.COURSE)
        ));
        getItems().add(menuItemAddCourse);
        MenuItem menuItemAddLevel = new MenuItem(resources.getString("addLevel"));
        menuItemAddLevel.setOnAction(event -> dataService.dataChangeEventSource().push(
              new DataChangeEvent(DataChangeType.NEW_ENTITY,
                    LevelWrapper.createEmptyLevelWrapper(), EntityType.LEVEL, entityWrapper)
        ));
        getItems().add(menuItemAddLevel);
        break;
      case LEVEL:
        MenuItem menuItemAddLevelToLevel = new MenuItem(resources.getString("addLevel"));
        menuItemAddLevelToLevel.setOnAction(event -> dataService.dataChangeEventSource().push(
              new DataChangeEvent(DataChangeType.NEW_ENTITY,
                    LevelWrapper.createEmptyLevelWrapper(), EntityType.LEVEL, entityWrapper)
        ));
        getItems().add(menuItemAddLevelToLevel);
        MenuItem menuItemAddModule = new MenuItem(resources.getString("addModule"));
        menuItemAddModule.setOnAction(event -> dataService.dataChangeEventSource().push(
              new DataChangeEvent(DataChangeType.NEW_ENTITY,
                    ModuleWrapper.createEmptyModuleWrapper(), EntityType.MODULE, entityWrapper)
        ));
        getItems().add(menuItemAddModule);
        break;
      case MODULE:
        MenuItem menuItemAddAbstractUnit = new MenuItem(resources.getString("addAbstractUnit"));
        menuItemAddAbstractUnit.setOnAction(event -> dataService.dataChangeEventSource().push(
            new DataChangeEvent(DataChangeType.NEW_ENTITY,
                  AbstractUnitWrapper.createEmptyAbstractUnitWrapper(),
                  EntityType.ABSTRACT_UNIT, entityWrapper)));
        getItems().add(menuItemAddAbstractUnit);
        break;
      case ABSTRACT_UNIT:
        MenuItem menuItemAddUnit = new MenuItem(resources.getString("addUnit"));
        menuItemAddUnit.setOnAction(event -> dataService.dataChangeEventSource().push(
              new DataChangeEvent(DataChangeType.NEW_ENTITY,
                    UnitWrapper.createEmptyUnitWrapper(), EntityType.UNIT, entityWrapper)
        ));
        getItems().add(menuItemAddUnit);
        break;
      case UNIT:
        MenuItem menuItemAddGroup = new MenuItem(resources.getString("addGroup"));
        menuItemAddGroup.setOnAction(event -> dataService.dataChangeEventSource().push(
              new DataChangeEvent(DataChangeType.NEW_ENTITY,
                    GroupWrapper.createEmptyGroupWrapper(), EntityType.GROUP, entityWrapper)
        ));
        getItems().add(menuItemAddGroup);
        break;
      case GROUP:
        MenuItem menuItemAddSession = new MenuItem(resources.getString("addSession"));
        menuItemAddSession.setOnAction(event -> dataService.dataChangeEventSource().push(
            new DataChangeEvent(DataChangeType.NEW_ENTITY,
                  SessionWrapper.createEmptySessionWrapper(), EntityType.SESSION, entityWrapper)
        ));
        getItems().add(menuItemAddSession);
        break;
      case SESSION:
        MenuItem menuItemCopy = new MenuItem(resources.getString("copy"));
        getItems().add(menuItemCopy);
        break;
      default:
    }
    MenuItem deleteItem = new MenuItem(resources.getString("delete"));
    deleteItem.setOnAction(event -> {
      Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
            resources.getString("deleteConfirmation") + " " + entityWrapper.toString(),
            ButtonType.OK, ButtonType.CANCEL);
      Optional<ButtonType> result = alert.showAndWait();
      if (!result.isPresent() || result.get() == ButtonType.CANCEL) {
        return;
      }
      dataService.dataChangeEventSource().push(
            new DataChangeEvent(DataChangeType.DELETE_ENTITY, entityWrapper,
                  entityWrapper.getEntityType()));
    });
    getItems().add(deleteItem);
  }

  void setEntityWrapper(final EntityWrapper entityWrapper) {
    entityWrapperProperty.set(entityWrapper);
  }
}
