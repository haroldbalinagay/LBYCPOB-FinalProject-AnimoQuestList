package ph.edu.dlsu.lbycpob.animoquest.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxWeaverException;
import org.springframework.stereotype.Component;

@Component
public class WelcomeController {

    private final FxWeaver fxWeaver;

    public WelcomeController(FxWeaver fxWeaver) {
        this.fxWeaver = fxWeaver;
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        switchScene(event, ph.edu.dlsu.lbycpob.animoquest.controller.LoginController.class);
    }

    @FXML
    private void handleSignUp(ActionEvent event) {
        switchScene(event, AddAccountController.class);
    }

    private void switchScene(
            ActionEvent event,
            Class<?> controllerClass
    ) {
        try {
            Parent root = fxWeaver.loadView(controllerClass);

            Stage stage = (Stage) ((Node) event.getSource())
                    .getScene()
                    .getWindow();

            stage.setScene(new Scene(root));
            stage.show();

        } catch (FxWeaverException e) {
            e.printStackTrace();
        }
    }
}