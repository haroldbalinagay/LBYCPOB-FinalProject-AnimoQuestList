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

    public CurriculumProgress(
            Long studentId,
            Long courseId,
            int termTaken,
            boolean passed,
            boolean inProgress
    ) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.termTaken = termTaken;
        this.passed = passed;
        this.inProgress = inProgress;
    }

    /**
     * Returns the status of the course as text.
     */
    public String getStatus() {

        if (passed) {
            return "PASSED";
        }

        if (inProgress) {
            return "IN-PROGRESS";
        }

        return "FAILED";
    }

    /**
     * Updates the database flags based on the selected status.
     */
    public void setStatus(String status) {

        switch (status) {

            case "PASSED":
                this.passed = true;
                this.inProgress = false;
                break;

            case "IN-PROGRESS":
                this.passed = false;
                this.inProgress = true;
                break;

            case "FAILED":
                this.passed = false;
                this.inProgress = false;
                break;

            default:
                throw new IllegalArgumentException(
                        "Invalid course status."
                );
        }
    }
}