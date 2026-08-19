package ph.edu.dlsu.lbycpob.animoquest;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxWeaver;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import ph.edu.dlsu.lbycpob.animoquest.controller.WelcomeController;

import java.util.Objects;

public class JavaFxApplication extends Application {

    private ConfigurableApplicationContext applicationContext;

    @Override
    public void init() {
        String[] args = getParameters().getRaw().toArray(new String[0]);

        this.applicationContext = new SpringApplicationBuilder()
                .sources(AnimoQuestSpringBootApplication.class)
                .run(args);
    }

    @Override
    public void start(Stage stage) {
        // Load and add the global CSS file
        String cssPath = Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm();
        Application.setUserAgentStylesheet(cssPath);

        FxWeaver fxWeaver = applicationContext.getBean(FxWeaver.class);
        Parent root = fxWeaver.loadView(WelcomeController.class);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("AnimoQuest");

        stage.show();
    }

    @Override
    public void stop() {
        this.applicationContext.close();
        Platform.exit();
    }

}
