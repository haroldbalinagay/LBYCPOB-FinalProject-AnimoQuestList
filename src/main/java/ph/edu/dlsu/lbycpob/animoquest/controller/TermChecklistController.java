package ph.edu.dlsu.lbycpob.animoquest.controller;

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

    private static final String CURRENT_DEGREE =
            "BS Computer Engineering";

    @FXML
    private TabPane termTabPane;

    @FXML
    private Button addSelectedButton;

    private final TermChecklistService termChecklistService;

    private final CurriculumService curriculumService;

    /*
     * Student who is currently logged in.
     */
    private Long currentStudentId;

    /*
     * Current term selected by the student
     * in the Enrollment Planning screen.
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


    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    public TermChecklistController(
            TermChecklistService termChecklistService,
            CurriculumService curriculumService
    ) {
        this.termChecklistService = termChecklistService;
        this.curriculumService = curriculumService;
    }


    // ============================================================
    // SET STUDENT ID
    // ============================================================

    public void setStudentId(Long studentId) {

        this.currentStudentId = studentId;
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
                        CURRENT_DEGREE
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
                        CURRENT_DEGREE,
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
             * Store the course ID inside the CheckBox.
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
    private void handleAddSelectedCourses() {

        /*
         * Make sure a student is logged in.
         */
        if (currentStudentId == null) {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Student Not Found",
                    "No student is currently logged in."
            );

            return;
        }

        /*
         * Collect all selected course IDs.
         *
         * HashSet prevents the same course from
         * accidentally being added more than once.
         */
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

        /*
         * No courses selected.
         */
        if (selectedCourseIds.isEmpty()) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "No Courses Selected",
                    "Please select at least one course."
            );

            return;
        }

        int successful = 0;

        StringBuilder errors =
                new StringBuilder();

        /*
         * Add each selected course to
         * curriculum_progress.
         */
        for (Long courseId :
                selectedCourseIds) {

            try {

                curriculumService.addCourse(
                        currentStudentId,
                        courseId,
                        currentTerm,
                        "IN-PROGRESS"
                );

                successful++;

            } catch (Exception e) {

                errors.append(
                        e.getMessage()
                );

                errors.append(
                        "\n\n"
                );
            }
        }

        /*
         * Show successful additions.
         */
        if (successful > 0) {

            showAlert(
                    Alert.AlertType.INFORMATION,
                    "Courses Added",
                    successful
                            + " course(s) were added "
                            + "to Term "
                            + currentTerm
                            + "."
            );
        }

        /*
         * Show courses that could not be added.
         *
         * This can happen if:
         * - the course already exists
         * - a hard prerequisite is missing
         * - a soft prerequisite is missing
         * - a co-requisite is missing
         */
        if (!errors.isEmpty()) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "Some Courses Were Not Added",
                    errors.toString()
            );
        }

        /*
         * Uncheck the courses after processing.
         */
        clearSelections();
    }


    // ============================================================
    // CLEAR CHECKBOX SELECTIONS
    // ============================================================

    private void clearSelections() {

        for (List<CheckBox> checkBoxes :
                termCheckBoxes.values()) {

            for (CheckBox checkBox :
                    checkBoxes) {

                checkBox.setSelected(false);
            }
        }
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