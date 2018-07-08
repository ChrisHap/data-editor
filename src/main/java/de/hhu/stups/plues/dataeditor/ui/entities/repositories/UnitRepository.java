package de.hhu.stups.plues.dataeditor.ui.entities.repositories;

import de.hhu.stups.plues.dataeditor.ui.entities.Unit;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface UnitRepository extends CrudRepository<Unit,Integer> {
  @Query("SELECT coalesce(max(unit.id), 1) FROM Unit unit")
  int getMaxId();
}
