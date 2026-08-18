package ph.edu.dlsu.lbycpob.animoquest.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "course_masterlist")
public class MasterlistCourse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int units;

    @Column(nullable = false)
    private boolean passFail;

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

    public MasterlistCourse() {
    }

    // TODO: missing reqIds & reqTypes (use builder)
    public MasterlistCourse(String code, String name, int units, boolean passFail) {
        this.code = code;
        this.name = name;
        this.units = units;
        this.passFail = passFail;
    }

    // TODO: Add attributes & behaviors for course in masterlist

    /**
     * @return Whether the course has no requisites
     */
    public boolean hasNoRequisites() {
        return reqId1 == null && reqId2 == null && reqId3 == null;
    }

    /**
     * @param requisite The requisite "number"
     * @return The desired requisite ID
     */
    public Long getRequisiteIdAt(int requisite) {
        switch (requisite) {
            case 1 -> { return reqId1; }
            case 2 -> { return reqId2; }
            case 3 -> { return reqId3; }
            default -> { return null; }
        }
    }

    /**
     * @param requisite The requisite "number"
     * @return The desired requisite type
     */
    public String getRequisiteTypeAt(int requisite) {
        switch (requisite) {
            case 1 -> { return reqType1; }
            case 2 -> { return reqType2; }
            case 3 -> { return reqType3; }
            default -> { return null; }
        }
    }
}
