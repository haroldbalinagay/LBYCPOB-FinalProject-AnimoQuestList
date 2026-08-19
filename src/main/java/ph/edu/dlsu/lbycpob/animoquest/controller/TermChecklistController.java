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

    private static final String CURRENT_DEGREE =
            "BS Computer Engineering";

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