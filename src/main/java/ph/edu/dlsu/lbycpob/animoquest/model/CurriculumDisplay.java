package ph.edu.dlsu.lbycpob.animoquest.model;

import lombok.Getter;

@Getter
public class CurriculumDisplay {

    private final Long courseId;
    private final String code;
    private final String name;
    private final int units;
    private final String status;
    private final int term;

    private final String requisites;
    private final boolean valid;
    private final String warningMessage;

    public CurriculumDisplay(
            Long courseId,
            String code,
            String name,
            int units,
            String status,
            int term,
            String requisites,
            boolean valid,
            String warningMessage
    ) {
        this.courseId = courseId;
        this.code = code;
        this.name = name;
        this.units = units;
        this.status = status;
        this.term = term;
        this.requisites = requisites;
        this.valid = valid;
        this.warningMessage = warningMessage;
    }
}