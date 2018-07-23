package de.hhu.stups.plues.dataeditor.ui.components.datavisualization;

import de.hhu.stups.plues.dataeditor.ui.database.DataService;
import de.hhu.stups.plues.dataeditor.ui.database.events.DataChangeEvent;
import de.hhu.stups.plues.dataeditor.ui.entities.EntityType;
import de.hhu.stups.plues.dataeditor.ui.entities.EntityWrapper;
import de.hhu.stups.plues.dataeditor.ui.layout.Inflater;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.utils.FontAwesomeIconFactory;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import org.controlsfx.control.textfield.CustomTextField;
import org.fxmisc.easybind.EasyBind;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Component
public class DataListView extends VBox implements Initializable {

  private final DataService dataService;
  private final ExtendedDataContextMenu dataContextMenu;

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

  /**
   * Initialize {@link DataService} and {@link ExtendedDataContextMenu}.
   */
  @Autowired
  public DataListView(final Inflater inflater,
                      final DataService dataService) {
    this.dataService = dataService;
    this.dataContextMenu = new ExtendedDataContextMenu(dataService, DataContextMenuType.FLAT);
    inflater.inflate("components/datavisualization/data_list_view", this, this, "data_view");
  }

  @Override
  public void initialize(final URL location, final ResourceBundle resources) {
    this.resources = resources;
    initializeComboBox();
    txtQuery.setLeft(FontAwesomeIconFactory.get().createIcon(FontAwesomeIcon.SEARCH, "12"));
    txtQuery.prefWidthProperty().bind(widthProperty());
    listView.prefWidthProperty().bind(widthProperty());
    listView.prefHeightProperty().bind(heightProperty().subtract(
        cbEntityType.heightProperty()).subtract(txtQuery.heightProperty()));
    dataService.dataChangeEventSource().subscribe(this::updateDataList);
    listView.setOnMouseClicked(event -> {
      dataContextMenu.hide();
      final EntityWrapper selectedEntityWrapper = listView.getSelectionModel().getSelectedItem();
      if (selectedEntityWrapper != null && MouseButton.SECONDARY.equals(event.getButton())) {
        dataContextMenu.setEntityWrapper(selectedEntityWrapper);
        dataContextMenu.show(this, event.getScreenX(), event.getScreenY());
      }
    });
    listView.setOnDragDetected(test -> {
      if (listView.getSelectionModel().getSelectedItem() == null) {
        return;
      }
      EntityWrapper wrapper = listView.getSelectionModel().getSelectedItem();
      dataService.draggedEntityProperty().set(wrapper);
      Dragboard db = listView.startDragAndDrop(TransferMode.COPY);
      // an empty clipboard, we use the DataService to pass the object
      ClipboardContent clipboardContent = new ClipboardContent();
      clipboardContent.putString("");
      db.setContent(clipboardContent);
    });
    EasyBind.subscribe(txtQuery.textProperty(), filter -> loadFilteredData(filter.toLowerCase()));
  }

  private void updateDataList(final DataChangeEvent dataChangeEvent) {
    switch (dataChangeEvent.getDataChangeType()) {
      case RELOAD_DB:
        Platform.runLater(() -> loadData(cbEntityType.getSelectionModel().getSelectedItem()));
        break;
      case INSERT_NEW_ENTITY:
        if (cbEntityType.getValue() == dataChangeEvent.getChangedEntity().getEntityType()) {
          listView.getItems().add(dataChangeEvent.getChangedEntity());
        }
        break;
      case DELETE_ENTITY:
        listView.getItems().remove(dataChangeEvent.getChangedEntity());
        break;
    }
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
      case GROUP:
        listView.getItems().addAll(dataService.getGroupWrappers().values());
        break;
      case SESSION:
        listView.getItems().addAll(dataService.getSessionWrappers().values());
        break;
      default:
        break;
    }
  }

  private void loadFilteredData(String filter) {
    listView.getItems().clear();
    switch (cbEntityType.getSelectionModel().getSelectedItem()) {
      case COURSE:
        listView.setItems((dataService.getCourseWrappers().values().stream().filter(
            courseWrapper -> courseWrapper.getLongName().toLowerCase().contains(filter)
                || courseWrapper.getShortName().toLowerCase().contains(filter)
                || courseWrapper.getKey().toLowerCase().contains(filter))).collect(
            Collectors.toCollection(FXCollections::observableArrayList)));
        break;
      case LEVEL:
        listView.setItems((dataService.getLevelWrappers().values().stream().filter(
            levelWrapper -> levelWrapper.getName().toLowerCase().contains(filter))).collect(
            Collectors.toCollection(FXCollections::observableArrayList)));
        break;
      case MODULE:
        listView.setItems((dataService.getModuleWrappers().values().stream().filter(
            moduleWrapper -> moduleWrapper.getKey().toLowerCase().contains(filter)
                || moduleWrapper.getTitle().toLowerCase().contains(filter))).collect(
            Collectors.toCollection(FXCollections::observableArrayList)));
        break;
      case ABSTRACT_UNIT:
        listView.setItems((dataService.getAbstractUnitWrappers().values().stream().filter(
            abstractUnitWrapper -> abstractUnitWrapper.getKey().toLowerCase().contains(filter)
                || abstractUnitWrapper.getTitle().toLowerCase().contains(filter))).collect(
            Collectors.toCollection(FXCollections::observableArrayList)));
        break;
      case UNIT:
        listView.setItems((dataService.getUnitWrappers().values().stream().filter(
            unitWrapper -> unitWrapper.getKey().toLowerCase().contains(filter)
                || unitWrapper.getTitle().toLowerCase().contains(filter))).collect(
            Collectors.toCollection(FXCollections::observableArrayList)));
        break;
      case GROUP:
        listView.setItems((dataService.getGroupWrappers().values().stream().filter(
            groupWrapper -> String.valueOf(groupWrapper.getId()).equals(filter))).collect(
            Collectors.toCollection(FXCollections::observableArrayList)));
        break;
      case SESSION:
        listView.setItems((dataService.getSessionWrappers().values().stream().filter(
            sessionWrapper -> String.valueOf(sessionWrapper.getId()).equals(filter))).collect(
            Collectors.toCollection(FXCollections::observableArrayList)));
        break;
      default:
        break;
    }
  }
}
