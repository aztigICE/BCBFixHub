package bcbfixhub.bcbfixhub.controllers;

import bcbfixhub.bcbfixhub.UserApplication;
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


    public void onProducts(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(UserApplication.class.getResource("product.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Register");
        stage.setScene(scene);
        stage.show();
    }
    public void onOrder(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(UserApplication.class.getResource("order.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Register");
        stage.setScene(scene);
        stage.show();
    }
    public void onLogin(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(UserApplication.class.getResource("login-view.fxml"));
        Scene scene = new Scene(loader.load());
        scene.getStylesheets().add(Objects.requireNonNull(UserApplication.
                class.getResource("login.css")).toExternalForm());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Register");
        stage.setScene(scene);
        stage.show();
    }
    public void onUser(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(UserApplication.class.getResource("users.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Register");
        stage.setScene(scene);
        stage.show();
    }
}
