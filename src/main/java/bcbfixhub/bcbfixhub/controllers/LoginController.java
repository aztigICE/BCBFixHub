package bcbfixhub.bcbfixhub.controllers;

import bcbfixhub.bcbfixhub.models.User;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController extends BaseController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    private final User user = new User();

    @FXML
    public void initialize() {
        // No property bindings — User does not support them
    }

    @FXML
    protected void onLogin() {
        String enteredEmail = emailField.getText();
        String enteredPassword = passwordField.getText();

        // Basic validation
        boolean isAuthenticated =
                enteredEmail != null && !enteredEmail.isBlank()
                        && enteredPassword != null && !enteredPassword.isBlank();

        if (isAuthenticated) {
            user.setEmail(enteredEmail);
            user.setPassword(enteredPassword);

            // Store user in BaseController
            setCurrentUser(user);

            showAlert(
                    Alert.AlertType.INFORMATION,
                    "Login Success",
                    "Welcome, " + enteredEmail + "."
            );
        } else {
            showAlert(
                    Alert.AlertType.ERROR,
                    "Login Failed",
                    "Invalid credentials."
            );
        }

        clearFields();
    }

    @FXML
    protected void onRegister() {
        showAlert(
                Alert.AlertType.INFORMATION,
                "Register",
                "Registration screen not implemented yet."
        );
    }

    @FXML
    protected void onHome() {
        showAlert(
                Alert.AlertType.INFORMATION,
                "Home",
                "Home navigation not implemented."
        );
    }

    private void clearFields() {
        emailField.clear();
        passwordField.clear();
    }
}
