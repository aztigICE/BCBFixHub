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
        FXMLLoader loader = new FXMLLoader(testScene.class.getResource("/bcbfixhub/bcbfixhub/payment-view.fxml")); // Scene you want to test.
        Scene scene = new Scene(loader.load());

        stage.setScene(scene);
        stage.show();

    }

}
