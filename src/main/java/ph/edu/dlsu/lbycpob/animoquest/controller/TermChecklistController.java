package ph.edu.dlsu.lbycpob.animoquest.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;
import ph.edu.dlsu.lbycpob.animoquest.model.MasterlistCourse;
import ph.edu.dlsu.lbycpob.animoquest.model.TermChecklist;
import ph.edu.dlsu.lbycpob.animoquest.service.TermChecklistService;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@FxmlView("term-checklist.fxml")
public class TermChecklistController {

    private static final int CURRENT_BATCH = 125;

    private static final String CURRENT_DEGREE =
            "BS Computer Engineering";

    @FXML
    private TabPane termTabPane;

    @FXML
    private Button addSelectedButton;

    private final TermChecklistService termChecklistService;

    /*
     * Stores the CheckBoxes for each term.
     *
     * Example:
     *
     * Term 1 -> [checkbox1, checkbox2, checkbox3]
     * Term 2 -> [checkbox4, checkbox5, checkbox6]
     */
    private final Map<Integer, List<CheckBox>> termCheckBoxes =
            new HashMap<>();

    public TermChecklistController(
            TermChecklistService termChecklistService
    ) {
        this.termChecklistService = termChecklistService;
    }

    // ============================================================
    // INITIALIZE
    // ============================================================

    @FXML
    public void initialize() {

        loadTermChecklists();
    }

    // ============================================================
    // LOAD TERMS
    // ============================================================

    private void loadTermChecklists() {

        List<TermChecklist> checklists =
                termChecklistService.getChecklists(
                        CURRENT_BATCH,
                        CURRENT_DEGREE
                );

        termTabPane.getTabs().clear();

        for (TermChecklist checklist : checklists) {

            createTermTab(checklist);
        }
    }

    // ============================================================
    // CREATE TERM TAB
    // ============================================================

    private void createTermTab(
            TermChecklist checklist
    ) {

        int termNumber =
                checklist.getTermNumber();

        VBox container =
                new VBox(10);

        container.setPadding(
                new javafx.geometry.Insets(15)
        );

        Label maxUnitsLabel =
                new Label(
                        "Maximum Units: "
                                + checklist.getMaxUnits()
                );

        container.getChildren().add(
                maxUnitsLabel
        );

        List<MasterlistCourse> courses =
                termChecklistService.getCoursesForTerm(
                        CURRENT_BATCH,
                        CURRENT_DEGREE,
                        termNumber
                );

        List<CheckBox> checkBoxes =
                new java.util.ArrayList<>();

        for (MasterlistCourse course : courses) {

            CheckBox checkBox =
                    new CheckBox();

            checkBox.setText(
                    course.getCode()
                            + " - "
                            + course.getName()
                            + " ("
                            + course.getUnits()
                            + " units)"
            );

            /*
             * Store course ID inside the CheckBox.
             */
            checkBox.setUserData(
                    course.getId()
            );

            checkBoxes.add(checkBox);

            container.getChildren().add(
                    checkBox
            );
        }

        termCheckBoxes.put(
                termNumber,
                checkBoxes
        );

        Tab tab =
                new Tab(
                        "Term " + termNumber
                );

        tab.setContent(container);

        tab.setClosable(false);

        termTabPane.getTabs().add(tab);
    }

    // ============================================================
    // ADD SELECTED COURSES
    // ============================================================

    @FXML
    private void handleAddSelectedCourses() {

        Set<Long> selectedCourseIds =
                new HashSet<>();

        for (List<CheckBox> checkBoxes :
                termCheckBoxes.values()) {

            for (CheckBox checkBox :
                    checkBoxes) {

                if (checkBox.isSelected()) {

                    Long courseId =
                            (Long) checkBox.getUserData();

                    selectedCourseIds.add(
                            courseId
                    );
                }
            }
        }

        if (selectedCourseIds.isEmpty()) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "No Courses Selected",
                    "Please select at least one course."
            );

            return;
        }

        showAlert(
                Alert.AlertType.INFORMATION,
                "Courses Selected",
                "Selected "
                        + selectedCourseIds.size()
                        + " course(s)."
        );
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
}