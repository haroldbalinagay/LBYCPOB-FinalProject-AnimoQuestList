package ph.edu.dlsu.lbycpob.animoquest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ph.edu.dlsu.lbycpob.animoquest.model.TermChecklist;

import java.util.Optional;
import java.util.List;

public interface TermChecklistRepository
        extends JpaRepository<TermChecklist, Long> {

    Optional<TermChecklist> findByBatchAndDegreeAndTermNumber(
            int batch,
            String degree,
            int termNumber
    );

    List<TermChecklist> findByBatchAndDegreeOrderByTermNumberAsc(
            int batch,
            String degree
    );
}