package de.hhu.stups.plues.dataeditor.ui.components.datavisualization;

import com.google.inject.Inject;

import de.hhu.stups.plues.dataeditor.ui.entities.EntityType;
import de.hhu.stups.plues.dataeditor.ui.entities.EntityWrapper;
import de.hhu.stups.plues.dataeditor.ui.layout.Inflater;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.utils.FontAwesomeIconFactory;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import org.controlsfx.control.textfield.CustomTextField;

import java.net.URL;
import java.util.ResourceBundle;

public class DataListView extends VBox implements Initializable {

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
  public DataListView(final Inflater inflater) {
    inflater.inflate("components/datavisualization/data_list_view", this, this, "data_view");
  }

  @Override
  public void initialize(final URL location, final ResourceBundle resources) {
    this.resources = resources;
    initializeComboBox();
    listView.prefHeightProperty().bind(this.heightProperty());
    txtQuery.setLeft(FontAwesomeIconFactory.get().createIcon(FontAwesomeIcon.SEARCH, "12"));
  }

  private void initializeComboBox() {
    cbEntityType.getSelectionModel().selectFirst();
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
  }
}
