package de.hhu.stups.plues.dataeditor.ui.components.datavisualization;

import de.hhu.stups.plues.dataeditor.ui.database.DataService;
import de.hhu.stups.plues.dataeditor.ui.database.events.DataChangeEvent;
import de.hhu.stups.plues.dataeditor.ui.database.events.DataChangeType;
import de.hhu.stups.plues.dataeditor.ui.entities.EntityType;
import de.hhu.stups.plues.dataeditor.ui.entities.EntityWrapper;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
              new DataChangeEvent(DataChangeType.NEW_ENTITY, null, EntityType.COURSE)
        ));
        getItems().add(menuItemAddCourse);
        MenuItem menuItemAddLevel = new MenuItem(resources.getString("addLevel"));
        menuItemAddLevel.setOnAction(event -> dataService.dataChangeEventSource().push(
              new DataChangeEvent(DataChangeType.NEW_ENTITY, entityWrapper, EntityType.LEVEL)
        ));
        getItems().add(menuItemAddLevel);
        break;
      case LEVEL:
        MenuItem menuItemAddLevelToLevel = new MenuItem(resources.getString("addLevel"));
        menuItemAddLevelToLevel.setOnAction(event -> dataService.dataChangeEventSource().push(
              new DataChangeEvent(DataChangeType.NEW_ENTITY, entityWrapper, EntityType.LEVEL)
        ));
        getItems().add(menuItemAddLevelToLevel);
        MenuItem menuItemAddModule = new MenuItem(resources.getString("addModule"));
        menuItemAddModule.setOnAction(event -> dataService.dataChangeEventSource().push(
              new DataChangeEvent(DataChangeType.NEW_ENTITY, entityWrapper, EntityType.MODULE)
        ));
        getItems().add(menuItemAddModule);
        break;
      case MODULE:
        MenuItem menuItemAddAbstractUnit = new MenuItem(resources.getString("addAbstractUnit"));
        menuItemAddAbstractUnit.setOnAction(event -> dataService.dataChangeEventSource().push(
              new DataChangeEvent(DataChangeType.NEW_ENTITY, entityWrapper,
                    EntityType.ABSTRACT_UNIT)));
        getItems().add(menuItemAddAbstractUnit);
        break;
      case ABSTRACT_UNIT:
        MenuItem menuItemAddUnit = new MenuItem(resources.getString("addUnit"));
        menuItemAddUnit.setOnAction(event -> dataService.dataChangeEventSource().push(
              new DataChangeEvent(DataChangeType.NEW_ENTITY, entityWrapper, EntityType.UNIT)
        ));
        getItems().add(menuItemAddUnit);
        break;
      case UNIT:
        MenuItem menuItemAddGroup = new MenuItem(resources.getString("addGroup"));
        menuItemAddGroup.setOnAction(event -> dataService.dataChangeEventSource().push(
              new DataChangeEvent(DataChangeType.NEW_ENTITY, entityWrapper, EntityType.GROUP)
        ));
        getItems().add(menuItemAddGroup);
        break;
      case GROUP:
        MenuItem menuItemAddSession = new MenuItem(resources.getString("addSession"));
        menuItemAddSession.setOnAction(event -> dataService.dataChangeEventSource().push(
              new DataChangeEvent(DataChangeType.NEW_ENTITY, entityWrapper, EntityType.SESSION)
        ));
        getItems().add(menuItemAddSession);
        break;
      case SESSION:
        MenuItem menuItemCopy = new MenuItem(resources.getString("copy"));
        getItems().add(menuItemCopy);
        break;
      default:
    }
  }

  void setEntityWrapper(final EntityWrapper entityWrapper) {
    entityWrapperProperty.set(entityWrapper);
  }
}
