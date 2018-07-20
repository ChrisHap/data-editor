package de.hhu.stups.plues.dataeditor.ui.entities.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RepositoryFactory {
  private final CourseRepository courseRepository;
  private final LevelRepository levelRepository;
  private final ModuleRepository moduleRepository;
  private final AbstractUnitRepository abstractUnitRepository;
  private final UnitRepository unitRepository;
  private final GroupRepository groupRepository;
  private final SessionRepository sessionRepository;

  /**
   * A Factory Module that provides all Repositories to the components.
   */
  @Autowired
  public RepositoryFactory(CourseRepository courseRepository,
                           LevelRepository levelRepository,
                           ModuleRepository moduleRepository,
                           AbstractUnitRepository abstractUnitRepository,
                           UnitRepository unitRepository, GroupRepository groupRepository,
                           SessionRepository sessionRepository) {
    this.courseRepository = courseRepository;
    this.levelRepository = levelRepository;
    this.moduleRepository = moduleRepository;
    this.abstractUnitRepository = abstractUnitRepository;
    this.unitRepository = unitRepository;
    this.groupRepository = groupRepository;
    this.sessionRepository = sessionRepository;
  }

  public SessionRepository getSessionRepository() {
    return sessionRepository;
  }

  public GroupRepository getGroupRepository() {
    return groupRepository;
  }

  public UnitRepository getUnitRepository() {
    return unitRepository;
  }

  public AbstractUnitRepository getAbstractUnitRepository() {
    return abstractUnitRepository;
  }

  public ModuleRepository getModuleRepository() {
    return moduleRepository;
  }

  public LevelRepository getLevelRepository() {
    return levelRepository;
  }

  public CourseRepository getCourseRepository() {
    return courseRepository;
  }
}
