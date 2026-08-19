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

import java.util.Objects;

@Component
@FxmlView("login.fxml")
public class LoginController {


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


        String idNumber = idNumberField.getText().trim();
        String password = passwordField.getText();

        // --------------------------------------------------------
        // CHECK EMPTY FIELDS
        // --------------------------------------------------------

        if (idNumber.isEmpty() || password.isEmpty()) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "Missing Information",
                    "Please enter your ID number and password."
            );

            return;
        }

        try {

            // ----------------------------------------------------
            // AUTHENTICATE USER
            // ----------------------------------------------------

            User user = loginService.login(
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


                Admin admin = (Admin) user;


                openAdminDashboard(
                        event,
                        admin
                );
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

            root.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm());

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
                    "AnimoQuest - Enrollment"
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

    private void openAdminDashboard(
            ActionEvent event,
            Admin admin
    ) {


        try {


            Parent root =
                    fxWeaver.loadView(
                            AdminDashboardController.class
                    );


            AdminDashboardController controller =
                    fxWeaver.getBean(
                            AdminDashboardController.class
                    );


            controller.setAdmin(
                    admin
            );


            Stage stage =
                    (Stage) ((Node) event.getSource())
                            .getScene()
                            .getWindow();


            stage.setScene(
                    new Scene(root)
            );


            stage.setTitle(
                    "AnimoQuestList - Admin Dashboard"
            );


            stage.show();
            stage.setMaximized(true);


        } catch (Exception e) {


            e.printStackTrace();


            showAlert(
                    Alert.AlertType.ERROR,
                    "Navigation Error",
                    "Unable to open the Admin Dashboard."
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