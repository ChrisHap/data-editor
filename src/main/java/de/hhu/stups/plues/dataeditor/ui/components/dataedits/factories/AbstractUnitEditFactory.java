package de.hhu.stups.plues.dataeditor.ui.components.dataedits.factories;

import com.google.inject.Inject;

import de.hhu.stups.plues.dataeditor.ui.components.dataedits.AbstractUnitEdit;
import de.hhu.stups.plues.dataeditor.ui.entities.AbstractUnitWrapper;

public interface AbstractUnitEditFactory {
  @Inject
  AbstractUnitEdit create(AbstractUnitWrapper abstractUnitWrapper);
}
