package bcbfixhub.bcbfixhub;

import bcbfixhub.bcbfixhub.controllers.ScenesController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ScenesApplication extends Application {

    private Stage mainStage;
    private final Map<String, Scene> scenes = new HashMap<>();

    @Override
    public void start(Stage stage) throws IOException {
        this.mainStage = stage;

        // Preload initial scenes
        addScene("login", "login-view.fxml", 400, 300);
        addScene("register", "register-view.fxml", 400, 300);

        switchTo("login");
        stage.show();
    }

    private void addScene(String name, String fxml, int width, int height) throws IOException {
        // Try both possible folder formats for safety
        URL fxmlUrl = getClass().getResource("/bcbfixhub/bcbfixhub/" + fxml);
        if (fxmlUrl == null) {
            fxmlUrl = getClass().getResource("/bcbfixhub.bcbfixhub/" + fxml);
        }

        if (fxmlUrl == null) {
            throw new IOException("FXML file not found for scene: " + name);
        }

        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Scene scene = new Scene(loader.load(), width, height);

        // Optional CSS
        URL cssUrl = getClass().getResource("/bcbfixhub/bcbfixhub/" + name + ".css");
        if (cssUrl == null) {
            cssUrl = getClass().getResource("/bcbfixhub.bcbfixhub/" + name + ".css");
        }
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }

        // Set controller link
        ScenesController controller = loader.getController();
        controller.setApplication(this);

        // Store scene
        scenes.put(name, scene);

        // Debug info
        System.out.println("Loaded scene: " + name + " from " + fxmlUrl);
    }

    public void switchTo(String name) {
        Scene scene = scenes.get(name);
        if (scene == null) {
            try {
                addScene(name, name + "-view.fxml", 480, 300);
                scene = scenes.get(name);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        mainStage.setScene(scene);
        mainStage.setTitle(capitalize(name));
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    public static void main(String[] args) {
        launch();
    }
}
