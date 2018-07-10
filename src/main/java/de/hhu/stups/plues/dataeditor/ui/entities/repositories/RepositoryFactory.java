package de.hhu.stups.plues.dataeditor.ui.entities.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class RepositoryFactory {

  private ConfigurableApplicationContext context;

  @Autowired
  public RepositoryFactory(ConfigurableApplicationContext context) {
    this.context = context;
  }

  public AbstractUnitRepository abstractUnitRepository() {
    return context.getBean(AbstractUnitRepository.class);
  }

  public CourseRepository courseRepository() {
    return context.getBean(CourseRepository.class);
  }

  public GroupRepository groupRepository() {
    return context.getBean(GroupRepository.class);
  }

  public LevelRepository levelRepository() {
    return context.getBean(LevelRepository.class);
  }

  public ModuleRepository moduleRepository() {
    return context.getBean(ModuleRepository.class);
  }

  public SessionRepository sessionRepository() {
    return context.getBean(SessionRepository.class);
  }

  public UnitRepository unitRepository() {
    return context.getBean(UnitRepository.class);
  }
}
