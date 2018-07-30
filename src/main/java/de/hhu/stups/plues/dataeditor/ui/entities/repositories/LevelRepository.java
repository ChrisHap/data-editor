package de.hhu.stups.plues.dataeditor.ui.entities.repositories;

import de.hhu.stups.plues.dataeditor.ui.entities.Level;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface LevelRepository extends CrudRepository<Level,Integer> {

  @Query("SELECT coalesce(max(level.id), 1) FROM Level level")
  int getMaxId();

  @Transactional
  @Query(value = "insert into levels (id, name, tm, art, min, max, "
        + "min_credit_points, max_credit_points, parent_id) "
        + "VALUES (?1, ?2, ?3, ?4, ?5, ?6, ?7, ?8, ?9)",
        nativeQuery = true)
  @Modifying
  void insertSimpleLevel(@Param("id") int id, @Param("name") String name, @Param("tm") String tm,
                         @Param("art") String art, @Param("min") int min, @Param("max") int max,
                         @Param("min_credit_points") int minCp,
                         @Param("max_credit_points") int maxCp,
                         @Param("parent_id") Integer parentId);

  @Transactional
  @Query(value = "update levels set name = :name, tm = :tm, art = :art, min = :min, max = :max, "
        + "min_credit_points = :min_credit_points, max_credit_points = :max_credit_points, "
        + "parent_id = :parent_id where id = :id", nativeQuery = true)
  @Modifying
  void updateSimpleLevel(@Param("id") int id, @Param("name") String name, @Param("tm") String tm,
                         @Param("art") String art, @Param("min") int min, @Param("max") int max,
                         @Param("min_credit_points") int minCp,
                         @Param("max_credit_points") int maxCp,
                         @Param("parent_id") Integer parentId);

  @Transactional
  @Modifying
  @Query(value = "insert into course_levels (course_id, level_id) VALUES (?1, ?2)",
        nativeQuery = true)
  void insertCourseLevel(int courseId, int levelId);

  @Transactional
  @Modifying
  @Query(value = "delete from course_levels where level_id = ?1", nativeQuery = true)
  void deleteCourseLevel(int levelId);

  @Transactional
  @Modifying
  @Query(value = "delete from module_levels where level_id = ?1", nativeQuery = true)
  void deleteModuleLevel(int levelId);

}
