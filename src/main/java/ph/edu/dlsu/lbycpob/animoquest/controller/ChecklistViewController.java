package ph.edu.dlsu.lbycpob.animoquest.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;
import ph.edu.dlsu.lbycpob.animoquest.model.*;
import ph.edu.dlsu.lbycpob.animoquest.service.CurriculumService;
import ph.edu.dlsu.lbycpob.animoquest.service.FxmlLoaderService;
import ph.edu.dlsu.lbycpob.animoquest.service.TermChecklistService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    // Checklist Editor
    @FXML private TableView<MasterlistCourse> checklistCourseEditor;
    @FXML private ComboBox<String> checklistCourseEditorCombobox;
    @FXML private TableView<TermChecklist> checklistOrderEditor;

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
                Parent subView;
                try {
                    subView = fxmlLoader.load(getClass().getResource("term-checklist.fxml"));
                } catch (IOException e) {
                    checklistGrid.add(new Label("Error loading checklist"), col, row);
                    continue;
                }

                // Extract and save the instance's controller into a list
                TermChecklistController controller = fxmlLoader.getController();
                checklistControllers.add(controller);

                // Define the listener for the instance
                controller.addListener(this::handleOnCourseClick);

                // Set the term number of the instance (causes it to start searching for its courses)
                controller.setTermNumber(count);
                controller.populateCourses(progressList);
                count++;

                // Show the instance on screen
                checklistGrid.add(subView, col, row);
            }
        }

        initializeChecklistCourseEditor();
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

    // Define a custom identifier for your internal row data transfer
    private static final DataFormat SERIALIZED_MIME_TYPE = new DataFormat("application/x-java-serialized-object");

    private void initializeChecklistCourseEditor() {
        // Add combo box items
        for (int i = 1; i <= 12 ; i++) {
            checklistCourseEditorCombobox.getItems().add("Term " + i);
        }

        // Put this in your controller / initialization block:
        TableView<MasterlistCourse> tableView = checklistCourseEditor;

        tableView.setRowFactory(tv -> {
            TableRow<MasterlistCourse> row = new TableRow<>();

            // 1. START DRAG: When user starts dragging a non-empty row
            row.setOnDragDetected(event -> {
                if (!row.isEmpty()) {
                    Integer index = row.getIndex();
                    Dragboard db = row.startDragAndDrop(TransferMode.MOVE);
                    db.setDragView(row.snapshot(null, null)); // Optional: Shows ghostly row preview

                    ClipboardContent cc = new ClipboardContent();
                    cc.put(SERIALIZED_MIME_TYPE, index); // Store original index
                    db.setContent(cc);
                    event.consume();
                }
            });

            // 2. DRAG OVER: Accept the drag only if it is a valid row move
            row.setOnDragOver(event -> {
                Dragboard db = event.getDragboard();
                if (db.hasContent(SERIALIZED_MIME_TYPE)) {
                    // Find out which row index we are dragging from
                    int draggedIndex = (Integer) db.getContent(SERIALIZED_MIME_TYPE);

                    // Don't drop a row onto itself
                    if (draggedIndex != row.getIndex()) {
                        event.acceptTransferModes(TransferMode.MOVE);
                        event.consume();
                    }
                }
            });

            // 3. DROP: Rearrange the observableArrayList
            row.setOnDragDropped(event -> {
                Dragboard db = event.getDragboard();
                if (db.hasContent(SERIALIZED_MIME_TYPE)) {
                    int draggedIndex = (Integer) db.getContent(SERIALIZED_MIME_TYPE);

                    // Calculate targets (handle drop on empty space vs filled row)
                    int dropIndex = row.isEmpty() ? tableView.getItems().size() : row.getIndex();

                    // Perform atomic item move within the underlying list
                    MasterlistCourse draggedItem = tableView.getItems().remove(draggedIndex);
                    tableView.getItems().add(dropIndex, draggedItem);

                    // Re-select the dragged item so it stays highlighted
                    tableView.getSelectionModel().select(dropIndex);

                    event.setDropCompleted(true);
                    event.consume();
                }
            });

            return row;
        });
    }
}
