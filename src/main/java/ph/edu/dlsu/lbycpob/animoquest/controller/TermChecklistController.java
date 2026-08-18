package ph.edu.dlsu.lbycpob.animoquest.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.edu.dlsu.lbycpob.animoquest.model.MasterlistCourse;
import ph.edu.dlsu.lbycpob.animoquest.model.TermChecklist;
import ph.edu.dlsu.lbycpob.animoquest.service.FxmlLoaderService;
import ph.edu.dlsu.lbycpob.animoquest.service.TermChecklistService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

@Component
@Scope("prototype") // Ensures that Spring creates new controller instance for each fxml instance
@FxmlView("term-checklist.fxml")
public class TermChecklistController {
    @FXML private Label termNumberLabel;
    @FXML private CheckBox enrollInAllCheckbox;
    @FXML private Label maxUnitsLabel;
    @FXML private VBox coursesView;

    private int TERM_NUMBER;
    private List<CourseBoxController> courseControllers = new ArrayList<>();

    private TermChecklist checklist;
    private List<MasterlistCourse> courses = new ArrayList<>();

    private List<CourseBoxController> highlightedCourses = new ArrayList<>();

    private Consumer<MasterlistCourse> onCourseClickListener;

    private final FxmlLoaderService fxmlLoader;
    private final TermChecklistService checklistService;

    public TermChecklistController(FxmlLoaderService fxmlLoader, TermChecklistService checklistService) {
        this.fxmlLoader = fxmlLoader;
        this.checklistService = checklistService;
    }

    /**
     * Sets the term number of the checklist which is used to find the courses assigned to the checklist.
     * @param number The term number
     */
    public void setTermNumber(int number) {
        if (number <= 0) return;
        TERM_NUMBER = number;
        termNumberLabel.setText("TERM " + number);
        populateCourses();
    }

    /**
     * Populates the courses of the checklist.
     */
    private void populateCourses() {
        checklist = checklistService.getChecklistOf("CPE", 125, TERM_NUMBER); // TODO: TEMP

        // Do not attempt to generate course boxes if the checklist is not found
        if (checklist == null) {
            coursesView.getChildren().add(new Label("Checklist not found"));
            return;
        }

        courses = checklistService.getCourseDetailsOf(checklist);

        // Set up each course listed in the term checklist
        int orderIdx = 0;
        for (MasterlistCourse course : courses) {
            Parent courseBox;
            // Load an instance of the course box
            try {
                courseBox = fxmlLoader.load(getClass().getResource("course-box.fxml"));
            } catch (IOException e) {
                coursesView.getChildren().add(new Label("Error loading course"));
                continue;
            }

            // Extract and save the instance's controller into a list
            CourseBoxController controller = fxmlLoader.getController();
            courseControllers.add(controller);

            // Provide the instance with the course code and units
            controller.setCourseCode(course.getCode());
            controller.setCourseUnits(course.getUnits());
            controller.setOrderInChecklist(orderIdx);
            orderIdx++; // Increment the index

            // Define the listener for the instance
            controller.addListener(this::handleOnCourseClick);

            // Add the instance to the checklist view
            coursesView.getChildren().add(courseBox);
        }

        // Update max units
        maxUnitsLabel.setText(String.valueOf(checklist.getMaxUnits()));
    }

    /**
     * Executes when ANY child (course) instance is clicked.
     * Passes the course model of the clicked on course to the parent (checklist view) controller.
     * @param orderIdx The order index of the clicked on course
     */
    private void handleOnCourseClick(int orderIdx) {
        if (onCourseClickListener != null) {
            // Pass the data back to the main controller listener
            onCourseClickListener.accept(courses.get(orderIdx));
        }
    }

    /**
     * Attaches a listener to the controller.
     * @param listener
     */
    public void addListener(Consumer<MasterlistCourse> listener) {
        onCourseClickListener = listener;
    }

    /**
     * Counts how many courses have the source course as 1 of its requisites.
     * @param sourceCourse The course to match against
     * @return A count of dependents
     */
    public int countDependentsOf(MasterlistCourse sourceCourse) {
        int count = 0;

        // Simply return 0 if there are no courses
        if (courses.isEmpty()) return count;

        // Loop through each course in the checklist
        int idx = -1;
        for (MasterlistCourse course : courses) {
            idx++;
            // Skip this course if it has no reqs
            if (course.hasNoRequisites()) continue;

            // Check each req slot of the course
            for (int i = 1; i <= 3; i++) {
                Long reqId = course.getRequisiteIdAt(i);

                if (reqId == null) continue; // Skip req slot if empty

                // If req does match source course, increment counter and trigger dependent visualization
                if (reqId.equals(sourceCourse.getId())) {
                    count++;
                    IO.println(course.getCode() + " is a dependent of " + sourceCourse.getCode());
                    // TODO: Trigger for dependent coloring
                    highlightDependent(idx);
                }
            }
        }
        return count;
    }

    /**
     * Highlights all requisites of the source course found in the checklist.
     * @param sourceCourse The course to match against
     */
    public void highlightRequisitesOf(MasterlistCourse sourceCourse) {
        // Simply return if the source course has no reqs
        if (sourceCourse.hasNoRequisites()) return;

        // Simply return if there are no courses in checklist
        if (courses.isEmpty()) return;

        // Check each req slot of the source course
        for (int i = 0; i < 3; i++) {
            Long reqId = sourceCourse.getRequisiteIdAt(i);

            if (reqId == null) continue; // Skip req slot if empty

            // Loop through each course in the checklist
            int idx = 0;
            for (MasterlistCourse course : courses) {
                if (reqId.equals(course.getId())) {
                    IO.println(course.getCode() + " is a requisite of " + sourceCourse.getCode());
                    highlightRequisite(idx);
                }
                idx++;
            }
        }
    }

    /**
     * Highlights the course as a requisite.
     * @param courseIdx The order index of the course
     */
    private void highlightRequisite(int courseIdx) {
        CourseBoxController courseBox = courseControllers.get(courseIdx);
        highlightedCourses.add(courseBox);

        courseBox.setCourseUnits(5000);
        courseBox.updateHighlight(0); // TODO: TEMP
    }

    /**
     * Highlights the course as a dependent.
     * @param courseIdx The order index of the course
     */
    private void highlightDependent(int courseIdx) {
        CourseBoxController courseBox = courseControllers.get(courseIdx);
        highlightedCourses.add(courseBox);

        courseBox.setCourseUnits(1000);
        courseBox.updateHighlight(3); // TODO: TEMP
    }

    /**
     * Removes the highlight from all highlighted courses.
     */
    public void resetHighlights() {
        for (CourseBoxController courseBox : highlightedCourses) {
            courseBox.setCourseUnits(10);
            courseBox.resetHighlight();
        }
        highlightedCourses.clear();
    }
}
