package ph.edu.dlsu.lbycpob.animoquest.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
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

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @FXML
    private void handleLogin(ActionEvent event) {

        String username = usernameField.getText().trim();
        String idNumber = idNumberField.getText().trim();
        String password = passwordField.getText();

        // Check empty fields
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

            User user = loginService.login(
                    username,
                    idNumber,
                    password
            );

            if (user instanceof Student) {

                showAlert(
                        Alert.AlertType.INFORMATION,
                        "Login Successful",
                        "Welcome, " + user.getFullName() + "!"
                );

                // TODO:
                // Open Student Dashboard

            } else if (user instanceof Admin) {