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

    public CurriculumController(
            CurriculumService curriculumService
    ) {
        this.curriculumService = curriculumService;
    }

    @FXML
    private void initialize() {

        // Terms
        termComboBox.getItems().addAll(
                1, 2, 3, 4, 5, 6, 7, 8
        );

        // Filters
        filterComboBox.getItems().addAll(
                "Current Term",
                "All Terms",
                "Alphabetical"
        );

        // Status options
        statusComboBox.getItems().addAll(
                "IN-PROGRESS",
                "PASSED",
                "FAILED"
        );
    }

    @FXML
    private void handleApplyFilter(ActionEvent event) {

        // We'll connect this to the logged-in student
        // and database in the next step.

        System.out.println("Apply Filter pressed.");
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