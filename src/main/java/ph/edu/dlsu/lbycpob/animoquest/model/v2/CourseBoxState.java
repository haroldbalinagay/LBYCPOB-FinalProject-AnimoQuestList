package ph.edu.dlsu.lbycpob.animoquest.model.v2;

import javafx.css.PseudoClass;
import lombok.Getter;

@Getter
public enum CourseBoxState {
    REQ_PASSED("requisite-passed"),
    REQ_FAILED("requisite-failed"),
    REQ_IN_PROGRESS("requisite-in-progress"),
    DEPENDENT("dependent"),
    ELIGIBLE("source-eligible"),
    INELIGIBLE("source-ineligible");

    private final PseudoClass pseudoClass;

    // Constructor creates the JavaFX PseudoClass automatically
    CourseBoxState(String cssStateName) {
        this.pseudoClass = PseudoClass.getPseudoClass(cssStateName);
    }

    /**
     * Converts a CourseStatus to a CourseBoxState.
     * <p>
     *     NOTE: CourseStatus.NOT_TAKEN is simply null.
     * </p>
     * @param status The CourseStatus to convert
     * @return The corresponding CourseBoxState
     */
    public static CourseBoxState convertStatusToState(CourseStatus status) {
        CourseBoxState state = null;
        switch (status) {
            case PASSED -> state = REQ_PASSED;
            case FAILED -> state = REQ_FAILED;
            case IN_PROGRESS -> state = REQ_IN_PROGRESS;
            case DEPENDENT -> state = DEPENDENT;
        }
        return state;
    }
}
