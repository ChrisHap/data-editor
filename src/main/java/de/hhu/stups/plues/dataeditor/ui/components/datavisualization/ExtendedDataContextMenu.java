package de.hhu.stups.plues.dataeditor.ui.components.datavisualization;

import de.hhu.stups.plues.dataeditor.ui.database.DataService;
import de.hhu.stups.plues.dataeditor.ui.database.events.DataChangeEvent;
import de.hhu.stups.plues.dataeditor.ui.database.events.DataChangeType;
import de.hhu.stups.plues.dataeditor.ui.entities.AbstractUnitWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.CourseWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.EntityWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.GroupWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.LevelWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.ModuleWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.SessionWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.UnitWrapper;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

class ExtendedDataContextMenu extends BasicDataContextMenu {

  private final DataContextMenuType dataContextMenuType;

  /**
   * Extended {@link BasicDataContextMenu} additionally having the option to add entities.
   */
  ExtendedDataContextMenu(final DataService dataService) {
    super(dataService);
    this.dataContextMenuType = DataContextMenuType.NESTED;
    this.entityWrapperProperty.addListener((observable, oldValue, newValue) ->
        initializeContextMenu(newValue));
  }

  ExtendedDataContextMenu(final DataService dataService,
                                 final DataContextMenuType dataContextMenuType) {
    super(dataService);
    this.dataContextMenuType = dataContextMenuType;
    this.entityWrapperProperty.addListener((observable, oldValue, newValue) ->
        initializeContextMenu(newValue));
  }

  private void initializeContextMenu(final EntityWrapper entityWrapper) {
    switch (entityWrapper.getEntityType()) {
      case COURSE:
        addListItem(resources.getString("addCourse"),
            event -> dataService.dataChangeEventSource().push(
                new DataChangeEvent(DataChangeType.NEW_ENTITY,
                    CourseWrapper.createEmptyCourseWrapper())));
        addNestedListItem(resources.getString("addLevel"),
            (event -> dataService.dataChangeEventSource().push(
                new DataChangeEvent(DataChangeType.NEW_ENTITY,
                    LevelWrapper.createEmptyLevelWrapper()))));
        break;
      case LEVEL:
        addListItem(resources.getString("addLevel"),
            event -> dataService.dataChangeEventSource().push(
                new DataChangeEvent(DataChangeType.NEW_ENTITY,
                    LevelWrapper.createEmptyLevelWrapper())));
        addNestedListItem(resources.getString("addModule"),
            (event -> dataService.dataChangeEventSource().push(
                new DataChangeEvent(DataChangeType.NEW_ENTITY,
                    ModuleWrapper.createEmptyModuleWrapper()))));
        break;
      case MODULE:
        addFlatListItem(resources.getString("addModule"),
            (event -> dataService.dataChangeEventSource().push(
                new DataChangeEvent(DataChangeType.NEW_ENTITY,
                    ModuleWrapper.createEmptyModuleWrapper()))));
        addNestedListItem(resources.getString("addAbstractUnit"),
            (event -> dataService.dataChangeEventSource().push(
                new DataChangeEvent(DataChangeType.NEW_ENTITY,
                    AbstractUnitWrapper.createEmptyAbstractUnitWrapper()))));
        break;
      case ABSTRACT_UNIT:
        addFlatListItem(resources.getString("addAbstractUnit"),
            (event -> dataService.dataChangeEventSource().push(
                new DataChangeEvent(DataChangeType.NEW_ENTITY,
                    AbstractUnitWrapper.createEmptyAbstractUnitWrapper()))));
        addNestedListItem(resources.getString("addUnit"),
            (event -> dataService.dataChangeEventSource().push(
                new DataChangeEvent(DataChangeType.NEW_ENTITY,
                    UnitWrapper.createEmptyUnitWrapper()))));
        break;
      case UNIT:
        addFlatListItem(resources.getString("addUnit"),
            (event -> dataService.dataChangeEventSource().push(
                new DataChangeEvent(DataChangeType.NEW_ENTITY,
                    UnitWrapper.createEmptyUnitWrapper()))));
        addNestedListItem(resources.getString("addGroup"),
            (event -> dataService.dataChangeEventSource().push(
                new DataChangeEvent(DataChangeType.NEW_ENTITY,
                    GroupWrapper.createEmptyGroupWrapper()))));
        break;
      case GROUP:
        addFlatListItem(resources.getString("addGroup"),
            (event -> dataService.dataChangeEventSource().push(
                new DataChangeEvent(DataChangeType.NEW_ENTITY,
                    GroupWrapper.createEmptyGroupWrapper()))));
        addNestedListItem(resources.getString("addSession"),
            (event -> dataService.dataChangeEventSource().push(
                new DataChangeEvent(DataChangeType.NEW_ENTITY,
                    SessionWrapper.createEmptySessionWrapper()))));
        break;
      case SESSION:
        addFlatListItem(resources.getString("addSession"),
            (event -> dataService.dataChangeEventSource().push(
                new DataChangeEvent(DataChangeType.NEW_ENTITY,
                    SessionWrapper.createEmptySessionWrapper()))));
        //MenuItem menuItemCopy = new MenuItem(resources.getString("copy"));
        //getItems().add(menuItemCopy);
        break;
      default:
    }
  }

  /**
   * Additional list entry to add a new entity if {@link DataContextMenuType#FLAT}
   * is set.
   */
  private void addFlatListItem(final String text, final EventHandler<ActionEvent> event) {
    if (dataContextMenuType != null && !dataContextMenuType.isFlat()) {
      return;
    }
    addListItem(text, event);
  }

  /**
   * Additional list entry to add a new parent entity if {@link DataContextMenuType#NESTED}
   * is set.
   */
  private void addNestedListItem(final String text, final EventHandler<ActionEvent> event) {
    if (dataContextMenuType != null && dataContextMenuType.isFlat()) {
      return;
    }
    addListItem(text, event);
  }
}
