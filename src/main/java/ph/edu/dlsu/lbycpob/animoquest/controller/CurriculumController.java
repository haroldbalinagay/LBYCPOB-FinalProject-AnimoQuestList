package ph.edu.dlsu.lbycpob.animoquest.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;
import ph.edu.dlsu.lbycpob.animoquest.model.CurriculumDisplay;
import ph.edu.dlsu.lbycpob.animoquest.service.CurriculumService;

import java.util.Comparator;

@Component
@FxmlView("enrollment.fxml")
public class CurriculumController {

    @FXML
    private ComboBox<Integer> currentTermComboBox;

    @FXML
    private ComboBox<String> termFilterComboBox;

    @FXML
    private ComboBox<String> sortComboBox;

    @FXML
    private ListView<CurriculumDisplay> courseListView;

    @FXML
    private ComboBox<String> statusComboBox;

    private final CurriculumService curriculumService;

    private final ObservableList<CurriculumDisplay> courses =
            FXCollections.observableArrayList();

    /*
     * TEMPORARY:
     * This will eventually be replaced by the ID
     * of the student who successfully logged in.
     */
    private Long currentStudentId = 1L;


    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    public CurriculumController(
            CurriculumService curriculumService
    ) {
        this.curriculumService = curriculumService;
    }


    // ============================================================
    // SET CURRENT STUDENT
    // ============================================================

    public void setStudentId(Long studentId) {

        this.currentStudentId = studentId;

        loadCourses();
    }


    // ============================================================
    // INITIALIZE
    // ============================================================

    @FXML
    public void initialize() {

        // --------------------------------------------------------
        // CURRENT TERM
        // --------------------------------------------------------

        currentTermComboBox.setItems(
                FXCollections.observableArrayList(
                        1, 2, 3, 4, 5, 6,
                        7, 8, 9, 10, 11, 12
                )
        );

        // Default current term
        currentTermComboBox.setValue(1);


        // --------------------------------------------------------
        // TERM FILTER
        // --------------------------------------------------------

        termFilterComboBox.setItems(
                FXCollections.observableArrayList(
                        "All",
                        "Term 1",
                        "Term 2",
                        "Term 3",
                        "Term 4",
                        "Term 5",
                        "Term 6",
                        "Term 7",
                        "Term 8",
                        "Term 9",
                        "Term 10",
                        "Term 11",
                        "Term 12"
                )
        );

        // Show all courses by default
        termFilterComboBox.setValue("All");


        // --------------------------------------------------------
        // SORTING OPTIONS
        // --------------------------------------------------------

        sortComboBox.setItems(
                FXCollections.observableArrayList(
                        "By Term",
                        "Alphabetical"
                )
        );

        // Default sorting
        sortComboBox.setValue("By Term");


        // --------------------------------------------------------
        // COURSE STATUS OPTIONS
        // --------------------------------------------------------

        statusComboBox.setItems(
                FXCollections.observableArrayList(
                        "IN-PROGRESS",
                        "PASSED",
                        "FAILED"
                )
        );

        // No status selected initially
        statusComboBox.getSelectionModel().clearSelection();


        // --------------------------------------------------------
        // COURSE LIST DISPLAY
        // --------------------------------------------------------