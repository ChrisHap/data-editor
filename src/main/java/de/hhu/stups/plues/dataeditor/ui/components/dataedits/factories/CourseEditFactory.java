package de.hhu.stups.plues.dataeditor.ui.components.dataedits.factories;

import com.google.inject.Inject;

import de.hhu.stups.plues.dataeditor.ui.components.dataedits.CourseEdit;
import de.hhu.stups.plues.dataeditor.ui.entities.CourseWrapper;

public interface CourseEditFactory {
  @Inject
  CourseEdit create(CourseWrapper courseWrapper);
}
