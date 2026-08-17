package ph.edu.dlsu.lbycpob.animoquest.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.edu.dlsu.lbycpob.animoquest.service.FxmlLoaderService;

import java.io.IOException;

@Component
@Scope("prototype") // Ensures that Spring creates new controller instance for each fxml instance
@FxmlView("term-checklist.fxml")
public class TermChecklistController {
    @FXML private Label termNumberLabel;
    @FXML private CheckBox selectAllCheckbox;
    @FXML private Label maxUnitsLabel;
    @FXML private VBox coursesView;

    private int termNumber;

    private final FxmlLoaderService fxmlLoader;

    public TermChecklistController(FxmlLoaderService fxmlLoader) {
        this.fxmlLoader = fxmlLoader;
    }

    /**
     * TEMP
     */
    @FXML
    public void initialize() {
        for (int i = 0; i < 10; i++) {
            try {
                Parent courseBox = fxmlLoader.load(getClass().getResource("course-box.fxml"));
                coursesView.getChildren().add(courseBox);
            } catch (IOException e) {
                coursesView.getChildren().add(new Label("Error loading course"));
            }
        }
    }

    public void setTermNumber(int number) {
        if (number <= 0) return;
        termNumber = number;
        termNumberLabel.setText("TERM " + number);
    }
}
