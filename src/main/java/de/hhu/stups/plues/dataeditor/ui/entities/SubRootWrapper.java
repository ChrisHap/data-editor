package de.hhu.stups.plues.dataeditor.ui.entities;

public class SubRootWrapper implements EntityWrapper {

  private final String text;

  /**
   * A class representing sub root nodes in the
   * {@link de.hhu.stups.plues.dataeditor.ui.components.datavisualization.DataTreeView}
   * without any attributes like the 'minors' tag.
   */
  public SubRootWrapper(final String text) {
    this.text = text;
  }

  @Override
  public String toString() {
    return text;
  }
}
