package de.hhu.stups.plues.dataeditor.injector;

import de.hhu.stups.plues.dataeditor.ui.database.DbService;
import javafx.fxml.FXMLLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;

import java.util.Locale;
import java.util.ResourceBundle;
import javax.sql.DataSource;

/**
 * The DataEditorModule provides the necessary beans fór dependency injection with spring.
 */
@Configuration
public class DataEditorModule {
  private final ConfigurableApplicationContext context;
  private final Locale locale = new Locale("en");
  private final ResourceBundle bundle = ResourceBundle.getBundle("lang.main", locale);
  private final DbService dbService;

  /**
   * Autowired constructor.
   * @param context the application context provided by Main and used to create instances of beans.
   * @param dbService the dbService is injected to provide the DataSource.
   */
  @Autowired
  public DataEditorModule(ConfigurableApplicationContext context, DbService dbService) {
    this.context = context;
    this.dbService = dbService;
  }

  /**
   * Provides the Locale for choosing the language.
   */
  @Bean
  public Locale getLocale() {
    return locale;
  }

  /**
   * Provides the resource bundle where the necessary text is stored.
   */
  @Bean
  public ResourceBundle getResourceBundle() {
    return bundle;
  }

  /**
   * Provide the {@link FXMLLoader}.
   */
  @Bean
  @Autowired
  @Scope("prototype")
  public FXMLLoader provideLoader(final SpringBuilderFactory builderFactory,
                                  final ResourceBundle bundle) {
    final FXMLLoader fxmlLoader = new FXMLLoader();
    fxmlLoader.setBuilderFactory(builderFactory);
    fxmlLoader.setControllerFactory(context::getBean);
    fxmlLoader.setResources(bundle);
    return fxmlLoader;
  }

  /**
   * Provide the DataSource.
   */
  @Bean
  @Lazy
  public DataSource dataSource() {
    return dbService.dataSourceProperty().get();
  }
}
