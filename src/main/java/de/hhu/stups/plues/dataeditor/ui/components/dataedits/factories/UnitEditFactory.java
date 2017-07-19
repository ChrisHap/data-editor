package de.hhu.stups.plues.dataeditor.ui.components.dataedits.factories;

import com.google.inject.Inject;

import de.hhu.stups.plues.dataeditor.ui.components.dataedits.UnitEdit;
import de.hhu.stups.plues.dataeditor.ui.entities.UnitWrapper;

public interface UnitEditFactory {
  @Inject
  UnitEdit create(UnitWrapper unitWrapper);
}
