package de.hhu.stups.plues.dataeditor.ui.components.dataedits;

import de.hhu.stups.plues.dataeditor.ui.entities.AbstractUnit;
import de.hhu.stups.plues.dataeditor.ui.entities.Course;
import de.hhu.stups.plues.dataeditor.ui.entities.Group;
import de.hhu.stups.plues.dataeditor.ui.entities.Level;
import de.hhu.stups.plues.dataeditor.ui.entities.Module;
import de.hhu.stups.plues.dataeditor.ui.entities.Session;
import de.hhu.stups.plues.dataeditor.ui.entities.Unit;
import de.hhu.stups.plues.dataeditor.ui.database.DataService;
import de.hhu.stups.plues.dataeditor.ui.entities.AbstractUnitWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.CourseWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.GroupWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.LevelWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.ModuleWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.SessionWrapper;
import de.hhu.stups.plues.dataeditor.ui.entities.UnitWrapper;
import de.hhu.stups.plues.dataeditor.ui.layout.Inflater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashSet;

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
      Course course = new Course();
      course.setCreditPoints(5);
      course.setDegree("bk");
      course.setPo(2016);
      courseWrapper = new CourseWrapper(course);
    }
    return new CourseEdit(springContext.getBean(Inflater.class),dataService,courseWrapper);
  }

  /**
   * Provides a AbstractUnitEdit view.
   */
  public AbstractUnitEdit getAbstractUnitEditView(AbstractUnitWrapper abstractUnitWrapper) {
    if (abstractUnitWrapper == null) {
      AbstractUnit abstractUnit = new AbstractUnit();
      abstractUnit.setTitle("");
      abstractUnit.setId(0);
      abstractUnit.setModules(new HashSet<>());
      abstractUnit.setUnits(new HashSet<>());
      abstractUnitWrapper = new AbstractUnitWrapper(abstractUnit);
    }
    return new AbstractUnitEdit(springContext.getBean(Inflater.class),
            dataService, abstractUnitWrapper);
  }

  /**
   * Provides a UnitEdit view.
   */
  public UnitEdit getUnitEditView(UnitWrapper unitWrapper) {
    if (unitWrapper == null) {
      Unit unit = new Unit();
      unit.setSemesters(new HashSet<>());
      unitWrapper = new UnitWrapper(unit);
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
      Module module = new Module();
      module.setPordnr(0);
      module.setBundled(false);
      module.setElectiveUnits(0);
      moduleWrapper = new ModuleWrapper(module);
    }
    return new ModuleEdit(springContext.getBean(Inflater.class),dataService,moduleWrapper);
  }

  /**
   * Provides a LevelEdit view.
   */
  public LevelEdit getLevelEditView(LevelWrapper levelWrapper) {
    if (levelWrapper == null) {
      Level level = new Level();
      level.setId(0);
      levelWrapper = new LevelWrapper(level);
    }
    return new LevelEdit(springContext.getBean(Inflater.class),dataService,levelWrapper);
  }

  /**
   * Provides a GroupEdit view.
   */
  public GroupEdit getGroupEditView(GroupWrapper groupWrapper) {
    if (groupWrapper == null) {
      Group group = new Group();
      group.setHalfSemester(0);
      Unit unit = new Unit();
      unit.setTitle("");
      unit.setSemesters(new HashSet<>());
      group.setUnit(unit);
      group.setSessions(new HashSet<>());
      groupWrapper = new GroupWrapper(group);
      groupWrapper.setUnit(new UnitWrapper(unit));
    }
    return new GroupEdit(springContext.getBean(Inflater.class),dataService,groupWrapper);
  }
}
