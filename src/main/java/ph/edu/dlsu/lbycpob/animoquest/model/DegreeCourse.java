package ph.edu.dlsu.lbycpob.animoquest.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "degree_courses")
public class DegreeCourse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(nullable = false)
    private String degree;

    @Column(name = "req_1")
    private Long reqId1;

    @Column(name = "req_type_1")
    private String reqType1;

    @Column(name = "req_2")
    private Long reqId2;

    @Column(name = "req_type_2")
    private String reqType2;

    @Column(name = "req_3")
    private Long reqId3;

    @Column(name = "req_type_3")
    private String reqType3;

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    public DegreeCourse() {
    }

    // TODO: still missing reqIds & reqTypes (use builder)
    public DegreeCourse(Long courseId, String degree) {
        this.courseId = courseId;
        this.degree = degree;
    }

    // TODO: Add attributes & behaviors for course with degree-specific info
}
