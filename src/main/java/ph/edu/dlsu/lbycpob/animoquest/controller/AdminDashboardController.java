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

@Component
@FxmlView("admin-dashboard.fxml")
public class AdminDashboardController {

        @FXML
        private Label welcomeLabel;

        private final FxWeaver fxWeaver;

        private Admin currentAdmin;

        public AdminDashboardController(
                FxWeaver fxWeaver
        ) {
            this.fxWeaver = fxWeaver;
        }

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

        // COURSE MANAGEMENT

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
        private void handleLogout(
                ActionEvent event
        ) {
            try {
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
            } catch (Exception e) {
                e.printStackTrace();
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