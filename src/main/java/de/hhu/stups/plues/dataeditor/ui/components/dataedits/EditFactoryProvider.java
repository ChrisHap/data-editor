package de.hhu.stups.plues.dataeditor.ui.components.dataedits;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.hhu.stups.plues.dataeditor.ui.components.dataedits.factories.AbstractUnitEditFactory;
import de.hhu.stups.plues.dataeditor.ui.components.dataedits.factories.CourseEditFactory;
import de.hhu.stups.plues.dataeditor.ui.components.dataedits.factories.GroupEditFactory;
import de.hhu.stups.plues.dataeditor.ui.components.dataedits.factories.LevelEditFactory;
import de.hhu.stups.plues.dataeditor.ui.components.dataedits.factories.ModuleEditFactory;
import de.hhu.stups.plues.dataeditor.ui.components.dataedits.factories.SessionEditFactory;
import de.hhu.stups.plues.dataeditor.ui.components.dataedits.factories.UnitEditFactory;

@Singleton
public class EditFactoryProvider {

  private final CourseEditFactory courseEditFactory;
  private final AbstractUnitEditFactory abstractUnitEditFactory;
  private final UnitEditFactory unitEditFactory;
  private final ModuleEditFactory moduleEditFactory;
  private final LevelEditFactory levelEditFactory;
  private final GroupEditFactory groupEditFactory;
  private final SessionEditFactory sessionEditFactory;

  /**
   * Delegate provider for edit views.
   */
  @Inject
  public EditFactoryProvider(final CourseEditFactory courseEditFactory,
                             final AbstractUnitEditFactory abstractUnitEditFactory,
                             final UnitEditFactory unitEditFactory,
                             final ModuleEditFactory moduleEditFactory,
                             final GroupEditFactory groupEditFactory,
                             final SessionEditFactory sessionEditFactory,
                             final LevelEditFactory levelEditFactory) {
    this.courseEditFactory = courseEditFactory;
    this.abstractUnitEditFactory = abstractUnitEditFactory;
    this.unitEditFactory = unitEditFactory;
    this.moduleEditFactory = moduleEditFactory;
    this.levelEditFactory = levelEditFactory;
    this.groupEditFactory = groupEditFactory;
    this.sessionEditFactory = sessionEditFactory;
  }

  public CourseEditFactory getCourseEditFactory() {
    return courseEditFactory;
  }

  public AbstractUnitEditFactory getAbstractUnitEditFactory() {
    return abstractUnitEditFactory;
  }

  public UnitEditFactory getUnitEditFactory() {
    return unitEditFactory;
  }

  public SessionEditFactory getSessionEditFactory() {
    return sessionEditFactory;
  }

  public ModuleEditFactory getModuleEditFactory() {
    return moduleEditFactory;
  }

  public LevelEditFactory getLevelEditFactory() {
    return levelEditFactory;
  }

  public GroupEditFactory getGroupEditFactory() {
    return groupEditFactory;
  }
}
