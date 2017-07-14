package de.hhu.stups.plues.dataeditor.injector;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.util.Providers;

import de.codecentric.centerdevice.MenuToolkit;

import de.hhu.stups.plues.dataeditor.ui.components.DataEditView;
import de.hhu.stups.plues.dataeditor.ui.components.LabeledTextField;
import de.hhu.stups.plues.dataeditor.ui.components.MainMenu;
import de.hhu.stups.plues.dataeditor.ui.components.SideBar;
import de.hhu.stups.plues.dataeditor.ui.components.dataedits.AbstractUnitEdit;
import de.hhu.stups.plues.dataeditor.ui.components.dataedits.EntityEditProvider;
import de.hhu.stups.plues.dataeditor.ui.components.dataedits.LevelEdit;
import de.hhu.stups.plues.dataeditor.ui.components.dataedits.ModuleEdit;
import de.hhu.stups.plues.dataeditor.ui.components.dataedits.UnitEdit;
import de.hhu.stups.plues.dataeditor.ui.components.datavisualization.DataContextMenu;
import de.hhu.stups.plues.dataeditor.ui.components.datavisualization.DataListView;
import de.hhu.stups.plues.dataeditor.ui.components.datavisualization.DataTreeView;
import de.hhu.stups.plues.dataeditor.ui.controller.DataEditor;
import de.hhu.stups.plues.dataeditor.ui.database.DataService;
import de.hhu.stups.plues.dataeditor.ui.database.DbService;
import javafx.fxml.FXMLLoader;

import java.util.Locale;
import java.util.ResourceBundle;

public class DataEditorModule extends AbstractModule {

  private static final boolean IS_MAC = System.getProperty("os.name", "")
      .toLowerCase().contains("mac");

  private final Locale locale = new Locale("en");
  private final ResourceBundle bundle = ResourceBundle.getBundle("lang.main", locale);

  @Override
  protected void configure() {
    bind(UnitEdit.class);
    bind(LevelEdit.class);
    bind(ModuleEdit.class);
    bind(AbstractUnitEdit.class);
    bind(LabeledTextField.class);
    bind(DataService.class);
    bind(DbService.class);
    bind(EntityEditProvider.class);
    bind(DataEditView.class);
    bind(DataContextMenu.class);
    bind(DataTreeView.class);
    bind(DataListView.class);
    bind(MainMenu.class);
    bind(SideBar.class);
    bind(DataEditor.class);
    bind(Locale.class).toInstance(locale);
    bind(ResourceBundle.class).toInstance(bundle);

    if (IS_MAC) {
      bind(MenuToolkit.class).toInstance(MenuToolkit.toolkit(locale));
    } else {
      bind(MenuToolkit.class).toProvider(Providers.of(null));
    }
  }

  /**
   * Provide the {@link FXMLLoader}.
   */
  @Provides
  public FXMLLoader provideLoader(final Injector injector,
                                  final GuiceBuilderFactory builderFactory,
                                  final ResourceBundle bundle) {
    final FXMLLoader fxmlLoader = new FXMLLoader();
    fxmlLoader.setBuilderFactory(builderFactory);
    fxmlLoader.setControllerFactory(injector::getInstance);
    fxmlLoader.setResources(bundle);
    return fxmlLoader;
  }
}
