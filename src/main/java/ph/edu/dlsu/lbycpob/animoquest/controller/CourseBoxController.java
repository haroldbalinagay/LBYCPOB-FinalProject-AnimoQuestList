package ph.edu.dlsu.lbycpob.animoquest.controller;

import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import lombok.Getter;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.function.IntConsumer;

@Component
@Scope("prototype") // Ensures that Spring creates new controller instance for each fxml instance
@FxmlView("course-box.fxml")
public class CourseBoxController {
    @FXML private Label courseCodeLabel;
    @FXML private Label courseUnitsLabel;
    @FXML private CheckBox enrollInCheckbox;
    @FXML private GridPane courseBox;

    private int orderIdxInChecklist;

    // The listener that will notify the checklist controller of a click
    private IntConsumer onClickListener;

    // JavaFX Pseudo-Classes for handling dynamic styling states
    private static final PseudoClass REQ_PASSED_STATE = PseudoClass.getPseudoClass("requisite-passed");
    private static final PseudoClass REQ_FAILED_STATE = PseudoClass.getPseudoClass("requisite-failed");
    private static final PseudoClass REQ_IN_PROGRESS_STATE = PseudoClass.getPseudoClass("requisite-in-progress");
    private static final PseudoClass DEPENDENT_STATE = PseudoClass.getPseudoClass("dependent");

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

    /**
     * Sets a highlight on the course box.
     * @param type
     */
    public void updateHighlight(int type) {
        resetHighlight();
        switch (type) { // TODO: TEMP
            case 0 -> courseBox.pseudoClassStateChanged(REQ_PASSED_STATE, true);
            case 1 -> courseBox.pseudoClassStateChanged(REQ_FAILED_STATE, true);
            case 2 -> courseBox.pseudoClassStateChanged(REQ_IN_PROGRESS_STATE, true);
            case 3 -> courseBox.pseudoClassStateChanged(DEPENDENT_STATE, true);
        }
    }

    /**
     * Resets the highlight on the course box.
     */
    public void resetHighlight() {
        courseBox.pseudoClassStateChanged(REQ_PASSED_STATE, false);
        courseBox.pseudoClassStateChanged(REQ_FAILED_STATE, false);
        courseBox.pseudoClassStateChanged(REQ_IN_PROGRESS_STATE, false);
        courseBox.pseudoClassStateChanged(DEPENDENT_STATE, false);
    }
}
