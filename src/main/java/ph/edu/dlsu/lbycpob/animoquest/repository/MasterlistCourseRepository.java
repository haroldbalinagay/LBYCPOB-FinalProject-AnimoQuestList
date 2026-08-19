package ph.edu.dlsu.lbycpob.animoquest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ph.edu.dlsu.lbycpob.animoquest.model.MasterlistCourse;

import java.util.List;

public interface MasterlistCourseRepository
        extends JpaRepository<MasterlistCourse, Long> {

    List<MasterlistCourse> findByIdIn(List<Long> ids);
}