package ph.edu.dlsu.lbycpob.animoquest.repository.v2;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ph.edu.dlsu.lbycpob.animoquest.model.v2.TermChecklistV2;

import java.util.List;

public interface TermChecklistRepositoryV2 extends JpaRepository<TermChecklistV2, Long> {

    TermChecklistV2 findTermChecklistByDegreeAndBatchAndTermNumber(String degree, int batch, int termNumber);

    List<TermChecklistV2> findAllByDegreeAndBatch(String degree, int batch);

    @Modifying
    @Transactional
    @Query("UPDATE TermChecklistV2 t SET t.courseIds = :ids, t.maxUnits = :units WHERE t.id = :id")
    void updateCourseIds(@Param("id") Long id,
                         @Param("units") int maxUnits,
                         @Param("ids") long[] courseIds);

}
