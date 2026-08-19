package ph.edu.dlsu.lbycpob.animoquest.service;

import org.springframework.stereotype.Service;
import ph.edu.dlsu.lbycpob.animoquest.model.MasterlistCourse;
import ph.edu.dlsu.lbycpob.animoquest.model.TermChecklist;
import ph.edu.dlsu.lbycpob.animoquest.repository.MasterlistCourseRepository;
import ph.edu.dlsu.lbycpob.animoquest.repository.TermChecklistRepository;

import java.util.ArrayList;
import java.util.Arrays;
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

    /**
     * Gets all term checklists for a specific batch and degree.
     */
    public List<TermChecklist> getChecklists(
            int batch,
            String degree
    ) {

        return termChecklistRepository
                .findByBatchAndDegreeOrderByTermNumberAsc(
                        batch,
                        degree
                );
    }

    /**
     * Gets the checklist for one specific term.
     */
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
                                "Term checklist was not found."
                        )
                );
    }

    /**
     * Gets the actual courses belonging to a term checklist.
     */
    public List<MasterlistCourse> getCoursesForTerm(
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

        Long[] courseIds = checklist.getCourseIds();

        if (courseIds == null || courseIds.length == 0) {
            return new ArrayList<>();
        }

        return masterlistCourseRepository.findByIdIn(
                Arrays.asList(courseIds)
        );
    }
}