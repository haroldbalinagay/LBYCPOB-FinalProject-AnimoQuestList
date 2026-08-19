package ph.edu.dlsu.lbycpob.animoquest.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

import net.rgielen.fxweaver.core.FxmlView;
import net.rgielen.fxweaver.core.FxWeaver;

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

    // ============================================================
    // CURRENT BATCH
    // ============================================================

    private static final int CURRENT_BATCH = 125;


    // ============================================================
    // FXML COMPONENTS
    // ============================================================

    @FXML
    private TabPane termTabPane;

    @FXML
    private Button addSelectedButton;


    // ============================================================
    // SERVICES
    // ============================================================

    private final TermChecklistService termChecklistService;

    private final CurriculumService curriculumService;

    private final FxWeaver fxWeaver;


    // ============================================================
    // CURRENT STUDENT
    // ============================================================

    /*
     * ID of the student who is currently logged in.
     */
    private Long currentStudentId;


    /*
     * Major/degree of the currently logged-in student.
     *
     * This comes from Student.major during Sign Up/Login.
     *
     * Example:
     *
     * "BS Computer Engineering"
     * "BS Electronics Engineering"
     */
    private String currentDegree;


    // ============================================================
    // CURRENT TERM
    // ============================================================

    /*
     * Current term selected in the Enrollment screen.
     *
     * This is an application/UI value.
     *
     * It is NOT stored as a separate database field.
     *
     * It is used as term_taken when adding a new course.
     */
    private int currentTerm = 1;


    // ============================================================
    // CHECKBOX STORAGE
    // ============================================================

    /*
     * Stores all course CheckBoxes according to their term.
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
            CurriculumService curriculumService,
            FxWeaver fxWeaver
    ) {

        this.termChecklistService =
                termChecklistService;

        this.curriculumService =
                curriculumService;

        this.fxWeaver =
                fxWeaver;
    }


    // ============================================================
    // INITIALIZE
    // ============================================================

    @FXML
    public void initialize() {

        /*
         * Do NOT load the checklist here.
         *
         * currentStudentId and currentDegree have not
         * necessarily been provided yet.
         *
         * The checklist will be loaded after
         * setStudentId() and setDegree() are called.
         */
    }


    // ============================================================
    // SET STUDENT ID
    // ============================================================

    public void setStudentId(Long studentId) {

        this.currentStudentId = studentId;

        tryLoadChecklist();
    }


// ============================================================
// SET DEGREE / MAJOR
// ============================================================

    public void setDegree(String degree) {

        this.currentDegree = degree;

        tryLoadChecklist();
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
    // TRY TO LOAD CHECKLIST
    // ============================================================

    private void tryLoadChecklist() {

        /*
         * We only load the checklist once we know
         * which student and degree we are dealing with.
         */
        if (currentStudentId == null) {
            return;
        }

        if (currentDegree == null ||
                currentDegree.isBlank()) {

            return;
        }

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


        /*
         * Check whether a curriculum was found.
         */
        if (checklists.isEmpty()) {

            Tab emptyTab =
                    new Tab("No Curriculum");

            VBox container =
                    new VBox(10);

            container.setPadding(
                    new javafx.geometry.Insets(15)
            );

            Label message =
                    new Label(
                            "No curriculum was found for:\n\n"
                                    + currentDegree
                                    + "\n\n"
                                    + "Batch: "
                                    + CURRENT_BATCH
                    );

            container.getChildren().add(
                    message
            );

            emptyTab.setContent(
                    container
            );

            emptyTab.setClosable(
                    false
            );

            termTabPane.getTabs().add(
                    emptyTab
            );

            return;
        }


        /*
         * Create a tab for every term.
         */
        for (TermChecklist checklist :
                checklists) {

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


        // --------------------------------------------------------
        // CONTAINER
        // --------------------------------------------------------

        VBox container =
                new VBox(10);

        container.setPadding(
                new javafx.geometry.Insets(15)
        );


        // --------------------------------------------------------
        // MAXIMUM UNITS
        // --------------------------------------------------------

        Label maxUnitsLabel =
                new Label(
                        "Maximum Units: "
                                + checklist.getMaxUnits()
                );

        container.getChildren().add(
                maxUnitsLabel
        );

        // --------------------------------------------------------
        // GET COURSES FOR TERM
        // --------------------------------------------------------

        List<MasterlistCourse> courses =
                termChecklistService.getCoursesForTerm(
                        CURRENT_BATCH,
                        currentDegree,
                        termNumber
                );


        // --------------------------------------------------------
        // CREATE CHECKBOXES
        // --------------------------------------------------------

        List<CheckBox> checkBoxes =
                new ArrayList<>();


        for (MasterlistCourse course :
                courses) {

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


            checkBoxes.add(
                    checkBox
            );


            container.getChildren().add(
                    checkBox
            );
        }


        // --------------------------------------------------------
        // STORE CHECKBOXES
        // --------------------------------------------------------

        termCheckBoxes.put(
                termNumber,
                checkBoxes
        );


        // --------------------------------------------------------
        // CREATE TAB
        // --------------------------------------------------------

        Tab tab =
                new Tab(
                        "Term " + termNumber
                );


        tab.setContent(
                container
        );


        tab.setClosable(
                false
        );


        termTabPane.getTabs().add(
                tab
        );
    }


    // ============================================================
    // ADD SELECTED COURSES
    // ============================================================

    @FXML
    private void handleAddSelectedCourses() {

        // --------------------------------------------------------
        // CHECK STUDENT
        // --------------------------------------------------------

        if (currentStudentId == null) {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Student Not Found",
                    "No student is currently logged in."
            );

            return;
        }


        // --------------------------------------------------------
        // CHECK CURRENT DEGREE
        // --------------------------------------------------------

        if (currentDegree == null ||
                currentDegree.isBlank()) {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Degree Not Found",
                    "The student's degree could not be determined."
            );

            return;
        }


        // --------------------------------------------------------
        // COLLECT SELECTED COURSES
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
        // NO COURSES SELECTED
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

        int successful = 0;

        StringBuilder errors =
                new StringBuilder();


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


        // --------------------------------------------------------
        // SUCCESS MESSAGE
        // --------------------------------------------------------

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


        // --------------------------------------------------------
        // ERRORS
        // --------------------------------------------------------

        if (!errors.isEmpty()) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "Some Courses Were Not Added",
                    errors.toString()
            );
        }


        // --------------------------------------------------------
        // CLEAR SELECTIONS
        // --------------------------------------------------------

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

                checkBox.setSelected(
                        false
                );
            }
        }
    }


    // ============================================================
    // BACK BUTTON
    // ============================================================

    @FXML
    private void handleBack(
            ActionEvent event
    ) {

        try {

            Node source =
                    (Node) event.getSource();

            Stage stage =
                    (Stage) source
                            .getScene()
                            .getWindow();


            Parent root =
                    fxWeaver.loadView(
                            EnrollmentController.class
                    );


            Scene scene =
                    new Scene(root);


            stage.setScene(
                    scene
            );


            stage.setTitle(
                    "Enrollment Planning List"
            );


            stage.show();

        } catch (Exception e) {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Navigation Error",
                    "Unable to return to the Enrollment Planning List.\n\n"
                            + e.getMessage()
            );
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

        alert.setTitle(
                title
        );

        alert.setHeaderText(
                null
        );

        alert.setContentText(
                message
        );

        alert.showAndWait();
    }
}