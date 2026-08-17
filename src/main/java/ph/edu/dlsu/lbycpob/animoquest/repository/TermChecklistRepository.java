package ph.edu.dlsu.lbycpob.animoquest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ph.edu.dlsu.lbycpob.animoquest.model.TermChecklist;

import java.util.List;
import java.util.Optional;

public interface TermChecklistRepository extends JpaRepository<TermChecklist, Long> {

    TermChecklist findTermChecklistByDegreeAndBatchAndTermNumber(String degree, int batch, int termNumber);

    List<TermChecklist> findAllByDegreeAndBatch(String degree, int batch);

}
