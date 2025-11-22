package bcbfixhub.bcbfixhub;

import bcbfixhub.bcbfixhub.controllers.*;
import bcbfixhub.bcbfixhub.models.User;
import bcbfixhub.bcbfixhub.controllers.MainController.Product;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * The type Bcbfixhub application. Class responsible for switching our scenes
 */
public class BcbfixhubApplication extends Application {

        private Stage primaryStage;
        private final Map<String, Scene> sceneMap = new HashMap<>(); // Use hashmap instead of declaring each scene
        private User currentUser; // For when the user is logged in ,and we want to store who that is.

    /**
     * Sets current user.
     *
     * @param user the user
     */
    public void setCurrentUser(User user) {
            this.currentUser = user;
        }

    /**
     * Gets current user.
     *
     * @return the current user
     */
    public User getCurrentUser() {
            return this.currentUser;
        }

        @Override
        public void start(Stage primaryStage) throws IOException {
            this.primaryStage = primaryStage;

            // Preload each scene
            preloadScene("home", "home-view.fxml");
            preloadScene("login", "login-view.fxml");

            switchScene("home");

            primaryStage.setTitle("BCB FixHub - Computer Parts Shop!");
            primaryStage.setResizable(false);
            primaryStage.show();
        }

        /**
         * Preloads an FXML scene, stores it in the scenes map, and injects the application reference into the controller.
         *
         * @param name The logical name for the scene (e.g., "login", "admin-menu").
         * @param fxmlFile The filename of the FXML resource (e.g., "login-view.fxml").
         * @throws IOException if the FXML file cannot be found or loaded.
         */
        private void preloadScene(String name, String fxmlFile) throws IOException {
            // Streamline resource loading: try full path first, then root path as fallback
            String fullPath = "/bcbfixhub/bcbfixhub/" + fxmlFile;
            URL fxmlUrl = getClass().getResource(fullPath);

            if (fxmlUrl == null) {
                fxmlUrl = getClass().getResource("/" + fxmlFile);
            }

            if (fxmlUrl == null) {
                throw new IOException("FXML not found: " + fxmlFile + " at path: " + fullPath);
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            // FXMLLoader handles resource closing internally, no need for explicit try-with-resources here.
            Scene scene = new Scene(loader.load());

            // Store the loader in the scene's UserData for later retrieval of the controller
            scene.setUserData(loader);

            Object controller = loader.getController();
            // Inject the main application instance into the controller if it extends BaseController
            if (controller instanceof BaseController baseController) {
                baseController.setApp(this);
            }

            sceneMap.put(name, scene);
        }

    /**
     * Gets controller.
     *
     * @param sceneName the scene name
     * @return the controller
     */
    public Object getController(String sceneName) {
        Scene scene = sceneMap.get(sceneName);
        if (scene == null) return null;
        FXMLLoader loader = (FXMLLoader) scene.getUserData();
        return loader.getController();
    }

    /**
     * Switches the primary stage to a different scene and configures the controller.
     *
     * @param name         The logical name of the scene to switch to.
     * @param loggedInUser The user currently logged in (can be null for login/register).
     */
    public void switchScene(String name, User loggedInUser) {
        Scene scene = sceneMap.get(name);
        if (scene == null) return;

        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.centerOnScreen();

        // Retrieve the controller instance from the scene's user data
        FXMLLoader loader = (FXMLLoader) scene.getUserData();
        Object controller = loader.getController();

        // Configure the controller with the current user and call lifecycle hook
        if (controller instanceof BaseController baseController) {
            baseController.setCurrentUser(loggedInUser);
            // Calls a lifecycle method when the scene is actually displayed
            baseController.onSceneShown();
        }

        // Logging statement improved for clarity
        if (loggedInUser != null) {
            System.out.println("Switching to scene: " + name + " for user " + loggedInUser.getUsername());
        } else {
            System.out.println("Switching to scene: " + name + " (no user logged in)");
        }
    }

    /**
     * Overload to switch scenes using the currently stored 'currentUser'.
     *
     * @param name the name
     */
    public void switchScene(String name) {
        switchScene(name, currentUser);
    }

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        launch();
    }
}
