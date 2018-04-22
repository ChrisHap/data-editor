package de.hhu.stups.plues.dataeditor.ui.components.dataedits;

import de.hhu.stups.plues.dataeditor.ui.components.LabeledTextField;
import de.hhu.stups.plues.dataeditor.ui.database.DataService;
import de.hhu.stups.plues.dataeditor.ui.database.events.DataChangeEvent;
import de.hhu.stups.plues.dataeditor.ui.database.events.DataChangeType;
import de.hhu.stups.plues.dataeditor.ui.entities.GroupWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.SessionWrapper;
import de.hhu.stups.plues.dataeditor.ui.layout.Inflater;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.GridPane;
import org.fxmisc.easybind.EasyBind;

import java.net.URL;
import java.util.ResourceBundle;

public class GroupEdit extends GridPane implements Initializable {

  private final GroupWrapper groupWrapper;
  private final BooleanProperty dataChangedProperty;
  private final DataService dataService;

  @FXML
  @SuppressWarnings("unused")
  private LabeledTextField txtUnit;
  @FXML
  @SuppressWarnings("unused")
  private ListView<SessionWrapper> listViewSessions;
  @FXML
  @SuppressWarnings("unused")
  private RadioButton rbFirstHalf;
  @FXML
  @SuppressWarnings("unused")
  private RadioButton rbSecondHalf;
  @FXML
  @SuppressWarnings("unused")
  private RadioButton rbWholeSemester;
  @FXML
  @SuppressWarnings("unused")
  private Button btPersistChanges;

  /**
   * Initialize group edit.
   */
  public GroupEdit(final Inflater inflater,
                   final DataService dataService,
                   final GroupWrapper groupWrapper) {
    this.groupWrapper = groupWrapper;
    this.dataService = dataService;
    dataChangedProperty = new SimpleBooleanProperty(false);
    inflater.inflate("components/dataedits/group_edit", this, this, "group_edit");
  }

  @Override
  public void initialize(final URL location, final ResourceBundle resources) {
    btPersistChanges.disableProperty().bind(dataChangedProperty.not());
    txtUnit.setLabelText(resources.getString("unit"));
    loadGroupData();
    setDataListener();
  }

  private void loadGroupData() {
    setUnit();
    selectSemesterType();
    loadSessions();
    dataChangedProperty.set(false);
  }

  private void selectSemesterType() {
    if (groupWrapper.getHalfSemester() == 1) {
      rbFirstHalf.setSelected(true);
      return;
    }
    if (groupWrapper.getHalfSemester() == 2) {
      rbSecondHalf.setSelected(true);
      return;
    }
    rbWholeSemester.setSelected(true);
  }

  private void setDataListener() {
    EasyBind.subscribe(groupWrapper.unitProperty(), unitWrapper -> setUnit());
    EasyBind.subscribe(groupWrapper.sessionsProperty(), sessionWrappers ->
        loadSessions());
    EasyBind.subscribe(groupWrapper.halfSemesterProperty(), number -> selectSemesterType());
    updateDataChanged();
  }

  private void updateDataChanged() {
    EasyBind.subscribe(txtUnit.textProperty(), s -> dataChangedProperty.set(true));
    EasyBind.subscribe(listViewSessions.itemsProperty(), s -> dataChangedProperty.set(true));
    EasyBind.subscribe(rbFirstHalf.selectedProperty(), s -> dataChangedProperty.set(true));
    EasyBind.subscribe(rbSecondHalf.selectedProperty(), s -> dataChangedProperty.set(true));
    EasyBind.subscribe(rbWholeSemester.selectedProperty(), s -> dataChangedProperty.set(true));
  }

  private void setUnit() {
    txtUnit.setText(groupWrapper.getUnit().getTitle());
  }

  private void loadSessions() {
    listViewSessions.getItems().clear();
    listViewSessions.getItems().addAll(groupWrapper.getSessions());
  }

  /**
   * Push the {@link #groupWrapper} to the {@link #dataService} and set
   * {@link #dataChangedProperty} to false.
   */
  @FXML
  @SuppressWarnings("unused")
  public void persistChanges() {
    dataService.dataChangeEventSource().push(
        new DataChangeEvent(DataChangeType.STORE_ENTITY, groupWrapper));
    dataChangedProperty.set(false);
  }
}
