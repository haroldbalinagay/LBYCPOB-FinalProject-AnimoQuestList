package ph.edu.dlsu.lbycpob.animoquest.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;
import ph.edu.dlsu.lbycpob.animoquest.model.CurriculumProgress;
import ph.edu.dlsu.lbycpob.animoquest.service.CurriculumService;

@Component
@FxmlView("enrollment.fxml")
public class CurriculumController {

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
     * TEMPORARY:
     * This will eventually be replaced with the ID
     * of the student who actually logged in.
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
    // INITIALIZE
    // ============================================================

    @FXML
    public void initialize() {

        // --------------------------------------------------------
        // Term filter
        // --------------------------------------------------------

        termFilterComboBox.setItems(
                FXCollections.observableArrayList(
                        1,
                        2,
                        3,
                        4,
                        5,
                        6,
                        7,
                        8,
                        9,
                        10
                )
        );

        // --------------------------------------------------------
        // Sorting options
        // --------------------------------------------------------

        sortComboBox.setItems(
                FXCollections.observableArrayList(
                        "By Term",
                        "Alphabetical"
                )
        );


        // --------------------------------------------------------
        // Course status options
        // --------------------------------------------------------

        statusComboBox.setItems(
                FXCollections.observableArrayList(
                        "IN-PROGRESS",
                        "PASSED",
                        "FAILED"
                )
        );


        // --------------------------------------------------------
        // How each course appears in the ListView
        // --------------------------------------------------------

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


        // --------------------------------------------------------
        // Load student's courses
        // --------------------------------------------------------

        loadCourses();
    }


    // ============================================================
    // LOAD COURSES
    // ============================================================

    private void loadCourses() {

        try {

            courses.setAll(
                    curriculumService.getStudentCourses(
                            currentStudentId
                    )
            );

            courseListView.setItems(courses);

        } catch (Exception e) {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Error",
                    "Unable to load your enrollment list."
            );

            e.printStackTrace();
        }
    }
// ============================================================
    // APPLY TERM FILTER
    // ============================================================

    @FXML
    private void handleApplyFilter(ActionEvent event) {

        Integer selectedTerm =
                termFilterComboBox.getValue();


        // If no term is selected,
        // show all courses.

        if (selectedTerm == null) {

            loadCourses();

            return;
        }


        try {

            courses.setAll(
                    curriculumService.getCoursesByTerm(
                            currentStudentId,
                            selectedTerm
                    )
            );

            courseListView.setItems(courses);

        } catch (Exception e) {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Filter Error",
                    "Unable to apply the selected filter."
            );

            e.printStackTrace();
        }
    }


    // ============================================================
    // SAVE COURSE STATUS
    // ============================================================

    @FXML
    private void handleSaveStatus(ActionEvent event) {

        CurriculumProgress selectedCourse =
                courseListView
                        .getSelectionModel()
                        .getSelectedItem();


        // --------------------------------------------------------
        // Check if a course was selected
        // --------------------------------------------------------

        if (selectedCourse == null) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "No Course Selected",
                    "Please select a course first."
            );

            return;
        }


        // --------------------------------------------------------
        // Get selected status
        // --------------------------------------------------------

        String selectedStatus =
                statusComboBox.getValue();


        if (selectedStatus == null) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "No Status Selected",
                    "Please select a status."
            );

            return;
        }


        // --------------------------------------------------------
        // Update status
        // --------------------------------------------------------

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


            // Refresh list
            loadCourses();


            // Clear selected status
            statusComboBox.getSelectionModel().clearSelection();

        } catch (Exception e) {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Update Failed",
                    e.getMessage()
            );

            e.printStackTrace();
        }
    }


    // ============================================================
    // REMOVE COURSE
    // ============================================================

    @FXML
    private void handleRemoveCourse(ActionEvent event) {

        CurriculumProgress selectedCourse =
                courseListView
                        .getSelectionModel()
                        .getSelectedItem();


        // --------------------------------------------------------
        // Check if a course was selected
        // --------------------------------------------------------

        if (selectedCourse == null) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "No Course Selected",
                    "Please select a course first."
            );

            return;
        }


        // --------------------------------------------------------
        // Confirm removal
        // --------------------------------------------------------

        Alert confirmation =
                new Alert(Alert.AlertType.CONFIRMATION);

        confirmation.setTitle("Remove Course");
        confirmation.setHeaderText(null);

        confirmation.setContentText(
                "Are you sure you want to remove this course?"
        );


        if (confirmation.showAndWait()
                .orElse(null)
                != javafx.scene.control.ButtonType.OK) {

            return;
        }


        // --------------------------------------------------------
        // Remove course
        // --------------------------------------------------------

        try {

            curriculumService.removeCourse(
                    currentStudentId,
                    selectedCourse.getCourseId()
            );


            showAlert(
                    Alert.AlertType.INFORMATION,
                    "Course Removed",
                    "The course was successfully removed."
            );


            // Refresh list
            loadCourses();

        } catch (Exception e) {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Removal Failed",
                    e.getMessage()
            );

            e.printStackTrace();
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