package de.hhu.stups.plues.dataeditor.ui.entities.repositories;

import de.hhu.stups.plues.dataeditor.ui.entities.Course;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

public interface CourseRepository extends CrudRepository<Course, Integer> {
  @Query("SELECT coalesce(max(course.id), 1) FROM Course course")
  int getMaxId();

  @Transactional
  @Modifying
  @Query(value = "insert into courses(id, key, degree, short_name, name, kzfa, po, credit_points) "
        + "VALUES (?1, ?2, ?3, ?4, ?5 ,?6, ?7, ?8)",
        nativeQuery = true)
  void insertSimpleCourse(int id, String key, String degree, String shortName, String name,
                          String kzfa, int po, int creditPoints);

  @Transactional
  @Modifying
  @Query(value = "update courses set id = :id, key = :key, degree = :degree, "
        + "short_name = :shortName, name = :name, kzfa = :kzfa, po = :po, "
        + "credit_points = :creditPoints",
        nativeQuery = true)
  void updateSimpleCourse(int id, String key, String degree, String shortName, String name,
                          String kzfa, int po, int creditPoints);

  @Transactional
  @Modifying
  @Query(value = "insert into minors (course_id, minor_course_id) VALUES (?1, ?2)",
        nativeQuery = true)
  void insertMinor(int minorId, int majorId);

  @Transactional
  @Modifying
  @Query(value = "delete from minors where minor_course_id = ?1",
        nativeQuery = true)
  void deleteMinor(int minorId);

}
