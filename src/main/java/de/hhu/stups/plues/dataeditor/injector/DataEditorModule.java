package de.hhu.stups.plues.dataeditor.injector;

import javafx.fxml.FXMLLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.Locale;
import java.util.ResourceBundle;
import javax.sql.DataSource;

@Configuration
public class DataEditorModule {
  private final ConfigurableApplicationContext context;
  private final Locale locale = new Locale("en");
  private final ResourceBundle bundle = ResourceBundle.getBundle("lang.main", locale);

  @Autowired
  public DataEditorModule(ConfigurableApplicationContext context) {
    this.context = context;
  }


  @Bean
  public Locale getLocale() {
    return locale;
  }

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
  public DataSource dataSource() {
    //TODO dynamisch umsetzen
    DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
    dataSourceBuilder.type(org.sqlite.SQLiteDataSource.class);
    dataSourceBuilder.driverClassName("org.sqlite.JDBC");
    dataSourceBuilder.url("jdbc:sqlite:cs.sqlite3");
    return dataSourceBuilder.build();
  }
}
