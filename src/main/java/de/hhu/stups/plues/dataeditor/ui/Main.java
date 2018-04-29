package de.hhu.stups.plues.dataeditor.ui;

import de.hhu.stups.plues.dataeditor.ui.controller.DataEditor;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("de.hhu.stups.plues.dataeditor")
@SpringBootApplication
public class Main extends Application {

  private ConfigurableApplicationContext springContext;
  private DataEditor root;

  public static void main(final String... args) {
    launch(args);
  }

  @Override
  public void init() {
    springContext = SpringApplication.run(Main.class);
  }


  @Override
  public void start(final Stage stage) {
    root = springContext.getBean(DataEditor.class);
    final Scene mainScene = new Scene(root, 1024, 768);
    root.getStylesheets().add("styles/main.css");

    stage.setTitle("PlÜS Data-Editor");
    stage.setScene(mainScene);
    stage.setOnCloseRequest(e -> Platform.exit());

    stage.show();
  }

  @Override
  public void stop() {
    root.closeApplication();
    springContext.close();
    Platform.exit();
  }

  //TODO später vielleicht anders erhalten
  @Bean
  public ConfigurableApplicationContext getSpringContext() {
    return springContext;
  }
}
