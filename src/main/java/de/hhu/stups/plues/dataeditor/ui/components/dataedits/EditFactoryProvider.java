package de.hhu.stups.plues.dataeditor.ui.components.dataedits;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.hhu.stups.plues.dataeditor.ui.components.dataedits.factories.AbstractUnitEditFactory;
import de.hhu.stups.plues.dataeditor.ui.components.dataedits.factories.CourseEditFactory;
import de.hhu.stups.plues.dataeditor.ui.components.dataedits.factories.LevelEditFactory;
import de.hhu.stups.plues.dataeditor.ui.components.dataedits.factories.ModuleEditFactory;
import de.hhu.stups.plues.dataeditor.ui.components.dataedits.factories.UnitEditFactory;

@Singleton
public class EditFactoryProvider {

  private final CourseEditFactory courseEditFactory;
  private final AbstractUnitEditFactory abstractUnitEditFactory;
  private final UnitEditFactory unitEditFactory;
  private ModuleEditFactory moduleEditFactory;
  private LevelEditFactory levelEditFactory;

  /**
   * Delegate provider for edit views.
   */
  @Inject
  public EditFactoryProvider(final CourseEditFactory courseEditFactory,
                             final AbstractUnitEditFactory abstractUnitEditFactory,
                             final UnitEditFactory unitEditFactory,
                             final ModuleEditFactory moduleEditFactory,
                             final LevelEditFactory levelEditFactory) {
    this.courseEditFactory = courseEditFactory;
    this.abstractUnitEditFactory = abstractUnitEditFactory;
    this.unitEditFactory = unitEditFactory;
    this.moduleEditFactory = moduleEditFactory;
    this.levelEditFactory = levelEditFactory;
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

  public ModuleEditFactory getModuleEditFactory() {
    return moduleEditFactory;
  }

  public LevelEditFactory getLevelEditFactory() {
    return levelEditFactory;
  }
}
