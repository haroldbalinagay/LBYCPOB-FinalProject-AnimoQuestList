package ph.edu.dlsu.lbycpob.animoquest.repository.v2;

import org.springframework.data.jpa.repository.JpaRepository;
import ph.edu.dlsu.lbycpob.animoquest.model.v2.CurriculumProgress;

import java.util.List;
import java.util.Optional;

public interface CurriculumRepository
        extends JpaRepository<CurriculumProgress, Long> {

    List<CurriculumProgress> findByStudentId(Long studentId);

    Optional<CurriculumProgress> findByStudentIdAndCourseId(
            Long studentId,
            Long courseId
    );

    List<CurriculumProgress> findByStudentIdAndTermTaken(
            Long studentId,
            int termTaken
    );
}