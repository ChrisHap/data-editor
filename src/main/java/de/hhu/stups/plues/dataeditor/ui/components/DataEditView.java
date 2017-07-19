package de.hhu.stups.plues.dataeditor.ui.components;

import com.google.inject.Inject;

import de.hhu.stups.plues.dataeditor.ui.components.dataedits.AbstractUnitEdit;
import de.hhu.stups.plues.dataeditor.ui.components.dataedits.CourseEdit;
import de.hhu.stups.plues.dataeditor.ui.components.dataedits.EditFactoryProvider;
import de.hhu.stups.plues.dataeditor.ui.components.dataedits.LevelEdit;
import de.hhu.stups.plues.dataeditor.ui.components.dataedits.ModuleEdit;
import de.hhu.stups.plues.dataeditor.ui.components.dataedits.UnitEdit;
import de.hhu.stups.plues.dataeditor.ui.database.DataService;
import de.hhu.stups.plues.dataeditor.ui.database.DbService;
import de.hhu.stups.plues.dataeditor.ui.database.events.DataChangeEvent;
import de.hhu.stups.plues.dataeditor.ui.database.events.DbEvent;
import de.hhu.stups.plues.dataeditor.ui.database.events.DbEventType;
import de.hhu.stups.plues.dataeditor.ui.entities.AbstractUnitWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.CourseWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.EntityWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.LevelWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.ModuleWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.UnitWrapper;
import de.hhu.stups.plues.dataeditor.ui.layout.Inflater;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.net.URL;
import java.util.ResourceBundle;

public class DataEditView extends TabPane implements Initializable {

  private final DataService dataService;
  private final DbService dbService;
  private final EditFactoryProvider editFactoryProvider;

  private ResourceBundle resources;

  /**
   * Inject the {@link DataService}.
   */
  @Inject
  public DataEditView(final Inflater inflater,
                      final DataService dataService,
                      final DbService dbService,
                      final EditFactoryProvider editFactoryProvider) {
    this.dataService = dataService;
    this.dbService = dbService;
    this.editFactoryProvider = editFactoryProvider;
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
      addEmptyEntityTab(dataChangeEvent.getChangedEntity());
    }
  }

  private void addEmptyEntityTab(final EntityWrapper entityWrapper) {
    final Tab tab = new Tab();
    switch (entityWrapper.getEntityType()) {
      case COURSE:
        tab.setText(resources.getString("course"));
        tab.setContent(editFactoryProvider.getCourseEditFactory().create(null));
        break;
      case LEVEL:
        tab.setText(resources.getString("level"));
        tab.setContent(editFactoryProvider.getLevelEditFactory().create(null));
        break;
      case MODULE:
        tab.setText(resources.getString("module"));
        tab.setContent(editFactoryProvider.getModuleEditFactory().create(null));
        break;
      case ABSTRACT_UNIT:
        tab.setText(resources.getString("abstract_unit"));
        tab.setContent(editFactoryProvider.getAbstractUnitEditFactory().create(null));
        break;
      case UNIT:
        tab.setText(resources.getString("unit"));
        tab.setContent(editFactoryProvider.getUnitEditFactory().create(null));
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
        final CourseEdit courseEdit = editFactoryProvider.getCourseEditFactory()
            .create((CourseWrapper) entityWrapper);
        tab.setContent(courseEdit);
        break;
      case LEVEL:
        tab.setText(resources.getString("level"));
        final LevelEdit levelEdit = editFactoryProvider.getLevelEditFactory()
            .create((LevelWrapper) entityWrapper);
        tab.setContent(levelEdit);
        break;
      case MODULE:
        tab.setText(resources.getString("module"));
        final ModuleEdit moduleEdit = editFactoryProvider.getModuleEditFactory()
            .create((ModuleWrapper) entityWrapper);
        tab.setContent(moduleEdit);
        break;
      case ABSTRACT_UNIT:
        tab.setText(resources.getString("abstract_unit"));
        final AbstractUnitEdit abstractUnitEdit = editFactoryProvider.getAbstractUnitEditFactory()
            .create((AbstractUnitWrapper) entityWrapper);
        tab.setContent(abstractUnitEdit);
        break;
      case UNIT:
        tab.setText(resources.getString("unit"));
        final UnitEdit unitEdit = editFactoryProvider.getUnitEditFactory()
            .create((UnitWrapper) entityWrapper);
        tab.setContent(unitEdit);
        break;
      default:
        break;
    }
    getTabs().add(tab);
    getSelectionModel().select(tab);
  }
}
