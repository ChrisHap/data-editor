package de.hhu.stups.plues.dataeditor.ui.components.dataedits;

import com.google.inject.Inject;

import de.hhu.stups.plues.dataeditor.ui.layout.Inflater;
import javafx.fxml.Initializable;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ResourceBundle;

public class LevelEdit extends GridPane implements Initializable {

  @Inject
  public LevelEdit(final Inflater inflater) {
    inflater.inflate("components/dataedits/level_edit", this, this, "level_edit");
  }

  @Override
  public void initialize(final URL location, final ResourceBundle resources) {

  }
}
