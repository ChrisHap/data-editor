package de.hhu.stups.plues.dataeditor.ui.components.datavisualization;

import com.google.inject.Inject;

import com.google.inject.Singleton;

import de.hhu.stups.plues.dataeditor.ui.entities.EntityWrapper;
import de.hhu.stups.plues.dataeditor.ui.layout.Inflater;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.utils.FontAwesomeIconFactory;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.VBox;
import org.controlsfx.control.textfield.CustomTextField;

import java.net.URL;
import java.util.ResourceBundle;

@Singleton
public class DataTreeView extends VBox implements Initializable {

  @FXML
  @SuppressWarnings("unused")
  private CustomTextField txtQuery;
  @FXML
  @SuppressWarnings("unused")
  private TreeTableView<EntityWrapper> treeTableView;

  @Inject
  public DataTreeView(final Inflater inflater) {
    inflater.inflate("components/datavisualization/data_tree_view", this, this, "data_view");
  }

  @Override
  public void initialize(final URL location, final ResourceBundle resources) {
    treeTableView.prefHeightProperty().bind(this.heightProperty());
    txtQuery.setLeft(FontAwesomeIconFactory.get().createIcon(FontAwesomeIcon.SEARCH, "12"));
  }
}
