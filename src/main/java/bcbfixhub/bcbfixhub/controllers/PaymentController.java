package bcbfixhub.bcbfixhub.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class PaymentController extends ScenesController implements Initializable {

    @FXML private ToggleButton gcashToggle;
    @FXML private ToggleButton paypalToggle;
    @FXML private ToggleButton creditDebitToggle;

    @FXML private VBox gcashBox;
    @FXML private VBox paypalBox;
    @FXML private VBox creditDebitBox;

    @FXML private TextField gcashNumberField;
    @FXML private TextField gcashNameField;
    @FXML private TextField paypalEmailField;
    @FXML private TextField cardNumberField;
    @FXML private TextField expiryDateField;
    @FXML private TextField cvvField;

    @FXML private Button checkoutButton;

    private ToggleGroup paymentToggleGroup;
    private bcbfixhub.bcbfixhub.ScenesApplication application;

    @Override
    public void setApplication(bcbfixhub.bcbfixhub.ScenesApplication application) {
        super.setApplication(application);
        this.application = application;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        paymentToggleGroup = new ToggleGroup();
        gcashToggle.setToggleGroup(paymentToggleGroup);
        paypalToggle.setToggleGroup(paymentToggleGroup);
        creditDebitToggle.setToggleGroup(paymentToggleGroup);

        paymentToggleGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            handlePaymentMethodChange(newToggle);
        });

        checkoutButton.setOnAction(event -> handleCheckout());
    }

    private void handlePaymentMethodChange(Toggle selectedToggle) {
        gcashBox.setVisible(false);
        paypalBox.setVisible(false);
        creditDebitBox.setVisible(false);

        if (selectedToggle == gcashToggle) gcashBox.setVisible(true);
        else if (selectedToggle == paypalToggle) paypalBox.setVisible(true);
        else if (selectedToggle == creditDebitToggle) creditDebitBox.setVisible(true);
    }

    private void handleCheckout() {
        String errorMessage = validateInputs();
        if (errorMessage == null) {
            // Confirm payment
            showConfirmationPopup();
            // Clear cart after payment
            if (application != null) application.getCart().clear();
        } else {
            showErrorPopup(errorMessage);
        }
    }

    private String validateInputs() {
        Toggle selected = paymentToggleGroup.getSelectedToggle();

        if (selected == gcashToggle) {
            if (gcashNumberField.getText().trim().isEmpty() || gcashNameField.getText().trim().isEmpty())
                return "Please fill in all GCash fields.";
        } else if (selected == paypalToggle) {
            if (paypalEmailField.getText().trim().isEmpty())
                return "Please fill in your Paypal email.";
        } else if (selected == creditDebitToggle) {
            if (cardNumberField.getText().trim().isEmpty() ||
                    expiryDateField.getText().trim().isEmpty() ||
                    cvvField.getText().trim().isEmpty())
                return "Please fill in all credit card fields.";
        } else return "Please select a payment method.";

        return null;
    }

    private void showErrorPopup(String message) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Validation Error");

        Label label = new Label(message);
        Button okButton = new Button("OK");
        okButton.setOnAction(e -> popup.close());

        VBox layout = new VBox(20, label, okButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #FFFFFF;");

        popup.setScene(new Scene(layout, 300, 150));
        popup.showAndWait();
    }

    private void showConfirmationPopup() {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Confirmation");

        Label label = new Label("Order Confirmed!");
        Button okButton = new Button("OK");
        okButton.setOnAction(e -> {
            popup.close();
            // Redirect to dashboard
            if (application != null) application.switchTo("user-dashboard");
            else showErrorPopup("Failed to return to the main screen.");
        });

        VBox layout = new VBox(20, label, okButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #FFFFFF;");

        popup.setScene(new Scene(layout, 300, 150));
        popup.showAndWait();
    }
}
