package de.hhu.stups.plues.dataeditor.ui.components.dataedits;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import de.hhu.stups.plues.dataeditor.ui.components.LabeledTextField;
import de.hhu.stups.plues.dataeditor.ui.database.DataService;
import de.hhu.stups.plues.dataeditor.ui.entities.EntityWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.UnitWrapper;
import de.hhu.stups.plues.dataeditor.ui.layout.Inflater;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import org.fxmisc.easybind.EasyBind;

import java.net.URL;
import java.util.ResourceBundle;

public class UnitEdit extends GridPane implements Initializable {

  private final DataService dataService;

  private UnitWrapper unitWrapper;
  private ResourceBundle resources;

  @FXML
  @SuppressWarnings("unused")
  private LabeledTextField txtUnit;
  @FXML
  @SuppressWarnings("unused")
  private LabeledTextField txtId;
  @FXML
  @SuppressWarnings("unused")
  private LabeledTextField txtSemester;
  @FXML
  @SuppressWarnings("unused")
  private TableView<AbstractUnitTableCell> tableViewAbstractUnits;
  @FXML
  @SuppressWarnings("unused")
  private TableColumn<AbstractUnitTableCell, String> tableColumnAbstractUnitTitle;
  @FXML
  @SuppressWarnings("unused")
  private TableColumn<AbstractUnitTableCell, CheckBox> tableColumnConsiderAbstractUnit;
  @FXML
  @SuppressWarnings("unused")
  private Button persistChanges;
  @FXML
  @SuppressWarnings("unused")
  private ListView<EntityWrapper> tableViewSessions;

  /**
   * Initialize unit edit.
   */
  @Inject
  public UnitEdit(final Inflater inflater,
                  final DataService dataService,
                  @Assisted final UnitWrapper unitWrapper) {
    this.dataService = dataService;
    this.unitWrapper = unitWrapper;
    inflater.inflate("components/dataedits/unit_edit", this, this, "unit_edit");
  }

  @Override
  public void initialize(final URL location, final ResourceBundle resources) {
    this.resources = resources;
    initializeInputFields();

    tableColumnAbstractUnitTitle.setCellValueFactory(param -> param.getValue().titleProperty());
    tableColumnConsiderAbstractUnit.setCellValueFactory(param -> {
      final AbstractUnitTableCell abstractUnitTableCell = param.getValue();
      final CheckBox checkBox = new CheckBox();
      checkBox.selectedProperty()
          .bindBidirectional(abstractUnitTableCell.considerAbstractUnitProperty());
      return new SimpleObjectProperty<>(checkBox);
    });

    loadAbstractUnits();
    EasyBind.subscribe(dataService.abstractUnitWrappersProperty(),
        abstractUnitWrapperMap -> loadAbstractUnits());
    loadUnitData();
  }

  private void loadAbstractUnits() {
    tableViewAbstractUnits.getItems().clear();
    dataService.abstractUnitWrappersProperty().values().forEach(abstractUnitWrapper -> {
      // TODO: fix this
      final AbstractUnitTableCell cell = new AbstractUnitTableCell(abstractUnitWrapper,
          (unitWrapper != null)
              && unitWrapper.getAbstractUnitsProperty().contains(abstractUnitWrapper));
      tableViewAbstractUnits.getItems().add(cell);
    });
  }

  @FXML
  @SuppressWarnings("unused")
  public void persistChanges() {
    //
  }


  private void initializeInputFields() {
    txtUnit.setLabelText(resources.getString("unit"));
    txtId.setLabelText(resources.getString("id"));
    txtSemester.setLabelText(resources.getString("semester"));
  }

  private void loadUnitData() {
    txtUnit.textProperty().bind(unitWrapper.titleProperty());
    txtSemester.textProperty().bind(unitWrapper.semestersProperty().asString());
    txtId.textProperty().bind(unitWrapper.keyProperty());

  }
}
