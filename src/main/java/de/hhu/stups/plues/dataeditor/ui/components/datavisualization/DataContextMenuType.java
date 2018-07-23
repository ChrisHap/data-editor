package de.hhu.stups.plues.dataeditor.ui.components.datavisualization;

/**
 * FLAT only offers options to add entities on the top level.
 * NESTED additionally enables to add child entities if applicable.
 */
public enum DataContextMenuType {
  FLAT, NESTED;

  public boolean isFlat() {
    return this.equals(FLAT);
  }
}
