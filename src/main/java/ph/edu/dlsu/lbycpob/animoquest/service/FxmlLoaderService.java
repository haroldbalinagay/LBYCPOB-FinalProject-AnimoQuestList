package ph.edu.dlsu.lbycpob.animoquest.service;

import javafx.fxml.FXMLLoader;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.net.URL;

/**
 * FxmlLoaderService tells the JavaFX FXMLLoader to allow Spring to manage the FXML's controller as a Bean.
 */
@Service
public class FxmlLoaderService {

    private final ApplicationContext context;

    public FxmlLoaderService(ApplicationContext context) {
        this.context = context;
    }

    /**
     * Loads a FXML file and allows Spring to manage its controller as a Bean.
     * @param fxmlPath
     * @return The parent node of the FXML file
     * @param <T>
     * @throws IOException
     */
    public <T> T load(URL fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(fxmlPath);

        // Link the existing context to this specific loader instance
        loader.setControllerFactory(context::getBean);

        return loader.load();
    }
}
