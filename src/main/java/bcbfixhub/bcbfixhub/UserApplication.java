package bcbfixhub.bcbfixhub;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class UserApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(UserApplication.class.getResource("login.fxml"));
        Scene scene = new Scene(loader.load());
        scene.getStylesheets().add(Objects.requireNonNull(UserApplication.
                class.getResource("login.css")).toExternalForm());
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }

}
