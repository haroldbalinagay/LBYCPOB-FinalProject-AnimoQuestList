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
    private PasswordField passwordField;

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField middleNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField majorField;

    private final LoginService loginService;

    public AddAccountController(LoginService loginService) {
        this.loginService = loginService;
    }

    @FXML
    private void handleAddAccount(ActionEvent event) {

        String idNumber = idNumberField.getText().trim();
        String password = passwordField.getText();
        String firstName = firstNameField.getText().trim();
        String middleName = middleNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String major = majorField.getText().trim();

        // Check if any field is empty
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