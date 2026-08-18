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
    // GET ALL COURSES FOR A STUDENT
    // ============================================================

    public List<CurriculumProgress> getStudentCourses(Long studentId) {

        return curriculumRepository.findByStudentId(studentId);
    }


    // ============================================================
    // GET COURSES FOR A SPECIFIC TERM
    // ============================================================

    public List<CurriculumProgress> getStudentCoursesByTerm(
            Long studentId,
            int term
    ) {

        return curriculumRepository.findByStudentIdAndTermTaken(
                studentId,
                term
        );
    }


    // ============================================================
    // FIND A SPECIFIC COURSE
    // ============================================================

    public CurriculumProgress getStudentCourse(
            Long studentId,
            Long courseId
    ) {

        return curriculumRepository
                .findByStudentIdAndCourseId(studentId, courseId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Course was not found in the student's curriculum."
                        )
                );
    }


    // ============================================================
    // UPDATE COURSE STATUS
    // ============================================================

    public void updateCourseStatus(
            Long studentId,
            Long courseId,
            String status
    ) {

        CurriculumProgress progress =
                getStudentCourse(studentId, courseId);

        progress.setStatus(status);

        curriculumRepository.save(progress);
    }


    // ============================================================
    // REMOVE COURSE
    // ============================================================

    public void removeCourse(
            Long studentId,
            Long courseId
    ) {

        CurriculumProgress progress =
                getStudentCourse(studentId, courseId);

        curriculumRepository.delete(progress);
    }
}