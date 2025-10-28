package bcbfixhub.bcbfixhub.controllers;

import bcbfixhub.bcbfixhub.UserApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class RegisterController {
    public PasswordField confirmPasswordField;
    public TextField usernameField;
    public TextField phoneField;
    public TextField emailField;
    public TextField passwordField;
    public VBox register;

    public void onRegister(ActionEvent actionEvent) {

    }

    public void onLogin(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(UserApplication.class.getResource("login.fxml"));
        Scene scene = new Scene(loader.load());
        scene.getStylesheets().add(Objects.requireNonNull(UserApplication.
                class.getResource("login.css")).toExternalForm());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Register");
        stage.setScene(scene);
        stage.show();
    }
}
