package ph.edu.dlsu.lbycpob.animoquest.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "curriculum_progress")
public class CurriculumProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "term_taken", nullable = false)
    private int termTaken;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CourseStatus status;

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    public CurriculumProgress() {
    }

    public CurriculumProgress(
            Long studentId,
            Long courseId,
            int termTaken,
            CourseStatus status
    ) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.termTaken = termTaken;
        this.status = status;
    }
}