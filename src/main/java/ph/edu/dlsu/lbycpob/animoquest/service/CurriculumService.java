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