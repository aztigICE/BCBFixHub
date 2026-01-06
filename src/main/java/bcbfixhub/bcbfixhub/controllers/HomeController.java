package bcbfixhub.bcbfixhub.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class HomeController extends BaseController {

    @FXML
    private Button exitButton;

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
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }
}
