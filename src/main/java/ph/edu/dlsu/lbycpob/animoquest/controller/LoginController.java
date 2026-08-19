package ph.edu.dlsu.lbycpob.animoquest.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;
import ph.edu.dlsu.lbycpob.animoquest.model.Admin;
import ph.edu.dlsu.lbycpob.animoquest.model.Student;
import ph.edu.dlsu.lbycpob.animoquest.model.User;
import ph.edu.dlsu.lbycpob.animoquest.service.LoginService;


@Component
@FxmlView("login.fxml")
public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField idNumberField;

    @FXML
    private PasswordField passwordField;

    private final LoginService loginService;
    private final FxWeaver fxWeaver;

    public LoginController(
            LoginService loginService,
            FxWeaver fxWeaver
    ) {
        this.loginService = loginService;
        this.fxWeaver = fxWeaver;
    }

    // ============================================================
    // LOGIN
    // ============================================================

    @FXML
    private void handleLogin(ActionEvent event) {

        String username = usernameField.getText().trim();
        String idNumber = idNumberField.getText().trim();
        String password = passwordField.getText();

        // --------------------------------------------------------
        // CHECK EMPTY FIELDS
        // --------------------------------------------------------

        if (username.isEmpty()
                || idNumber.isEmpty()
                || password.isEmpty()) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "Missing Information",
                    "Please enter your username, ID number, and password."
            );

            return;
        }

        // --------------------------------------------------------
        // LOGIN
        // --------------------------------------------------------

        try {

            User user = loginService.login(
                    username,
                    idNumber,
                    password
            );

            // ----------------------------------------------------
            // STUDENT LOGIN
            // ----------------------------------------------------

            if (user instanceof Student student) {

                showAlert(
                        Alert.AlertType.INFORMATION,
                        "Login Successful",
                        "Welcome, " + user.getFullName() + "!"
                );

                // Load Enrollment page
                Parent root =
                        fxWeaver.loadView(
                                EnrollmentController.class
                        );

                // Pass logged-in student's ID
                EnrollmentController controller =
                        fxWeaver.getBean(
                                EnrollmentController.class
                        );

                controller.setStudentId(
                        student.getId()
                );

                // Change scene
                Stage stage =
                        (Stage) ((Node) event.getSource())
                                .getScene()
                                .getWindow();

                stage.setScene(
                        new Scene(root)
                );

                stage.show();

            }

            // ----------------------------------------------------
            // ADMIN LOGIN
            // ----------------------------------------------------

            else if (user instanceof Admin) {

                showAlert(
                        Alert.AlertType.INFORMATION,
                        "Login Successful",
                        "Welcome, " + user.getFullName() + "!"
                );

                // TODO:
                // Redirect to Admin Dashboard
            }

        } catch (IllegalArgumentException e) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "Login Failed",
                    e.getMessage()
            );

        } catch (Exception e) {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Login Error",
                    "An unexpected error occurred while logging in."
            );

            e.printStackTrace();
        }
    }
    // ============================================================
    // BACK TO WELCOME
    // ============================================================

    @FXML
    private void handleBack(ActionEvent event) {

        try {

            fxWeaver.loadView(WelcomeController.class);

            javafx.scene.Parent root =
                    fxWeaver.loadView(
                            WelcomeController.class
                    );

            javafx.stage.Stage stage =
                    (javafx.stage.Stage)
                            ((javafx.scene.Node) event.getSource())
                                    .getScene()
                                    .getWindow();

            stage.setScene(
                    new javafx.scene.Scene(root)
            );

            stage.show();

        } catch (Exception e) {

            e.printStackTrace();
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