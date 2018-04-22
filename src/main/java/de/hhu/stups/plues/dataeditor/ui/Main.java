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

  /**
   * Die main Methode ist der Einstiegspunkt des Programms und startet die Anwendung.
   * @param args Die übergebenen Kommandozeilenargumente.
   */
  public static void main(final String... args) {
    launch(args);
  }

  /**
   * Die Methode initialisiert die Spring Komponente der Anwendung.
   * Sie überschreibt die Methode der Oberklasse Application.
   * Sie wird automatisch vor der Methode start aufgerufen.
   * @throws Exception wird von der Oberklasse benötigt.
   */
  @Override
  public void init() throws Exception {
    springContext = SpringApplication.run(Main.class);
  }


  @Override
  public void start(final Stage stage) throws Exception {
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

  @Bean
  public ConfigurableApplicationContext getSpringContext() {
    return springContext;
  }
}
