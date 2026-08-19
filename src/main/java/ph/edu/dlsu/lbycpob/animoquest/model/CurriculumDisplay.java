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

    // Prerequisite information
    private final String requisiteInfo;
    private final String missingPrerequisiteWarning;

    public CurriculumDisplay(
            Long courseId,
            String code,
            String name,
            int units,
            String status,
            int term,
            String requisiteInfo,
            String missingPrerequisiteWarning
    ) {
        this.courseId = courseId;
        this.code = code;
        this.name = name;
        this.units = units;
        this.status = status;
        this.term = term;
        this.requisiteInfo = requisiteInfo;
        this.missingPrerequisiteWarning = missingPrerequisiteWarning;
    }
}