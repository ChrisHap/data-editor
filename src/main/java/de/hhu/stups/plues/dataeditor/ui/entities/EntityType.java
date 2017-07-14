package de.hhu.stups.plues.dataeditor.ui.entities;

public enum EntityType {
  COURSE, LEVEL, MODULE, MODULE_LEVEL, ABSTRACT_UNIT, UNIT, SESSION;

  @Override
  public String toString() {
    switch (this) {
      case COURSE:
        return "course";
      case LEVEL:
        return "level";
      case MODULE:
        return "module";
      case MODULE_LEVEL:
        return "moduleLevel";
      case ABSTRACT_UNIT:
        return "abstractUnit";
      case UNIT:
        return "unit";
      case SESSION:
        return "session";
      default:
        return "";
    }
  }
}
