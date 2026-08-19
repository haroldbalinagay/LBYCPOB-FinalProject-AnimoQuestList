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
import ph.edu.dlsu.lbycpob.animoquest.model.CurriculumProgress;
import ph.edu.dlsu.lbycpob.animoquest.service.CurriculumService;

@Component
@FxmlView("enrollment.fxml")
public class CurriculumController {

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
    // INITIALIZE
    // ============================================================

    @FXML
    public void initialize() {

        // --------------------------------------------------------
        // Term filter
        // --------------------------------------------------------

        termFilterComboBox.setItems(
                FXCollections.observableArrayList(
                        1,
                        2,
                        3,
                        4,
                        5,
                        6,
                        7,
                        8,
                        9,
                        10
                )
        );

        // --------------------------------------------------------
        // Sorting options
        // --------------------------------------------------------

        sortComboBox.setItems(
                FXCollections.observableArrayList(
                        "By Term",
                        "Alphabetical"
                )
        );


        // --------------------------------------------------------
        // Course status options
        // --------------------------------------------------------

        statusComboBox.setItems(
                FXCollections.observableArrayList(
                        "IN-PROGRESS",
                        "PASSED",
                        "FAILED"
                )
        );


        // --------------------------------------------------------
        // How each course appears in the ListView
        // --------------------------------------------------------

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


        // --------------------------------------------------------
        // Load student's courses
        // --------------------------------------------------------

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
