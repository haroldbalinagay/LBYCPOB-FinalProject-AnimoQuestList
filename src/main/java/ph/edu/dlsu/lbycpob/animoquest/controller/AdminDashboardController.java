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
import ph.edu.dlsu.lbycpob.animoquest.service.SessionService;

@Component
@FxmlView("admin-dashboard.fxml")
public class AdminDashboardController {

    @FXML
    private Label welcomeLabel;

    private final FxWeaver fxWeaver;
    private final SessionService sessionService;

    private Admin currentAdmin;

    public AdminDashboardController(
            FxWeaver fxWeaver,
            SessionService sessionService
    ) {
        this.fxWeaver = fxWeaver;
        this.sessionService = sessionService;
    }

    // ============================================================
    // SET ADMIN
    // ============================================================

    public void setAdmin(Admin admin) {

        this.currentAdmin = admin;

        if (welcomeLabel != null) {

            welcomeLabel.setText(
                    "Welcome, "
                            + admin.getFullName()
                            + "!"
            );
        }
    }

    // ============================================================
    // COURSE MANAGEMENT
    // ============================================================

    @FXML
    private void handleCourseManagement(
            ActionEvent event
    ) {

        showAlert(
                Alert.AlertType.INFORMATION,
                "Course Management",
                "Course Management will be implemented here."
        );
    }

    // ============================================================
    // CHECKLIST MANAGEMENT
    // ============================================================

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

// ============================================================
// LOGOUT
// ============================================================
@FXML
private void handleLogout(
        ActionEvent event
) {

    try {

        // ----------------------------------------------------
        // CLEAR CURRENT SESSION
        // ----------------------------------------------------

        sessionService.logout();

        // ----------------------------------------------------
        // RETURN TO WELCOME PAGE
        // ----------------------------------------------------

        Parent root =
                fxWeaver.loadView(
                        WelcomeController.class
                );

        Stage stage =
                (Stage) ((Node) event.getSource())
                        .getScene()
                        .getWindow();

        stage.setScene(
                new Scene(root)
        );

        stage.setTitle(
                "AnimoQuest"
        );

        stage.show();

        stage.setMaximized(false);

    } catch (Exception e) {

        e.printStackTrace();

        showAlert(
                Alert.AlertType.ERROR,
                "Logout Error",
                "Unable to log out."
        );
    }
}

    // ============================================================
    // ALERT
    // ============================================================

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