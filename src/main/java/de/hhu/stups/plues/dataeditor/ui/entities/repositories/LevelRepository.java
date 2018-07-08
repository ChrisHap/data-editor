package de.hhu.stups.plues.dataeditor.ui.entities.repositories;

import de.hhu.stups.plues.dataeditor.ui.entities.Level;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface LevelRepository extends CrudRepository<Level,Integer> {
  @Query("SELECT coalesce(max(level.id), 1) FROM Level level")
  int getMaxId();
}
