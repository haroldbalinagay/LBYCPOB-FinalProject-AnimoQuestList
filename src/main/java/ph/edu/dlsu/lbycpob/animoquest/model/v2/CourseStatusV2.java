package ph.edu.dlsu.lbycpob.animoquest.model.v2;

import lombok.Getter;

@Getter
public enum CourseStatusV2 {
    PASSED("PASSED"),
    FAILED("FAILED"),
    IN_PROGRESS("IN-PROGRESS"),
    NOT_TAKEN("NOT TAKEN"),
    DEPENDENT("");

    private final String status;

    CourseStatusV2(String status) {
        this.status = status;
    }

}
