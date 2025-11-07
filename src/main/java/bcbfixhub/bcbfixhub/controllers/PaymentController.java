package bcbfixhub.bcbfixhub.controllers;

import bcbfixhub.bcbfixhub.ScenesApplication;
import bcbfixhub.bcbfixhub.controllers.MainController.Product;
import bcbfixhub.bcbfixhub.utils.MongoDBConnectionManager; // MongoDB utility
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
    private static final String PAYMENT_DB = "Payment-Details"; // fixed database name

    // uses scenesapplication and initializes the shopping cart
    @Override
    public void setApplication(ScenesApplication application) {
        super.setApplication(application);
        this.application = application;

        if (cartItemsContainer != null) loadCart();
    }

    // the payment options
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

    // which payment method to use
    private void handlePaymentMethodChange(Toggle selectedToggle) {
        gcashBox.setVisible(false);
        paypalBox.setVisible(false);
        creditDebitBox.setVisible(false);

        if (selectedToggle == gcashToggle) gcashBox.setVisible(true);
        else if (selectedToggle == paypalToggle) paypalBox.setVisible(true);
        else if (selectedToggle == creditDebitToggle) creditDebitBox.setVisible(true);
    }

    // checkout button
    private void handleCheckout() {
        String errorMessage = validateInputs();
        if (errorMessage != null) {
            showErrorPopup(errorMessage);
            return;
        }

        savePaymentToDatabase();

        if (application != null) application.getCart().clear();
        loadCart();

        showConfirmationPopup();
    }

    // check if the inputs are empty
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

    // saves the payment information to mongoDB
    private void savePaymentToDatabase() {
        if (application == null || application.getLoggedInUser() == null) return;

        MongoDatabase db = MongoDBConnectionManager.getDatabase(PAYMENT_DB);
        MongoCollection<Document> collection = db.getCollection("payments");

        double subtotal = application.getCart().stream().mapToDouble(Product::getPrice).sum();
        double tax = subtotal * TAX_RATE;
        double total = subtotal + tax;

        // whats inside the document
        Document paymentDoc = new Document()
                .append("username", application.getLoggedInUser().getEmail())
                .append("subtotal", subtotal)
                .append("tax", tax)
                .append("total", total);

        // whats items were ordered
        for (Product product : application.getCart()) {
            Document item = new Document()
                    .append("brand", product.getBrand())
                    .append("model", product.getModel())
                    .append("price", product.getPrice());
            paymentDoc.append("items", item);
        }

        // inserts the document inside the collection payment-details
        collection.insertOne(paymentDoc);
        System.out.println("Payment saved to MongoDB for user: " + application.getLoggedInUser().getEmail());
    }

    // scene
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

    // shows a popup for if the order was confirmed
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

    // gets the cart from the previous application and all the products inside and prints in the box
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
