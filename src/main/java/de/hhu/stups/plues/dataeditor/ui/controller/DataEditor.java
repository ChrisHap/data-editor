package de.hhu.stups.plues.dataeditor.ui.controller;

import de.hhu.stups.plues.dataeditor.ui.components.SideBar;
import de.hhu.stups.plues.dataeditor.ui.components.dataedits.CourseEdit;
import de.hhu.stups.plues.dataeditor.ui.components.dataedits.ViewProvider;
import de.hhu.stups.plues.dataeditor.ui.layout.Inflater;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.fxmisc.easybind.EasyBind;

import javax.inject.Inject;
import javax.inject.Provider;
import java.net.URL;
import java.util.ResourceBundle;

public class DataEditor extends VBox implements Initializable {

  private final ViewProvider viewProvider;
  private double lastDividerPosition;

  private SplitPane.Divider splitPaneDivider;

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
  private TabPane tabPane;
  @FXML
  @SuppressWarnings("unused")
  private Button btToggleDivider;

  @Inject
  public DataEditor(final Inflater inflater,
                    final ViewProvider viewProvider) {
    this.viewProvider = viewProvider;
    inflater.inflate("controller/data_editor", this, this, "main");
  }

  @Override
  public void initialize(final URL location, final ResourceBundle resources) {
    splitPaneDivider = mainSplitPane.getDividers().get(0);
    lastDividerPosition = splitPaneDivider.getPosition();
    mainSplitPane.prefHeightProperty().bind(heightProperty());
    mainSplitPane.heightProperty().addListener((observable, oldValue, newValue) -> {
      AnchorPane.setLeftAnchor(btToggleDivider, 0.0);
      AnchorPane.setTopAnchor(btToggleDivider, newValue.doubleValue() / 2
          - btToggleDivider.getHeight() / 2);
    });

    EasyBind.subscribe(sideBar.showSideBarProperty(), showSideBar -> {
      if (showSideBar && !mainSplitPane.getItems().contains(sideBar)) {
        mainSplitPane.getItems().add(0, sideBar);
        splitPaneDivider = mainSplitPane.getDividers().get(0);
      } else if (!showSideBar) {
        lastDividerPosition = splitPaneDivider.getPosition();
      }
      toggleSideBar(showSideBar);
    });

    tabPane.prefWidthProperty().bind(contentAnchorPane.widthProperty());

    final Tab tab = new Tab();
    tab.setContent(viewProvider.courseEditProvider().get());
    tabPane.getTabs().add(tab);
  }

  /**
   * Run an animation either showing or hiding the {@link #sideBar}.
   */
  private void toggleSideBar(final boolean showSideBar) {
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
}
