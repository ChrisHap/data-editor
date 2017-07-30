package de.hhu.stups.plues.dataeditor.ui.components.dataedits.factories;

import de.hhu.stups.plues.dataeditor.ui.components.dataedits.GroupEdit;
import de.hhu.stups.plues.dataeditor.ui.entities.GroupWrapper;

public interface GroupEditFactory {
  GroupEdit create(GroupWrapper unitWrapper);
}
