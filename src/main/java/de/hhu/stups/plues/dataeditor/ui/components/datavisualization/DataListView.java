package de.hhu.stups.plues.dataeditor.ui.components.datavisualization;

import com.google.inject.Inject;

import de.hhu.stups.plues.dataeditor.ui.database.DataService;
import de.hhu.stups.plues.dataeditor.ui.entities.EntityType;
import de.hhu.stups.plues.dataeditor.ui.entities.EntityWrapper;
import de.hhu.stups.plues.dataeditor.ui.layout.Inflater;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.utils.FontAwesomeIconFactory;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.controlsfx.control.textfield.CustomTextField;

import java.net.URL;
import java.util.ResourceBundle;

public class DataListView extends GridPane implements Initializable {

  private final DataService dataService;

  private ResourceBundle resources;

  @FXML
  @SuppressWarnings("unused")
  private CustomTextField txtQuery;
  @FXML
  @SuppressWarnings("unused")
  private ComboBox<EntityType> cbEntityType;
  @FXML
  @SuppressWarnings("unused")
  private ListView<EntityWrapper> listView;

  @Inject
  public DataListView(final Inflater inflater,
                      final DataService dataService) {
    this.dataService = dataService;
    inflater.inflate("components/datavisualization/data_list_view", this, this, "data_view");
  }

  @Override
  public void initialize(final URL location, final ResourceBundle resources) {
    this.resources = resources;
    initializeComboBox();
    listView.setCellFactory(getListCellCallback());
    txtQuery.setLeft(FontAwesomeIconFactory.get().createIcon(FontAwesomeIcon.SEARCH, "12"));
    txtQuery.prefWidthProperty().bind(widthProperty());
    listView.prefWidthProperty().bind(widthProperty());
    dataService.dataChangeEventSource().subscribe(dataChangeEvent -> {
      if (dataChangeEvent.getDataChangeType().reloadDb()) {
        loadData(cbEntityType.getSelectionModel().getSelectedItem());
      }
    });
  }

  private void initializeComboBox() {
    cbEntityType.getSelectionModel().selectedItemProperty().addListener(
        (observable, oldValue, newValue) -> Platform.runLater(() -> loadData(newValue)));
    cbEntityType.setConverter(new StringConverter<EntityType>() {
      @Override
      public String toString(final EntityType entityType) {
        return resources.getString(entityType.toString());
      }

      @Override
      public EntityType fromString(final String string) {
        return null;
      }
    });
    cbEntityType.getSelectionModel().selectFirst();
  }

  private void loadData(final EntityType entityType) {
    listView.getItems().clear();
    switch (entityType) {
      case COURSE:
        listView.getItems().addAll(dataService.getCourseWrappers().values());
        break;
      case LEVEL:
        listView.getItems().addAll(dataService.getLevelWrappers().values());
        break;
      case MODULE:
        listView.getItems().addAll(dataService.getModuleWrappers().values());
        break;
      case ABSTRACT_UNIT:
        listView.getItems().addAll(dataService.getAbstractUnitWrappers().values());
        break;
      case UNIT:
        listView.getItems().addAll(dataService.getUnitWrappers().values());
        break;
      case SESSION:
        listView.getItems().addAll(dataService.getSessionWrappers().values());
        break;
      default:
        break;
    }
  }

  private Callback<ListView<EntityWrapper>, ListCell<EntityWrapper>> getListCellCallback() {
    return new Callback<ListView<EntityWrapper>, ListCell<EntityWrapper>>() {
      @Override
      public ListCell<EntityWrapper> call(ListView<EntityWrapper> listView) {
        return new ListCell<EntityWrapper>() {
          @Override
          protected void updateItem(final EntityWrapper entityWrapper,
                                    final boolean bool) {
            super.updateItem(entityWrapper, bool);
            if (entityWrapper != null) {
              setText(entityWrapper.toString());
            }
          }

        };
      }
    };
  }
}
