package ph.edu.dlsu.lbycpob.animoquest.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;
import ph.edu.dlsu.lbycpob.animoquest.model.MasterlistCourse;
import ph.edu.dlsu.lbycpob.animoquest.service.FxmlLoaderService;

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

    private final FxmlLoaderService fxmlLoader;

    public ChecklistViewController(FxmlLoaderService fxmlLoader) {
        this.fxmlLoader = fxmlLoader;
    }

    /**
     * Creates the 12 Term Checklists in the checklist grid pane.
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
    }

    /**
     * Executes when ANY course is clicked from ANY checklist.
     * @param course The course model of the clicked on course
     */
    private void handleOnCourseClick(MasterlistCourse course) {
        showCourseDetails(course);
    }

    private void showCourseDetails(MasterlistCourse course) {
        courseNameLabel.setText(course.getName());
        courseCodeLabel.setText(course.getCode());
        courseUnitsLabel.setText(String.valueOf(course.getUnits()));
        courseRequisitesLabel.setText("TBD");
        courseDependentsLabel.setText("TBD");
        courseStatusLabel.setText("TBD");
        eligibleLabel.setText("TBD");
        eligibleReasonLabel.setText("TBD");
    }
}
