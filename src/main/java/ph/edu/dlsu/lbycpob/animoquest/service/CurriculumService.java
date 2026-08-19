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

        String requisites =
                String.join(", ", requisiteDescriptions);

        String warningMessage;

        if (valid) {
            warningMessage = "";
        } else {
            warningMessage =
                    "Missing: "
                            + String.join(
                            ", ",
                            missingRequirements
                    );
        }

        return new CurriculumDisplay(
                course.getId(),
                course.getCode(),
                course.getName(),
                course.getUnits(),
                progress.getStatus(),
                progress.getTermTaken(),
                requisites,
                valid,
                warningMessage
        );
    }

    // ============================================================
    // CHECK ONE REQUISITE
    // ============================================================

    private RequisiteResult checkRequisite(
            CurriculumProgress currentCourse,
            Long requisiteId,
            String requisiteType,
            List<CurriculumProgress> studentProgress
    ) {

        MasterlistCourse requisiteCourse =
                masterlistCourseRepository
                        .findById(requisiteId)
                        .orElse(null);

        String requisiteCode =
                requisiteCourse != null
                        ? requisiteCourse.getCode()
                        : "Course ID " + requisiteId;

        String type =
                requisiteType == null
                        ? ""
                        : requisiteType.trim().toUpperCase();

        // --------------------------------------------------------
        // H = HARD PREREQUISITE
        // Must be PASSED
        // --------------------------------------------------------

        if (type.equals("H")) {

            CurriculumProgress progress =
                    findProgress(
                            requisiteId,
                            studentProgress
                    );

            boolean satisfied =
                    progress != null
                            && progress.isPassed();

            return new RequisiteResult(
                    satisfied,
                    requisiteCode + " (H)"
            );
        }

        // --------------------------------------------------------
        // S = SOFT PREREQUISITE
        // Must have been TAKEN
        // Passing is NOT required
        // --------------------------------------------------------

        if (type.equals("S")) {

            CurriculumProgress progress =
                    findProgress(
                            requisiteId,
                            studentProgress
                    );

            boolean satisfied =
                    progress != null;

            return new RequisiteResult(
                    satisfied,
                    requisiteCode + " (S)"
            );
        }

        // --------------------------------------------------------
        // C = CO-REQUISITE
        // Must be taken in the SAME TERM
        // --------------------------------------------------------

        if (type.equals("C")) {

            CurriculumProgress progress =
                    findProgress(
                            requisiteId,
                            studentProgress
                    );

            boolean satisfied =
                    progress != null
                            && progress.getTermTaken()
                            == currentCourse.getTermTaken();

            return new RequisiteResult(
                    satisfied,
                    requisiteCode + " (C)"
            );
        }

        // --------------------------------------------------------
        // UNKNOWN REQUISITE TYPE
        // --------------------------------------------------------

        return new RequisiteResult(
                true,
                requisiteCode
                        + " ("
                        + type
                        + ")"
        );
    }

    // ============================================================
    // FIND STUDENT'S COURSE PROGRESS
    // ============================================================

    private CurriculumProgress findProgress(
            Long courseId,
            List<CurriculumProgress> studentProgress
    ) {

        for (CurriculumProgress progress :
                studentProgress) {

            if (progress.getCourseId().equals(courseId)) {
                return progress;
            }
        }

        return null;
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

        // PASSED courses cannot be changed.
        if (progress.isPassed()) {
            throw new IllegalArgumentException(
                    "A passed course cannot have its status changed."
            );
        }

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

    // ============================================================
    // REQUISITE RESULT
    // ============================================================

    private record RequisiteResult(
            boolean satisfied,
            String description
    ) {
    }
}