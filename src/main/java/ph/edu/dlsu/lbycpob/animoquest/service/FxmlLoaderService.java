package ph.edu.dlsu.lbycpob.animoquest.service;

import javafx.fxml.FXMLLoader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.net.URL;

/**
 * FxmlLoaderService tells the JavaFX FXMLLoader to allow Spring to manage the FXML's controller as a Bean.
 */
@Service
@Scope("prototype") // Ensures that Spring creates a new service instance for each fxml instance
public class FxmlLoaderService {

    private FXMLLoader loader;

    private final ApplicationContext context;

    public FxmlLoaderService(ApplicationContext context) {
        this.context = context;
    }

    /**
     * Loads an object hierarchy from a FXML file and allows Spring to manage its controller as a Bean.
     * @param fxmlPath
     * @return The loaded FXML hierarchy
     * @param <T> The type of the root object
     * @throws IOException
     */
    public <T> T load(URL fxmlPath) throws IOException {
        loader = new FXMLLoader(fxmlPath);

        // Link the existing context to this specific loader instance
        loader.setControllerFactory(context::getBean);

        return loader.load();
    }

    /**
     * Returns the controller associated with the root object.
     * @return The controller associated with the root object
     * @param <T> The type of the controller
     */
    public <T> T getController() {
        return loader.getController();
    }
}
