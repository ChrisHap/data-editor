package de.hhu.stups.plues.dataeditor.ui.components;

import com.google.inject.Inject;

import de.hhu.stups.plues.dataeditor.ui.components.datavisualization.DataListView;
import de.hhu.stups.plues.dataeditor.ui.components.datavisualization.DataTreeView;
import de.hhu.stups.plues.dataeditor.ui.components.datavisualization.VisualizationType;
import de.hhu.stups.plues.dataeditor.ui.layout.Inflater;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import org.fxmisc.easybind.EasyBind;

import java.net.URL;
import java.util.ResourceBundle;

public class SideBar extends VBox implements Initializable {

  private final BooleanProperty showSideBarProperty;
  private ResourceBundle resources;

  @FXML
  @SuppressWarnings("unused")
  private ComboBox<VisualizationType> cbVisualizationType;
  @FXML
  @SuppressWarnings("unused")
  private DataTreeView dataTreeView;
  @FXML
  @SuppressWarnings("unused")
  private DataListView dataListView;

  @Inject
  public SideBar(final Inflater inflater) {
    showSideBarProperty = new SimpleBooleanProperty(true);
    inflater.inflate("components/side_bar", this, this, "side_bar");
  }

  @Override
  public void initialize(final URL location, final ResourceBundle resources) {
    this.resources = resources;
    getChildren().remove(dataListView);
    initializeComboBox();
  }

  /**
   * Initialize the {@link #cbVisualizationType} to select between module tree or module data.
   */
  private void initializeComboBox() {
    cbVisualizationType.getSelectionModel().selectFirst();
    cbVisualizationType.setConverter(new StringConverter<VisualizationType>() {
      @Override
      public String toString(final VisualizationType visualizationType) {
        return resources.getString(visualizationType.toString());
      }

      @Override
      public VisualizationType fromString(final String string) {
        return null;
      }
    });
    EasyBind.subscribe(cbVisualizationType.getSelectionModel().selectedItemProperty(),
        this::handleVisualizationType);
  }

  /**
   * Show either {@link #dataTreeView} or {@link #dataListView}.
   */
  private void handleVisualizationType(final VisualizationType visualizationType) {
    switch (visualizationType) {
      case TREE:
        if (getChildren().contains(dataListView)) {
          getChildren().remove(dataListView);
          getChildren().add(dataTreeView);
        }
        return;
      case LIST:
        if (getChildren().contains(dataTreeView)) {
          getChildren().remove(dataTreeView);
          getChildren().add(dataListView);
        }
        return;
      default:
        break;
    }
  }

  public BooleanProperty showSideBarProperty() {
    return showSideBarProperty;
  }

}
