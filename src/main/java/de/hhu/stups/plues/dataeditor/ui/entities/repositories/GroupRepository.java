package de.hhu.stups.plues.dataeditor.ui.entities.repositories;

import de.hhu.stups.plues.dataeditor.ui.entities.Group;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface GroupRepository extends CrudRepository<Group,Integer> {
  @Query("SELECT coalesce(max(mygroup.id), 1) FROM Group mygroup")
  int getMaxId();
}
