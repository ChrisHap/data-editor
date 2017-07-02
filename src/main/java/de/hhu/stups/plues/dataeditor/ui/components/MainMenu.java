package de.hhu.stups.plues.dataeditor.ui.components;

import com.google.inject.Inject;

import de.hhu.stups.plues.dataeditor.ui.database.DbService;
import de.hhu.stups.plues.dataeditor.ui.database.events.LoadDbEvent;
import de.hhu.stups.plues.dataeditor.ui.layout.Inflater;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class MainMenu extends MenuBar implements Initializable {

  private final DbService dbService;

  private ResourceBundle resources;

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
  public MainMenu(final Inflater inflater,
                  final DbService dbService) {
    this.dbService = dbService;
    inflater.inflate("components/main_menu", this, this, "main");
  }

  @Override
  public void initialize(final URL location, final ResourceBundle resources) {
    this.resources = resources;
  }

  /**
   * Show a file chooser dialog and save the .sqlite database using the obtained path.
   */
  @FXML
  @SuppressWarnings("unused")
  private void saveDbAs() {

  }

  /**
   * Save the .sqlite database.
   */
  @FXML
  @SuppressWarnings("unused")
  private void saveDb() {

  }

  /**
   * Open a .sqlite database.
   */
  @FXML
  @SuppressWarnings("unused")
  private void openDb() {
    final FileChooser fileChooser = new FileChooser();
    final FileChooser.ExtensionFilter extFilter =
        new FileChooser.ExtensionFilter("SQLite Database (*.sqlite3)", "*.sqlite3");
    fileChooser.getExtensionFilters().add(extFilter);
    fileChooser.setTitle(resources.getString("openDbTitle"));
    final File file = fileChooser.showOpenDialog(this.getScene().getWindow());
    if (file != null) {
      dbService.dbEventSource().push(new LoadDbEvent(file));
    }
  }

  /**
   * Export the database to raw .xml files.
   */
  @FXML
  @SuppressWarnings("unused")
  private void exportDb() {

  }

  /**
   * Close the application.
   */
  @FXML
  @SuppressWarnings("unused")
  private void closeWindow() {
    ((Stage) this.getScene().getWindow()).close();
  }

}
