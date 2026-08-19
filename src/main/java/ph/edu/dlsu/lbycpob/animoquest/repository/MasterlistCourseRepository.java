package ph.edu.dlsu.lbycpob.animoquest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ph.edu.dlsu.lbycpob.animoquest.model.MasterlistCourse;

public interface MasterlistCourseRepository
        extends JpaRepository<MasterlistCourse, Long> {

}