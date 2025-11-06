package bcbfixhub.bcbfixhub.controllers;

import bcbfixhub.bcbfixhub.models.User;
import bcbfixhub.bcbfixhub.models.UserDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class RegisterController extends ScenesController {

    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField usernameField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private VBox register;

    private final User user = new User();
    private final UserDAO userDAO = new UserDAO();

    public void initialize() {
        usernameField.textProperty().bindBidirectional(user.usernameProperty());
        emailField.textProperty().bindBidirectional(user.emailProperty());
        passwordField.textProperty().bindBidirectional(user.passwordProperty());
        phoneField.textProperty().bindBidirectional(user.phoneProperty());
    }

    @FXML
    protected void onRegister(ActionEvent event) {
        String confirmPassword = confirmPasswordField.getText();

        if (user.getUsername().isEmpty() || user.getEmail().isEmpty() ||
                user.getPassword().isEmpty() || confirmPassword.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing Fields", "Please fill in all required fields.");
            return;
        }

        if (!user.getPassword().equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Password Mismatch", "Passwords do not match.");
            return;
        }

        boolean created = userDAO.createUser(
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.getPhone()
        );

        if (created) {
            showAlert(Alert.AlertType.INFORMATION, "Registration Success", "Account created successfully!");
            app.switchTo("login");
            clearFields();
        } else {
            showAlert(Alert.AlertType.ERROR, "Registration Failed", "Email already exists.");
        }
    }

    @FXML
    protected void onLogin(ActionEvent event) {
        app.switchTo("login");
    }

    private void clearFields() {
        user.setUsername("");
        user.setEmail("");
        user.setPassword("");
        user.setPhone("");
        confirmPasswordField.clear();
    }

    private void showAlert(Alert.AlertType type, String header, String content) {
        Alert alert = new Alert(type);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
