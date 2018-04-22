package de.hhu.stups.plues.dataeditor.injector;

import de.codecentric.centerdevice.MenuToolkit;
import javafx.fxml.FXMLLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.Locale;
import java.util.ResourceBundle;

@Configuration
public class DataEditorModule {
  private final ConfigurableApplicationContext context;
  private final Locale locale = new Locale("en");
  private final ResourceBundle bundle = ResourceBundle.getBundle("lang.main", locale);

  @Autowired
  public DataEditorModule(ConfigurableApplicationContext context) {
    this.context = context;
  }

  private static final boolean IS_MAC = System.getProperty("os.name", "")
      .toLowerCase().contains("mac");

  /**
   * Following the given Code example
   * //    if (IS_MAC) {
   * //      bind(MenuToolkit.class).toInstance(MenuToolkit.toolkit(locale));
   * //    } else {
   * //      bind(MenuToolkit.class).toProvider(Providers.of(null));
   * //    }
   * @return the requested MenuToolkit
   */
  @Bean
  public MenuToolkit getMenuToolkit() {
    if (IS_MAC) {
      return MenuToolkit.toolkit(locale);
    }
    return null;
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

}
