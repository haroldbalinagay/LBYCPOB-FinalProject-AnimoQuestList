package ph.edu.dlsu.lbycpob.animoquest.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;
import ph.edu.dlsu.lbycpob.animoquest.model.CurriculumProgress;
import ph.edu.dlsu.lbycpob.animoquest.service.CurriculumService;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import ph.edu.dlsu.lbycpob.animoquest.model.CurriculumDisplay;


@Component
@FxmlView("curriculum.fxml")
public class CurriculumController {

    @FXML
    private ComboBox<Integer> termComboBox;

    @FXML
    private ComboBox<String> filterComboBox;

    @FXML
    private TableView<CurriculumDisplay> courseTable;

    @FXML
    private TableColumn<CurriculumDisplay, String> codeColumn;

    @FXML
    private TableColumn<CurriculumDisplay, String> nameColumn;

    @FXML
    private TableColumn<CurriculumDisplay, Integer> unitsColumn;

    @FXML
    private TableColumn<CurriculumDisplay, String> statusColumn;

    @FXML
    private TableColumn<CurriculumDisplay, Integer> termColumn;

    @FXML
    private TableColumn<CurriculumDisplay, String> requisiteColumn;

    @FXML
    private ComboBox<String> statusComboBox;

    private final CurriculumService curriculumService;

    // ID of the currently logged-in student
    private Long studentId;

    public CurriculumController(
            CurriculumService curriculumService
    ) {
        this.curriculumService = curriculumService;
    }

    @FXML
    private void initialize() {

        termComboBox.getItems().addAll(
                1, 2, 3, 4, 5, 6, 7, 8
        );

        filterComboBox.getItems().addAll(
                "Current Term",
                "All Terms",
                "Alphabetical"
        );

        statusComboBox.getItems().addAll(
                "IN-PROGRESS",
                "PASSED",
                "FAILED"
        );

        // ============================================================
        // TABLE COLUMNS
        // ============================================================

        codeColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getCode()
                )
        );

        nameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getName()
                )
        );

        unitsColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(
                        cellData.getValue().getUnits()
                ).asObject()
        );

        statusColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getStatus()
                )
        );

        termColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(
                        cellData.getValue().getTerm()
                ).asObject()
        );
    }

    /**
     * Receives the ID of the student who logged in.
     */
    public void setStudentId(Long studentId) {

        this.studentId = studentId;

        loadStudentCourses();
    }

    /**
     * Loads this student's courses from the database.
     */
    private void loadStudentCourses() {

        if (studentId == null) {
            return;
        }

        var courses =
                curriculumService.getStudentCourseDisplay(studentId);

        courseTable.getItems().setAll(courses);
    }


    @FXML
    private void handleApplyFilter(ActionEvent event) {

        if (studentId == null) {
            return;
        }

        loadStudentCourses();
    }

    @FXML
    private void handleSaveStatus(ActionEvent event) {

        System.out.println("Save Status pressed.");
    }

    @FXML
    private void handleRemoveCourse(ActionEvent event) {

        System.out.println("Remove Course pressed.");
    }
}