package ph.edu.dlsu.lbycpob.animoquest.repository.v2;

import org.springframework.data.jpa.repository.JpaRepository;
import ph.edu.dlsu.lbycpob.animoquest.model.v2.CurriculumProgressV2;

import java.util.List;
import java.util.Optional;

public interface CurriculumRepositoryV2
        extends JpaRepository<CurriculumProgressV2, Long> {

    List<CurriculumProgressV2> findByStudentId(Long studentId);

    Optional<CurriculumProgressV2> findByStudentIdAndCourseId(
            Long studentId,
            Long courseId
    );

    List<CurriculumProgressV2> findByStudentIdAndTermTaken(
            Long studentId,
            int termTaken
    );
}