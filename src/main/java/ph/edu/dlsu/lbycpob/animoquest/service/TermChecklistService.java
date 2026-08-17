package ph.edu.dlsu.lbycpob.animoquest.service;

import org.springframework.stereotype.Service;
import ph.edu.dlsu.lbycpob.animoquest.model.MasterlistCourse;
import ph.edu.dlsu.lbycpob.animoquest.model.TermChecklist;
import ph.edu.dlsu.lbycpob.animoquest.repository.MasterlistCourseRepository;
import ph.edu.dlsu.lbycpob.animoquest.repository.TermChecklistRepository;

import java.util.List;

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

    public List<MasterlistCourse> getCourseDetailsOf(long[] courseIds) {
        return courseRepository.findByIdIn(courseIds);
    }
}
