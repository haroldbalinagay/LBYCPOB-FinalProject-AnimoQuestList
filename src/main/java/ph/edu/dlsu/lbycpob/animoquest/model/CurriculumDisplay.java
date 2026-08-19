package ph.edu.dlsu.lbycpob.animoquest.model;

public class CurriculumDisplay {

    private Long courseId;
    private String code;
    private String name;
    private int units;
    private String status;
    private int term;
    private String requisites;
    private boolean valid;
    private String warningMessage;

    // ============================================================
    // CONSTRUCTOR
    // ============================================================

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

    // ============================================================
    // GETTERS
    // ============================================================

    public Long getCourseId() {
        return courseId;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public int getUnits() {
        return units;
    }

    public String getStatus() {
        return status;
    }

    public int getTerm() {
        return term;
    }

    public String getRequisites() {
        return requisites;
    }

    public boolean isValid() {
        return valid;
    }

    public String getWarningMessage() {
        return warningMessage;
    }

    // ============================================================
    // SETTERS
    // ============================================================

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUnits(int units) {
        this.units = units;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTerm(int term) {
        this.term = term;
    }

    public void setRequisites(String requisites) {
        this.requisites = requisites;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public void setWarningMessage(String warningMessage) {
        this.warningMessage = warningMessage;
    }
}