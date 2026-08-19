package ph.edu.dlsu.lbycpob.animoquest.repository.v2;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ph.edu.dlsu.lbycpob.animoquest.model.v2.MasterlistCourseV2;

import java.util.List;
import java.util.Optional;

public interface MasterlistCourseRepositoryV2 extends JpaRepository<MasterlistCourseV2, Long> {

    Optional<MasterlistCourseV2> findMasterlistCourseById(Long id);

    Optional<MasterlistCourseV2> findMasterlistCourseByNameIgnoreCase(String name);

    // TODO: Doesn't actually find in order
    List<MasterlistCourseV2> findByIdIn(long[] ids);

    @Query(value = "SELECT * FROM course_masterlist WHERE id IN (:ids) ORDER BY CASE id WHEN :ids THEN 0 END", nativeQuery = true)
    List<MasterlistCourseV2> findByIdInOrderOfArray(long[] ids);

}
