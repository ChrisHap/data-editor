package de.hhu.stups.plues.dataeditor.ui.components;

import de.hhu.stups.plues.dataeditor.ui.components.dataedits.AbstractUnitEdit;
import de.hhu.stups.plues.dataeditor.ui.components.dataedits.CourseEdit;
import de.hhu.stups.plues.dataeditor.ui.components.dataedits.EditViewProvider;
import de.hhu.stups.plues.dataeditor.ui.components.dataedits.GroupEdit;
import de.hhu.stups.plues.dataeditor.ui.components.dataedits.LevelEdit;
import de.hhu.stups.plues.dataeditor.ui.components.dataedits.ModuleEdit;
import de.hhu.stups.plues.dataeditor.ui.components.dataedits.SessionEdit;
import de.hhu.stups.plues.dataeditor.ui.components.dataedits.UnitEdit;
import de.hhu.stups.plues.dataeditor.ui.database.DataService;
import de.hhu.stups.plues.dataeditor.ui.database.DbService;
import de.hhu.stups.plues.dataeditor.ui.database.events.DataChangeEvent;
import de.hhu.stups.plues.dataeditor.ui.database.events.DbEvent;
import de.hhu.stups.plues.dataeditor.ui.database.events.DbEventType;
import de.hhu.stups.plues.dataeditor.ui.entities.AbstractUnitWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.CourseWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.EntityType;
import de.hhu.stups.plues.dataeditor.ui.entities.EntityWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.GroupWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.LevelWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.ModuleWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.SessionWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.UnitWrapper;
import de.hhu.stups.plues.dataeditor.ui.layout.Inflater;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class DataEditView extends TabPane implements Initializable {

  private final DataService dataService;
  private final DbService dbService;
  private final EditViewProvider editViewProvider;

  private ResourceBundle resources;

  /**
   * Inject the {@link DataService}.
   */
  @Autowired
  public DataEditView(final Inflater inflater,
                      final DataService dataService,
                      final DbService dbService,
                      final EditViewProvider editViewProvider) {
    this.dataService = dataService;
    this.dbService = dbService;
    this.editViewProvider = editViewProvider;
    inflater.inflate("components/data_edit_view", this, this, "data_edit_view");
  }

  @Override
  public void initialize(final URL location, final ResourceBundle resources) {
    this.resources = resources;
    dataService.dataChangeEventSource().subscribe(this::openEditTab);
    dbService.dbEventSource().subscribe(this::clearTabsOnDbReload);
  }

  private void clearTabsOnDbReload(final DbEvent dbEvent) {
    if (DbEventType.LOAD_DB.equals(dbEvent.getEventType())) {
      getTabs().clear();
    }
  }

  private void openEditTab(final DataChangeEvent dataChangeEvent) {
    if (dataChangeEvent.getDataChangeType().changeEntity()) {
      addEntityTab(dataChangeEvent.getChangedEntity());
    } else if (dataChangeEvent.getDataChangeType().addEntity()) {
      addEmptyEntityTab(dataChangeEvent.getChangedEntity().getEntityType(),
            dataChangeEvent.getChangedEntity());
    }
  }

  private void addEmptyEntityTab(EntityType newEntityType,
                                 EntityWrapper newEntity) {
    final Tab tab = new Tab();
    switch (newEntityType) {
      case COURSE:
        tab.setText(resources.getString("course"));
        tab.setContent(editViewProvider.getCourseEditView((CourseWrapper)newEntity));
        break;
      case LEVEL:
        tab.setText(resources.getString("level"));
        LevelEdit levelEdit = editViewProvider.getLevelEditView((LevelWrapper)newEntity);
        tab.setContent(levelEdit);
        break;
      case MODULE:
        tab.setText(resources.getString("module"));
        ModuleEdit moduleEdit = editViewProvider.getModuleEditView((ModuleWrapper) newEntity);
        tab.setContent(moduleEdit);
        break;
      case ABSTRACT_UNIT:
        tab.setText(resources.getString("abstract_unit"));
        AbstractUnitEdit abstractUnitEdit = editViewProvider.getAbstractUnitEditView(
              (AbstractUnitWrapper) newEntity);
        tab.setContent(abstractUnitEdit);
        break;
      case UNIT:
        UnitEdit unitEdit = editViewProvider.getUnitEditView((UnitWrapper) newEntity);
        tab.setContent(unitEdit);
        break;
      case GROUP:
        GroupEdit groupEdit = editViewProvider.getGroupEditView((GroupWrapper) newEntity);
        tab.setContent(groupEdit);
        break;
      case SESSION:
        SessionEdit sessionEdit = editViewProvider.getSessionEditView((SessionWrapper) newEntity);
        tab.setContent(sessionEdit);
        break;
      default:
        break;
    }
    getTabs().add(tab);
    getSelectionModel().select(tab);
  }

  private void addEntityTab(final EntityWrapper entityWrapper) {
    final Tab tab = new Tab();
    switch (entityWrapper.getEntityType()) {
      case COURSE:
        tab.setText(resources.getString("course"));
        final CourseEdit courseEdit =
                editViewProvider.getCourseEditView((CourseWrapper) entityWrapper);
        tab.setContent(courseEdit);
        break;
      case LEVEL:
        tab.setText(resources.getString("level"));
        final LevelEdit levelEdit =
                editViewProvider.getLevelEditView((LevelWrapper) entityWrapper);
        tab.setContent(levelEdit);
        break;
      case MODULE:
        tab.setText(resources.getString("module"));
        final ModuleEdit moduleEdit =
                editViewProvider.getModuleEditView((ModuleWrapper) entityWrapper);
        tab.setContent(moduleEdit);
        break;
      case ABSTRACT_UNIT:
        tab.setText(resources.getString("abstract_unit"));
        final AbstractUnitEdit abstractUnitEdit =
                editViewProvider.getAbstractUnitEditView((AbstractUnitWrapper) entityWrapper);
        tab.setContent(abstractUnitEdit);
        break;
      case UNIT:
        tab.setText(resources.getString("unit"));
        final UnitEdit unitEdit =
                editViewProvider.getUnitEditView((UnitWrapper) entityWrapper);
        tab.setContent(unitEdit);
        break;
      case GROUP:
        tab.setText(resources.getString("group"));
        tab.setContent(editViewProvider.getGroupEditView((GroupWrapper) entityWrapper));
        break;
      case SESSION:
        tab.setText(resources.getString("session"));
        tab.setContent(editViewProvider.getSessionEditView((SessionWrapper) entityWrapper));
        break;
      default:
        break;
    }
    getTabs().add(tab);
    getSelectionModel().select(tab);
  }
}
