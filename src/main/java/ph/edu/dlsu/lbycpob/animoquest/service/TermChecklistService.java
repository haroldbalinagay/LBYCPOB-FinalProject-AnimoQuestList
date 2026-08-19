package ph.edu.dlsu.lbycpob.animoquest.service;

import org.springframework.stereotype.Service;
import ph.edu.dlsu.lbycpob.animoquest.model.MasterlistCourse;
import ph.edu.dlsu.lbycpob.animoquest.model.TermChecklist;
import ph.edu.dlsu.lbycpob.animoquest.repository.MasterlistCourseRepository;
import ph.edu.dlsu.lbycpob.animoquest.repository.TermChecklistRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class TermChecklistService {

    private final TermChecklistRepository termChecklistRepository;
    private final MasterlistCourseRepository masterlistCourseRepository;

    public TermChecklistService(
            TermChecklistRepository termChecklistRepository,
            MasterlistCourseRepository masterlistCourseRepository
    ) {
        this.termChecklistRepository = termChecklistRepository;
        this.masterlistCourseRepository = masterlistCourseRepository;
    }

    // ============================================================
    // GET CHECKLIST
    // ============================================================

    public TermChecklist getChecklist(
            int batch,
            String degree,
            int termNumber
    ) {

        return termChecklistRepository
                .findByBatchAndDegreeAndTermNumber(
                        batch,
                        degree,
                        termNumber
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "No checklist was found for "
                                        + degree
                                        + " Term "
                                        + termNumber
                        )
                );
    }

    // ============================================================
    // GET COURSES FOR CHECKLIST
    // ============================================================

    public List<MasterlistCourse> getCoursesForChecklist(
            int batch,
            String degree,
            int termNumber
    ) {

        TermChecklist checklist =
                getChecklist(
                        batch,
                        degree,
                        termNumber
                );

        List<MasterlistCourse> courses =
                new ArrayList<>();

        if (checklist.getCourseIds() == null) {
            return courses;
        }

        for (Long courseId : checklist.getCourseIds()) {

            MasterlistCourse course =
                    masterlistCourseRepository
                            .findById(courseId)
                            .orElse(null);

            if (course != null) {
                courses.add(course);
            }
        }

        return courses;
    }

    // ============================================================
    // GET MAX UNITS
    // ============================================================

    public int getMaxUnits(
            int batch,
            String degree,
            int termNumber
    ) {

        return getChecklist(
                batch,
                degree,
                termNumber
        ).getMaxUnits();
    }
}