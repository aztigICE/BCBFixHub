package bcbfixhub.bcbfixhub.controllers;

import bcbfixhub.bcbfixhub.ScenesApplication;
import bcbfixhub.bcbfixhub.controllers.MainController.Product;
import bcbfixhub.bcbfixhub.utils.MongoDBConnectionManager;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.bson.Document;

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

    @FXML private VBox cartItemsContainer;
    @FXML private Label subtotalLabel;
    @FXML private Label taxLabel;
    @FXML private Label totalLabel;

    private ToggleGroup paymentToggleGroup;
    private ScenesApplication application;

    private static final double TAX_RATE = 0.08;
    private static final String PAYMENT_DB = "payment-details";

    @Override
    public void setApplication(ScenesApplication application) {
        super.setApplication(application);
        this.application = application;

        if (cartItemsContainer != null) loadCart(); // populate cart immediately if FXML loaded
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        paymentToggleGroup = new ToggleGroup();
        gcashToggle.setToggleGroup(paymentToggleGroup);
        paypalToggle.setToggleGroup(paymentToggleGroup);
        creditDebitToggle.setToggleGroup(paymentToggleGroup);

        paymentToggleGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) ->
                handlePaymentMethodChange(newToggle));

        checkoutButton.setOnAction(event -> handleCheckout());

        if (application != null) loadCart();
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
        if (errorMessage != null) {
            showErrorPopup(errorMessage);
            return;
        }

        savePaymentToDatabase(); // save payment details to MongoDB

        if (application != null) application.getCart().clear();
        loadCart(); // refresh cart display

        showConfirmationPopup();
    }

    private String validateInputs() {
        Toggle selected = paymentToggleGroup.getSelectedToggle();

        if (selected == gcashToggle) {
            if (gcashNumberField.getText().trim().isEmpty() || gcashNameField.getText().trim().isEmpty())
                return "Please fill in all GCash fields.";
        } else if (selected == paypalToggle) {
            if (paypalEmailField.getText().trim().isEmpty())
                return "Please fill in your PayPal email.";
        } else if (selected == creditDebitToggle) {
            if (cardNumberField.getText().trim().isEmpty() ||
                    expiryDateField.getText().trim().isEmpty() ||
                    cvvField.getText().trim().isEmpty())
                return "Please fill in all credit/debit card fields.";
        } else return "Please select a payment method.";

        return null;
    }

    private void savePaymentToDatabase() {
        if (application == null || application.getLoggedInUser() == null) return;

        MongoDatabase db = MongoDBConnectionManager.getDatabase(PAYMENT_DB);
        MongoCollection<Document> collection = db.getCollection("payments");

        double subtotal = application.getCart().stream().mapToDouble(Product::getPrice).sum();
        double tax = subtotal * TAX_RATE;
        double total = subtotal + tax;

        Document paymentDoc = new Document()
                .append("username", application.getLoggedInUser().getEmail())
                .append("subtotal", subtotal)
                .append("tax", tax)
                .append("total", total);

        // Add cart items as a list of documents
        var items = application.getCart().stream().map(product -> new Document()
                        .append("brand", product.getBrand())
                        .append("model", product.getModel())
                        .append("price", product.getPrice())
                        .append("imageName", product.getImageName()))
                .toList();

        paymentDoc.append("items", items);

        collection.insertOne(paymentDoc);
        System.out.println("Payment saved to MongoDB for user: " + application.getLoggedInUser().getEmail());
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

        popup.setScene(new javafx.scene.Scene(layout, 300, 150));
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
            if (application != null) application.switchTo("user-dashboard");
        });

        VBox layout = new VBox(20, label, okButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        popup.setScene(new javafx.scene.Scene(layout, 300, 150));
        popup.showAndWait();
    }

    public void loadCart() {
        if (cartItemsContainer == null || application == null) return;

        cartItemsContainer.getChildren().clear();
        double subtotal = 0;

        if (application.getCart().isEmpty()) {
            Label emptyLabel = new Label("Your cart is empty.");
            emptyLabel.setPadding(new Insets(10));
            cartItemsContainer.getChildren().add(emptyLabel);
        }

        for (Product product : application.getCart()) {
            HBox itemBox = new HBox(10);
            itemBox.setPadding(new Insets(5));

            Label nameLabel = new Label(product.getBrand() + " " + product.getModel());
            nameLabel.setPrefWidth(200);

            Label priceLabel = new Label("PHP " + String.format("%.2f", product.getPrice()));
            priceLabel.setPrefWidth(80);

            itemBox.getChildren().addAll(nameLabel, priceLabel);
            cartItemsContainer.getChildren().add(itemBox);

            subtotal += product.getPrice();
        }

        double tax = subtotal * TAX_RATE;
        double total = subtotal + tax;

        subtotalLabel.setText("PHP " + String.format("%.2f", subtotal));
        taxLabel.setText("PHP " + String.format("%.2f", tax));
        totalLabel.setText("PHP " + String.format("%.2f", total));
    }
}
