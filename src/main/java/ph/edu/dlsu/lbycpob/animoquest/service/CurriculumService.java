package ph.edu.dlsu.lbycpob.animoquest.service;

import org.springframework.stereotype.Service;
import ph.edu.dlsu.lbycpob.animoquest.model.CurriculumDisplay;
import ph.edu.dlsu.lbycpob.animoquest.model.CurriculumProgress;
import ph.edu.dlsu.lbycpob.animoquest.model.MasterlistCourse;
import ph.edu.dlsu.lbycpob.animoquest.repository.CurriculumRepository;
import ph.edu.dlsu.lbycpob.animoquest.repository.MasterlistCourseRepository;

import java.util.ArrayList;
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

    public List<CurriculumProgress> getStudentCourses(
            Long studentId
    ) {
        return curriculumRepository.findByStudentId(studentId);
    }

    // ============================================================
    // GET DISPLAY COURSES FOR A STUDENT
    // ============================================================

    public List<CurriculumDisplay> getStudentCourseDisplays(
            Long studentId
    ) {

        List<CurriculumProgress> progressList =
                curriculumRepository.findByStudentId(studentId);

        List<CurriculumDisplay> displays =
                new ArrayList<>();

        for (CurriculumProgress progress : progressList) {

            MasterlistCourse course =
                    masterlistCourseRepository
                            .findById(progress.getCourseId())
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "Course was not found in the masterlist."
                                    )
                            );

            String requisiteInfo =
                    buildRequisiteInfo(course);

            displays.add(
                    new CurriculumDisplay(
                            course.getId(),
                            course.getCode(),
                            course.getName(),
                            course.getUnits(),
                            progress.getStatus(),
                            progress.getTermTaken(),
                            requisiteInfo,
                            ""
                    )
            );
        }

        return displays;
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
    // GET DISPLAY COURSES FOR A SPECIFIC TERM
    // ============================================================

    public List<CurriculumDisplay> getCourseDisplaysByTerm(
            Long studentId,
            int term
    ) {

        List<CurriculumProgress> progressList =
                curriculumRepository
                        .findByStudentIdAndTermTaken(
                                studentId,
                                term
                        );

        List<CurriculumDisplay> displays =
                new ArrayList<>();

        for (CurriculumProgress progress : progressList) {

            MasterlistCourse course =
                    masterlistCourseRepository
                            .findById(progress.getCourseId())
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "Course was not found in the masterlist."
                                    )
                            );

            String requisiteInfo =
                    buildRequisiteInfo(course);

            displays.add(
                    new CurriculumDisplay(
                            course.getId(),
                            course.getCode(),
                            course.getName(),
                            course.getUnits(),
                            progress.getStatus(),
                            progress.getTermTaken(),
                            requisiteInfo,
                            ""
                    )
            );
        }

        return displays;
    }

    // ============================================================
    // BUILD REQUISITE INFORMATION
    // ============================================================

    private String buildRequisiteInfo(
            MasterlistCourse course
    ) {

        List<String> requisites = new ArrayList<>();

        addRequisite(
                requisites,
                course.getReqId1(),
                course.getReqType1()
        );

        addRequisite(
                requisites,
                course.getReqId2(),
                course.getReqType2()
        );

        addRequisite(
                requisites,
                course.getReqId3(),
                course.getReqType3()
        );

        if (requisites.isEmpty()) {
            return "None";
        }

        return String.join(", ", requisites);
    }

    // ============================================================
    // ADD ONE REQUISITE TO THE DISPLAY
    // ============================================================

    private void addRequisite(
            List<String> requisites,
            Long requisiteId,
            String requisiteType
    ) {

        if (requisiteId == null) {
            return;
        }

        MasterlistCourse requisiteCourse =
                masterlistCourseRepository
                        .findById(requisiteId)
                        .orElse(null);

        if (requisiteCourse == null) {
            return;
        }

        String type =
                requisiteType == null
                        ? ""
                        : " (" + requisiteType + ")";

        requisites.add(
                requisiteCourse.getCode() + type
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