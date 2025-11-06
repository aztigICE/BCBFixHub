package bcbfixhub.bcbfixhub.controllers;

import bcbfixhub.bcbfixhub.models.User;
import bcbfixhub.bcbfixhub.models.UserDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController extends ScenesController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    private final User user = new User();
    private final UserDAO userDAO = new UserDAO();
    private Alert alert = new Alert(Alert.AlertType.NONE);

    public void initialize() {
        this.emailField.textProperty().bindBidirectional(user.emailProperty());
        this.passwordField.textProperty().bindBidirectional(user.passwordProperty());
        this.alert.setTitle("Authentication");
    }

    @FXML
    protected void onLogin() {
        String enteredEmail = user.getEmail();
        String enteredPassword = user.getPassword();

        // Admin shortcut
        if ("admin".equals(enteredEmail) && "1234".equals(enteredPassword)) {
            showAlert(Alert.AlertType.INFORMATION, "Admin Login Success", "Welcome, admin.");
            app.switchTo("admin");
            clearFields();
            return;
        }

        // Regular authentication
        boolean isAuthenticated = userDAO.authenticate(enteredEmail, enteredPassword);

        if (isAuthenticated) {
            showAlert(Alert.AlertType.INFORMATION, "Login Success", "Welcome, " + enteredEmail + ".");
            app.switchTo("user-dashboard");
        } else {
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid credentials.");
        }
        clearFields();


    }

    @FXML
    protected void onRegister() {
        app.switchTo("register");
    }

    @FXML
    protected void onClose() {
        emailField.getScene().getWindow().hide();
    }

    private void clearFields() {
        user.setEmail("");
        user.setPassword("");
    }

    private void showAlert(Alert.AlertType type, String header, String content) {
        alert.setAlertType(type);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
