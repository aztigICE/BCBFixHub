package bcbfixhub.bcbfixhub;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class testScene extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(testScene.class.getResource("/bcbfixhub/bcbfixhub/payment.fxml")); // Scene you want to test.
        Scene scene = new Scene(loader.load());
        scene.getStylesheets().add(Objects.requireNonNull(testScene.class.getResource("/bcbfixhub/bcbfixhub/css/payment.css")).toExternalForm()); // css style sheet used
        stage.setTitle("test scene");
        stage.setScene(scene);
        stage.show();

    }

}
