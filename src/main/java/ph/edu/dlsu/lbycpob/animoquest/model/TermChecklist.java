package ph.edu.dlsu.lbycpob.animoquest.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "term_checklists")
public class TermChecklist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int batch;

    @Column(nullable = false)
    private String degree;

    @Column(name = "term_number", nullable = false)
    private int termNumber;

    @Column(name = "max_units", nullable = false)
    private int maxUnits;

    @Column(name = "course_ids", nullable = false)
    private long[] courseIds;

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    public TermChecklist() {
    }

    public TermChecklist(int batch, String degree, int termNumber, int maxUnits, long[] courseIds) {
        this.batch = batch;
        this.degree = degree;
        this.termNumber = termNumber;
        this.maxUnits = maxUnits;
        this.courseIds = courseIds;
    }

// TODO: Add attributes & behaviors for term checklist
}
