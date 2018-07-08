package de.hhu.stups.plues.dataeditor.ui.entities;

public enum CourseKzfa {
  MAJOR, MINOR;

  /**
   * Return the {@link CourseKzfa} for a given kzfa like 'N' or 'H'.
   */
  public static CourseKzfa getKzfaFromString(final String kzfa) {
    if ("N".equals(kzfa)) {
      return MINOR;
    } else if ("H".equals(kzfa)) {
      return MAJOR;
    }
    return null;
  }

  /**
   * Return the {@link CourseKzfa} as a String.
   */
  public static String toString(CourseKzfa kzfa) {
    if (kzfa == CourseKzfa.MAJOR) {
      return "H";
    } else {
      return "N";
    }
  }
}
