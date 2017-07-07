package de.hhu.stups.plues.dataeditor.ui.entities;

public enum CourseDegree {
  BK, BA, MSC;

  /**
   * Return the {@link CourseDegree} for a given string.
   */
  public static CourseDegree getDegreeFromString(final String degree) {
    switch (degree) {
      case "ba":
        return BA;
      case "bk":
        return BK;
      case "msc":
        return MSC;
      default:
        return null;
    }
  }
}
