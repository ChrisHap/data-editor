package de.hhu.stups.plues.dataeditor.ui.components.datavisualization;

import de.hhu.stups.plues.dataeditor.ui.database.DataService;
import de.hhu.stups.plues.dataeditor.ui.database.events.DataChangeEvent;
import de.hhu.stups.plues.dataeditor.ui.database.events.DataChangeType;
import de.hhu.stups.plues.dataeditor.ui.entities.EntityWrapper;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

import java.util.Optional;
import java.util.ResourceBundle;

class BasicDataContextMenu extends ContextMenu {

  final ResourceBundle resources;
  final ObjectProperty<EntityWrapper> entityWrapperProperty;
  final DataService dataService;

  /**
   * A basic context menu to delete or edit an entity with database connection.
   */
  BasicDataContextMenu(final DataService dataService) {
    this.dataService = dataService;
    this.resources = ResourceBundle.getBundle("lang.data_context_menu");
    entityWrapperProperty = new SimpleObjectProperty<>();
    entityWrapperProperty.addListener((observable, oldValue, newValue) ->
        initializeBasicContextMenu(newValue));
  }

  private void initializeBasicContextMenu(final EntityWrapper entityWrapper) {
    getItems().clear();
    addListItem(resources.getString("editEntity"),
        event -> dataService.dataChangeEventSource().push(
            new DataChangeEvent(DataChangeType.CHANGE_ENTITY, entityWrapper)));
    addDeleteListItem(entityWrapper);
  }

  private void addDeleteListItem(final EntityWrapper entityWrapper) {
    final MenuItem deleteItem = new MenuItem(resources.getString("delete"));
    deleteItem.setOnAction(event -> {
      final Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
          resources.getString("deleteConfirmation") + " " + entityWrapper.toString(),
          ButtonType.OK, ButtonType.CANCEL);
      Optional<ButtonType> result = alert.showAndWait();
      if (!result.isPresent() || result.get() == ButtonType.CANCEL) {
        return;
      }
      dataService.dataChangeEventSource().push(
          new DataChangeEvent(DataChangeType.DELETE_ENTITY, entityWrapper));
    });
    getItems().add(deleteItem);
  }

  void addListItem(final String text, final EventHandler<ActionEvent> event) {
    final MenuItem menuItem = new MenuItem(text);
    menuItem.setOnAction(event);
    getItems().add(menuItem);
  }

  void setEntityWrapper(final EntityWrapper entityWrapper) {
    entityWrapperProperty.set(entityWrapper);
  }

}
