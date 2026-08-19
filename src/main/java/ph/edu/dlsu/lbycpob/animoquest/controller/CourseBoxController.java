package ph.edu.dlsu.lbycpob.animoquest.controller;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import lombok.Getter;
import lombok.Setter;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.edu.dlsu.lbycpob.animoquest.model.CourseBoxState;
import ph.edu.dlsu.lbycpob.animoquest.model.CourseStatus;

import java.util.function.IntConsumer;

@Component
@Scope("prototype") // Ensures that Spring creates new controller instance for each fxml instance
@FxmlView("course-box.fxml")
public class CourseBoxController {
    @FXML private Label courseCodeLabel;
    @FXML private Label courseUnitsLabel;
    @FXML private CheckBox enrollInCheckbox; // TODO: HARMONY POINT
    @FXML private GridPane courseBox;

    private int orderIdxInChecklist;
    @Getter private CourseStatus status;

    // The listener that will notify the checklist controller of a click
    private IntConsumer onClickListener;

    // SETTERS
    public void setCourseCode(String courseCode) {
        if (courseCode == null) return;
        courseCodeLabel.setText(courseCode);
    }

    public void setCourseUnits(int courseUnits) {
        if (courseUnits < 0) return;
        courseUnitsLabel.setText(String.valueOf(courseUnits));
    }

    public void setOrderInChecklist(int index) {
        this.orderIdxInChecklist = index;
    }

    public void setStatus(CourseStatus status) {
        this.status = status;

        // Disables the checkbox if already passed
        if (status == CourseStatus.PASSED) enrollInCheckbox.setDisable(true);
    }

    // LISTENER

    /**
     * Attaches a listener to the controller.
     * @param listener
     */
    public void addListener(IntConsumer listener) {
        onClickListener = listener;
    }

    /**
     * Passes the order index to the parent (checklist) controller listener.
     * @param event
     */
    @FXML
    private void handleSelfClick(MouseEvent event) {
        if (onClickListener != null) {
            onClickListener.accept(orderIdxInChecklist);
        }
    }

    // HIGHLIGHTING

    /**
     * Sets a highlight on the course box based on its status instance field.
     */
    public void updateHighlight() {
        updateHighlight(status);
    }

    /**
     * Sets a highlight on the course box based on the given status.
     * @param status The specific highlight type
     */
    public void updateHighlight(CourseStatus status) {
        if (status == null) return;
        resetHighlight();

        // Converts the status to the corresponding JavaFX PseudoClass enum state
        CourseBoxState newState = CourseBoxState.convertStatusToState(status);
        if (newState == null) return; // Meaning course is NOT TAKEN (keep default highlight)

        // Activate the corresponding highlight
        courseBox.pseudoClassStateChanged(newState.getPseudoClass(), true);
    }

    /**
     * Sets a highlight on the course box based on the given state.
     * This method does not need to convert from course status to course box state.
     * @param newState The specific highlight type.
     */
    public void updateHighlight(CourseBoxState newState) {
        if (newState == null) return;
        resetHighlight();

        courseBox.pseudoClassStateChanged(newState.getPseudoClass(), true);
    }

    /**
     * Resets the highlight on the course box.
     */
    public void resetHighlight() {
        // Turn off all JavaFX PseudoClass enum states to prevent overlapping rules
        for (CourseBoxState state : CourseBoxState.values()) {
            courseBox.pseudoClassStateChanged(state.getPseudoClass(), false);
        }
    }
}
