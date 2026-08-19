package ph.edu.dlsu.lbycpob.animoquest.service;

import org.springframework.stereotype.Service;
import ph.edu.dlsu.lbycpob.animoquest.model.CurriculumProgress;
import ph.edu.dlsu.lbycpob.animoquest.model.MasterlistCourse;
import ph.edu.dlsu.lbycpob.animoquest.repository.CurriculumRepository;
import ph.edu.dlsu.lbycpob.animoquest.repository.MasterlistCourseRepository;

import java.util.List;

@Service
public class CurriculumService {

    private final CurriculumRepository curriculumRepository;
    private final MasterlistCourseRepository masterlistCourseRepository;

    public CurriculumService(
            CurriculumRepository curriculumRepository,
            MasterlistCourseRepository masterlistCourseRepository
    ) {
        this.curriculumRepository = curriculumRepository;
        this.masterlistCourseRepository = masterlistCourseRepository;
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

