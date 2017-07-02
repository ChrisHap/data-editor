package de.hhu.stups.plues.dataeditor.ui.components.dataedits;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.hhu.stups.plues.dataeditor.ui.components.dataedits.factories.CourseEditFactory;

@Singleton
public class EditViewFactories {

  private final CourseEditFactory courseEditFactory;

  @Inject
  public EditViewFactories(final CourseEditFactory courseEditFactory) {
    this.courseEditFactory = courseEditFactory;
  }

  public CourseEditFactory courseEditFactory() {
    return courseEditFactory;
  }
}
