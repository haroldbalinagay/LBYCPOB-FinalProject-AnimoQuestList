package ph.edu.dlsu.lbycpob.animoquest.service;

import org.springframework.stereotype.Service;
import ph.edu.dlsu.lbycpob.animoquest.model.CourseStatus;
import ph.edu.dlsu.lbycpob.animoquest.model.CurriculumProgress;
import ph.edu.dlsu.lbycpob.animoquest.model.MasterlistCourse;
import ph.edu.dlsu.lbycpob.animoquest.model.TermChecklist;
import ph.edu.dlsu.lbycpob.animoquest.repository.MasterlistCourseRepository;
import ph.edu.dlsu.lbycpob.animoquest.repository.TermChecklistRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class TermChecklistService {
    private final MasterlistCourseRepository courseRepository;
    private final TermChecklistRepository checklistRepository;

    private final CurriculumService progressService;

    public TermChecklistService(MasterlistCourseRepository courseRepository, TermChecklistRepository checklistRepository, CurriculumService progressService) {
        this.courseRepository = courseRepository;
        this.checklistRepository = checklistRepository;
        this.progressService = progressService;
    }
    
    public List<TermChecklist> getTermChecklistsOf(String degree, int batch) {
        if (degree == null || batch <= 0) return null;
        return checklistRepository.findAllByDegreeAndBatch(degree, batch);
    }

    public TermChecklist getChecklistOf(String degree, int batch, int termNumber) {
        if (degree == null || batch <= 0 || termNumber <= 0) return null;
        return checklistRepository.findTermChecklistByDegreeAndBatchAndTermNumber(degree, batch, termNumber);
    }

    public List<MasterlistCourse> getCourseDetailsOf(TermChecklist checklist) {
        return courseRepository.findByIdIn(checklist.getCourseIds());
    }

    /**
     * Finds the requisite course associated with each requisite ID of the provided course.
     * @param course The source to get the requisite IDs
     * @return A list of requisite course codes
     */
    public List<String> getCompleteRequisitesOf(MasterlistCourse course) {
        List<String> reqs = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            Long reqId = course.getRequisiteIdAt(i);

            if (reqId == null) continue;

            Optional<MasterlistCourse> reqObject = courseRepository.findById(reqId);
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
    public CourseStatus getStatusOf(MasterlistCourse course, List<CurriculumProgress> progressList) {
        CourseStatus status = CourseStatus.NOT_TAKEN;
        int latestTermTaken = 0;

        // Loop through each progress record of the student
        // NO BREAK: Since possible to take the same course multiple times (ex. failed Term 1 -> take again Term 2)
        for (CurriculumProgress progress : progressList) {
            // Check if source course is the same as course in record
            if (Objects.equals(course.getId(), progress.getCourseId())) {
                // Save the status of the MOST RECENT info about course
                if (progress.getTermTaken() >= latestTermTaken) {
                    status = progress.getStatus();
                }
            }
        }
        IO.println(status);
        return status;
    }

    public boolean checkEnrollEligibilityOf(MasterlistCourse course) {
        return false;
    }
}
