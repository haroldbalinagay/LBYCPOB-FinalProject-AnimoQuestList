package ph.edu.dlsu.lbycpob.animoquest.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
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

    private int orderIdxInChecklist;

    // The listener that will notify the main controller
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
}
