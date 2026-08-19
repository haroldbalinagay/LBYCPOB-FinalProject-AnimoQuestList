package ph.edu.dlsu.lbycpob.animoquest.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;
import ph.edu.dlsu.lbycpob.animoquest.service.LoginService;

@Component
@FxmlView("add-account.fxml")
public class AddAccountController {

    @FXML
    private TextField idNumberField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField passwordTextField;

    @FXML
    private CheckBox showPasswordCheckBox;

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField middleNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField majorField;

    private final LoginService loginService;
    private final FxWeaver fxWeaver;

    public AddAccountController(
            LoginService loginService,
            FxWeaver fxWeaver
    ) {
        this.loginService = loginService;
        this.fxWeaver = fxWeaver;
    }

    @FXML
    private void handleAddAccount(ActionEvent event) {

        String idNumber = idNumberField.getText().trim();

        String password;

        if (showPasswordCheckBox.isSelected()) {
            password = passwordTextField.getText();
        } else {
            password = passwordField.getText();
        }

        String firstName = firstNameField.getText().trim();
        String middleName = middleNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String major = majorField.getText().trim();


        // ============================================================
        // CHECK FOR EMPTY FIELDS
        // ============================================================

        if (idNumber.isEmpty()
                || password.isEmpty()
                || firstName.isEmpty()
                || middleName.isEmpty()
                || lastName.isEmpty()
                || major.isEmpty()) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "Missing Information",
                    "Please fill in all fields."
            );

            return;
        }


        // ============================================================
        // CREATE ACCOUNT
        // ============================================================

        try {

            loginService.addStudentAccount(
                    idNumber,
                    password,
                    firstName,
                    middleName,
                    lastName,
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
// ============================================================
    // SHOW / HIDE PASSWORD
    // ============================================================

    @FXML
    private void handleShowPassword(ActionEvent event) {

        if (showPasswordCheckBox.isSelected()) {

            // Copy hidden password to visible field
            passwordTextField.setText(
                    passwordField.getText()
            );

            // Hide PasswordField
            passwordField.setVisible(false);
            passwordField.setManaged(false);

            // Show normal TextField
            passwordTextField.setVisible(true);
            passwordTextField.setManaged(true);

        } else {

            // Copy visible password back to PasswordField
            passwordField.setText(
                    passwordTextField.getText()
            );

            // Hide normal TextField
            passwordTextField.setVisible(false);
            passwordTextField.setManaged(false);

            // Show PasswordField
            passwordField.setVisible(true);
            passwordField.setManaged(true);
        }
    }


    // ============================================================
    // BACK BUTTON
    // ============================================================

    @FXML
    private void handleBack(ActionEvent event) {

        try {

            Parent root = fxWeaver.loadView(
                    WelcomeController.class
            );

            Stage stage = (Stage) ((Node) event.getSource())
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

        Alert alert = new Alert(type);

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }


    // ============================================================
    // CLEAR FIELDS
    // ============================================================

    private void clearFields() {

        idNumberField.clear();

        passwordField.clear();
        passwordTextField.clear();

        showPasswordCheckBox.setSelected(false);

        passwordTextField.setVisible(false);
        passwordTextField.setManaged(false);

        passwordField.setVisible(true);
        passwordField.setManaged(true);

        firstNameField.clear();
        middleNameField.clear();
        lastNameField.clear();
        majorField.clear();
    }
}
