package ph.edu.dlsu.lbycpob.animoquest.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
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

        try {

            // ----------------------------------------------------
            // AUTHENTICATE USER
            // ----------------------------------------------------

            User user = loginService.login(
                    username,
                    idNumber,
                    password
            );

            // ----------------------------------------------------
            // STUDENT LOGIN
            // ----------------------------------------------------

            if (user instanceof Student) {

                Student student = (Student) user;

                openEnrollment(
                        event,
                        student
                );

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
                // Open Admin Dashboard here
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
    // OPEN ENROLLMENT
    // ============================================================

    private void openEnrollment(
            ActionEvent event,
            Student student
    ) {

        try {

            /*
             * Load the EnrollmentController through FxWeaver.
             */
            Parent root =
                    fxWeaver.loadView(
                            EnrollmentController.class
                    );

            /*
             * Get the EnrollmentController instance
             * that belongs to this FXML view.
             */
            EnrollmentController controller =
                    fxWeaver.getBean(
                            EnrollmentController.class
                    );

            /*
             * Pass the logged-in student.
             *
             * This gives EnrollmentController access to:
             * - student ID
             * - student's major
             * - student's name
             */
            controller.setStudent(
                    student
            );
            controller.setDegree(
                    student.getMajor()
            );

            /*
             * Change the current scene.
             */
            Stage stage =
                    (Stage) ((Node) event.getSource())
                            .getScene()
                            .getWindow();

            stage.setScene(
                    new Scene(root)
            );

            stage.setTitle(
                    "AnimoQuestList - Enrollment"
            );

            stage.show();
            stage.setMaximized(true);

        } catch (Exception e) {

            e.printStackTrace();

            showAlert(
                    Alert.AlertType.ERROR,
                    "Navigation Error",
                    "Unable to open the enrollment page."
            );
        }
    }


    // ============================================================
    // BACK
    // ============================================================

    @FXML
    private void handleBack(ActionEvent event) {

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