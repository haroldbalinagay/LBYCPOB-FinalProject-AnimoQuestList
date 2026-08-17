package ph.edu.dlsu.lbycpob.animoquest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ph.edu.dlsu.lbycpob.animoquest.model.MasterlistCourse;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MasterlistCourseRepository extends JpaRepository<MasterlistCourse, Long> {

    Optional<MasterlistCourse> findMasterlistCourseById(Long id);

    Optional<MasterlistCourse> findMasterlistCourseByNameIgnoreCase(String name);

    // TODO: Doesn't actually find in order
    List<MasterlistCourse> findByIdIn(long[] ids);

}
