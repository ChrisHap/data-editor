package de.hhu.stups.plues.dataeditor.ui.components.dataedits;

import de.hhu.stups.plues.dataeditor.ui.database.DataService;
import de.hhu.stups.plues.dataeditor.ui.entities.AbstractUnitWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.CourseWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.GroupWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.LevelWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.ModuleWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.Session;
import de.hhu.stups.plues.dataeditor.ui.entities.SessionWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.UnitWrapper;
import de.hhu.stups.plues.dataeditor.ui.layout.Inflater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class EditViewProvider {

  private final ConfigurableApplicationContext springContext;
  private final DataService dataService;

  /**
   * Delegate provider for edit views.
   */
  @Autowired
  public EditViewProvider(ConfigurableApplicationContext springContext,
                          final DataService dataService) {
    this.springContext = springContext;
    this.dataService = dataService;
  }

  /**
   * Provides a CourseEdit view.
   */
  public CourseEdit getCourseEditView(CourseWrapper courseWrapper) {
    if (courseWrapper == null) {
      courseWrapper = CourseWrapper.createEmptyCourseWrapper();
    }
    return new CourseEdit(springContext.getBean(Inflater.class),dataService,courseWrapper);
  }

  /**
   * Provides a AbstractUnitEdit view.
   */
  public AbstractUnitEdit getAbstractUnitEditView(AbstractUnitWrapper abstractUnitWrapper) {
    if (abstractUnitWrapper == null) {
      abstractUnitWrapper = AbstractUnitWrapper.createEmptyAbstractUnitWrapper();
    }
    return new AbstractUnitEdit(springContext.getBean(Inflater.class),
            dataService, abstractUnitWrapper);
  }

  /**
   * Provides a UnitEdit view.
   */
  public UnitEdit getUnitEditView(UnitWrapper unitWrapper) {
    if (unitWrapper == null) {
      unitWrapper = UnitWrapper.createEmptyUnitWrapper();
    }
    return new UnitEdit(springContext.getBean(Inflater.class),dataService,unitWrapper);
  }

  /**
   * Provides a SessionEdit view.
   */
  public SessionEdit getSessionEditView(SessionWrapper sessionWrapper) {
    if (sessionWrapper == null) {
      Session session = new Session();
      session.setDay("");
      session.setDuration(0);
      session.setTime(0);
      session.setRhythm(0);
      sessionWrapper = new SessionWrapper(session);
    }
    return new SessionEdit(springContext.getBean(Inflater.class),dataService,sessionWrapper);
  }

  /**
   * Provides a ModuleEdit view.
   */
  public ModuleEdit getModuleEditView(ModuleWrapper moduleWrapper) {
    if (moduleWrapper == null) {
      moduleWrapper = ModuleWrapper.createEmptyModuleWrapper();
    }
    return new ModuleEdit(springContext.getBean(Inflater.class),dataService,moduleWrapper);
  }

  /**
   * Provides a LevelEdit view.
   */
  public LevelEdit getLevelEditView(LevelWrapper levelWrapper) {
    if (levelWrapper == null) {
      levelWrapper = LevelWrapper.createEmptyLevelWrapper();
    }
    return new LevelEdit(springContext.getBean(Inflater.class),dataService,levelWrapper);
  }

  /**
   * Provides a GroupEdit view.
   */
  public GroupEdit getGroupEditView(GroupWrapper groupWrapper) {
    if (groupWrapper == null) {
      groupWrapper = GroupWrapper.createEmptyGroupWrapper();
    }
    return new GroupEdit(springContext.getBean(Inflater.class),dataService,groupWrapper);
  }
}
