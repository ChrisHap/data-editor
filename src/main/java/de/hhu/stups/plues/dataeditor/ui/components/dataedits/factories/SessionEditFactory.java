package de.hhu.stups.plues.dataeditor.ui.components.dataedits.factories;

import de.hhu.stups.plues.dataeditor.ui.components.dataedits.SessionEdit;
import de.hhu.stups.plues.dataeditor.ui.entities.SessionWrapper;

public interface SessionEditFactory {
  SessionEdit create(SessionWrapper sessionWrapper);
}
