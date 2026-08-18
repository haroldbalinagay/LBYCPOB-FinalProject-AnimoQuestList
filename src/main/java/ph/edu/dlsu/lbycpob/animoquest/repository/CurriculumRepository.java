package ph.edu.dlsu.lbycpob.animoquest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ph.edu.dlsu.lbycpob.animoquest.model.CurriculumProgress;

import java.util.List;
import java.util.Optional;

public interface CurriculumRepository
        extends JpaRepository<CurriculumProgress, Long> {

    // Get all courses belonging to a specific student
    List<CurriculumProgress> findByStudentId(Long studentId);

    // Get courses for a student in a specific term
    List<CurriculumProgress> findByStudentIdAndTermTaken(
            Long studentId,
            int termTaken
    );

    // Find a specific course taken by a student
    Optional<CurriculumProgress> findByStudentIdAndCourseId(
            Long studentId,
            Long courseId
    );

    // Check whether a student already has a particular course
    boolean existsByStudentIdAndCourseId(
            Long studentId,
            Long courseId
    );
}