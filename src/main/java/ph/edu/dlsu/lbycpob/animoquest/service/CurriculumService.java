package ph.edu.dlsu.lbycpob.animoquest.service;

import org.springframework.stereotype.Service;
import ph.edu.dlsu.lbycpob.animoquest.model.CurriculumProgress;
import ph.edu.dlsu.lbycpob.animoquest.model.MasterlistCourse;
import ph.edu.dlsu.lbycpob.animoquest.repository.CurriculumRepository;
import ph.edu.dlsu.lbycpob.animoquest.repository.MasterlistCourseRepository;
import ph.edu.dlsu.lbycpob.animoquest.model.CurriculumDisplay;

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

        return curriculumRepository
                .findByStudentIdAndCourseId(studentId, courseId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Course was not found in the student's curriculum."
                        )
                );
    }


    // ============================================================
    // GET MASTERLIST COURSE
    // ============================================================

    public MasterlistCourse getMasterlistCourse(Long courseId) {

        return masterlistCourseRepository
                .findById(courseId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Course was not found in the masterlist."
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

    public List<CurriculumDisplay> getStudentCourseDisplay(Long studentId) {

        List<CurriculumProgress> progressList =
                curriculumRepository.findByStudentId(studentId);

        return progressList.stream()
                .map(progress -> {

                    MasterlistCourse course =
                            getMasterlistCourse(progress.getCourseId());

                    return new CurriculumDisplay(
                            progress.getCourseId(),
                            course.getCode(),
                            course.getName(),
                            course.getUnits(),
                            progress.getStatus(),
                            progress.getTermTaken()
                    );
                })
                .toList();
    }
}