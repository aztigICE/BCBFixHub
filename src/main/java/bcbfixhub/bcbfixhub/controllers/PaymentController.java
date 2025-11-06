package bcbfixhub.bcbfixhub.controllers;

import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Toggle;
import javafx.scene.control.Button;
import javafx.scene.control.Label; // Import Label
import javafx.scene.layout.VBox;
import javafx.stage.Modality; // Import Modality
import javafx.stage.Stage; // Import Stage

import java.net.URL;
import java.util.ResourceBundle;

public class PaymentController implements Initializable {

    // Removed ChoiceBox
    // @FXML private ChoiceBox<String> PaymentChoiceBox;

    // FXML references for the payment method boxes
    @FXML
    private VBox gcashBox;
    @FXML
    private VBox paypalBox;
    @FXML
    private VBox creditDebitBox;

    // FXML references for the new ToggleButtons
    @FXML
    private ToggleGroup paymentToggleGroup;
    @FXML
    private ToggleButton gcashToggle;
    @FXML
    private ToggleButton paypalToggle;
    @FXML
    private ToggleButton creditDebitToggle;

    // FXML references for the text fields
    @FXML private TextField gcashNumberField;
    @FXML private TextField gcashNameField;
    @FXML private TextField paypalEmailField;
    @FXML private TextField cardNumberField;
    @FXML private TextField expiryDateField;
    @FXML private TextField cvvField;

    // FXML reference for the button
    @FXML private Button checkoutButton;


    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Ensure all boxes are hidden initially
        if (gcashBox != null) gcashBox.setVisible(false);
        if (paypalBox != null) paypalBox.setVisible(false);
        if (creditDebitBox != null) creditDebitBox.setVisible(false);

        // Add a listener to the ToggleGroup to show/hide payment fields
        if (paymentToggleGroup != null) {
            paymentToggleGroup.selectedToggleProperty().addListener(
                    (observable, oldToggle, newToggle) -> handlePaymentOptionChange(newToggle)
            );
        }

        // You can add an action for the checkout button here
        if (checkoutButton != null) {
            checkoutButton.setOnAction(event -> handleCheckout());
        }
    }

    /**
     * Shows the correct VBox based on the payment option selected and hides the others.
     * @param selectedToggle The Toggle that was selected in the ToggleGroup.
     */
    private void handlePaymentOptionChange(Toggle selectedToggle) {
        // Hide all payment boxes first
        if (gcashBox != null) gcashBox.setVisible(false);
        if (paypalBox != null) paypalBox.setVisible(false);
        if (creditDebitBox != null) creditDebitBox.setVisible(false);

        // Show the relevant box
        if (selectedToggle == gcashToggle) {
            if (gcashBox != null) gcashBox.setVisible(true);
        } else if (selectedToggle == paypalToggle) {
            if (paypalBox != null) paypalBox.setVisible(true);
        } else if (selectedToggle == creditDebitToggle) {
            if (creditDebitBox != null) creditDebitBox.setVisible(true);
        }
    }

    /**
     * Handles the checkout button click.
     * Validates input and shows confirmation or error.
     */
    private void handleCheckout() {
        String validationError = validateInputs();

        if (validationError == null) {
            // No errors, proceed to payment logic
            System.out.println("Validation successful. Processing payment...");

            // Show confirmation popup
            showConfirmationPopup();

            // Close the payment window
            Stage stage = (Stage) checkoutButton.getScene().getWindow();
            stage.close();

        } else {
            // Show validation error
            showErrorPopup(validationError);
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
     * Creates and displays a modal error popup.
     * @param message The error message to display.
     */
    private void showErrorPopup(String message) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Validation Error");

        Label label = new Label(message);
        Button okButton = new Button("OK");
        okButton.setOnAction(e -> popupStage.close());

        VBox vbox = new VBox(label, okButton);
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(15);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: #FFFFFF;"); // Match light theme

        Scene scene = new Scene(vbox, 300, 150);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }

    /**
     * Creates and displays a modal confirmation popup.
     */
    private void showConfirmationPopup() {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Success");

        Label label = new Label("Order Confirmed!");
        label.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Button okButton = new Button("OK");
        okButton.setOnAction(e -> popupStage.close());

        VBox vbox = new VBox(label, okButton);
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(15);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: #FFFFFF;");

        Scene scene = new Scene(vbox, 300, 150);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }
}