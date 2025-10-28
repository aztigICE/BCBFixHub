package bcbfixhub.bcbfixhub.controllers;
import bcbfixhub.bcbfixhub.UserApplication;
import bcbfixhub.bcbfixhub.models.User;
import bcbfixhub.bcbfixhub.models.UserDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class UserController {

    @FXML
    private TextField emailField;

    @FXML
    private Label emailLabel;

    @FXML
    private PasswordField passwordField;

    private User user = new User();
    private UserDAO userDAO = new UserDAO();

    private Alert alert;

    public void initialize() {
        this.emailField.textProperty().bindBidirectional(user.emailProperty());
        this.passwordField.textProperty().bindBidirectional(user.passwordProperty());
        this.alert = new Alert(Alert.AlertType.NONE);
        this.alert.setTitle("Authentication");
    }

    @FXML
    protected void onLogin() {
        boolean isAuthenticated = userDAO.authenticate(user.getEmail(), user.getPassword());

        if (isAuthenticated) {
            this.alert.setAlertType(Alert.AlertType.INFORMATION);
            this.alert.setHeaderText("Login Success");

            // ✅ Display the logged-in email in the label
            emailLabel.setText("Logged in as: " + user.getEmail());
        } else {
            this.alert.setAlertType(Alert.AlertType.ERROR);
            this.alert.setHeaderText("Login Failed");
        }
        alert.showAndWait();

        this.user.setEmail("");
        this.user.setPassword("");
    }

    @FXML
    //register button
    protected void onRegister(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(UserApplication.class.getResource("register.fxml"));
        Scene scene = new Scene(loader.load());
        scene.getStylesheets().add(Objects.requireNonNull(UserApplication.
                class.getResource("login.css")).toExternalForm());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Register");
        stage.setScene(scene);
        stage.show();
    }


    @FXML
    protected void onClose(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage window = (Stage) source.getScene().getWindow();
        window.close();
    }
}
