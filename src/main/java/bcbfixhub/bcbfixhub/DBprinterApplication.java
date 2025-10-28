package bcbfixhub.bcbfixhub;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class DBprinterApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException
    {
        FXMLLoader loader = new FXMLLoader(DBprinterApplication.class.getResource("test_print_database.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setTitle("Database Printer");
        stage.setScene(scene);
        stage.show();
    }
}
