package ph.edu.dlsu.lbycpob.animoquest.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxWeaver;
import org.springframework.stereotype.Component;
import net.rgielen.fxweaver.core.FxmlView;

import java.util.Objects;

@Component
@FxmlView("welcome.fxml")
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
            root.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm());

            Stage stage = (Stage) ((Node) event.getSource())
                    .getScene()
                    .getWindow();

            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}