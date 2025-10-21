package bcbfixhub.bcbfixhub.controllers;

import bcbfixhub.bcbfixhub.models.User;
import bcbfixhub.bcbfixhub.models.UserDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


public class UserController {
    @FXML
    private TextField emailField;

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
        } else {
            this.alert.setAlertType(Alert.AlertType.ERROR);
            this.alert.setHeaderText("Login Failed");
        }
        alert.showAndWait();

        this.user.setEmail("");
        this.user.setPassword("");
    }

    @FXML
    protected void onRegister() {
        boolean success = userDAO.createUser(user.getEmail(), user.getPassword());

        if (success) {
            alert.setAlertType(Alert.AlertType.INFORMATION);
            alert.setHeaderText("User Registered Successfully!");
        } else {
            alert.setAlertType(Alert.AlertType.WARNING);
            alert.setHeaderText("Email already exists.");
        }

        alert.showAndWait();
        user.setEmail("");
        user.setPassword("");
    }


    @FXML
    protected void onClose(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage window = (Stage) source.getScene().getWindow();
        window.close();
    }
}
