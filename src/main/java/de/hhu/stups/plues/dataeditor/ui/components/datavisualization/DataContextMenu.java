package de.hhu.stups.plues.dataeditor.ui.components.datavisualization;

import com.google.inject.Inject;

import de.hhu.stups.plues.dataeditor.ui.database.DataService;
import de.hhu.stups.plues.dataeditor.ui.database.events.DataChangeEvent;
import de.hhu.stups.plues.dataeditor.ui.database.events.DataChangeType;
import de.hhu.stups.plues.dataeditor.ui.entities.EntityWrapper;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

import java.util.ResourceBundle;

public class DataContextMenu extends ContextMenu {

  private final ObjectProperty<EntityWrapper> entityWrapperProperty;
  private final ResourceBundle resources;

  private MenuItem menuItemAddEntity;

  /**
   * Initialize the menu items and {@link #entityWrapperProperty}.
   */
  @Inject
  public DataContextMenu(final DataService dataService) {
    resources = ResourceBundle.getBundle("lang.data_context_menu");
    entityWrapperProperty = new SimpleObjectProperty<>();
    entityWrapperProperty.addListener((observable, oldValue, newValue) ->
        initializeContextMenu(newValue));

    final MenuItem menuItemEditEntity = new MenuItem(resources.getString("editEntity"));
    menuItemAddEntity = new MenuItem();
    getItems().addAll(menuItemAddEntity, menuItemEditEntity);

    menuItemAddEntity.setOnAction(event -> dataService.dataChangeEventSource().push(
        new DataChangeEvent(DataChangeType.NEW_ENTITY, entityWrapperProperty.get())));
    menuItemEditEntity.setOnAction(event -> dataService.dataChangeEventSource().push(
        new DataChangeEvent(DataChangeType.CHANGE_ENTITY, entityWrapperProperty.get())));
  }

  private void initializeContextMenu(final EntityWrapper entityWrapper) {
    final String resourceName;
    switch (entityWrapper.getEntityType()) {
      case COURSE:
        resourceName = "addCourse";
        break;
      case LEVEL:
        resourceName = "addLevel";
        break;
      case MODULE:
        resourceName = "addModule";
        break;
      case MODULE_LEVEL:
        resourceName = "addModuleLevel";
        break;
      case ABSTRACT_UNIT:
        resourceName = "addAbstractUnit";
        break;
      case UNIT:
        resourceName = "addUnit";
        break;
      case SESSION:
        resourceName = "addSession";
        break;
      default:
        return;
    }
    menuItemAddEntity.setText(resources.getString(resourceName));
  }

  void setEntityWrapper(final EntityWrapper entityWrapper) {
    entityWrapperProperty.set(entityWrapper);
  }
}
