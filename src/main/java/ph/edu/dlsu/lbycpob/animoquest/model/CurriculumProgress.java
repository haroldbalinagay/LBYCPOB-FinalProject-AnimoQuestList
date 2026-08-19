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

    @Column(nullable = false)
    private boolean passed;

    @Column(name = "in_progress", nullable = false)
    private boolean inProgress;

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    public CurriculumProgress() {
    }

    public CurriculumProgress(Long studentId, Long courseId, int termTaken, boolean passed) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.termTaken = termTaken;
        this.passed = passed;
    }

    // TODO: Add attributes & behaviors for curriculum progress of a student

    public CourseStatus getStatus() {
        if (passed) return CourseStatus.PASSED;
        else if (inProgress) return CourseStatus.IN_PROGRESS;
        else return CourseStatus.FAILED;
    }
}
