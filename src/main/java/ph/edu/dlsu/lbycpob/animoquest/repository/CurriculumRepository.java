package ph.edu.dlsu.lbycpob.animoquest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ph.edu.dlsu.lbycpob.animoquest.model.CurriculumProgress;

import java.util.List;
import java.util.Optional;

public interface CurriculumRepository
        extends JpaRepository<CurriculumProgress, Long> {

    List<CurriculumProgress> findByStudentId(Long studentId);

    List<CurriculumProgress> findByStudentIdAndTermTaken(
            Long studentId,
            int termTaken
    );

    Optional<CurriculumProgress> findByStudentIdAndCourseId(
            Long studentId,
            Long courseId
    );

    void deleteByStudentIdAndCourseId(
            Long studentId,
            Long courseId
    );
}