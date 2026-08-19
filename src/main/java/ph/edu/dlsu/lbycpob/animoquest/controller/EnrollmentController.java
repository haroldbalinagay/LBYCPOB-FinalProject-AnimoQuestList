package ph.edu.dlsu.lbycpob.animoquest.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;
import ph.edu.dlsu.lbycpob.animoquest.model.CurriculumProgress;
import ph.edu.dlsu.lbycpob.animoquest.service.CurriculumService;

@Component
@FxmlView("enrollment.fxml")
public class EnrollmentController {

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
     * Temporary student ID.
     *
     * Later, this should come from the User/Student
     * who successfully logged in.
     */
    private Long currentStudentId = 1L;

    public EnrollmentController(
            CurriculumService curriculumService
    ) {
        this.curriculumService = curriculumService;
    }

    @FXML
    public void initialize() {

        // Term filter
        termFilterComboBox.setItems(
                FXCollections.observableArrayList(
                        1, 2, 3, 4, 5, 6, 7, 8, 9, 10
                )
        );

        // Sorting options
        sortComboBox.setItems(
                FXCollections.observableArrayList(
                        "By Term",
                        "Alphabetical"
                )
        );

        // Status options
        statusComboBox.setItems(
                FXCollections.observableArrayList(
                        "IN-PROGRESS",
                        "PASSED",
                        "FAILED"
                )
        );

        // Display courses properly
        courseListView.setCellFactory(
                listView -> new ListCell<>() {

                    @Override
                    protected void updateItem(
                            CurriculumProgress course,
                            boolean empty
                    ) {