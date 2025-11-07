package bcbfixhub.bcbfixhub.controllers;

import bcbfixhub.bcbfixhub.ScenesApplication;
import bcbfixhub.bcbfixhub.controllers.MainController.Product;
import bcbfixhub.bcbfixhub.utils.MongoDBConnectionManager;
import com.mongodb.MongoException;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
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
    @FXML private Button backToStore;

    @FXML private VBox cartItemsContainer;
    @FXML private Label subtotalLabel;
    @FXML private Label taxLabel;
    @FXML private Label totalLabel;

    private ToggleGroup paymentToggleGroup;
    private ScenesApplication application;

    private static final double TAX_RATE = 0.08;
    private static final String PAYMENT_DB = "Payment-Details";

    @Override
    public void setApplication(ScenesApplication application) {
        super.setApplication(application);
        this.application = application;

        if (cartItemsContainer != null) loadCartSafely();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        paymentToggleGroup = new ToggleGroup();
        gcashToggle.setToggleGroup(paymentToggleGroup);
        paypalToggle.setToggleGroup(paymentToggleGroup);
        creditDebitToggle.setToggleGroup(paymentToggleGroup);

        // Toggle listener
        paymentToggleGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) ->
                handlePaymentMethodChange(newToggle));

        checkoutButton.setOnAction(event -> handleCheckoutSafely());
    }

    /* -------------------- PAYMENT METHOD VISIBILITY -------------------- */
    private void handlePaymentMethodChange(Toggle selectedToggle) {
        gcashBox.setVisible(false);
        paypalBox.setVisible(false);
        creditDebitBox.setVisible(false);

        if (selectedToggle == gcashToggle) gcashBox.setVisible(true);
        else if (selectedToggle == paypalToggle) paypalBox.setVisible(true);
        else if (selectedToggle == creditDebitToggle) creditDebitBox.setVisible(true);
    }

    /* -------------------- CHECKOUT -------------------- */
    private void handleCheckoutSafely() {
        try {
            String errorMessage = validateInputs();
            if (errorMessage != null) {
                showErrorPopup(errorMessage);
                return;
            }

            if (application == null || application.getCart().isEmpty()) {
                showErrorPopup("Your cart is empty.");
                return;
            }

            savePaymentToDatabase();
            updateProductStockSafely();

            if (application != null) {
                application.getCart().clear();

                Object controller = application.getControllerForScene("cart");
                if (controller instanceof CartController cartController) {
                    cartController.loadCart();
                }
            }

            showConfirmationPopup();
        } catch (Exception e) {
            e.printStackTrace();
            showErrorPopup("An unexpected error occurred during checkout.");
        }
    }

    /* -------------------- INPUT VALIDATION -------------------- */
    private String validateInputs() {
        Toggle selected = paymentToggleGroup.getSelectedToggle();

        if (selected == null) return "Please select a payment method.";

        if (selected == gcashToggle) {
            String number = gcashNumberField.getText().trim();
            String name = gcashNameField.getText().trim();
            if (number.isEmpty() || name.isEmpty()) return "Please fill in all GCash fields.";
            if (!number.matches("\\d{11,13}")) return "Enter a valid GCash number.";
        } else if (selected == paypalToggle) {
            String email = paypalEmailField.getText().trim();
            if (email.isEmpty()) return "Please enter your PayPal email.";
            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) return "Invalid email format.";
        } else if (selected == creditDebitToggle) {
            String card = cardNumberField.getText().trim();
            String expiry = expiryDateField.getText().trim();
            String cvv = cvvField.getText().trim();

            if (card.isEmpty() || expiry.isEmpty() || cvv.isEmpty())
                return "Please fill in all credit/debit card fields.";

            if (!card.matches("\\d{13,19}")) return "Invalid card number.";
            if (!expiry.matches("^(0[1-9]|1[0-2])/\\d{2}$")) return "Invalid expiry format (MM/YY).";
            if (!cvv.matches("\\d{3,4}")) return "Invalid CVV.";
        }

        return null;
    }

    /* -------------------- DATABASE OPERATIONS -------------------- */
    private void savePaymentToDatabase() {
        if (application == null || application.getLoggedInUser() == null) return;

        try {
            MongoDatabase db = MongoDBConnectionManager.getDatabase(PAYMENT_DB);
            MongoCollection<Document> collection = db.getCollection("payments");

            double subtotal = application.getCart().stream().mapToDouble(Product::getPrice).sum();
            double tax = subtotal * TAX_RATE;
            double total = subtotal + tax;

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDate = LocalDateTime.now().format(formatter);

            Document paymentDoc = new Document()
                    .append("username", application.getLoggedInUser().getEmail())
                    .append("date", formattedDate) // ✅ add this line
                    .append("subtotal", subtotal)
                    .append("tax", tax)
                    .append("total", total);

            List<Product> cart = application.getCart();
            for (Product product : cart) {
                Document item = new Document()
                        .append("brand", product.getBrand())
                        .append("model", product.getModel())
                        .append("price", product.getPrice());
                paymentDoc.append("item_" + product.getModel(), item);
            }

            collection.insertOne(paymentDoc);
            System.out.println("[MongoDB] Payment recorded for user: " + application.getLoggedInUser().getEmail());
        } catch (MongoException e) {
            e.printStackTrace();
            showErrorPopup("Failed to save payment. Please try again later.");
        }
    }

    private void updateProductStockSafely() {
        try {
            MongoDatabase db = MongoDBConnectionManager.getDatabase("Product-Details");
            String[] categories = {"keyboard", "mouse", "memory", "storage", "monitor"};

            for (Product product : application.getCart()) {
                String brand = product.getBrand();
                String model = product.getModel();

                for (String category : categories) {
                    MongoCollection<Document> collection = db.getCollection(category);
                    Document doc = collection.find(new Document("brand", brand).append("model", model)).first();

                    if (doc != null) {
                        try {
                            int currentStock = Integer.parseInt(doc.getString("stock"));
                            int newStock = Math.max(currentStock - 1, 0);

                            collection.updateOne(
                                    new Document("brand", brand).append("model", model),
                                    new Document("$set", new Document("stock", String.valueOf(newStock)))
                            );
                            System.out.println("[Stock] Updated " + brand + " " + model + " to " + newStock);
                        } catch (NumberFormatException e) {
                            System.err.println("[Stock] Invalid stock value for " + brand + " " + model);
                        }
                        break;
                    }
                }
            }
        } catch (MongoException e) {
            System.err.println("[MongoDB] Error updating stock: " + e.getMessage());
        }
    }

    /* -------------------- POPUPS -------------------- */
    private void showErrorPopup(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Payment Error");
        alert.setHeaderText("Something went wrong");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showConfirmationPopup() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Order Confirmed");
        alert.setHeaderText(null);
        alert.setContentText("Your payment was successful!\nThank you for shopping with us.");
        alert.showAndWait();

        if (application != null) application.switchTo("user-dashboard");
    }

    /* -------------------- CART LOADING -------------------- */
    public void loadCartSafely() {
        if (cartItemsContainer == null || application == null) return;

        cartItemsContainer.getChildren().clear();
        double subtotal = 0;

        List<Product> cart = application.getCart();
        if (cart == null || cart.isEmpty()) {
            Label emptyLabel = new Label("Your cart is empty.");
            emptyLabel.setPadding(new Insets(10));
            cartItemsContainer.getChildren().add(emptyLabel);
            subtotalLabel.setText("PHP 0.00");
            taxLabel.setText("PHP 0.00");
            totalLabel.setText("PHP 0.00");
            return;
        }

        for (Product product : cart) {
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

    /* -------------------- CANCEL HANDLER -------------------- */
    public void onBackToStore() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cancel Order");
        alert.setHeaderText("Are you sure you want to cancel?");
        alert.setContentText("Your current order will not be saved.");

        ButtonType yesButton = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
        ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(yesButton, noButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == yesButton && application != null) {
            application.switchTo("user-dashboard");
        }
    }
}
