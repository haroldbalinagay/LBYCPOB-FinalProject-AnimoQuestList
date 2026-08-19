package ph.edu.dlsu.lbycpob.animoquest.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;
import ph.edu.dlsu.lbycpob.animoquest.model.CurriculumProgress;
import ph.edu.dlsu.lbycpob.animoquest.service.CurriculumService;

@Component
@FxmlView("enrollment.fxml")
public class EnrollmentController {

    @FXML
    private ComboBox<Integer> termFilterComboBox;

    @FXML
    private ComboBox<String> sortComboBox;

    @FXML
    private ListView<CurriculumProgress> courseListView;

    @FXML
    private ComboBox<String> statusComboBox;

    private final CurriculumService curriculumService;

    private final ObservableList<CurriculumProgress> courses =
            FXCollections.observableArrayList();

    /*
     * Temporary student ID.
     *
     * Later, this should come from the User/Student
     * who successfully logged in.
     */
    private Long currentStudentId = 1L;

    public EnrollmentController(
            CurriculumService curriculumService
    ) {
        this.curriculumService = curriculumService;
    }

    @FXML
    public void initialize() {

        // Term filter
        termFilterComboBox.setItems(
                FXCollections.observableArrayList(
                        1, 2, 3, 4, 5, 6, 7, 8, 9, 10
                )
        );

        // Sorting options
        sortComboBox.setItems(
                FXCollections.observableArrayList(
                        "By Term",
                        "Alphabetical"
                )
        );

        // Status options
        statusComboBox.setItems(
                FXCollections.observableArrayList(
                        "IN-PROGRESS",
                        "PASSED",
                        "FAILED"
                )
        );

        // Display courses properly
        courseListView.setCellFactory(
                listView -> new ListCell<>() {

                    @Override
                    protected void updateItem(
                            CurriculumProgress course,
                            boolean empty
                    ) {

                        super.updateItem(course, empty);

                        if (empty || course == null) {
                            setText(null);
                        } else {

                            setText(
                                    "Course ID: "
                                            + course.getCourseId()
                                            + " | Term: "
                                            + course.getTermTaken()
                                            + " | Status: "
                                            + course.getStatus()
                            );
                        }
                    }
                }
        );

        loadCourses();
    }

    // ============================================================
    // LOAD COURSES
    // ============================================================

    private void loadCourses() {

        courses.setAll(
                curriculumService.getStudentCourses(
                        currentStudentId
                )
        );

        courseListView.setItems(courses);
    }

    // ============================================================
    // FILTER
    // ============================================================

    @FXML
    private void handleApplyFilter(ActionEvent event) {

        Integer selectedTerm =
                termFilterComboBox.getValue();

        if (selectedTerm == null) {
            loadCourses();
            return;
        }

        courses.setAll(
                curriculumService.getCoursesByTerm(
                        currentStudentId,
                        selectedTerm
                )
        );

        courseListView.setItems(courses);
    }

    // ============================================================
    // UPDATE STATUS
    // ============================================================

    @FXML
    private void handleSaveStatus(ActionEvent event) {

        CurriculumProgress selectedCourse =
                courseListView.getSelectionModel()
                        .getSelectedItem();

        String selectedStatus =
                statusComboBox.getValue();

        if (selectedCourse == null) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "No Course Selected",
                    "Please select a course first."
            );

            return;
        }

        if (selectedStatus == null) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "No Status Selected",
                    "Please select a status."
            );

            return;
        }

        try {

            curriculumService.updateStatus(
                    currentStudentId,
                    selectedCourse.getCourseId(),
                    selectedStatus
            );

            showAlert(
                    Alert.AlertType.INFORMATION,
                    "Status Updated",
                    "The course status was successfully updated."
            );

            loadCourses();

        } catch (Exception e) {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Update Failed",
                    e.getMessage()
            );
        }
    }

    // ============================================================
    // REMOVE COURSE
    // ============================================================

    @FXML
    private void handleRemoveCourse(ActionEvent event) {

        CurriculumProgress selectedCourse =
                courseListView.getSelectionModel()
                        .getSelectedItem();

        if (selectedCourse == null) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "No Course Selected",
                    "Please select a course first."
            );

            return;
        }

        try {

            curriculumService.removeCourse(
                    currentStudentId,
                    selectedCourse.getCourseId()
            );

            showAlert(
                    Alert.AlertType.INFORMATION,
                    "Course Removed",
                    "The course was removed from your enrollment list."
            );

            loadCourses();

        } catch (Exception e) {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Removal Failed",
                    e.getMessage()
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

        Alert alert = new Alert(type);

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }
}