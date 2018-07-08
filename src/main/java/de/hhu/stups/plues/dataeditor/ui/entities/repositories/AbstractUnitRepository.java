package de.hhu.stups.plues.dataeditor.ui.entities.repositories;

import de.hhu.stups.plues.dataeditor.ui.entities.AbstractUnit;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface AbstractUnitRepository extends CrudRepository<AbstractUnit,Integer> {
  @Query("SELECT coalesce(max(abstractUnit.id), 1) FROM AbstractUnit abstractUnit")
  int getMaxId();
}
