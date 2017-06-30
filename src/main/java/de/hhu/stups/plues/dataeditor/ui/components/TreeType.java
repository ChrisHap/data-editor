package de.hhu.stups.plues.dataeditor.ui.components;

public enum TreeType {
  MODULE_TREE, DATA_TREE;

  @Override
  public String toString() {
    switch (this) {
      case MODULE_TREE:
        return "moduleTree";
      case DATA_TREE:
        return "moduleData";
      default:
        return "";
    }
  }
}
