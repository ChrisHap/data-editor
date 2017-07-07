package de.hhu.stups.plues.dataeditor.ui.components;

import com.google.inject.Inject;

import de.hhu.stups.plues.dataeditor.ui.components.dataedits.CourseEdit;
import de.hhu.stups.plues.dataeditor.ui.components.dataedits.LevelEdit;
import de.hhu.stups.plues.dataeditor.ui.components.dataedits.ModuleEdit;
import de.hhu.stups.plues.dataeditor.ui.layout.Inflater;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class DataEditView extends VBox implements Initializable {

  @FXML
  @SuppressWarnings("unused")
  private CourseEdit courseEdit;
  @FXML
  @SuppressWarnings("unused")
  private LevelEdit levelEdit;
  @FXML
  @SuppressWarnings("unused")
  private ModuleEdit moduleEdit;


  @Inject
  public DataEditView(final Inflater inflater) {
    inflater.inflate("components/data_edit_view", this, this, "data_edit_view");
  }

  @Override
  public void initialize(final URL location, final ResourceBundle resources) {

  }
}
