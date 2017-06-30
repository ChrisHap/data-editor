package de.hhu.stups.plues.dataeditor.ui.components;

import de.hhu.stups.plues.dataeditor.ui.entities.EntityWrapper;
import de.hhu.stups.plues.dataeditor.ui.layout.Inflater;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.utils.FontAwesomeIconFactory;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import org.controlsfx.control.textfield.CustomTextField;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

public class SideBar extends VBox implements Initializable {

  private final BooleanProperty showSideBarProperty;
  private ResourceBundle resources;

  @FXML
  @SuppressWarnings("unused")
  private ComboBox<TreeType> cbDataType;
  @FXML
  @SuppressWarnings("unused")
  private CustomTextField txtQuery;
  @FXML
  @SuppressWarnings("unused")
  private TreeTableView<EntityWrapper> treeTableView;

  @Inject
  public SideBar(final Inflater inflater) {
    showSideBarProperty = new SimpleBooleanProperty(true);
    inflater.inflate("components/side_bar", this, this, "side_bar");
  }

  @Override
  public void initialize(final URL location, final ResourceBundle resources) {
    this.resources = resources;
    treeTableView.prefHeightProperty().bind(
        this.heightProperty().subtract(cbDataType.heightProperty()));
    txtQuery.setLeft(FontAwesomeIconFactory.get().createIcon(FontAwesomeIcon.SEARCH, "12"));
    initializeComboBox();
  }

  /**
   * Initialize the {@link #cbDataType} to select between module tree or module data.
   */
  private void initializeComboBox() {
    cbDataType.getSelectionModel().selectFirst();
    cbDataType.setConverter(new StringConverter<TreeType>() {
      @Override
      public String toString(final TreeType treeType) {
        return resources.getString(treeType.toString());
      }

      @Override
      public TreeType fromString(final String string) {
        return null;
      }
    });
  }

  public BooleanProperty showSideBarProperty() {
    return showSideBarProperty;
  }

}
