package de.hhu.stups.plues.dataeditor.ui.components.dataedits.factories;

import de.hhu.stups.plues.data.entities.Course;
import de.hhu.stups.plues.dataeditor.ui.components.dataedits.CourseEdit;

@FunctionalInterface
public interface CourseEditFactory {
  CourseEdit create(final Course course);
}
