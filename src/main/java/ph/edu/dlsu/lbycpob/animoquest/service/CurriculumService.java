package ph.edu.dlsu.lbycpob.animoquest.service;

import org.springframework.stereotype.Service;
import ph.edu.dlsu.lbycpob.animoquest.model.CurriculumProgress;
import ph.edu.dlsu.lbycpob.animoquest.repository.CurriculumRepository;

import java.util.List;

@Service
public class CurriculumService {

    private final CurriculumRepository curriculumRepository;

    public CurriculumService(CurriculumRepository curriculumRepository) {
        this.curriculumRepository = curriculumRepository;
    }

    // ============================================================
    // GET ALL ENROLLED COURSES FOR A STUDENT
    // ============================================================

    public List<CurriculumProgress> getStudentCourses(Long studentId) {

        if (studentId == null) {
            throw new IllegalArgumentException(
                    "Student ID cannot be null."
            );
        }

        return curriculumRepository.findByStudentId(studentId);
    }

    // ============================================================
    // GET COURSES FOR A SPECIFIC TERM
    // ============================================================

    public List<CurriculumProgress> getStudentCoursesByTerm(
            Long studentId,
            int term
    ) {

        if (studentId == null) {
            throw new IllegalArgumentException(
                    "Student ID cannot be null."
            );
        }

        if (term <= 0) {
            throw new IllegalArgumentException(
                    "Term must be greater than zero."
            );
        }

        return curriculumRepository
                .findByStudentIdAndTermTaken(studentId, term);
    }

    // ============================================================
    // UPDATE COURSE STATUS
    // ============================================================

    public CurriculumProgress updateCourseStatus(
            Long studentId,
            Long courseId,
            String status
    ) {

        if (studentId == null) {
            throw new IllegalArgumentException(
                    "Student ID cannot be null."
            );
        }

        if (courseId == null) {
            throw new IllegalArgumentException(
                    "Course ID cannot be null."
            );
        }

        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Course status cannot be empty."
            );
        }

        CurriculumProgress progress =
                curriculumRepository
                        .findByStudentIdAndCourseId(
                                studentId,
                                courseId
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Course enrollment was not found."
                                )
                        );

        progress.setStatus(status.trim().toUpperCase());

        return curriculumRepository.save(progress);
    }

    // ============================================================
    // REMOVE COURSE FROM STUDENT'S ENROLLMENT
    // ============================================================

    public void removeCourse(
            Long studentId,
            Long courseId
    ) {

        if (studentId == null) {
            throw new IllegalArgumentException(
                    "Student ID cannot be null."
            );
        }

        if (courseId == null) {
            throw new IllegalArgumentException(
                    "Course ID cannot be null."
            );
        }

        CurriculumProgress progress =
                curriculumRepository
                        .findByStudentIdAndCourseId(
                                studentId,
                                courseId
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Course enrollment was not found."
                                )
                        );

        curriculumRepository.delete(progress);
    }
}