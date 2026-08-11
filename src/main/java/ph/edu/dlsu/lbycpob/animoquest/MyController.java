package ph.edu.dlsu.lbycpob.animoquest;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.context.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

@Component
@FxmlView("format-test.fxml")
public class MyController {

    @FXML private GridPane gridPane;
    //@FXML private ScrollPane scrollPane;

    private final ApplicationContext springContext;

    // Spring injects its own context here
    public MyController(ApplicationContext springContext) {
        this.springContext = springContext;
    }

    public void includeIntoGrid(ActionEvent actionEvent) {
        //this.gridPane.add(weatherLabel, 2, 0);
        try {
            int targetColumn = 2;

            //gridPane.add(subView, 2, 0);
            for (int i = 0; i < 4; i++) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("main-stage.fxml"));
                loader.setControllerFactory(springContext::getBean);
                Parent subView = loader.load();
                //loader.getController();

                int finalI = i;
                gridPane.getChildren().removeIf(node -> {
                    Integer col = GridPane.getColumnIndex(node);
                    Integer row = GridPane.getRowIndex(node);
                    return (col != null && col == targetColumn) && (row != null && row == finalI);
                });

                gridPane.add(subView, 2, i);
            }

            

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void insertButtons(ActionEvent event) {
        // 1. Create a container for the content
        VBox contentBox = new VBox(10);
        for (int i = 1; i <= 10; i++) {
            contentBox.getChildren().add(new Button("Button " + i));
        }

        ScrollPane scrollPane = new ScrollPane(contentBox);

        gridPane.add(scrollPane, 1, 3);
    }
}
