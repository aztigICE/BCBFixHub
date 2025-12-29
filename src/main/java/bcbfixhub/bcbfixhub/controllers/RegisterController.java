package bcbfixhub.bcbfixhub.controllers;

import bcbfixhub.bcbfixhub.models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import net.synedra.validatorfx.Validator;

public class RegisterController extends BaseController {

    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField usernameField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private VBox register;
    @FXML private Button registerButton; // ensure this matches FXML fx:id!

    private final User user = new User();
    private final UserDAO userDAO = new UserDAO();
    private final Validator validator = new Validator();

    public void initialize() {
        // Bind text properties
        usernameField.textProperty().bindBidirectional(user.usernameProperty());
        emailField.textProperty().bindBidirectional(user.emailProperty());
        passwordField.textProperty().bindBidirectional(user.passwordProperty());
        phoneField.textProperty().bindBidirectional(user.phoneProperty());

        // ===== VALIDATION RULES =====
        validator.createCheck()
                .dependsOn("username", usernameField.textProperty())
                .withMethod(c -> {
                    String name = c.get("username");
                    if (name == null || name.trim().isEmpty()) {
                        c.error("Username is required.");
                    }
                })
                .decorates(usernameField)
                .immediate();

        validator.createCheck()
                .dependsOn("email", emailField.textProperty())
                .withMethod(c -> {
                    String email = c.get("email");
                    if (email == null || email.trim().isEmpty()) {
                        c.error("Email is required.");
                    } else if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,}$")) {
                        c.error("Invalid email format.");
                    }
                })
                .decorates(emailField)
                .immediate();

        validator.createCheck()
                .dependsOn("password", passwordField.textProperty())
                .withMethod(c -> {
                    String pass = c.get("password");
                    if (pass == null || pass.trim().isEmpty()) {
                        c.error("Password is required.");
                    } else if (pass.length() < 6) {
                        c.error("Password must be at least 6 characters.");
                    }
                })
                .decorates(passwordField)
                .immediate();

        validator.createCheck()
                .dependsOn("confirmPassword", confirmPasswordField.textProperty())
                .dependsOn("password", passwordField.textProperty())
                .withMethod(c -> {
                    String pass = c.get("password");
                    String confirm = c.get("confirmPassword");
                    if (confirm == null || confirm.trim().isEmpty()) {
                        c.error("Please confirm your password.");
                    } else if (!confirm.equals(pass)) {
                        c.error("Passwords do not match.");
                    }
                })
                .decorates(confirmPasswordField)
                .immediate();

        validator.createCheck()
                .dependsOn("phone", phoneField.textProperty())
                .withMethod(c -> {
                    String phone = c.get("phone");
                    if (phone != null && !phone.trim().isEmpty()) {
                        if (!phone.matches("^\\d{11}$")) {
                            c.error("Phone number must be 11 digits.");
                        }
                    }
                })
                .decorates(phoneField)
                .immediate();

        // disables register button when there are validation errors
        registerButton.disableProperty().bind(validator.containsErrorsProperty());
    }

    @FXML
    protected void onRegister(ActionEvent event) {
        if (validator.containsErrors()) {
            validator.validate(); // Force immediate validation messages
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
    // goes back to home screen
    @FXML
    protected void onHome(ActionEvent event) {
        app.switchTo("home");
    }
    // clears fields
    private void clearFields() {
        user.setUsername("");
        user.setEmail("");
        user.setPassword("");
        user.setPhone("");
        confirmPasswordField.clear();
    }
    // shows alerts
    private void showAlert(Alert.AlertType type, String header, String content) {
        Alert alert = new Alert(type);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}