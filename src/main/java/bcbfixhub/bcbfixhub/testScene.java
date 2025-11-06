package bcbfixhub.bcbfixhub;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class testScene extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(UserApplication.class.getResource("store-front.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }

}
