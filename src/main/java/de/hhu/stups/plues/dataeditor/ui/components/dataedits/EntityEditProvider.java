package de.hhu.stups.plues.dataeditor.ui.components.dataedits;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

@Singleton
public class EntityEditProvider {

  private final Provider<CourseEdit> courseEditProvider;
  private final Provider<LevelEdit> levelEditProvider;
  private final Provider<ModuleEdit> moduleEditProvider;
  private final Provider<AbstractUnitEdit> abstractUnitEditProvider;
  private final Provider<UnitEdit> unitEditProvider;

  /**
   * Delegate provider for edit views.
   */
  @Inject
  public EntityEditProvider(final Provider<CourseEdit> courseEditProvider,
                            final Provider<LevelEdit> levelEditProvider,
                            final Provider<ModuleEdit> moduleEditProvider,
                            final Provider<AbstractUnitEdit> abstractUnitEditProvider,
                            final Provider<UnitEdit> unitEditProvider) {
    this.courseEditProvider = courseEditProvider;
    this.levelEditProvider = levelEditProvider;
    this.moduleEditProvider = moduleEditProvider;
    this.abstractUnitEditProvider = abstractUnitEditProvider;
    this.unitEditProvider = unitEditProvider;
  }

  public CourseEdit getCourseEdit() {
    return courseEditProvider.get();
  }
  public LevelEdit getLevelEdit() {
    return levelEditProvider.get();
  }
  public ModuleEdit getModuleEdit() {
    return moduleEditProvider.get();
  }
  public AbstractUnitEdit getAbstractUnitEdit() {
    return abstractUnitEditProvider.get();
  }
  public UnitEdit getUnitEdit() {
    return unitEditProvider.get();
  }

}
