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
    private final TermChecklistService termChecklistService;

    public CurriculumService(
            CurriculumRepository curriculumRepository,
            MasterlistCourseRepository masterlistCourseRepository,
            TermChecklistService termChecklistService
    ) {
        this.curriculumRepository = curriculumRepository;
        this.masterlistCourseRepository = masterlistCourseRepository;
        this.termChecklistService = termChecklistService;
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
// GET RECOMMENDED COURSES FOR NEXT TERM
// ============================================================

    public List<MasterlistCourse> getRecommendedCourses(
            Long studentId,
            String degree,
            int currentTerm
    ) {

        if (studentId == null) {
            throw new IllegalArgumentException(
                    "Student ID cannot be null."
            );
        }

        if (degree == null || degree.isBlank()) {
            throw new IllegalArgumentException(
                    "Student degree cannot be empty."
            );
        }

        if (currentTerm < 1 || currentTerm >= 12) {
            throw new IllegalArgumentException(
                    "Current term must be between 1 and 11 " +
                            "to generate a next-term recommendation."
            );
        }

        int nextTerm = currentTerm + 1;

        // --------------------------------------------------------
        // GET THE CHECKLIST FOR THE NEXT TERM
        // --------------------------------------------------------

        List<MasterlistCourse> nextTermCourses =
                termChecklistService.getCoursesForTerm(
                        125,
                        degree,
                        nextTerm
                );

        // --------------------------------------------------------
        // GET STUDENT'S CURRENT PROGRESS
        // --------------------------------------------------------

        List<CurriculumProgress> studentProgress =
                curriculumRepository.findByStudentId(studentId);

        List<MasterlistCourse> recommendations =
                new ArrayList<>();

        // --------------------------------------------------------
        // CHECK EACH COURSE
        // --------------------------------------------------------

        for (MasterlistCourse course : nextTermCourses) {

            CurriculumProgress existingProgress =
                    findProgress(
                            course.getId(),
                            studentProgress
                    );

            // Already passed
            if (existingProgress != null
                    && existingProgress.isPassed()) {

                continue;
            }

            // Already in progress
            if (existingProgress != null
                    && existingProgress.isInProgress()) {

                continue;
            }

            // ----------------------------------------------------
            // CHECK PREREQUISITES
            // ----------------------------------------------------

            if (!hasSatisfiedPrerequisites(
                    course,
                    nextTerm,
                    studentProgress,
                    recommendations
            )) {

                continue;
            }

            recommendations.add(course);
        }

        // --------------------------------------------------------
        // RESPECT MAXIMUM UNITS
        // --------------------------------------------------------

        TermChecklist checklist =
                termChecklistService
                        .getChecklists(
                                125,
                                degree
                        )
                        .stream()
                        .filter(
                                c -> c.getTermNumber() == nextTerm
                        )
                        .findFirst()
                        .orElse(null);

        if (checklist == null) {
            return recommendations;
        }

        int maxUnits =
                checklist.getMaxUnits();

        List<MasterlistCourse> finalRecommendations =
                new ArrayList<>();

        int totalUnits = 0;

        for (MasterlistCourse course :
                recommendations) {

            if (totalUnits + course.getUnits()
                    > maxUnits) {

                continue;
            }

            finalRecommendations.add(course);

            totalUnits += course.getUnits();
        }

        return finalRecommendations;
    }

    // ============================================================
// CHECK PREREQUISITES FOR RECOMMENDATION
// ============================================================

    private boolean hasSatisfiedPrerequisites(
            MasterlistCourse course,
            int nextTerm,
            List<CurriculumProgress> studentProgress,
            List<MasterlistCourse> recommendations
    ) {

        // --------------------------------------------------------
        // REQUIREMENT 1
        // --------------------------------------------------------

        if (!isRequisiteSatisfiedForRecommendation(
                course.getReqId1(),
                course.getReqType1(),
                nextTerm,
                studentProgress,
                recommendations
        )) {

            return false;
        }

        // --------------------------------------------------------
        // REQUIREMENT 2
        // --------------------------------------------------------

        if (!isRequisiteSatisfiedForRecommendation(
                course.getReqId2(),
                course.getReqType2(),
                nextTerm,
                studentProgress,
                recommendations
        )) {

            return false;
        }

        // --------------------------------------------------------
        // REQUIREMENT 3
        // --------------------------------------------------------

        if (!isRequisiteSatisfiedForRecommendation(
                course.getReqId3(),
                course.getReqType3(),
                nextTerm,
                studentProgress,
                recommendations
        )) {

            return false;
        }

        return true;
    }

// ============================================================
// CHECK ONE REQUISITE FOR RECOMMENDATION
// ============================================================

    private boolean isRequisiteSatisfiedForRecommendation(
            Long requisiteId,
            String requisiteType,
            int nextTerm,
            List<CurriculumProgress> studentProgress,
            List<MasterlistCourse> recommendations
    ) {

        if (requisiteId == null) {
            return true;
        }

        String type =
                requisiteType == null
                        ? ""
                        : requisiteType.trim().toUpperCase();

        CurriculumProgress progress =
                findProgress(
                        requisiteId,
                        studentProgress
                );

        // --------------------------------------------------------
        // HARD PREREQUISITE
        // --------------------------------------------------------

        if ("H".equals(type)) {

            return progress != null
                    && progress.isPassed();
        }

        // --------------------------------------------------------
        // SOFT PREREQUISITE
        // --------------------------------------------------------

        if ("S".equals(type)) {

            return progress != null;
        }

        // --------------------------------------------------------
        // CO-REQUISITE
        // --------------------------------------------------------

        if ("C".equals(type)) {

            // Already being taken in the next term
            if (progress != null
                    && progress.getTermTaken() == nextTerm) {

                return true;
            }

            // Or recommended together with this course
            for (MasterlistCourse recommended :
                    recommendations) {

                if (recommended.getId()
                        .equals(requisiteId)) {

                    return true;
                }
            }

            return false;
        }

        // Unknown requisite types don't block
        return true;
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

        if (termTaken < 1 || termTaken > 12) {
            throw new IllegalArgumentException(
                    "Invalid term. Term must be between 1 and 12."
            );
        }

        // --------------------------------------------------------
        // FIND COURSE IN MASTERLIST
        // --------------------------------------------------------

        MasterlistCourse course =
                masterlistCourseRepository
                        .findById(courseId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Course was not found in the masterlist."
                                )
                        );

        // --------------------------------------------------------
        // CHECK IF ALREADY ENROLLED
        // --------------------------------------------------------

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

        // --------------------------------------------------------
        // GET ALL COURSES ALREADY TAKEN BY STUDENT
        // --------------------------------------------------------

        List<CurriculumProgress> studentProgress =
                curriculumRepository.findByStudentId(studentId);

        // --------------------------------------------------------
        // CHECK REQUISITES
        // --------------------------------------------------------

        checkEnrollmentRequisite(
                course,
                course.getReqId1(),
                course.getReqType1(),
                termTaken,
                studentProgress
        );

        checkEnrollmentRequisite(
                course,
                course.getReqId2(),
                course.getReqType2(),
                termTaken,
                studentProgress
        );

        checkEnrollmentRequisite(
                course,
                course.getReqId3(),
                course.getReqType3(),
                termTaken,
                studentProgress
        );

        // --------------------------------------------------------
        // CREATE CURRICULUM PROGRESS
        // --------------------------------------------------------

        CurriculumProgress progress =
                new CurriculumProgress();

        progress.setStudentId(studentId);
        progress.setCourseId(courseId);
        progress.setTermTaken(termTaken);
        progress.setStatus(status);

        return curriculumRepository.save(progress);
    }

    // ============================================================
// CHECK ENROLLMENT REQUISITE
// ============================================================

    private void checkEnrollmentRequisite(
            MasterlistCourse course,
            Long requisiteId,
            String requisiteType,
            int currentTerm,
            List<CurriculumProgress> studentProgress
    ) {

        if (requisiteId == null) {
            return;
        }

        MasterlistCourse requisiteCourse =
                masterlistCourseRepository
                        .findById(requisiteId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Requisite course was not found."
                                )
                        );

        String requisiteCode =
                requisiteCourse.getCode();

        String type =
                requisiteType == null
                        ? ""
                        : requisiteType.trim().toUpperCase();

        CurriculumProgress progress =
                findProgress(
                        requisiteId,
                        studentProgress
                );

        // --------------------------------------------------------
        // H = HARD PREREQUISITE
        // MUST HAVE PASSED
        // --------------------------------------------------------

        if ("H".equals(type)) {

            boolean passed =
                    progress != null
                            && progress.isPassed();

            if (!passed) {

                throw new IllegalArgumentException(
                        "Cannot enroll in "
                                + course.getCode()
                                + ". You must pass "
                                + requisiteCode
                                + " first."
                );
            }

            return;
        }

        // --------------------------------------------------------
        // S = SOFT PREREQUISITE
        // MUST HAVE TAKEN
        // PASSING IS NOT REQUIRED
        // --------------------------------------------------------

        if ("S".equals(type)) {

            boolean taken =
                    progress != null;

            if (!taken) {

                throw new IllegalArgumentException(
                        "Cannot enroll in "
                                + course.getCode()
                                + ". You must have taken "
                                + requisiteCode
                                + " first."
                );
            }

            return;
        }

        // --------------------------------------------------------
        // C = CO-REQUISITE
        // MUST BE TAKEN IN SAME TERM
        // --------------------------------------------------------

        if ("C".equals(type)) {

            boolean takenTogether =
                    progress != null
                            && progress.getTermTaken()
                            == currentTerm;

            if (!takenTogether) {

                throw new IllegalArgumentException(
                        "Cannot enroll in "
                                + course.getCode()
                                + ". "
                                + requisiteCode
                                + " must be taken in the same term."
                );
            }

            return;
        }
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