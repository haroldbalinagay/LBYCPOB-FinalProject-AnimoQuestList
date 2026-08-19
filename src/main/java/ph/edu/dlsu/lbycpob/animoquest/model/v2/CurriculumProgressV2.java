package ph.edu.dlsu.lbycpob.animoquest.model.v2;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "curriculum_progress")
public class CurriculumProgressV2 {
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

    public CurriculumProgressV2() {
    }

    public CurriculumProgressV2(Long studentId, Long courseId, int termTaken, boolean passed) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.termTaken = termTaken;
        this.passed = passed;
    }

    // TODO: Add attributes & behaviors for curriculum progress of a student

    public CourseStatusV2 getStatus() {
        if (passed) return CourseStatusV2.PASSED;
        else if (inProgress) return CourseStatusV2.IN_PROGRESS;
        else return CourseStatusV2.FAILED;
    }
}
