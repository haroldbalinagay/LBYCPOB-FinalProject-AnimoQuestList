package ph.edu.dlsu.lbycpob.animoquest.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import lombok.Getter;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.edu.dlsu.lbycpob.animoquest.model.*;
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
    @Getter
    @FXML private VBox checklistBox;

    private int termNumber;
    private List<CourseBoxController> courseControllers = new ArrayList<>();

    private TermChecklist checklist;
    @Getter
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
        termNumber = number;
        termNumberLabel.setText("TERM " + number);
    }

    /**
     * Populates the courses of the checklist.
     */
    public void populateCourses(List<CurriculumProgress> progressList) {
        // Get the appropriate checklist
        checklist = checklistService.getChecklistOf("CPE", 125, termNumber); // TODO: HARMONY POINT

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

            // Provide the instance with key data
            controller.setCourseCode(course.getCode());
            controller.setCourseUnits(course.getUnits());
            controller.setOrderInChecklist(orderIdx);

            CourseStatus status = checklistService.getStatusOf(course, progressList);
            controller.setStatus(status);
            orderIdx++; // Increment the index

            // Define the listener for the instance
            controller.addListener(this::handleOnCourseClick);

            // Add the instance to the checklist view
            coursesView.getChildren().add(courseBox);

            // Automatically highlight the instance based on its status
            highlightedCourses.add(controller);
            controller.updateHighlight();
        }

        // Update max units
        maxUnitsLabel.setText(String.valueOf(checklist.getMaxUnits()));
    }

    /**
     * Executes when ANY child (course box) instance is clicked.
     * Passes the course model of the clicked on course box to the parent (checklist view) controller.
     * @param orderIdx The order index of the clicked on course box
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
    public int countDependentsOf(MasterlistCourse sourceCourse, boolean renderHighlights) {
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

                // If req does match source course, increment counter and trigger dependent visualization (ONLY IF desired)
                if (reqId.equals(sourceCourse.getId())) {
                    count++;
                    IO.println(course.getCode() + " is a dependent of " + sourceCourse.getCode());
                    if (renderHighlights) {
                        highlightDependent(idx);
                    }
                }
            }
        }
        return count;
    }

    /**
     * Highlights all requisites of the source course found in the checklist.
     * @param sourceCourse   The course to match against
     * @param progressList   The complete curriculum progress records of a student
     */
    public void highlightRequisitesOf(MasterlistCourse sourceCourse, List<CurriculumProgress> progressList) {
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
                    highlightRequisite(idx, progressList);
                }
                idx++;
            }
        }
    }

    /**
     * Instructs the course box to highlight itself as a requisite.
     * The specific type of requisite is first determined before being passed on to the course box.
     * @param courseIdx      The order index of the course
     * @param progressList   The complete curriculum progress records of a student
     */
    private void highlightRequisite(int courseIdx, List<CurriculumProgress> progressList) {
        // Get the specific controller and save it to a list
        CourseBoxController courseBox = courseControllers.get(courseIdx);
        highlightedCourses.add(courseBox);

        // Ask the checklist service to get the status of the course based on the progress records
        CourseStatus status = checklistService.getStatusOf(courses.get(courseIdx), progressList);
        // Instruct the course box
        courseBox.updateHighlight(status);
    }

    /**
     * Instructs the course box to highlight itself as a dependent.
     * @param courseIdx The order index of the course
     */
    private void highlightDependent(int courseIdx) {
        // Get the specific controller and save it to a list
        CourseBoxController courseBox = courseControllers.get(courseIdx);
        highlightedCourses.add(courseBox);

        // Instruct the course box
        courseBox.updateHighlight(CourseStatus.DEPENDENT);
    }

    /**
     * Removes the highlight from all highlighted courses.
     */
    public void resetHighlights() {
        for (CourseBoxController courseBox : highlightedCourses) {
            courseBox.resetHighlight();
        }
        highlightedCourses.clear();
    }

    /**
     * Highlights the (clicked on) course based on the given state.
     * @param sourceCourse The clicked on course
     * @param newState The state to base the highlighting style on
     */
    public void highlightSourceCourse(MasterlistCourse sourceCourse, CourseBoxState newState) {
        if (sourceCourse == null) return;

        // Loop through each course in the checklist to find the source course
        int idx = 0;
        for (MasterlistCourse course : courses) {
            if (Objects.equals(sourceCourse.getId(), course.getId())) {
                CourseBoxController courseBox = courseControllers.get(idx);
                highlightedCourses.add(courseBox);
                courseBox.updateHighlight(newState);
            }
            idx++;
        }
    }

    /**
     * Instructs all course boxes to revert back to the highlights associated with their stored statuses.
     */
    public void restoreHighlights() {
        // Loop through each course in the checklist to restore its default highlight
        for (CourseBoxController courseBox : courseControllers) {
            highlightedCourses.add(courseBox);
            courseBox.updateHighlight();
        }
    }
}
