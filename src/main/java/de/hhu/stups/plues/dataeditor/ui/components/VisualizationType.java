package de.hhu.stups.plues.dataeditor.ui.components;

public enum VisualizationType {
  TREE, LIST;

  @Override
  public String toString() {
    switch (this) {
      case TREE:
        return "treeVisualization";
      case LIST:
        return "listVisualization";
      default:
        return "";
    }
  }
}
