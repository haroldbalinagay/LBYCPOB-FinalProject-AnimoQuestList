package ph.edu.dlsu.lbycpob.animoquest.controller;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype") // Ensures that Spring creates new controller instance for each fxml instance
@FxmlView("course-box.fxml")
public class CourseBoxController {
    @FXML private Label courseCodeLabel;
    @FXML private Label courseUnitsLabel;
    @FXML private CheckBox enrollInCheckbox;

    public void setCourseCode(String courseCode) {
        if (courseCode == null) return;
        courseCodeLabel.setText(courseCode);
    }

    public void setCourseUnits(int courseUnits) {
        if (courseUnits < 0) return;
        courseUnitsLabel.setText(String.valueOf(courseUnits));
    }
}
