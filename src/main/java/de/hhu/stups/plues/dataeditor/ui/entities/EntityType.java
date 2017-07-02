package de.hhu.stups.plues.dataeditor.ui.entities;

public enum EntityType {
  COURSE, LEVEL, MODULE, ABSTRACT_UNIT, UNIT, SESSION;

  @Override
  public String toString() {
    switch (this) {
      case COURSE:
        return "course";
      case LEVEL:
        return "level";
      case MODULE:
        return "module";
      case ABSTRACT_UNIT:
        return "abstract_unit";
      case UNIT:
        return "unit";
      case SESSION:
        return "session";
      default:
        return "";
    }
  }
}
