package de.hhu.stups.plues.dataeditor.ui.components;

import com.google.inject.Inject;

import de.hhu.stups.plues.dataeditor.ui.database.DbService;
import de.hhu.stups.plues.dataeditor.ui.database.events.CloseDbEvent;
import de.hhu.stups.plues.dataeditor.ui.database.events.LoadDbEvent;
import de.hhu.stups.plues.dataeditor.ui.layout.Inflater;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ResourceBundle;

public class MainMenu extends MenuBar implements Initializable {

  private final Logger logger = LoggerFactory.getLogger(getClass());
  private final DbService dbService;

  private ResourceBundle resources;

  @FXML
  @SuppressWarnings("unused")
  private MenuItem menuItemOpenDb;
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
    menuItemSaveDbAs.disableProperty().bind(dbService.storeProperty().isNull());
    menuItemExportDb.disableProperty().bind(dbService.storeProperty().isNull());
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
   * Show a file chooser dialog and save the .sqlite database using the obtained path.
   */
  @FXML
  @SuppressWarnings("unused")
  private void saveDbAs() {
    final Path source = dbService.getDbFile().toPath();
    final FileChooser fileChooser = new FileChooser();
    final FileChooser.ExtensionFilter extFilter =
        new FileChooser.ExtensionFilter("SQLite Database (*.sqlite3)", "*.sqlite3");
    fileChooser.getExtensionFilters().add(extFilter);
    fileChooser.setInitialFileName(source.getFileName().toString());
    final File file = fileChooser.showSaveDialog(this.getScene().getWindow());
    if (file != null) {
      final Path destination = file.toPath();
      try {
        Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
      } catch (final IOException exception) {
        logger.error("Error saving .sqlite3 database to " + destination.toString(), exception);
      }
    }
  }

  /**
   * Export the database to raw .xml files.
   */
  @FXML
  @SuppressWarnings("unused")
  private void exportDb() {
    //
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
