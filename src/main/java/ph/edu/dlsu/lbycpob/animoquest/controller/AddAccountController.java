package ph.edu.dlsu.lbycpob.animoquest.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.springframework.stereotype.Component;

@Component
public class AddAccountController {

    @FXML
    private TextField idNumberField;

    @FXML
    private TextField nameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField majorField;

    @FXML
    private void handleAddAccount(ActionEvent event) {
        System.out.println("Add account button clicked!");
    }
}