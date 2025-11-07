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

        // --- NEW STANDARD SIZE for auth windows ---
        int authWidth = 450;
        int authHeight = 550;

        // Preload initial scenes
        addScene("home", "home-view.fxml", authWidth, authHeight); // ADDED home scene
        addScene("login", "login-view.fxml", authWidth, authHeight); // EDITED size
        addScene("register", "register-view.fxml", authWidth, authHeight); // EDITED size

        // Other scenes
        addScene("admin", "admin-view.fxml",  900, 600);
        addScene("user-dashboard", "user-main-view.fxml", 1000, 700);
        addScene("product", "product-view.fxml", 900, 600);
        addScene("cart", "cart-view.fxml", 900, 600);
        addScene("payment", "payment-view.fxml", 950, 650);
        addScene("account", "account-view.fxml", 1000, 700);


        switchTo("home"); // EDITED to start on home
        stage.show();
        stage.centerOnScreen();

    }

    private void addScene(String name, String fxml, int width, int height) throws IOException {
        // Try both possible folder formats for safety
        URL fxmlUrl = getClass().getResource("/bcbfixhub/bcbfixhub/" + fxml);
        if (fxmlUrl == null) {
            fxmlUrl = getClass().getResource("/bcbfixhub.bcbfixhub/" + fxml);
        }

        if (fxmlUrl == null) {
            // EDITED: Try to load from root if nested fails (for FXML)
            fxmlUrl = getClass().getResource("/" + fxml);
        }

        if (fxmlUrl == null) {
            throw new IOException("FXML file not found for scene: " + name + " (tried: /bcbfixhub/bcbfixhub/" + fxml + ", /bcbfixhub.bcbfixhub/" + fxml + ", /" + fxml + ")");
        }

        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Scene scene = new Scene(loader.load(), width, height);

        // Optional CSS
        URL cssUrl = getClass().getResource("/bcbfixhub/bcbfixhub/css/" + name + ".css");
        if (cssUrl == null) {
            cssUrl = getClass().getResource("/bcbfixhub.bcbfixhub/css/" + name + ".css");
        }
        if (cssUrl == null) {
            // EDITED: Try to load from /css/ relative to root
            cssUrl = getClass().getResource("/css/" + name + ".css");
        }

        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
            System.out.println("Loaded CSS for scene: " + name + " -> " + cssUrl);
        } else {
            System.out.println("No CSS found for scene: " + name);
        }

        // Set controller link
        Object controller = loader.getController();
        if (controller instanceof ScenesController) {
            ((ScenesController) controller).setApplication(this);
        } else if (controller != null) {
            System.out.println("Warning: Controller " + controller.getClass().getName() + " does not extend ScenesController.");
        } else {
            System.out.println("Warning: No controller found for " + name);
        }


        // Store scene
        scenes.put(name, scene);

        System.out.println("Loaded scene: " + name + " from " + fxmlUrl);
    }

    public void switchTo(String name) {
        Scene scene = scenes.get(name);
        if (scene == null) {
            try {
                // Try to dynamically add if not preloaded
                String fxmlFile = name + "-view.fxml";
                // Guess dimensions if not preloaded
                addScene(name, fxmlFile, 800, 600);
                scene = scenes.get(name);
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Failed to load scene: " + name);
                return;
            }
        }

        if (scene != null) {
            mainStage.setScene(scene);
            mainStage.setTitle(capitalize(name));
            mainStage.centerOnScreen();
        }
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    public static void main(String[] args) {
        launch();
    }
}