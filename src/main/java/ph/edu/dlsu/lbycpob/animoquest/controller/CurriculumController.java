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

@Component
@FxmlView("curriculum.fxml")
public class CurriculumController {

    @FXML
    private ComboBox<Integer> termComboBox;

    @FXML
    private ComboBox<String> filterComboBox;

    @FXML
    private TableView<CurriculumProgress> courseTable;

    @FXML
    private TableColumn<CurriculumProgress, String> codeColumn;

    @FXML
    private TableColumn<CurriculumProgress, String> nameColumn;

    @FXML
    private TableColumn<CurriculumProgress, Integer> unitsColumn;

    @FXML
    private TableColumn<CurriculumProgress, String> statusColumn;

    @FXML
    private TableColumn<CurriculumProgress, Integer> termColumn;

    @FXML
    private TableColumn<CurriculumProgress, String> requisiteColumn;

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
                        String.valueOf(
                                cellData.getValue().getCourseId()
                        )
                )
        );

        statusColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getStatus()
                )
        );

        termColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(
                        cellData.getValue().getTermTaken()
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
                curriculumService.getStudentCourses(studentId);

        courseTable.getItems().setAll(courses);
    }

    @FXML
    private void handleApplyFilter(ActionEvent event) {

        if (studentId == null) {
            return;
        }

        if ("Current Term".equals(filterComboBox.getValue())) {

            Integer term = termComboBox.getValue();

            if (term != null) {

                courseTable.getItems().setAll(
                        curriculumService
                                .getStudentCoursesByTerm(
                                        studentId,
                                        term
                                )
                );
            }

        } else if ("All Terms".equals(filterComboBox.getValue())) {

            loadStudentCourses();

        } else if ("Alphabetical".equals(filterComboBox.getValue())) {

            // We'll implement alphabetical sorting later.
            loadStudentCourses();
        }
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