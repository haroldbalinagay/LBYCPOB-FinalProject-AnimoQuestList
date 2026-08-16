package ph.edu.dlsu.lbycpob.animoquest.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

@Component
@FxmlView("app-window.fxml")
public class MainController {

    @FXML private BorderPane appWindow;
    @FXML private Button courseEditorBtn;
    @FXML private Button termChecklistBtn;
    @FXML private Button enrollPlanBtn;
    @FXML private Button logoutBtn;

    private void loadCenterView(String fxmlFile) {
        try {
            // Load the subview
            Parent subView = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxmlFile)));

            // Swap out only the center layout
            appWindow.setCenter(subView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showCourseEditorView(ActionEvent event) {
        loadCenterView("admin-course-editor.fxml");
    }

    public void showTermChecklistView(ActionEvent event) {
        loadCenterView("checklist-view.fxml");
    }

    public void showEnrollmentPlanView(ActionEvent event) {
        loadCenterView("enroll-plan.fxml");
    }

    public void handleLogout(ActionEvent event) {
        IO.println("Trying to logout.");
    }
}
