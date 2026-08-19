package ph.edu.dlsu.lbycpob.animoquest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ph.edu.dlsu.lbycpob.animoquest.model.v2.MasterlistCourse;

import java.util.List;
import java.util.Optional;

public interface MasterlistCourseRepository extends JpaRepository<MasterlistCourse, Long> {

    Optional<MasterlistCourse> findMasterlistCourseById(Long id);

    Optional<MasterlistCourse> findMasterlistCourseByNameIgnoreCase(String name);

    // TODO: Doesn't actually find in order
    List<MasterlistCourse> findByIdIn(long[] ids);

    @Query(value = "SELECT * FROM course_masterlist WHERE id IN (:ids) ORDER BY CASE id WHEN :ids THEN 0 END", nativeQuery = true)
    List<MasterlistCourse> findByIdInOrderOfArray(long[] ids);

}
