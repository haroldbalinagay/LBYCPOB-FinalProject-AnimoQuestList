package ph.edu.dlsu.lbycpob.animoquest.repository.v2;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ph.edu.dlsu.lbycpob.animoquest.model.v2.TermChecklist;

import java.util.List;

public interface TermChecklistRepository extends JpaRepository<TermChecklist, Long> {

    TermChecklist findTermChecklistByDegreeAndBatchAndTermNumber(String degree, int batch, int termNumber);

    List<TermChecklist> findAllByDegreeAndBatch(String degree, int batch);

    @Modifying
    @Transactional
    @Query("UPDATE TermChecklist t SET t.courseIds = :ids, t.maxUnits = :units WHERE t.id = :id")
    void updateCourseIds(@Param("id") Long id,
                         @Param("units") int maxUnits,
                         @Param("ids") long[] courseIds);

}
