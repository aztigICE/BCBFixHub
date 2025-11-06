package bcbfixhub.bcbfixhub.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.geometry.Insets;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PaymentController implements Initializable {

    // --- FXML References for Toggles and Boxes ---
    @FXML
    private ToggleGroup paymentToggleGroup;
    @FXML
    private ToggleButton gcashToggle;
    @FXML
    private ToggleButton paypalToggle;
    @FXML
    private ToggleButton creditDebitToggle;
    @FXML
    private VBox gcashBox;
    @FXML
    private VBox paypalBox;
    @FXML
    private VBox creditDebitBox;

    // --- FXML References for GCash Fields ---
    @FXML
    private TextField gcashNumberField;
    @FXML
    private TextField gcashNameField;

    // --- FXML References for Paypal Fields ---
    @FXML
    private TextField paypalEmailField;

    // --- FXML References for Credit/Debit Fields ---
    @FXML
    private TextField cardNumberField;
    @FXML
    private TextField expiryDateField;
    @FXML
    private TextField cvvField;

    // --- FXML Reference for Checkout Button ---
    @FXML
    private Button checkoutButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Add listeners to toggle buttons
        paymentToggleGroup.selectedToggleProperty().addListener((observable, oldToggle, newToggle) -> {
            handlePaymentMethodChange(newToggle);
        });

        // Set default selection (optional, but good practice)
        // gcashToggle.setSelected(true);

        // Set action for the checkout button
        checkoutButton.setOnAction(event -> handleCheckout());
    }

    /**
     * Handles the logic for showing/hiding payment detail boxes
     * based on the selected toggle button.
     * @param selectedToggle The toggle button that was just selected.
     */
    private void handlePaymentMethodChange(Toggle selectedToggle) {
        // Hide all boxes first
        gcashBox.setVisible(false);
        paypalBox.setVisible(false);
        creditDebitBox.setVisible(false);

        // Show the correct box
        if (selectedToggle == gcashToggle) {
            gcashBox.setVisible(true);
        } else if (selectedToggle == paypalToggle) {
            paypalBox.setVisible(true);
        } else if (selectedToggle == creditDebitToggle) {
            creditDebitBox.setVisible(true);
        }
    }

    /**
     * Handles the checkout button click.
     * Validates inputs and shows a confirmation popup.
     */
    @FXML
    private void handleCheckout() {
        String errorMessage = validateInputs();

        if (errorMessage == null) {
            // All good, show confirmation
            showConfirmationPopup();
        } else {
            // Show error message
            showErrorPopup(errorMessage);
        }
    }

    /**
     * Validates the visible text fields based on the selected payment method.
     * @return An error message string, or null if all inputs are valid.
     */
    private String validateInputs() {
        Toggle selected = paymentToggleGroup.getSelectedToggle();

        if (selected == gcashToggle) {
            if (gcashNumberField.getText().trim().isEmpty() || gcashNameField.getText().trim().isEmpty()) {
                return "Please fill in all GCash fields.";
            }
        } else if (selected == paypalToggle) {
            if (paypalEmailField.getText().trim().isEmpty()) {
                return "Please fill in your Paypal email.";
            }
        } else if (selected == creditDebitToggle) {
            if (cardNumberField.getText().trim().isEmpty() ||
                    expiryDateField.getText().trim().isEmpty() ||
                    cvvField.getText().trim().isEmpty()) {
                return "Please fill in all credit card fields.";
            }
        } else {
            return "Please select a payment method.";
        }

        return null; // All good
    }

    /**
     * Displays a simple modal popup for error messages.
     * @param message The error message to display.
     */
    private void showErrorPopup(String message) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Validation Error");

        Label label = new Label(message);
        Button okButton = new Button("OK");
        okButton.setOnAction(e -> popupStage.close());

        VBox layout = new VBox(20, label, okButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #FFFFFF;");

        Scene scene = new Scene(layout, 300, 150);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }

    /**
     * Displays a confirmation popup and handles redirection.
     */
    private void showConfirmationPopup() {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Confirmation");

        Label label = new Label("Order Confirmed!");
        Button okButton = new Button("OK");

        okButton.setOnAction(e -> {
            // 1. Close the popup
            popupStage.close();

            // 2. Redirect to the main screen
            try {
                // Use absolute path from resources root
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/bcbfixhub/bcbfixhub/user-main-view.fxml"));
                Parent root = loader.load();

                // Get the current scene from the checkout button and set its root to the new one
                Scene scene = checkoutButton.getScene();
                scene.setRoot(root);

            } catch (IOException ioException) {
                System.err.println("Failed to load user-main-view.fxml:");
                ioException.printStackTrace();
                // Show an error popup if loading fails
                showErrorPopup("Failed to load the main screen.");
            } catch (Exception ex) {
                System.err.println("An unexpected error occurred during redirection:");
                ex.printStackTrace();
            }
        });

        VBox layout = new VBox(20, label, okButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #FFFFFF;");

        Scene scene = new Scene(layout, 300, 150);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }
}