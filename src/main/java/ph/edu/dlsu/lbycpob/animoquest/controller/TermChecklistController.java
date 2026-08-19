package ph.edu.dlsu.lbycpob.animoquest.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;
import ph.edu.dlsu.lbycpob.animoquest.model.MasterlistCourse;
import ph.edu.dlsu.lbycpob.animoquest.model.TermChecklist;
import ph.edu.dlsu.lbycpob.animoquest.service.CurriculumService;
import ph.edu.dlsu.lbycpob.animoquest.service.TermChecklistService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@FxmlView("term-checklist.fxml")
public class TermChecklistController {

    private static final int CURRENT_BATCH = 125;

    private String currentDegree;

    @FXML
    private TabPane termTabPane;

    @FXML
    private Button addSelectedButton;

    private final TermChecklistService termChecklistService;

    private final CurriculumService curriculumService;

    /*
     * The student who is currently logged in.
     */
    private Long currentStudentId;

    /*
     * The term the student is currently planning for.
     *
     * For now, default to Term 1.
     */
    private int currentTerm = 1;

    /*
     * Stores the CheckBoxes for each term.
     *
     * Example:
     *
     * Term 1 -> [checkbox1, checkbox2, checkbox3]
     * Term 2 -> [checkbox4, checkbox5, checkbox6]
     */
    private final Map<Integer, List<CheckBox>> termCheckBoxes =
            new HashMap<>();

    public TermChecklistController(
            TermChecklistService termChecklistService,
            CurriculumService curriculumService
    ) {

        this.termChecklistService = termChecklistService;
        this.curriculumService = curriculumService;
    }

    // ============================================================
    // SET STUDENT
    // ============================================================

    public void setStudentId(Long studentId) {

        this.currentStudentId = studentId;
    }

    // ============================================================
// SET DEGREE
// ============================================================

    public void setDegree(String degree) {

        if (degree == null || degree.isBlank()) {

            throw new IllegalArgumentException(
                    "Student degree cannot be empty."
            );
        }

        this.currentDegree = degree;
    }

    // ============================================================
    // SET CURRENT TERM
    // ============================================================

    public void setCurrentTerm(int currentTerm) {

        if (currentTerm < 1 || currentTerm > 12) {

            throw new IllegalArgumentException(
                    "Current term must be between 1 and 12."
            );
        }

        this.currentTerm = currentTerm;
    }

    // ============================================================
    // INITIALIZE
    // ============================================================

    @FXML
    public void initialize() {

        loadTermChecklists();
    }

    // ============================================================
    // LOAD TERMS
    // ============================================================

    private void loadTermChecklists() {

        List<TermChecklist> checklists =
                termChecklistService.getChecklists(
                        CURRENT_BATCH,
                        currentDegree
                );

        termTabPane.getTabs().clear();

        termCheckBoxes.clear();

        for (TermChecklist checklist : checklists) {

            createTermTab(checklist);
        }
    }

// ============================================================
// CREATE TERM TAB
// ============================================================

    private void createTermTab(
            TermChecklist checklist
    ) {

        int termNumber =
                checklist.getTermNumber();

        VBox container =
                new VBox(10);

        container.setPadding(
                new javafx.geometry.Insets(15)
        );

        Label maxUnitsLabel =
                new Label(
                        "Maximum Units: "
                                + checklist.getMaxUnits()
                );

        container.getChildren().add(
                maxUnitsLabel
        );

        List<MasterlistCourse> courses =
                termChecklistService.getCoursesForTerm(
                        CURRENT_BATCH,
                        currentDegree,
                        termNumber
                );

        List<CheckBox> checkBoxes =
                new ArrayList<>();

        for (MasterlistCourse course : courses) {

            CheckBox checkBox =
                    new CheckBox();

            checkBox.setText(
                    course.getCode()
                            + " - "
                            + course.getName()
                            + " ("
                            + course.getUnits()
                            + " units)"
            );

            /*
             * Store the course ID inside
             * the CheckBox.
             */
            checkBox.setUserData(
                    course.getId()
            );

            checkBoxes.add(checkBox);

            container.getChildren().add(
                    checkBox
            );
        }

        termCheckBoxes.put(
                termNumber,
                checkBoxes
        );

        Tab tab =
                new Tab(
                        "Term " + termNumber
                );

        tab.setContent(container);

        tab.setClosable(false);

        termTabPane.getTabs().add(tab);
    }

    // ============================================================
    // ADD SELECTED COURSES
    // ============================================================

    @FXML
    private void handleAddSelectedCourses(
            ActionEvent event
    ) {

        // --------------------------------------------------------
        // CHECK STUDENT
        // --------------------------------------------------------

        if (currentStudentId == null) {

            showAlert(
                    Alert.AlertType.ERROR,
                    "No Student",
                    "No student is currently logged in."
            );

            return;
        }

        // --------------------------------------------------------
        // FIND SELECTED COURSES
        // --------------------------------------------------------

        Set<Long> selectedCourseIds =
                new HashSet<>();

        for (List<CheckBox> checkBoxes :
                termCheckBoxes.values()) {

            for (CheckBox checkBox :
                    checkBoxes) {

                if (checkBox.isSelected()) {

                    Long courseId =
                            (Long) checkBox.getUserData();

                    selectedCourseIds.add(
                            courseId
                    );
                }
            }
        }

        // --------------------------------------------------------
        // CHECK IF NOTHING WAS SELECTED
        // --------------------------------------------------------

        if (selectedCourseIds.isEmpty()) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "No Courses Selected",
                    "Please select at least one course."
            );

            return;
        }

        // --------------------------------------------------------
        // ADD COURSES
        // --------------------------------------------------------

        int addedCount = 0;

        List<String> errors =
                new ArrayList<>();

        for (Long courseId :
                selectedCourseIds) {

            try {

                curriculumService.addCourse(
                        currentStudentId,
                        courseId,
                        currentTerm,
                        "IN-PROGRESS"
                );

                addedCount++;

            } catch (Exception e) {

                errors.add(
                        e.getMessage()
                );
            }
        }

        // --------------------------------------------------------
        // SHOW RESULT
        // --------------------------------------------------------

        if (errors.isEmpty()) {

            showAlert(
                    Alert.AlertType.INFORMATION,
                    "Courses Added",
                    addedCount
                            + " course(s) were successfully added "
                            + "to your enrollment plan for Term "
                            + currentTerm
                            + "."
            );

        } else {

            StringBuilder message =
                    new StringBuilder();

            message.append(
                    addedCount
                            + " course(s) successfully added."
            );

            message.append("\n\n");

            message.append(
                    "Some courses could not be added:\n"
            );

            for (String error : errors) {

                message.append(
                        "• "
                );

                message.append(
                        error
                );

                message.append(
                        "\n"
                );
            }

            showAlert(
                    Alert.AlertType.WARNING,
                    "Some Courses Were Not Added",
                    message.toString()
            );
        }

        // --------------------------------------------------------
        // CLEAR CHECKBOXES
        // --------------------------------------------------------

        for (List<CheckBox> checkBoxes :
                termCheckBoxes.values()) {

            for (CheckBox checkBox :
                    checkBoxes) {

                checkBox.setSelected(false);
            }
        }
    }

    // ============================================================
    // BACK
    // ============================================================

    @FXML
    private void handleBack(ActionEvent event) {

        /*
         * Keep your existing back-navigation code here.
         *
         * We are not changing navigation in this step.
         */
    }

    // ============================================================
    // ALERT
    // ============================================================

    private void showAlert(
            Alert.AlertType type,
            String title,
            String message
    ) {

        Alert alert =
                new Alert(type);

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }
}