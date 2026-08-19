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
import ph.edu.dlsu.lbycpob.animoquest.model.CurriculumDisplay;
import ph.edu.dlsu.lbycpob.animoquest.model.CurriculumProgress;
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

        // ============================================================
        // CURRENT TERM
        // ============================================================

        currentTermComboBox.setItems(
                FXCollections.observableArrayList(
                        1, 2, 3, 4, 5, 6,
                        7, 8, 9, 10, 11, 12
                )
        );

        // Default current term
        currentTermComboBox.setValue(1);


        // ============================================================
        // TERM FILTER
        // ============================================================

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


        // ============================================================
        // SORTING OPTIONS
        // ============================================================

        sortComboBox.setItems(
                FXCollections.observableArrayList(
                        "By Term",
                        "Alphabetical"
                )
        );

        // Default sorting
        sortComboBox.setValue("By Term");


        // ============================================================
        // COURSE STATUS OPTIONS
        // ============================================================

        statusComboBox.setItems(
                FXCollections.observableArrayList(
                        "IN-PROGRESS",
                        "PASSED",
                        "FAILED"
                )
        );

        // No status selected initially
        statusComboBox.getSelectionModel().clearSelection();


        // ============================================================
        // COURSE LIST DISPLAY
        // ============================================================

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


        // ============================================================
        // LOAD COURSES
        // ============================================================

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

        String selectedTerm =
                termFilterComboBox.getValue();

        String selectedSort =
                sortComboBox.getValue();

        // --------------------------------------------------------
        // LOAD COURSES
        // --------------------------------------------------------

        if ("All".equals(selectedTerm)) {

            courses.setAll(
                    curriculumService.getStudentCourseDisplays(
                            currentStudentId
                    )
            );

        } else {

            int term = Integer.parseInt(
                    selectedTerm.replace(
                            "Term ",
                            ""
                    )
            );

            courses.setAll(
                    curriculumService.getCourseDisplaysByTerm(
                            currentStudentId,
                            term
                    )
            );
        }

        // --------------------------------------------------------
        // SORT
        // --------------------------------------------------------

        if ("Alphabetical".equals(selectedSort)) {

            courses.sort(
                    Comparator.comparing(
                            CurriculumDisplay::getCode,
                            String.CASE_INSENSITIVE_ORDER
                    )
            );

        } else {

            courses.sort(
                    Comparator.comparingInt(
                            CurriculumDisplay::getTerm
                    )
            );
        }

        courseListView.setItems(courses);
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