package ph.edu.dlsu.lbycpob.animoquest.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;
import ph.edu.dlsu.lbycpob.animoquest.model.CurriculumDisplay;
import ph.edu.dlsu.lbycpob.animoquest.service.CurriculumService;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxWeaver;
import ph.edu.dlsu.lbycpob.animoquest.model.Student;
import ph.edu.dlsu.lbycpob.animoquest.model.MasterlistCourse;

import java.util.List;
import ph.edu.dlsu.lbycpob.animoquest.controller.TermChecklistController;

import java.util.Comparator;

@Component
@FxmlView("enrollment.fxml")
public class EnrollmentController {

    @FXML
    private ComboBox<Integer> currentTermComboBox;

    @FXML
    private ComboBox<Integer> termFilterComboBox;

    @FXML
    private ComboBox<String> sortComboBox;

    @FXML
    private ListView<CurriculumDisplay> courseListView;

    @FXML
    private ComboBox<String> statusComboBox;

    @FXML
    private ListView<MasterlistCourse> recommendedCourseListView;

    @FXML
    private Label recommendationLabel;

    private final CurriculumService curriculumService;

    private final ObservableList<CurriculumDisplay> courses =
            FXCollections.observableArrayList();

    /*
     * Temporary student ID.
     *
     * Later, this should come from the Student
     * who successfully logged in.
     */
    private Long currentStudentId;
    private String currentDegree;
    private int currentTerm = 1;



    private final FxWeaver fxWeaver;

    public EnrollmentController(
            CurriculumService curriculumService,
            FxWeaver fxWeaver
    ) {
        this.curriculumService = curriculumService;
        this.fxWeaver = fxWeaver;
    }
    public void setStudentId(Long studentId) {

        this.currentStudentId = studentId;

        loadCourses();
    }
    public void setStudent(
            Student student
    ) {

        this.currentStudentId = student.getId();
        this.currentDegree = student.getMajor();

        loadCourses();
    }

    public void setDegree(String degree) {

        this.currentDegree = degree;
    }

    // ============================================================
    // INITIALIZE
    // ============================================================

    @FXML
    public void initialize() {

        // --------------------------------------------------------
        // TERM OPTIONS
        // --------------------------------------------------------

        // --------------------------------------------------------
// CURRENT TERM
// --------------------------------------------------------

        currentTermComboBox.setItems(
                FXCollections.observableArrayList(
                        1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12
                )
        );

// Default current term
        currentTermComboBox.setValue(1);

// Keep the controller's currentTerm synchronized
        currentTermComboBox.setOnAction(event -> {

            Integer selectedTerm =
                    currentTermComboBox.getValue();

            if (selectedTerm != null) {
                currentTerm = selectedTerm;
                // Make the filter follow the current term
                termFilterComboBox.setValue(currentTerm);
            }
        });

// --------------------------------------------------------
// TERM FILTER
// --------------------------------------------------------

        termFilterComboBox.setItems(
                FXCollections.observableArrayList(
                        1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12
                )
        );

// Default filter = current term
        termFilterComboBox.setValue(currentTerm);

        // --------------------------------------------------------
        // SORT OPTIONS
        // --------------------------------------------------------

        sortComboBox.setItems(
                FXCollections.observableArrayList(
                        "By Term",
                        "Alphabetical"
                )
        );

        // Default filter
        sortComboBox.setValue("By Term");

        // --------------------------------------------------------
        // STATUS OPTIONS
        // --------------------------------------------------------

        statusComboBox.setItems(
                FXCollections.observableArrayList(
                        "IN-PROGRESS",
                        "PASSED",
                        "FAILED"
                )
        );

        // --------------------------------------------------------
        // COURSE LIST DISPLAY
        // --------------------------------------------------------

        courseListView.setCellFactory(
                listView -> new ListCell<>() {

                    @Override
                    protected void updateItem(
                            CurriculumDisplay course,
                            boolean empty
                    ) {

                        super.updateItem(course, empty);

                        if (empty || course == null) {

                            setText(null);

                        } else {

                            StringBuilder text =
                                    new StringBuilder();

                            text.append(
                                    course.getCode()
                            );

                            text.append(
                                    " | "
                            );

                            text.append(
                                    course.getName()
                            );

                            text.append(
                                    " | "
                            );

                            text.append(
                                    course.getUnits()
                            );

                            text.append(
                                    " units"
                            );

                            text.append(
                                    "\nTerm: "
                            );

                            text.append(
                                    course.getTerm()
                            );

                            text.append(
                                    " | Status: "
                            );

                            text.append(
                                    course.getStatus()
                            );

                            text.append(
                                    "\nRequisites: "
                            );

                            text.append(
                                    course.getRequisites()
                            );

                            if (
                                    course.getWarningMessage() != null
                                            &&
                                            !course.getWarningMessage().isBlank()
                            ) {

                                text.append(
                                        "\n⚠ "
                                );

                                text.append(
                                        course.getWarningMessage()
                                );
                            }

                            setText(
                                    text.toString()
                            );
                        }
                    }
                }
        );

        // --------------------------------------------------------
// RECOMMENDED COURSE LIST DISPLAY
// --------------------------------------------------------

        recommendedCourseListView.setCellFactory(
                listView -> new ListCell<>() {

                    @Override
                    protected void updateItem(
                            MasterlistCourse course,
                            boolean empty
                    ) {

                        super.updateItem(course, empty);

                        if (empty || course == null) {

                            setText(null);

                        } else {

                            setText(
                                    course.getCode()
                                            + " | "
                                            + course.getName()
                                            + " | "
                                            + course.getUnits()
                                            + " units"
                            );
                        }
                    }
                }
        );

        // --------------------------------------------------------
        // LOAD COURSES
        // --------------------------------------------------------

    }

    // ============================================================
    // LOAD COURSES
    // ============================================================

    private void loadCourses() {

        courses.setAll(
                curriculumService.getStudentCourseDisplays(
                        currentStudentId
                )
        );

        applySorting();

        courseListView.setItems(courses);
    }

    // ============================================================
    // APPLY FILTER / SORT
    // ============================================================

    @FXML
    private void handleApplyFilter(
            ActionEvent event
    ) {

        Integer selectedTerm =
                termFilterComboBox.getValue();

        String selectedSort =
                sortComboBox.getValue();

        if (selectedTerm == null) {
            loadCourses();
            return;
        }

        courses.setAll(
                curriculumService.getCourseDisplaysByTerm(
                        currentStudentId,
                        selectedTerm
                )
        );

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
    // SORT
    // ============================================================


    private void applySorting() {

        String selectedSort =
                sortComboBox.getValue();

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
    }

// ============================================================
// COURSE SELECTION
// ============================================================
@FXML
private void handleCourseSelection() {

    CurriculumDisplay selectedCourse =
            courseListView
                    .getSelectionModel()
                    .getSelectedItem();

    if (selectedCourse == null) {
        statusComboBox.setValue(null);
        statusComboBox.setDisable(false);
        return;
    }

    statusComboBox.setValue(
            selectedCourse.getStatus()
    );

    /*
     * PASSED courses cannot have their status changed.
     */
    if ("PASSED".equals(
            selectedCourse.getStatus()
    )) {

        statusComboBox.setDisable(true);

    } else {

        statusComboBox.setDisable(false);
    }
}

    // ============================================================
    // UPDATE STATUS
    // ============================================================

    @FXML
    private void handleSaveStatus(
            ActionEvent event
    ) {

        CurriculumDisplay selectedCourse =
                courseListView
                        .getSelectionModel()
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

        /*
         * Passed courses are locked.
         */
        if ("PASSED".equals(
                selectedCourse.getStatus()
        )) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "Status Locked",
                    "A passed course cannot have its status changed."
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

            statusComboBox.setValue(null);

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
    private void handleRemoveCourse(
            ActionEvent event
    ) {

        CurriculumDisplay selectedCourse =
                courseListView
                        .getSelectionModel()
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

            statusComboBox.setValue(null);

        } catch (Exception e) {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Removal Failed",
                    e.getMessage()
            );
        }
    }
// ============================================================
// GENERATE RECOMMENDED COURSES
// ============================================================

    @FXML
    private void handleGenerateRecommendations(
            ActionEvent event
    ) {

        if (currentStudentId == null) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "Student Not Found",
                    "No student is currently logged in."
            );

            return;
        }

        if (currentDegree == null
                || currentDegree.isBlank()) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "Degree Not Found",
                    "The student's degree could not be determined."
            );

            return;
        }

        Integer selectedTerm =
                currentTermComboBox.getValue();

        if (selectedTerm == null) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "Current Term Required",
                    "Please select your current term first."
            );

            return;
        }

        try {

            int nextTerm =
                    selectedTerm + 1;

            List<MasterlistCourse> recommendations =
                    curriculumService.getRecommendedCourses(
                            currentStudentId,
                            currentDegree,
                            selectedTerm
                    );

            recommendedCourseListView
                    .getSelectionModel()
                    .clearSelection();

            recommendedCourseListView
                    .setItems(
                            FXCollections.observableArrayList(
                                    recommendations
                            )
                    );

            recommendationLabel.setText(
                    "Recommended Courses for Term "
                            + nextTerm
            );

            if (recommendations.isEmpty()) {

                showAlert(
                        Alert.AlertType.INFORMATION,
                        "No Recommendations",
                        "There are currently no courses "
                                + "available for recommendation "
                                + "for Term "
                                + nextTerm
                                + "."
                );

            }

        } catch (Exception e) {

            e.printStackTrace();

            showAlert(
                    Alert.AlertType.ERROR,
                    "Recommendation Error",
                    "Unable to generate course recommendations.\n\n"
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

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }

    // ============================================================
// OPEN TERM CHECKLIST
// ============================================================

    @FXML
    private void handleOpenTermChecklist(ActionEvent event) {

        try {

            if (currentStudentId == null) {

                showAlert(
                        Alert.AlertType.WARNING,
                        "Student Not Found",
                        "No student is currently logged in."
                );

                return;
            }

            if (currentDegree == null
                    || currentDegree.isBlank()) {

                showAlert(
                        Alert.AlertType.WARNING,
                        "Degree Not Found",
                        "The student's degree could not be determined."
                );

                return;
            }

            Integer selectedTerm =
                    currentTermComboBox.getValue();

            if (selectedTerm == null) {
                selectedTerm = currentTerm;
            }

            /*
             * Load the Term Checklist view.
             */
            Parent root =
                    fxWeaver.loadView(
                            TermChecklistController.class
                    );

            /*
             * Get the controller used by the loaded view.
             */
            TermChecklistController controller =
                    fxWeaver.getBean(
                            TermChecklistController.class
                    );

            /*
             * Pass the logged-in student's information.
             */
            controller.setStudentId(
                    currentStudentId
            );

            controller.setDegree(
                    currentDegree
            );

            controller.setCurrentTerm(
                    selectedTerm
            );

            /*
             * Change the current scene.
             */
            Stage stage =
                    (Stage) ((Node) event.getSource())
                            .getScene()
                            .getWindow();

            stage.setScene(
                    new Scene(root)
            );

            stage.setTitle(
                    "AnimoQuestList - Term Checklist"
            );

            stage.show();

        } catch (Exception e) {

            e.printStackTrace();

            showAlert(
                    Alert.AlertType.ERROR,
                    "Navigation Error",
                    "Unable to open the Term Checklist.\n\n"
                            + e.getMessage()
            );
        }
    }
    public void setCurrentTerm(int currentTerm) {

        if (currentTerm < 1 || currentTerm > 12) {

            throw new IllegalArgumentException(
                    "Current term must be between 1 and 12."
            );
        }

        this.currentTerm = currentTerm;

        if (currentTermComboBox != null) {
            currentTermComboBox.setValue(currentTerm);
        }

        if (termFilterComboBox != null) {
            termFilterComboBox.setValue(currentTerm);
        }
    }
}