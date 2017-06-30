package de.hhu.stups.plues.dataeditor.ui.components;

import de.hhu.stups.plues.dataeditor.ui.layout.Inflater;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

public class MainMenu extends MenuBar implements Initializable {

  @FXML
  @SuppressWarnings("unused")
  private MenuItem menuItemOpenDb;
  @FXML
  @SuppressWarnings("unused")
  private MenuItem menuItemSaveDb;
  @FXML
  @SuppressWarnings("unused")
  private MenuItem menuItemSaveDbAs;
  @FXML
  @SuppressWarnings("unused")
  private MenuItem menuItemExportDb;

  @Inject
  public MainMenu(final Inflater inflater) {
    inflater.inflate("components/main_menu", this, this, "main");
  }

  @Override
  public void initialize(final URL location, final ResourceBundle resources) {

  }

  /**
   * Show a file chooser dialog and save the .sqlite database using the obtained path.
   */
  @FXML
  private void saveDbAs() {

  }

  /**
   * Save the .sqlite database.
   */
  @FXML
  private void saveDb() {

  }

  /**
   * Open a .sqlite database.
   */
  @FXML
  private void openDb() {

  }

  /**
   * Export the database to raw .xml files.
   */
  @FXML
  private void exportDb() {

  }

  /**
   * Close the application.
   */
  @FXML
  private void closeWindow() {

  }

}
