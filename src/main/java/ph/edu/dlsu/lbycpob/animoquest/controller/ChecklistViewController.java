package ph.edu.dlsu.lbycpob.animoquest.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;
import ph.edu.dlsu.lbycpob.animoquest.model.*;
import ph.edu.dlsu.lbycpob.animoquest.service.CurriculumService;
import ph.edu.dlsu.lbycpob.animoquest.service.FxmlLoaderService;
import ph.edu.dlsu.lbycpob.animoquest.service.TermChecklistService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@FxmlView("checklist-view.fxml")
public class ChecklistViewController {
    @FXML private Label courseNameLabel;
    @FXML private Label courseCodeLabel;
    @FXML private Label courseUnitsLabel;
    @FXML private Label courseRequisitesLabel;
    @FXML private Label courseDependentsLabel;
    @FXML private Label courseStatusLabel;
    @FXML private Label eligibleLabel;
    @FXML private Label eligibleReasonLabel;
    @FXML private Label checklistTitleLabel; // TODO: HARMONY POINT (LOGIN)

    @FXML private CheckBox showColorCodingCheckbox;
    @FXML private CheckBox showTargetReqsCheckbox;
    @FXML private CheckBox showTargetDependentsCheckbox;

    @FXML private GridPane checklistGrid;
    private boolean checklistGridClickLock = false; // A set-reset latch

    private List<TermChecklistController> checklistControllers = new ArrayList<>();

    private List<CurriculumProgress> progressList = new ArrayList<>();

    private final FxmlLoaderService fxmlLoader;
    private final TermChecklistService checklistService;
    private final CurriculumService progressService; // TODO: May be missing when merging

    public ChecklistViewController(FxmlLoaderService fxmlLoader, TermChecklistService checklistService, CurriculumService progressService) {
        this.fxmlLoader = fxmlLoader;
        this.checklistService = checklistService;
        this.progressService = progressService;
    }

    /**
     * Creates the 12 Term Checklists in the checklist grid pane. Loads in the student's curriculum progress records.
     */
    @FXML
    public void initialize() {
        // Reset variables (for when switching between app subviews)
        checklistControllers = new ArrayList<>();
        int count = 1;

        // Ask the curriculum service for the student's progress records
        progressList = progressService.getProgressOf(null); // TODO: HARMONY POINT

        // Loop through each cell of the checklist grid and create a new checklist instance
        for (int row = 0; row < 4 ; row++) {
            for (int col = 0; col < 3; col++) {
                initializeChecklist(col, row, count);
                count++;
            }
        }

        initializeChecklistEditor();
    }

    /**
     * Creates a term checklist in the checklist grid pane. This method is used when initializing checklists for the first time.
     * @param col Grid pane column
     * @param row Grid pane row
     * @param termNumber Term number of checklist
     */
    private void initializeChecklist(int col, int row, int termNumber) {
        // Load a new checklist instance
        Parent subView;
        try {
            subView = fxmlLoader.load(getClass().getResource("term-checklist.fxml"));
        } catch (IOException e) {
            checklistGrid.add(new Label("Error loading checklist"), col, row);
            return;
        }

        // Extract and save the instance's controller into a list
        TermChecklistController controller = fxmlLoader.getController();
        checklistControllers.add(controller);

        // Define the listener for the instance
        controller.addListener(this::handleOnCourseClick);

        // Set the term number of the instance (causes it to start searching for its courses)
        controller.setTermNumber(termNumber);
        controller.populateCourses(progressList);

        // Show the instance on screen
        checklistGrid.add(subView, col, row);
    }

    /**
     * Creates a term checklist in the checklist grid pane.
     * This method is used when initializing a checklist that was previously created (serves as a hard refresh).
     * @param termNumber Term number of checklist
     * @param insertAtIdx Insertion order index of the checklist (based on previous initialization)
     */
    private void initializeChecklist(int termNumber, int insertAtIdx) {
        // Calculate the col & row
        int col = (termNumber - 1) % 3;
        int row = (termNumber - 1) / 3;

        // Load a new checklist instance
        Parent subView;
        try {
            subView = fxmlLoader.load(getClass().getResource("term-checklist.fxml"));
        } catch (IOException e) {
            checklistGrid.add(new Label("Error loading checklist"), col, row);
            return;
        }

        // Extract and save the instance's controller into a list (by overriding)
        TermChecklistController controller = fxmlLoader.getController();
        checklistControllers.set(insertAtIdx, controller);

        // Define the listener for the instance
        controller.addListener(this::handleOnCourseClick);

        // Set the term number of the instance (causes it to start searching for its courses)
        controller.setTermNumber(termNumber);
        controller.populateCourses(progressList);

        // Show the instance on screen
        checklistGrid.add(subView, col, row);
    }

    /**
     * Executes when ANY course is clicked from ANY checklist.
     * @param course The course model of the clicked on course
     */
    private void handleOnCourseClick(MasterlistCourse course) {
        checklistGridClickLock = true; // Prevents the same click from triggering the grid pane on-mouse click
        showCourseDetails(course);
    }

    /**
     * Displays the course details of the clicked on course.
     * @param course The clicked on course
     */
    private void showCourseDetails(MasterlistCourse course) {
        courseNameLabel.setText(course.getName());
        courseCodeLabel.setText(course.getCode());
        courseUnitsLabel.setText(String.valueOf(course.getUnits()));

        // Reset all course highlights across all checklists
        for (TermChecklistController controller : checklistControllers) {
            controller.resetHighlights();
        }

        showCourseRequisites(course);
        showTotalCourseDependents(course);
        showCourseStatusAndEligibility(course);
    }

    /**
     * Displays the requisites of the clicked on course
     * @param course The clicked on course
     */
    private void showCourseRequisites(MasterlistCourse course) {
        StringBuilder sb = new StringBuilder();

        // Simply show "N/A" if course has no requisites
        if (course.hasNoRequisites()) {
            sb.append("N/A");
            courseRequisitesLabel.setText(sb.toString());
            return;
        }

        // Ask for the course codes of the requisites
        List<String> reqs = checklistService.getCompleteRequisitesOf(course);

        // Format non-null requisite codes
        int i = 1;
        for (String req : reqs) {
            if (req == null) continue;
            if (i > 1) sb.append(", ");

            sb.append(req).append(" (").append(course.getRequisiteTypeAt(i)).append(")");
            i++;
        }

        // Update the label
        courseRequisitesLabel.setText(sb.toString());

        // Ask each checklist to check if it contains the reqs to highlight on screen (ONLY IF checkbox is selected)
        if (showTargetReqsCheckbox.isSelected()) {
            for (TermChecklistController controller : checklistControllers) {
                controller.highlightRequisitesOf(course, progressList);
            }
        }
    }

    /**
     * Displays the total number of courses across all checklists that have the given course as 1 of its requisites.
     * @param course The clicked on course
     */
    private void showTotalCourseDependents(MasterlistCourse course) {
        int totalCount = 0;

        // Ask each checklist to check if it has any courses that depend on the given course
        for (TermChecklistController controller : checklistControllers) {
            // Increment the counter
            totalCount += controller.countDependentsOf(course, showTargetDependentsCheckbox.isSelected());
        }

        // Update the label
        courseDependentsLabel.setText(String.valueOf(totalCount));
    }

    /**
     * Displays the eligibility of a course to be enrolled in, and an accompanying reason.
     * @param course The clicked on course
     */
    private void showCourseStatusAndEligibility(MasterlistCourse course) {
        CourseStatus status = checklistService.getStatusOf(course, progressList);
        courseStatusLabel.setText(status.getStatus());

        // Ask the service to check if course is eligible
        TermChecklistService.EnrollEligibility eligibleData = checklistService.checkEnrollEligibilityOf(course, progressList);

        if (eligibleData.eligible()) eligibleLabel.setText("YES");
        else eligibleLabel.setText("NO");

        eligibleReasonLabel.setText(eligibleData.reason());

        // Ask each checklist to highlight the clicked on course (if it contains said course)
        for (TermChecklistController controller : checklistControllers) {
            if (eligibleData.eligible()) {
                controller.highlightSourceCourse(course, CourseBoxState.ELIGIBLE);
            }
            else controller.highlightSourceCourse(course, CourseBoxState.INELIGIBLE);
        }
    }

    /**
     * Trigger when any part of the checklist grid pane is clicked on.
     * The execution of this handler depends on a set-reset latch that is set when a course box is clicked,
     * as well as the "show status color coding" visual control.
     * @param event
     */
    @FXML
    public void handleChecklistGridClick(MouseEvent event) {
        // Acts like a set-reset latch that prevents the handler from doing anything,
        // except for when the user did not click on a course box.
        if (checklistGridClickLock) {
            checklistGridClickLock = false;
            return;
        }

        // Instruct each checklist to restore its course highlights
        for (TermChecklistController controller : checklistControllers) {
            if (showColorCodingCheckbox.isSelected()) {
                controller.restoreHighlights();
            } else {
                controller.resetHighlights();
            }
        }
    }

    // EDITORS

    @FXML private ComboBox<String> checklistEditorComboBox;
    @FXML private VBox comboBoxGroup;
    @FXML private Button saveBtn;

    List<ComboBox<MasterlistCourse>> comboBoxList = new ArrayList<>();

    /**
     * Sets up the checklist editor UI.
     */
    private void initializeChecklistEditor() {
        // Add combo box items
        for (int i = 1; i <= 12 ; i++) {
            checklistEditorComboBox.getItems().add("Term " + i);
        }

        // Trigger refresh code when the value changes
        checklistEditorComboBox.valueProperty().addListener((obs, oldValue, newValue) -> {
            // Get only the term number
            int termNumber = Integer.parseInt(newValue.substring(5));
            handleTermChange(termNumber);
        });

        // Create the observable list (options)
        ObservableList<MasterlistCourse> options = FXCollections.observableArrayList(checklistService.getAllCourses());

        // Create 15 combo boxes (1 for each course slot in a checklist)
        for (int i = 0; i < 15; i++) {
            ComboBox<MasterlistCourse> courseComboBox = new ComboBox<>();

            // Add options
            courseComboBox.getItems().add(null); // Blank option
            courseComboBox.getItems().addAll(options);

            // Define how the MasterlistCourse object converts to text and vice versa
            courseComboBox.setConverter(new StringConverter<>() {
                @Override
                public String toString(MasterlistCourse course) {
                    return course == null ? "" : course.getCode(); // Display name in UI
                }

                @Override
                public MasterlistCourse fromString(String string) {
                    return courseComboBox.getItems().stream()
                            .filter(course -> course.getCode().equals(string))
                            .findFirst()
                            .orElse(null);
                }
            });

            // Add into the vbox group
            courseComboBox.setMaxWidth(Double.MAX_VALUE);
            comboBoxList.add(courseComboBox);
            comboBoxGroup.getChildren().add(courseComboBox);
        }
    }

    /**
     * Refreshes the checklist editor to display the courses of the given checklist.
     * @param termNumber The term checklist to display
     */
    private void handleTermChange(int termNumber) {
        // Get the complete list of courses from a checklist
        List<MasterlistCourse> courses = checklistControllers.get(termNumber - 1).getCourses();
        IO.println();

        // Loop through all combo boxes to clear and repopulate safely
        for (int i = 0; i < comboBoxList.size(); i++) {
            ComboBox<MasterlistCourse> comboBox = comboBoxList.get(i);

            // Pause the event handler to stop cascading null loops
            EventHandler<ActionEvent> currentHandler = comboBox.getOnAction();
            comboBox.setOnAction(null);

            try {
                // Clear the combo box
                comboBox.setValue(null);

                // If a course exists, set it in the slot
                if (i < courses.size()) {
                    MasterlistCourse targetCourse = courses.get(i);

                    // Find the exact object reference inside the items list to prevent null-resets
                    MasterlistCourse matchedCourse = comboBox.getItems().stream()
                            .filter(Objects::nonNull) // Filter out null entries in the list
                            .filter(item -> item.equals(targetCourse))
                            .findFirst()
                            .orElse(null); // Fall back to null if object is missing from items list

                    comboBox.setValue(matchedCourse);

                    if (comboBox.getValue() != null) {
                        IO.println("Set combo box value " + i + " " + comboBox.getValue().getCode());
                    } else {
                        IO.println("Warning: Course " + targetCourse.getCode() + " not found in ComboBox items list!");
                    }
                }
            } finally {
                // Always restore the original action listener
                comboBox.setOnAction(currentHandler);
            }
        }
    }

    /**
     * Handles the steps that must be done to properly save the checklist data and display the new checklist on screen.
     * @param event
     */
    public void handleSaveChecklist(ActionEvent event) {
        // Get only the term number
        int termNumber = Integer.parseInt(checklistEditorComboBox.getValue().substring(5));

        // Get the complete list of courses from a checklist
        TermChecklistController checklist = checklistControllers.get(termNumber - 1);

        List<MasterlistCourse> courses = new ArrayList<>();

        for (ComboBox<MasterlistCourse> comboBox : comboBoxList) {
            courses.add(comboBox.getValue());
        }

        checklistService.saveChecklistData("CPE", 125, termNumber, courses); // TODO: HARMONY POINT

        checklistGrid.getChildren().remove(checklist.getChecklistBox());
        initializeChecklist(termNumber, termNumber - 1);

        // Restore / reset course highlights
        for (TermChecklistController controller : checklistControllers) {
            if (showColorCodingCheckbox.isSelected()) {
                controller.restoreHighlights();
            } else {
                controller.resetHighlights();
            }
        }
    }
}
