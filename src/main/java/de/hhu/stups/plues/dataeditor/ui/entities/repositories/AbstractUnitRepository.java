package de.hhu.stups.plues.dataeditor.ui.entities.repositories;

import de.hhu.stups.plues.dataeditor.ui.entities.AbstractUnit;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

public interface AbstractUnitRepository extends CrudRepository<AbstractUnit,Integer> {
  @Query("SELECT coalesce(max(abstractUnit.id), 1) FROM AbstractUnit abstractUnit")
  int getMaxId();

  @Transactional
  @Modifying
  @Query(value = "DELETE from modules_abstract_units_semesters where abstract_unit_id = ?1",
        nativeQuery = true)
  void deleteModuleAbstractUnitSemesterByAbstractUnit(int abstractUnitId);

  @Transactional
  @Modifying
  @Query(value = "DELETE from modules_abstract_units_semesters where module_id = ?1",
        nativeQuery = true)
  void deleteModuleAbstractUnitSemesterByModule(int moduleId);

  @Transactional
  @Modifying
  @Query(value = "DELETE from modules_abstract_units_types where abstract_unit_id = ?1",
        nativeQuery = true)
  void deleteModuleAbstractUnitTypeByAbstractUnit(int abstractUnitId);

  @Transactional
  @Modifying
  @Query(value = "DELETE from modules_abstract_units_types where module_id = ?1",
        nativeQuery = true)
  void deleteModuleAbstractUnitTypeByModule(int moduleId);

  @Transactional
  @Modifying
  @Query(value = "INSERT INTO modules_abstract_units_semesters "
        + "(module_id, abstract_unit_id, semester)"
        + "VALUES (?1, ?2, -1)", nativeQuery = true)
  void insertSimpleModuleAbstractUnitSemester(int moduleId, int abstractUnitId);

  @Transactional
  @Modifying
  @Query(value = "INSERT INTO modules_abstract_units_types "
        + "(module_id, abstract_unit_id, type)"
        + "VALUES (?1, ?2, 'm')", nativeQuery = true)
  void insertSimpleModuleAbstractUnitType(int moduleId, int abstractUnitId);

  @Transactional
  @Modifying
  @Query(value = "INSERT INTO unit_abstract_unit "
        + "(unit_id, abstract_unit_id)"
        + "VALUES (?1, ?2)", nativeQuery = true)
  void insertSimpleUnitAbstractUnit(int unit, int abstractUnitId);

  @Transactional
  @Modifying
  @Query(value = "DELETE FROM unit_abstract_unit WHERE abstract_unit_id = ?1",
        nativeQuery = true)
  void deleteUnitAbstractUnitByAbstractUnit(int abstractUnitId);
}
