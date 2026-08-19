package ph.edu.dlsu.lbycpob.animoquest.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;
import ph.edu.dlsu.lbycpob.animoquest.model.Admin;
import ph.edu.dlsu.lbycpob.animoquest.service.CourseManagementService;

@Component
@FxmlView("admin-dashboard.fxml")
public class AdminDashboardController {

    @FXML
    private Label welcomeLabel;

    private final FxWeaver fxWeaver;
    private final CourseManagementService courseService;

    private Admin currentAdmin;

    public AdminDashboardController(FxWeaver fxWeaver, CourseManagementService courseService) {
        this.fxWeaver = fxWeaver;
        this.courseService = courseService;
    }

    public void setAdmin(Admin admin) {
        this.currentAdmin = admin;
        if (welcomeLabel != null) {
            welcomeLabel.setText("Welcome, " + admin.getFullName() + "!");
        }
    }

    // COURSE MANAGEMENT

    @FXML
    private void handleCourseManagement(ActionEvent event) {
        try {Parent root = fxWeaver.loadView(CourseManagementController.class);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("AnimoQuest - Course Management");
            stage.show();}
        catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Unable to open Course Management.");
        }
    }

    // CHECKLIST MANAGEMENT

    @FXML
    private void handleChecklistManagement(
            ActionEvent event
    ) {

        showAlert(
                Alert.AlertType.INFORMATION,
                "Checklist Management",
                "Checklist Management will be implemented here."
        );
    }

    // LOGOUT

    @FXML
    private void handleLogout(ActionEvent event) {
        try {Parent root = fxWeaver.loadView(WelcomeController.class);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("AnimoQuest");
            stage.show();}
        catch (Exception e) {e.printStackTrace();
            showAlert(
                    Alert.AlertType.ERROR,
                    "Logout Error",
                    "Unable to return to the welcome page."
            );
        }
    }

    // ALERT

    private void showAlert(
            Alert.AlertType type,
            String title,
            String message
    ) {

        Alert alert =
                new Alert(type);

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }
}