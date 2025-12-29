package bcbfixhub.bcbfixhub.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class HomeController extends BaseController {

    @FXML
    private Button exitButton; // Need to get a reference to the stage

    @FXML
    protected void onLoginHandle() {
        app.switchScene("login");
    }

    @FXML
    protected void onRegisterHandle() {
        app.switchScene("register");
    }

    @FXML
    protected void onExit() {
        // Get the stage from the button and close it
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }
}