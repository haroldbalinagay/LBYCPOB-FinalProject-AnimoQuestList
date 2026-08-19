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

            String missingPrerequisiteWarning =
                    buildMissingPrerequisiteWarning(
                            course,
                            progress,
                            progressList
                    );

            displays.add(
                    new CurriculumDisplay(
                            course.getId(),
                            course.getCode(),
                            course.getName(),
                            course.getUnits(),
                            progress.getStatus(),
                            progress.getTermTaken(),
                            requisiteInfo,
                            missingPrerequisiteWarning
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

        /*
         * We still need ALL of the student's progress here.
         *
         * This is important because a course in Term 3 may have
         * a prerequisite that was taken in Term 1 or Term 2.
         */
        List<CurriculumProgress> allStudentProgress =
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

            String missingPrerequisiteWarning =
                    buildMissingPrerequisiteWarning(
                            course,
                            progress,
                            allStudentProgress
                    );

            displays.add(
                    new CurriculumDisplay(
                            course.getId(),
                            course.getCode(),
                            course.getName(),
                            course.getUnits(),
                            progress.getStatus(),
                            progress.getTermTaken(),
                            requisiteInfo,
                            missingPrerequisiteWarning
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