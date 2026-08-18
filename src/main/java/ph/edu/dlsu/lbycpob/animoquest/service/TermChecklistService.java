package ph.edu.dlsu.lbycpob.animoquest.service;

import org.springframework.stereotype.Service;
import ph.edu.dlsu.lbycpob.animoquest.model.MasterlistCourse;
import ph.edu.dlsu.lbycpob.animoquest.model.TermChecklist;
import ph.edu.dlsu.lbycpob.animoquest.repository.MasterlistCourseRepository;
import ph.edu.dlsu.lbycpob.animoquest.repository.TermChecklistRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TermChecklistService {
    private final MasterlistCourseRepository courseRepository;
    private final TermChecklistRepository checklistRepository;

    public TermChecklistService(MasterlistCourseRepository courseRepository, TermChecklistRepository checklistRepository) {
        this.courseRepository = courseRepository;
        this.checklistRepository = checklistRepository;
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
            Long reqId = course.getRequisiteIdOf(i);

            if (reqId == null) continue;

            Optional<MasterlistCourse> reqObject = courseRepository.findById(reqId);
            reqObject.ifPresent(masterlistCourse -> reqs.add(masterlistCourse.getCode()));
        }
        return reqs;
    }
}
