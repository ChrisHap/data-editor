package de.hhu.stups.plues.dataeditor.ui.controller;

import de.hhu.stups.plues.dataeditor.ui.components.DataEditView;
import de.hhu.stups.plues.dataeditor.ui.components.SideBar;
import de.hhu.stups.plues.dataeditor.ui.database.DbService;
import de.hhu.stups.plues.dataeditor.ui.database.events.CloseDbEvent;
import de.hhu.stups.plues.dataeditor.ui.layout.Inflater;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.controlsfx.control.StatusBar;
import org.fxmisc.easybind.EasyBind;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class DataEditor extends VBox implements Initializable {

  private final DbService dbService;
  private double lastDividerPosition;
  private SplitPane.Divider splitPaneDivider;
  private ResourceBundle resources;

  @FXML
  @SuppressWarnings("unused")
  private SideBar sideBar;
  @FXML
  @SuppressWarnings("unused")
  private SplitPane mainSplitPane;
  @FXML
  @SuppressWarnings("unused")
  private AnchorPane contentAnchorPane;
  @FXML
  @SuppressWarnings("unused")
  private DataEditView dataEditView;
  @FXML
  @SuppressWarnings("unused")
  private Button btToggleDivider;
  @FXML
  @SuppressWarnings("unused")
  private StatusBar statusBar;

  @Autowired
  public DataEditor(final Inflater inflater,
                    final DbService dbService) {
    this.dbService = dbService;
    inflater.inflate("controller/data_editor", this, this, "main");
  }


  @Override
  public void initialize(final URL location, final ResourceBundle resources) {
    this.resources = resources;
    initializeSplitPane();
    dataEditView.prefWidthProperty().bind(contentAnchorPane.widthProperty()
          .subtract(btToggleDivider.widthProperty()));
    dataEditView.prefHeightProperty().bind(heightProperty().subtract(
          statusBar.heightProperty().add(50)));
    dataEditView.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
      final KeyCombination combination = new KeyCodeCombination(KeyCode.W,
            KeyCombination.CONTROL_DOWN);
      public void handle(KeyEvent keyEvent) {
        if (combination.match(keyEvent)) {
          dataEditView.getTabs().remove(dataEditView.getSelectionModel().getSelectedItem());
          keyEvent.consume();
        }
      }
    });
    EasyBind.subscribe(dbService.dbTaskProperty(), this::setStatusBarProgress);
  }

  private void setStatusBarProgress(Task<Void> task) {
    Platform.runLater(() -> {
      if (task == null) {
        statusBar.setProgress(0);
        statusBar.textProperty().set(resources.getString("idle"));
      } else {
        statusBar.setProgress(-1);
        statusBar.textProperty().set(resources.getString("loading"));
      }
    });
  }

  private void initializeSplitPane() {
    splitPaneDivider = mainSplitPane.getDividers().get(0);
    lastDividerPosition = splitPaneDivider.getPosition();
    mainSplitPane.prefHeightProperty().bind(heightProperty());
    btToggleDivider.graphicProperty().bind(Bindings.when(sideBar.showSideBarProperty())
          .then(getToggleIconView(FontAwesomeIcon.ARROW_LEFT))
          .otherwise(getToggleIconView(FontAwesomeIcon.ARROW_RIGHT)));
    EasyBind.subscribe(sideBar.showSideBarProperty(), this::showOrHideSideBar);
    EasyBind.subscribe(mainSplitPane.heightProperty(),
        number -> updateToggleDividerPos(number.doubleValue()));
  }

  private FontAwesomeIconView getToggleIconView(final FontAwesomeIcon fontAwesomeIcon) {
    final FontAwesomeIconView fontAwesomeIconView = new FontAwesomeIconView(fontAwesomeIcon);
    fontAwesomeIconView.setGlyphSize(12);
    return fontAwesomeIconView;
  }

  /**
   * Update the position of {@link #btToggleDivider} to be centered when the height of
   * {@link #mainSplitPane} changes.
   */
  private void updateToggleDividerPos(final double height) {
    AnchorPane.setTopAnchor(btToggleDivider, height / 2
          - btToggleDivider.getHeight() / 2);
  }

  private void showOrHideSideBar(final boolean showSideBar) {
    if (showSideBar && mainSplitPane.getItems().contains(sideBar)) {
      return;
    }
    if (showSideBar) {
      // add split pane if bar was closed
      mainSplitPane.getItems().add(0, sideBar);
      splitPaneDivider = mainSplitPane.getDividers().get(0);
    } else {
      // store current divider position when closing
      lastDividerPosition = splitPaneDivider.getPosition();
    }
    animateSideBar(showSideBar);
  }

  /**
   * Run an animation either showing or hiding the {@link #sideBar}.
   */
  private void animateSideBar(final boolean showSideBar) {
    final Timeline timeline = new Timeline();
    if (showSideBar) {
      mainSplitPane.setDividerPosition(0, 0.0);
    }
    final KeyValue dividerPosition =
        new KeyValue(splitPaneDivider.positionProperty(), showSideBar ? lastDividerPosition : 0.0);
    final KeyFrame openSideBar = new KeyFrame(Duration.millis(250), dividerPosition);
    timeline.getKeyFrames().add(openSideBar);
    timeline.setOnFinished(event -> {
      if (!showSideBar) {
        mainSplitPane.getItems().remove(sideBar);
      }
    });
    Platform.runLater(timeline::play);
  }

  @FXML
  @SuppressWarnings("unused")
  private void toggleDivider() {
    sideBar.showSideBarProperty().set(!sideBar.showSideBarProperty().get());
  }

  public void closeApplication() {
    dbService.dbEventSource().push(new CloseDbEvent());
  }
}
