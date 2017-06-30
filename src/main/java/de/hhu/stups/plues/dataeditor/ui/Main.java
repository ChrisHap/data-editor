package de.hhu.stups.plues.dataeditor.ui;

import com.google.inject.Guice;
import com.google.inject.Injector;

import de.hhu.stups.plues.dataeditor.injector.DataEditorModule;
import de.hhu.stups.plues.dataeditor.ui.controller.DataEditor;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

  public static void main(final String... args) {
    launch(args);
  }

  @Override
  public void start(final Stage stage) throws Exception {
    final Injector injector = Guice.createInjector(new DataEditorModule());

    final DataEditor root = injector.getInstance(DataEditor.class);
    final Scene mainScene = new Scene(root, 1024, 768);
    root.getStylesheets().add("styles/main.css");

    stage.setTitle("PlÜS Data-Editor");
    stage.setScene(mainScene);
    stage.setOnCloseRequest(e -> Platform.exit());

    stage.show();
  }

  @Override
  public void stop() {
    Platform.exit();
  }
}
