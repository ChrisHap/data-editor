package de.hhu.stups.plues.dataeditor.ui.components.dataedits;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.inject.Provider;

@Singleton
public class ViewProvider {

  private final Provider<CourseEdit> courseEditProvider;

  @Inject
  public ViewProvider(final Provider<CourseEdit> courseEditProvider) {
    this.courseEditProvider = courseEditProvider;
  }

  public Provider<CourseEdit> courseEditProvider() {
    return courseEditProvider;
  }

}
