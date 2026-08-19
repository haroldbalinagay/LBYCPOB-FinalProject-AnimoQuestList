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
    // GET DISPLAY COURSES
    // Combines CurriculumProgress + MasterlistCourse
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
                            .orElse(null);

            // If the course no longer exists in the masterlist,
            // skip it rather than crashing the entire enrollment list.
            if (course == null) {
                continue;
            }

            displays.add(
                    buildDisplay(
                            progress,
                            course,
                            progressList
                    )
            );
        }

        return displays;
    }

    // ============================================================
    // GET DISPLAY COURSES FOR A SPECIFIC TERM
    // ============================================================

    public List<CurriculumDisplay> getCourseDisplaysByTerm(
            Long studentId,
            int term
    ) {

        List<CurriculumProgress> allProgress =
                curriculumRepository.findByStudentId(studentId);

        List<CurriculumDisplay> displays =
                new ArrayList<>();

        for (CurriculumProgress progress : allProgress) {

            if (progress.getTermTaken() != term) {
                continue;
            }

            MasterlistCourse course =
                    masterlistCourseRepository
                            .findById(progress.getCourseId())
                            .orElse(null);

            if (course == null) {
                continue;
            }

            displays.add(
                    buildDisplay(
                            progress,
                            course,
                            allProgress
                    )
            );
        }

        return displays;
    }

    // ============================================================
    // BUILD DISPLAY OBJECT
    // ============================================================

    private CurriculumDisplay buildDisplay(
            CurriculumProgress progress,
            MasterlistCourse course,
            List<CurriculumProgress> studentProgress
    ) {

        List<String> requisiteDescriptions =
                new ArrayList<>();

        List<String> missingRequirements =
                new ArrayList<>();

        boolean valid = true;

        // --------------------------------------------------------
        // REQUIREMENT 1
        // --------------------------------------------------------

        if (course.getReqId1() != null) {

            RequisiteResult result =
                    checkRequisite(
                            progress,
                            course.getReqId1(),
                            course.getReqType1(),
                            studentProgress
                    );

            requisiteDescriptions.add(
                    result.description()
            );

            if (!result.satisfied()) {
                valid = false;
                missingRequirements.add(
                        result.description()
                );
            }
        }

        // --------------------------------------------------------
        // REQUIREMENT 2
        // --------------------------------------------------------

        if (course.getReqId2() != null) {

            RequisiteResult result =
                    checkRequisite(
                            progress,
                            course.getReqId2(),
                            course.getReqType2(),
                            studentProgress
                    );

            requisiteDescriptions.add(
                    result.description()
            );

            if (!result.satisfied()) {
                valid = false;
                missingRequirements.add(
                        result.description()
                );
            }
        }

        // --------------------------------------------------------
        // REQUIREMENT 3
        // --------------------------------------------------------

        if (course.getReqId3() != null) {

            RequisiteResult result =
                    checkRequisite(
                            progress,
                            course.getReqId3(),
                            course.getReqType3(),
                            studentProgress
                    );

