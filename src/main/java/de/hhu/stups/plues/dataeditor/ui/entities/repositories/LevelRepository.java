package de.hhu.stups.plues.dataeditor.ui.entities.repositories;

import de.hhu.stups.plues.dataeditor.ui.entities.Level;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
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
  void insertSimpleLevel(int id, String name, String tm, String art, int min, int max,
                         int minCp, int maxCp, Integer parentId);

  @Transactional
  @Query(value = "update levels set name = ?2, tm = ?3, art = ?4, min = ?5, max = ?6, "
        + "min_credit_points = ?7, max_credit_points = ?8, "
        + "parent_id = ?9 where id = ?1", nativeQuery = true)
  @Modifying
  void updateSimpleLevel(int id, String name, String tm, String art, int min, int max,
                         int minCp, int maxCp, Integer parentId);

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
