package bcbfixhub.bcbfixhub.controllers;

import bcbfixhub.bcbfixhub.UserApplication;
import bcbfixhub.bcbfixhub.models.User;
import bcbfixhub.bcbfixhub.models.UserDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
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
        String enteredEmail = user.getEmail();
        String enteredPassword = user.getPassword();

        // Hardcoded admin credentials
        if ("admin".equals(enteredEmail) && "1234".equals(enteredPassword)) {
            try {
                FXMLLoader loader = new FXMLLoader(UserApplication.class.getResource("admin.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root);

                scene.getStylesheets().add(Objects.requireNonNull(UserApplication.class.getResource("login.css")).toExternalForm());
                Stage stage = (Stage) emailField.getScene().getWindow();
                stage.setTitle("Admin Dashboard");
                stage.setScene(scene);
                stage.show();

                // Informational alert (optional)
                this.alert.setAlertType(Alert.AlertType.INFORMATION);
                this.alert.setHeaderText("Admin Login Success");
                this.alert.setContentText("Welcome, admin.");
                alert.showAndWait();
            } catch (IOException e) {
                this.alert.setAlertType(Alert.AlertType.ERROR);
                this.alert.setHeaderText("Error");
                this.alert.setContentText("Unable to load admin dashboard: " + e.getMessage());
                alert.showAndWait();
            } finally {
                this.user.setEmail("");
                this.user.setPassword("");
            }
            return;
        }

        boolean isAuthenticated = userDAO.authenticate(enteredEmail, enteredPassword);

        if (isAuthenticated) {
            this.alert.setAlertType(Alert.AlertType.INFORMATION);
            this.alert.setHeaderText("Login Success");
            emailLabel.setText("Logged in as: " + enteredEmail);
        } else {
            this.alert.setAlertType(Alert.AlertType.ERROR);
            this.alert.setHeaderText("Login Failed");
            this.alert.setContentText("Invalid credentials.");
        }
        alert.showAndWait();

        this.user.setEmail("");
        this.user.setPassword("");
    }

    @FXML
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
