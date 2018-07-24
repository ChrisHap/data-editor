package de.hhu.stups.plues.dataeditor.ui.components.dataedits;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;

import java.util.ResourceBundle;

class EntityListViewContextMenu extends ContextMenu {

  private final ResourceBundle resources;

  private ListView<?> parent;

  EntityListViewContextMenu() {
    this.resources = ResourceBundle.getBundle("lang.entity_context_menu");
    initializeContextMenu();
  }

  private void initializeContextMenu() {
    getItems().clear();
    final MenuItem menuItemRemove = new MenuItem(resources.getString("remove"));
    menuItemRemove.setOnAction(event -> parent.getItems()
        .remove(parent.getSelectionModel().getSelectedItem()));
    getItems().add(menuItemRemove);
  }

  void setParent(ListView<?> parent) {
    this.parent = parent;
  }
}
