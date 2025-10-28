package bcbfixhub.bcbfixhub.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.net.URL;
import java.util.ResourceBundle;

public class DBprinterController implements Initializable {

    // These @FXML fields are linked to the elements in your FXML file
    @FXML
    private TabPane tabPane;
    @FXML
    private Tab tabOne;
    @FXML
    private Tab tabTwo;

    // You can also add controllers for elements inside the tabs
    // @FXML
    // private Label hiLabel;
    // @FXML
    // private Label helloLabel;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // This method is called by the FXMLLoader after the FXML has been loaded.
        // You can add initialization logic here.
        System.out.println("Controller initialized. The UI is ready.");
    }
}
