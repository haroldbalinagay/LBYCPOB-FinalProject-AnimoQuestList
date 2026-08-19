package ph.edu.dlsu.lbycpob.animoquest.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.Objects;

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

    public MasterlistCourse(String code, String name, int units, boolean passFail) {
        this.code = code;
        this.name = name;
        this.units = units;
        this.passFail = passFail;
    }

    public boolean hasNoRequisites() {
        return reqId1 == null && reqId2 == null && reqId3 == null;
    }

    public Long getRequisiteIdAt(int requisite) {
        return switch (requisite) {
            case 1 -> reqId1;
            case 2 -> reqId2;
            case 3 -> reqId3;
            default -> null;
        };
    }

    public String getRequisiteTypeAt(int requisite) {
        return switch (requisite) {
            case 1 -> reqType1;
            case 2 -> reqType2;
            case 3 -> reqType3;
            default -> null;
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MasterlistCourse that = (MasterlistCourse) o;
        return Objects.equals(this.code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
}