package ph.edu.dlsu.lbycpob.animoquest.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import ph.edu.dlsu.lbycpob.animoquest.service.FxmlLoaderService;

import java.io.IOException;

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

    @FXML private CheckBox showColorCodingCheckbox;
    @FXML private CheckBox showTargetReqsCheckbox;
    @FXML private CheckBox showTargetDependentsCheckbox;

    @FXML private GridPane checklistGrid;

    private final FxmlLoaderService fxmlLoader;

    public ChecklistViewController(FxmlLoaderService fxmlLoader) {
        this.fxmlLoader = fxmlLoader;
    }

    /**
     * Creates the 12 Term Checklists in the checklist grid pane.
     */
    @FXML
    public void initialize() {
        for (int row = 0; row < 4 ; row++) {
            for (int col = 0; col < 3; col++) {
                try {
                    Parent subView = fxmlLoader.load(getClass().getResource("term-checklist.fxml"));
                    checklistGrid.add(subView, col, row);
                } catch (IOException e) {
                    checklistGrid.add(new Label("Error loading checklist"), col, row);
                }
            }
        }
    }
}
