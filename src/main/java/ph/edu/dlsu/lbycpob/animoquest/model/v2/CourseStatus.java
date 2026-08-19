package ph.edu.dlsu.lbycpob.animoquest.model.v2;

import lombok.Getter;

@Getter
public enum CourseStatus {
    PASSED("PASSED"),
    FAILED("FAILED"),
    IN_PROGRESS("IN-PROGRESS"),
    NOT_TAKEN("NOT TAKEN"),
    DEPENDENT("");

    private final String status;

    CourseStatus(String status) {
        this.status = status;
    }

}
