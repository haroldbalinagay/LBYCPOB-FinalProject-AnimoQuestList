package ph.edu.dlsu.lbycpob.animoquest.controller.v2;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;
import ph.edu.dlsu.lbycpob.animoquest.service.v2.FxmlLoaderService;

import java.io.IOException;
import java.util.Objects;

@Component
@FxmlView("app-window.fxml")
public class MainController {

    @FXML private BorderPane appWindow;
    @FXML private Button courseEditorBtn;
    @FXML private Button termChecklistBtn;
    @FXML private Button enrollPlanBtn;
    @FXML private Button logoutBtn;
    @FXML private Label usernameLabel;
    @FXML private Label idNumberLabel;
    @FXML private Label degreeLabel;
    @FXML private Label currentTermLabel;
    @FXML private Label statusBarLabel;

    private final FxmlLoaderService fxmlLoader;

    public MainController(FxmlLoaderService fxmlLoader) {
        this.fxmlLoader = fxmlLoader;
    }

    /**
     * Initializes variable UI elements, namely the user profile info.
     */
    @FXML
    private void initialize() {
        usernameLabel.setText("Test Name");
        idNumberLabel.setText("12345678");
        degreeLabel.setText("BS CPE");
        currentTermLabel.setText("Term 3, AY 25-26");
    }

    private void loadCenterView(String fxmlFile) {
        try {
            // Load the subview
            Parent subView = fxmlLoader.load(Objects.requireNonNull(getClass().getResource(fxmlFile)));

            // Swap out only the center layout
            appWindow.setCenter(subView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void showCourseEditorView(ActionEvent event) {
        loadCenterView("admin-course-editor.fxml");
    }

    @FXML
    public void showTermChecklistView(ActionEvent event) {
        loadCenterView("checklist-view.fxml");
    }

    @FXML
    public void showEnrollmentPlanView(ActionEvent event) {
        loadCenterView("enroll-plan.fxml");
    }

    @FXML
    public void handleLogout(ActionEvent event) {
        displayStatusBarMsg("Trying to logout"); // temp
    }

    /**
     * Displays a message in the status bar at the bottom of the app window, which fades out after a few seconds.
     * @param message The message to be shown
     */
    public void displayStatusBarMsg(String message) {
        statusBarLabel.setText(message);
        statusBarLabel.setOpacity(1);
        statusBarLabel.setVisible(true);

        FadeTransition fadeOut = new FadeTransition();
        fadeOut.setNode(statusBarLabel);
        fadeOut.setDelay(Duration.seconds(5)); // Wait for a few seconds before fading out
        fadeOut.setDuration(Duration.millis(1500));
        fadeOut.setFromValue(1);  // Start fully visible (100% opacity)
        fadeOut.setToValue(0);    // End completely invisible (0% opacity)
        fadeOut.play();
        fadeOut.setOnFinished(e -> statusBarLabel.setVisible(false)); // Hide the label
    }
}
