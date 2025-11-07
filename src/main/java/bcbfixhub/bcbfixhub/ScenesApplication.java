package bcbfixhub.bcbfixhub;

import bcbfixhub.bcbfixhub.controllers.CartController;
import bcbfixhub.bcbfixhub.controllers.ScenesController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScenesApplication extends Application {

    private Stage mainStage;
    private final Map<String, Scene> scenes = new HashMap<>();
    private final List<Object> cart = new ArrayList<>(); // shared cart

    public List<Object> getCart() {
        return cart;
    }

    @Override
    public void start(Stage stage) throws IOException {
        this.mainStage = stage;

        addScene("home", "home-view.fxml", 450, 550);
        addScene("login", "login-view.fxml", 450, 550);
        addScene("register", "register-view.fxml", 450, 550);
        addScene("admin", "admin-view.fxml", 400, 300);
        addScene("user-dashboard", "user-main-view.fxml", 1000, 700);
        addScene("product", "product-view.fxml", 1200, 600);
        addScene("cart", "cart-view.fxml", 900, 600);
        addScene("payment", "payment-view.fxml", 950, 650);
        addScene("account", "account-view.fxml", 1000, 700);

        switchTo("home");
        stage.show();
        stage.centerOnScreen();
    }

    private void addScene(String name, String fxml, int width, int height) throws IOException {
        URL fxmlUrl = getClass().getResource("/bcbfixhub/bcbfixhub/" + fxml);
        if (fxmlUrl == null) fxmlUrl = getClass().getResource("/" + fxml);
        if (fxmlUrl == null) throw new IOException("FXML not found: " + fxml);

        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Scene scene = new Scene(loader.load(), width, height);
        scene.setUserData(loader); // store loader for later controller access

        // Set controller link
        Object controller = loader.getController();
        if (controller instanceof ScenesController sc) {
            sc.setApplication(this);
        }

        scenes.put(name, scene);
    }

    public void switchTo(String name) {
        Scene scene = scenes.get(name);
        if (scene == null) {
            try {
                addScene(name, name + "-view.fxml", 800, 600);
                scene = scenes.get(name);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        mainStage.setScene(scene);
        mainStage.setTitle(capitalize(name));
        mainStage.centerOnScreen();

        // Call loadCart() if cart scene
        Object controller = getControllerForScene(scene);
        if ("cart".equals(name) && controller instanceof CartController cartController) {
            cartController.loadCart();
        }
    }

    private Object getControllerForScene(Scene scene) {
        if (scene.getUserData() instanceof FXMLLoader loader) {
            return loader.getController();
        }
        return null;
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    public static void main(String[] args) {
        launch();
    }
}
