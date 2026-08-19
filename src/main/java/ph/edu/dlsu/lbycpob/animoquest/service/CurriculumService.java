package ph.edu.dlsu.lbycpob.animoquest.service;

import org.springframework.stereotype.Service;
import ph.edu.dlsu.lbycpob.animoquest.model.CurriculumProgress;
import ph.edu.dlsu.lbycpob.animoquest.repository.CurriculumRepository;

import java.util.List;

@Service
public class CurriculumService {

    private final CurriculumRepository curriculumRepository;

    public CurriculumService(
            CurriculumRepository curriculumRepository
    ) {
        this.curriculumRepository = curriculumRepository;
    }

    // ============================================================
    // GET ALL COURSES FOR A STUDENT
    // ============================================================

    public List<CurriculumProgress> getStudentCourses(
            Long studentId
    ) {
        return curriculumRepository.findByStudentId(studentId);
    }

    // ============================================================
    // GET COURSES FOR A SPECIFIC TERM
    // ============================================================

    public List<CurriculumProgress> getCoursesByTerm(
            Long studentId,
            int term
    ) {
        return curriculumRepository
                .findByStudentIdAndTermTaken(
                        studentId,
                        term
                );
    }

    // ============================================================
    // ADD COURSE
    // ============================================================

    public CurriculumProgress addCourse(
            Long studentId,
            Long courseId,
            int termTaken,
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

        if (termTaken < 1) {
            throw new IllegalArgumentException(
                    "Invalid term."
            );
        }
        if (curriculumRepository
                .findByStudentIdAndCourseId(
                        studentId,
                        courseId
                )
                .isPresent()) {

            throw new IllegalArgumentException(
                    "This course is already in your enrollment list."
            );
        }

        CurriculumProgress progress =
                new CurriculumProgress();

        progress.setStudentId(studentId);
        progress.setCourseId(courseId);
        progress.setTermTaken(termTaken);
        progress.setStatus(status);

        return curriculumRepository.save(progress);
    }

    // ============================================================
    // UPDATE COURSE STATUS
    // ============================================================

    public CurriculumProgress updateStatus(
            Long studentId,
            Long courseId,
            String status
    ) {

        CurriculumProgress progress =
                curriculumRepository
                        .findByStudentIdAndCourseId(
                                studentId,
                                courseId
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Course was not found."
                                )
                        );

        progress.setStatus(status);

        return curriculumRepository.save(progress);
    }

    // ============================================================
    // REMOVE COURSE
    // ============================================================

    public void removeCourse(
            Long studentId,
            Long courseId
    ) {

        CurriculumProgress progress =
                curriculumRepository
                        .findByStudentIdAndCourseId(
                                studentId,
                                courseId
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Course was not found."
                                )
                        );

        curriculumRepository.delete(progress);
    }
}