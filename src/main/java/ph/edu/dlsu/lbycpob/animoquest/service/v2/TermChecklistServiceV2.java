package ph.edu.dlsu.lbycpob.animoquest.service.v2;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ph.edu.dlsu.lbycpob.animoquest.model.v2.CourseStatusV2;
import ph.edu.dlsu.lbycpob.animoquest.model.v2.CurriculumProgressV2;
import ph.edu.dlsu.lbycpob.animoquest.model.v2.MasterlistCourseV2;
import ph.edu.dlsu.lbycpob.animoquest.model.v2.TermChecklistV2;
import ph.edu.dlsu.lbycpob.animoquest.repository.v2.MasterlistCourseRepositoryV2;
import ph.edu.dlsu.lbycpob.animoquest.repository.v2.TermChecklistRepositoryV2;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class TermChecklistServiceV2 {
    private final MasterlistCourseRepositoryV2 courseRepository;
    private final TermChecklistRepositoryV2 checklistRepository;

    private final CurriculumServiceV2 progressService;

    public TermChecklistServiceV2(MasterlistCourseRepositoryV2 courseRepository, TermChecklistRepositoryV2 checklistRepository, CurriculumServiceV2 progressService) {
        this.courseRepository = courseRepository;
        this.checklistRepository = checklistRepository;
        this.progressService = progressService;
    }

    /**
     * @return A list of all courses from the course masterlist.
     */
    public List<MasterlistCourseV2> getAllCourses() {
        return courseRepository.findAll(Sort.by(Sort.Direction.ASC, "code"));
    }

    public List<TermChecklistV2> getTermChecklistsOf(String degree, int batch) {
        if (degree == null || batch <= 0) return null;
        return checklistRepository.findAllByDegreeAndBatch(degree, batch);
    }

    /**
     * Supplies the corresponding term checklist based on the following parameters:
     * @param degree The student's degree program
     * @param batch The student's freshman start year ID
     * @param termNumber The specific term checklist to search for
     * @return A specific term checklist
     */
    public TermChecklistV2 getChecklistOf(String degree, int batch, int termNumber) {
        if (degree == null || batch <= 0 || termNumber <= 0) return null;
        return checklistRepository.findTermChecklistByDegreeAndBatchAndTermNumber(degree, batch, termNumber);
    }

    /**
     * Supplies an ordered list of the courses associated with the given term checklist.
     * @param checklist The term checklist to search for
     * @return An ordered list of courses
     */
    public List<MasterlistCourseV2> getCourseDetailsOf(TermChecklistV2 checklist) {
        long[] ids = checklist.getCourseIds();

        // Fetch unordered records from repository
        List<MasterlistCourseV2> courses = courseRepository.findByIdIn(ids);

        // Map IDs to their original array index for O(1) lookups
        Map<Long, Integer> idOrderMap = IntStream.range(0, ids.length)
                .boxed()
                .collect(Collectors.toMap(i -> ids[i], i -> i));

        // Sort the list based on the map
        courses.sort(Comparator.comparingInt(user -> idOrderMap.getOrDefault(user.getId(), Integer.MAX_VALUE)));

        return courses;
    }

    /**
     * Finds the requisite course associated with each requisite ID of the provided course.
     * @param course The source to get the requisite IDs
     * @return A list of requisite course codes
     */
    public List<String> getCompleteRequisitesOf(MasterlistCourseV2 course) {
        List<String> reqs = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            Long reqId = course.getRequisiteIdAt(i);

            if (reqId == null) continue;

            Optional<MasterlistCourseV2> reqObject = courseRepository.findById(reqId);
            reqObject.ifPresent(masterlistCourse -> reqs.add(masterlistCourse.getCode()));
        }
        return reqs;
    }

    /**
     * Searches for the MOST RECENT record containing the given course in the curriculum progress records of a student.
     * @param course The course to look for
     * @param progressList The complete curriculum progress records of a student
     * @return The most recent status of the course
     */
    public CourseStatusV2 getStatusOf(MasterlistCourseV2 course, List<CurriculumProgressV2> progressList) {
        CourseStatusV2 status = CourseStatusV2.NOT_TAKEN;
        int latestTermTaken = 0;

        // Loop through each progress record of the student
        // NO BREAK: Since possible to take the same course multiple times (ex. failed Term 1 -> take again Term 2)
        for (CurriculumProgressV2 progress : progressList) {
            // Check if source course is the same as course in record
            if (Objects.equals(course.getId(), progress.getCourseId())) {
                // Save the status of the MOST RECENT info about course
                if (progress.getTermTaken() >= latestTermTaken) {
                    status = progress.getStatus(); // TODO: HARMONY POINT
                }
            }
        }
        IO.println(status);
        return status;
    }

    /**
     * Determines if the given course is eligible to be enrolled in.
     * Takes into account the student's curriculum progress records and H/S/C requisite rules.
     * @param course The course to evaluate
     * @param progressList The complete curriculum progress records of a student
     * @return EnrollEligibility record containing whether eligible or not, and accompanying reason
     */
    public EnrollEligibility checkEnrollEligibilityOf(MasterlistCourseV2 course, List<CurriculumProgressV2> progressList) {
        // Setup tracking lists
        List<Boolean> reqChecked = new ArrayList<>();
        List<CourseStatusV2> reqStatuses = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            Long reqId = course.getRequisiteIdAt(i);
            if (reqId == null) reqChecked.add(null);
            else reqChecked.add(false);

            reqStatuses.add(null);
        }

        // Loop through each progress record of the student
        for (CurriculumProgressV2 progress : progressList) {
            // Check each req slot
            for (int i = 0; i < 3; i++) {
                Long reqId = course.getRequisiteIdAt(i);
                if (reqId == null) continue;

                // Check if source course is the same as course in record
                if (Objects.equals(progress.getCourseId(), reqId)) {
                    // Update tracking lists
                    reqChecked.set(i, true);
                    reqStatuses.set(i, progress.getStatus());
                }
            }
        }

        // Evaluate each req against general and H/S/C req rules
        boolean eligible = true;
        String reason = "All requisites have been met.";
        int reqNotTakenCount = 0;
        int hardReqFailCount = 0;
        int reqInProgressCount = 0;

        for (int i = 0; i < 3; i++) {
            if (reqChecked.get(i) == null) continue;

            // Check if req is NOT TAKEN
            if (!reqChecked.get(i)) {
                eligible = false;
                reqNotTakenCount++;
            }

            String reqType = course.getRequisiteTypeAt(i);

            // Check H/S/C rules
            if (reqStatuses.get(i) == CourseStatusV2.FAILED && Objects.equals(reqType, "H")) {
                eligible = false;
                hardReqFailCount++;
            }
            if (reqStatuses.get(i) == CourseStatusV2.IN_PROGRESS) {
                if (Objects.equals(reqType, "H") || Objects.equals(reqType, "S")) {
                    eligible = false;
                    reqInProgressCount++;
                }
            }

        }
        // Format output reason
        if (!eligible) {
            StringBuilder sb = new StringBuilder();

            if (reqNotTakenCount > 0) sb.append(reqNotTakenCount).append(" requisite not yet taken   ");
            if (reqInProgressCount > 0) sb.append(reqInProgressCount).append(" Hard/Soft requisite in-progress   ");
            if (hardReqFailCount > 0) sb.append(hardReqFailCount).append(" Hard requisite failed");

            reason = sb.toString();
        }

        // Return using a record
        return new EnrollEligibility(eligible, reason);
    }

    /**
     * A data transfer object.
     * @param eligible Whether a course is eligible to be enrolled in or not
     * @param reason Brief explanation of eligibility status
     */
    public record EnrollEligibility(boolean eligible, String reason) {}

    /**
     * Saves checklist data to the database.
     * This method either updates an existing record with new data OR creates a new record if one does not exist already.
     * @param degree The degree program
     * @param batch The freshman year ID code
     * @param termNumber The term number of checklist
     * @param courseList A list of course IDs
     */
    @Transactional
    public void saveChecklistData(String degree, int batch, int termNumber, List<MasterlistCourseV2> courseList) {
        if (degree == null || batch <= 0 || termNumber <= 0 || courseList.isEmpty()) return;

        int units = 0;
        List<Long> courseIds = new ArrayList<>();

        // Calculate max units & extract all course IDs into a list
        for (MasterlistCourseV2 course : courseList) {
            if (course == null) continue;
            units += course.getUnits();
            courseIds.add(course.getId());
        }

        // Convert ArrayList<Long> into long[] array
        long[] idsArray = courseIds.stream().mapToLong(Long::longValue).toArray();

        try {
            // Fetch the existing record from the database
            TermChecklistV2 existingChecklist = checklistRepository.findTermChecklistByDegreeAndBatchAndTermNumber(degree, batch, termNumber);
            // Throw exception if not found
            if (existingChecklist == null) throw new EntityNotFoundException("Checklist not found.");
            // Update the max units & course IDs of the record
            checklistRepository.updateCourseIds(existingChecklist.getId(), units, idsArray);

        } catch (EntityNotFoundException e) {
            // If no existing record, save a new record instead.
            checklistRepository.save(new TermChecklistV2(batch, degree, termNumber, units, idsArray));
        }
    }

}
