package de.hhu.stups.plues.dataeditor.ui.components.dataedits;

import de.hhu.stups.plues.dataeditor.ui.components.LabeledTextField;
import de.hhu.stups.plues.dataeditor.ui.database.DataService;
import de.hhu.stups.plues.dataeditor.ui.database.events.DataChangeEvent;
import de.hhu.stups.plues.dataeditor.ui.database.events.DataChangeType;
import de.hhu.stups.plues.dataeditor.ui.entities.Group;
import de.hhu.stups.plues.dataeditor.ui.entities.GroupWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.SessionWrapper;
import de.hhu.stups.plues.dataeditor.ui.layout.Inflater;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;
import org.fxmisc.easybind.EasyBind;

import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;

public class SessionEdit extends GridPane implements Initializable {

  private final SessionWrapper sessionWrapper;
  private final DataService dataService;
  private final BooleanProperty dataChangedProperty;

  private ResourceBundle resources;

  @FXML
  @SuppressWarnings("unused")
  private LabeledTextField txtDay;
  @FXML
  @SuppressWarnings("unused")
  private LabeledTextField txtTime;
  @FXML
  @SuppressWarnings("unused")
  private LabeledTextField txtRhythm;
  @FXML
  @SuppressWarnings("unused")
  private LabeledTextField txtDuration;
  @FXML
  @SuppressWarnings("unused")
  private CheckBox cbTentative;
  @FXML
  @SuppressWarnings("unused")
  private ComboBox<GroupWrapper> cbGroup;
  @FXML
  @SuppressWarnings("unused")
  private Button btPersistChanges;

  private HashSet<String> validDays;

  /**
   * Initialize session edit.
   */
  SessionEdit(final Inflater inflater,
              final DataService dataService,
              final SessionWrapper sessionWrapper) {
    this.sessionWrapper = sessionWrapper;
    this.dataService = dataService;
    dataChangedProperty = new SimpleBooleanProperty(false);
    inflater.inflate("components/dataedits/session_edit", this, this, "session_edit");
  }

  @Override
  public void initialize(final URL location, final ResourceBundle resources) {
    this.resources = resources;
    validDays = new HashSet<>();
    validDays.add("mon");
    validDays.add("tue");
    validDays.add("wed");
    validDays.add("thu");
    validDays.add("fri");
    btPersistChanges.disableProperty().bind(dataChangedProperty.not());
    txtDay.setLabelText(resources.getString("day"));
    txtTime.setLabelText(resources.getString("time"));
    txtRhythm.setLabelText(resources.getString("rhythm"));
    txtDuration.setLabelText(resources.getString("duration"));
    setDataListener();
    loadSessionData();
  }

  private void loadSessionData() {
    setDay();
    setTime();
    setRhythm();
    setDuration();
    cbTentative.setSelected(sessionWrapper.isTentative());
    loadGroups();
    dataChangedProperty.set(false);
  }

  private void updateDataChanged() {
    EasyBind.subscribe(txtDay.textProperty(), s -> dataChangedProperty.set(true));
    EasyBind.subscribe(txtTime.textProperty(), s -> dataChangedProperty.set(true));
    EasyBind.subscribe(txtRhythm.textProperty(), s -> dataChangedProperty.set(true));
    EasyBind.subscribe(txtDuration.textProperty(), s -> dataChangedProperty.set(true));
    EasyBind.subscribe(cbTentative.selectedProperty(), s -> dataChangedProperty.set(true));
  }

  /**
   * Update data if the wrapper has changed.
   */
  private void setDataListener() {
    EasyBind.subscribe(sessionWrapper.durationProperty(), s -> setDuration());
    EasyBind.subscribe(sessionWrapper.dayProperty(), s -> setDay());
    EasyBind.subscribe(sessionWrapper.timeProperty(), s -> setTime());
    EasyBind.subscribe(sessionWrapper.rhythmProperty(), s -> setRhythm());
    updateDataChanged();
  }

  private void setDuration() {
    txtDuration.setText(String.valueOf(sessionWrapper.getDuration()));
  }

  private void setRhythm() {
    txtRhythm.setText((String.valueOf(sessionWrapper.getRhythm())));
  }

  private void setTime() {
    txtTime.setText(String.valueOf(sessionWrapper.getTime()));
  }

  private void setDay() {
    txtDay.setText(sessionWrapper.getDay());
  }

  private void loadGroups() {
    cbGroup.getItems().clear();
    cbGroup.getItems().addAll(dataService.getGroupWrappers().values());
    final Group group = sessionWrapper.getGroup();
    if (group != null) {
      cbGroup.getSelectionModel().select(dataService.getGroupWrappers()
            .get(String.valueOf(group.getId())));
    }
  }

  /**
   * Push the {@link #sessionWrapper} to the {@link #dataService} and set
   * {@link #dataChangedProperty} to false.
   */
  @FXML
  @SuppressWarnings("unused")
  public void persistChanges() {
    if (cbGroup.getValue() != null) {
      new Alert(Alert.AlertType.ERROR, resources.getString("parentGroupError"),
            ButtonType.OK).showAndWait();
      return;
    }

    if (!validDays.contains(txtDay.textProperty().get())) {
      new Alert(Alert.AlertType.ERROR, resources.getString("dayError"),
            ButtonType.OK).showAndWait();
      return;
    }

    sessionWrapper.getSession().setDay(txtDay.textProperty().get());
    try {
      sessionWrapper.getSession().setDuration(Integer.parseInt(txtDuration.textProperty().get()));
      sessionWrapper.getSession().setRhythm(Integer.parseInt(txtRhythm.textProperty().get()));
      sessionWrapper.getSession().setTime(Integer.parseInt(txtTime.textProperty().get()));
    } catch (NumberFormatException exception) {
      new Alert(Alert.AlertType.ERROR, resources.getString("numberFormatError"), ButtonType.OK);
    }

    sessionWrapper.getSession().setGroup(cbGroup.getValue().getGroup());
    cbGroup.getValue().getGroup().getSessions().add(sessionWrapper.getSession());
    dataService.dataChangeEventSource().push(
          new DataChangeEvent(DataChangeType.STORE_ENTITY, cbGroup.getValue()));

    boolean isNew = sessionWrapper.getSession().getId() == 0;

    dataService.dataChangeEventSource().push(
          new DataChangeEvent(DataChangeType.STORE_ENTITY, sessionWrapper));
    sessionWrapper.setId(sessionWrapper.getSession().getId());

    if (isNew) {
      dataService.dataChangeEventSource().push(
            new DataChangeEvent(DataChangeType.INSERT_NEW_ENTITY, sessionWrapper));
    }

    dataChangedProperty.set(false);
  }
}
