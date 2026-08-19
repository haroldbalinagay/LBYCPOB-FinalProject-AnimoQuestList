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

import java.util.Comparator;

@Component
@FxmlView("enrollment.fxml")
public class EnrollmentController {

    @FXML
    private ComboBox<Integer> termFilterComboBox;

    @FXML
    private ComboBox<String> sortComboBox;

    @FXML
    private ListView<CurriculumDisplay> courseListView;

    @FXML
    private ComboBox<String> statusComboBox;

    private final CurriculumService curriculumService;

    private final ObservableList<CurriculumDisplay> courses =
            FXCollections.observableArrayList();

    /*
     * Temporary student ID.
     *
     * Later, this should come from the Student
     * who successfully logged in.
     */
    private Long currentStudentId = 1L;

    public EnrollmentController(
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
        // TERM OPTIONS
        // --------------------------------------------------------

        termFilterComboBox.setItems(
                FXCollections.observableArrayList(
                        1, 2, 3, 4, 5, 6, 7, 8, 9, 10
                )
        );

        // Default current term
        termFilterComboBox.setValue(1);

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
                                    course.getRequisiteInfo()
                            );

                            if (
                                    course
                                            .getMissingPrerequisiteWarning()
                                            != null
                                            &&
                                            !course
                                                    .getMissingPrerequisiteWarning()
                                                    .isBlank()
                            ) {

                                text.append(
                                        "\n⚠ "
                                );

                                text.append(
                                        course
                                                .getMissingPrerequisiteWarning()
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
        // LOAD COURSES
        // --------------------------------------------------------

        loadCourses();
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
