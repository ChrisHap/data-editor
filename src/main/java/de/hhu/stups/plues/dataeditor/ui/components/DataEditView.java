package de.hhu.stups.plues.dataeditor.ui.components;

import com.google.inject.Inject;

import de.hhu.stups.plues.dataeditor.ui.components.dataedits.AbstractUnitEdit;
import de.hhu.stups.plues.dataeditor.ui.components.dataedits.CourseEdit;
import de.hhu.stups.plues.dataeditor.ui.components.dataedits.EntityEditProvider;
import de.hhu.stups.plues.dataeditor.ui.components.dataedits.LevelEdit;
import de.hhu.stups.plues.dataeditor.ui.components.dataedits.ModuleEdit;
import de.hhu.stups.plues.dataeditor.ui.components.dataedits.UnitEdit;
import de.hhu.stups.plues.dataeditor.ui.database.DataService;
import de.hhu.stups.plues.dataeditor.ui.database.events.DataChangeEvent;
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
  private final EntityEditProvider entityEditProvider;

  private ResourceBundle resources;

  /**
   * Inject the {@link DataService}.
   */
  @Inject
  public DataEditView(final Inflater inflater,
                      final DataService dataService,
                      final EntityEditProvider entityEditProvider) {
    this.dataService = dataService;
    this.entityEditProvider = entityEditProvider;
    inflater.inflate("components/data_edit_view", this, this, "data_edit_view");
  }

  @Override
  public void initialize(final URL location, final ResourceBundle resources) {
    this.resources = resources;
    dataService.dataChangeEventSource().subscribe(this::openEditTab);
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
        tab.setContent(entityEditProvider.getCourseEdit());
        break;
      case LEVEL:
        tab.setText(resources.getString("level"));
        tab.setContent(entityEditProvider.getLevelEdit());
        break;
      case MODULE:
        tab.setText(resources.getString("module"));
        tab.setContent(entityEditProvider.getModuleEdit());
        break;
      case ABSTRACT_UNIT:
        tab.setText(resources.getString("abstract_unit"));
        tab.setContent(entityEditProvider.getAbstractUnitEdit());
        break;
      case UNIT:
        tab.setText(resources.getString("unit"));
        tab.setContent(entityEditProvider.getUnitEdit());
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
        final CourseEdit courseEdit = entityEditProvider.getCourseEdit();
        courseEdit.setCourseWrapper((CourseWrapper) entityWrapper);
        tab.setContent(courseEdit);
        break;
      case LEVEL:
        tab.setText(resources.getString("level"));
        final LevelEdit levelEdit = entityEditProvider.getLevelEdit();
        levelEdit.setLevelWrapper((LevelWrapper) entityWrapper);
        tab.setContent(levelEdit);
        break;
      case MODULE:
        tab.setText(resources.getString("module"));
        final ModuleEdit moduleEdit = entityEditProvider.getModuleEdit();
        moduleEdit.setModuleWrapper((ModuleWrapper) entityWrapper);
        tab.setContent(moduleEdit);
        break;
      case ABSTRACT_UNIT:
        tab.setText(resources.getString("abstract_unit"));
        final AbstractUnitEdit abstractUnitEdit = entityEditProvider.getAbstractUnitEdit();
        abstractUnitEdit.setAbstractUnitWrapper((AbstractUnitWrapper) entityWrapper);
        tab.setContent(abstractUnitEdit);
        break;
      case UNIT:
        tab.setText(resources.getString("unit"));
        final UnitEdit unitEdit = entityEditProvider.getUnitEdit();
        unitEdit.setUnitWrapper((UnitWrapper) entityWrapper);
        tab.setContent(unitEdit);
        break;
      default:
        break;
    }
    getTabs().add(tab);
    getSelectionModel().select(tab);
  }
}
