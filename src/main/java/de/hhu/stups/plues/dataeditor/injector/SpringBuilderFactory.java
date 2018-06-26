package de.hhu.stups.plues.dataeditor.injector;

import javafx.fxml.JavaFXBuilderFactory;
import javafx.util.Builder;
import javafx.util.BuilderFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;


// copied from https://github.com/bendisposto/prob2-ui

/**
 * Used to inject instances while inflating an fxml file.
 */
@Component
public class SpringBuilderFactory implements BuilderFactory {

  private final BuilderFactory javafxDefaultBuilderFactory = new JavaFXBuilderFactory();
  private ConfigurableApplicationContext context;

  @Autowired
  public SpringBuilderFactory(ConfigurableApplicationContext context) {
    this.context = context;
  }

  /**
   * Returns a builder for building an instance from a class.
   * @param type the desired class of the instance.
   * @return the instance of the given class.
   */
  @Override
  public Builder<?> getBuilder(final Class<?> type) {
    final Object instance;
    try {
      instance = context.getBean(type);
    } catch (NoSuchBeanDefinitionException noBean) {
      return javafxDefaultBuilderFactory.getBuilder(type);
    }
    return () -> instance;
  }
}