package ph.edu.dlsu.lbycpob.animoquest.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;
import ph.edu.dlsu.lbycpob.animoquest.service.LoginService;

@Component
@FxmlView("add-account.fxml")
public class AddAccountController {

    @FXML
    private TextField idNumberField;

    @FXML
    private TextField nameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField majorField;

    private final LoginService loginService;

    public AddAccountController(LoginService loginService) {
        this.loginService = loginService;
    }

    @FXML
    private void handleAddAccount(ActionEvent event) {

        String idNumber = idNumberField.getText().trim();
        String name = nameField.getText().trim();
        String password = passwordField.getText();
        String major = majorField.getText().trim();

        // Check if any field is empty
        if (idNumber.isEmpty()
                || name.isEmpty()
                || password.isEmpty()
                || major.isEmpty()) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "Missing Information",
                    "Please fill in all fields."
            );

            return;
        }

        try {

            loginService.addStudentAccount(
                    idNumber,
                    name,
                    password,
                    major
            );

            showAlert(
                    Alert.AlertType.INFORMATION,
                    "Account Created",
                    "Your AnimoQuest account was successfully created!"
            );

            clearFields();

        } catch (IllegalArgumentException e) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "Account Creation Failed",
                    e.getMessage()
            );

        } catch (Exception e) {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Database Error",
                    "An unexpected error occurred while creating the account."
            );

            e.printStackTrace();
        }
    }

    private void showAlert(
            Alert.AlertType type,
            String title,
            String message
    ) {

        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields() {
        idNumberField.clear();
        nameField.clear();
        passwordField.clear();
        majorField.clear();
    }
}