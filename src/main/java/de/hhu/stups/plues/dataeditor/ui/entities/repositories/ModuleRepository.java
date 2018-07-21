package de.hhu.stups.plues.dataeditor.ui.entities.repositories;

import de.hhu.stups.plues.dataeditor.ui.entities.Module;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ModuleRepository extends CrudRepository<Module,Integer> {
  @Query("SELECT coalesce(max(module.id), 1) FROM Module module")
  int getMaxId();

  @Transactional
  @Query(value = "insert into modules (id, key, title, pornr, elective_units, bundled) "
        + "VALUES (?1, ?2, ?3, ?4, ?5, ?6)",
        nativeQuery = true)
  @Modifying
  void insertSimpleModule(@Param("id") int id, @Param("key") String key,
                          @Param("title") String title, @Param("pordnr") int pordnr,
                          @Param("elective") int elective, @Param("bundled") boolean bundled);

  @Transactional
  @Query(value = "update modules set key = :key, title = :title, pordnr = :pordnr, "
        + "elective_units = :elective, bundled = :bundled where id = :id",
        nativeQuery = true)
  @Modifying
  void updateSimpleModule(@Param("id") int id, @Param("key") String key,
                          @Param("title") String title, @Param("pordnr") int pordnr,
                          @Param("elective") int elective, @Param("bundled") boolean bundled);


  @Transactional
  @Modifying
  @Query(value = "insert into module_levels (module_id, level_id, course_id, name, mandatory)"
        + " VALUES (?1, ?2, ?3, ?4, ?5)",
        nativeQuery = true)
  void insertModuleLevel(int moduleId, int levelId, int courseId, String name, boolean mandatory);

  @Transactional
  @Modifying
  @Query(value = "delete from module_levels where module_id = ?1", nativeQuery = true)
  void deleteModuleLevel(int levelId);

}
