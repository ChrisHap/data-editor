package de.hhu.stups.plues.dataeditor.ui.components.dataedits.factories;

import de.hhu.stups.plues.dataeditor.ui.components.dataedits.ModuleEdit;
import de.hhu.stups.plues.dataeditor.ui.entities.ModuleWrapper;

public interface ModuleEditFactory {
  ModuleEdit create(ModuleWrapper moduleWrapper);
}
