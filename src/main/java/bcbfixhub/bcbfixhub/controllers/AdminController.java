package bcbfixhub.bcbfixhub.controllers;

import bcbfixhub.bcbfixhub.ScenesApplication;
import javafx.application.Application;
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

public class AdminController extends ScenesController
{
    public VBox register;

    @FXML
    public void onProducts() {
        app.switchTo("product");
    }

    @FXML
    public void onOrder(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(ScenesController.class.getResource("order.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Register");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void onLogOut() {
        app.switchTo("login");
    }

    @FXML
    public void onUser() {
        app.switchTo("users");
    }

}
