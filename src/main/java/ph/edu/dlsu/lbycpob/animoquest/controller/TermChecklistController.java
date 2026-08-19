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

