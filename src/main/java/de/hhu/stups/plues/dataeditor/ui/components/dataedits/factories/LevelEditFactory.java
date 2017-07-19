package de.hhu.stups.plues.dataeditor.ui.components.dataedits.factories;

import com.google.inject.Inject;

import de.hhu.stups.plues.dataeditor.ui.components.dataedits.LevelEdit;
import de.hhu.stups.plues.dataeditor.ui.entities.LevelWrapper;

public interface LevelEditFactory {
  @Inject
  LevelEdit create(LevelWrapper levelWrapper);
}
