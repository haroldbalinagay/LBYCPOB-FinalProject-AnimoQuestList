package ph.edu.dlsu.lbycpob.animoquest.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;
import ph.edu.dlsu.lbycpob.animoquest.model.CurriculumProgress;
import ph.edu.dlsu.lbycpob.animoquest.model.MasterlistCourse;
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
    @FXML private Label checklistTitleLabel;

    @FXML private CheckBox showColorCodingCheckbox;
    @FXML private CheckBox showTargetReqsCheckbox;
    @FXML private CheckBox showTargetDependentsCheckbox;

    @FXML private GridPane checklistGrid;

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
                count++;

                // Show the instance on screen
                checklistGrid.add(subView, col, row);
            }
        }

        // Ask the curriculum service for the student's progress records
        progressList = progressService.getProgressOf(null); // TODO: HARMONY POINT
    }

    /**
     * Executes when ANY course is clicked from ANY checklist.
     * @param course The course model of the clicked on course
     */
    private void handleOnCourseClick(MasterlistCourse course) {
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
        showCourseStatus(course);
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

        // Ask each checklist to check if it contains the reqs to highlight on screen
        for (TermChecklistController controller : checklistControllers) {
            controller.highlightRequisitesOf(course, progressList);
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
            totalCount += controller.countDependentsOf(course); // Increment the counter
        }

        // Update the label
        courseDependentsLabel.setText(String.valueOf(totalCount));
    }

    // TODO: NEEDS ELIGIBILITY LOGIC + CURRICULUM PROGRESS
    private void showCourseStatus(MasterlistCourse course) {
        courseStatusLabel.setText("TBD");
        eligibleLabel.setText("TBD");
        eligibleReasonLabel.setText("TBD");
    }
}
