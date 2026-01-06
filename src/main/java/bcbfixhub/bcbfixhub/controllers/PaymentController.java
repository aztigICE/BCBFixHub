package bcbfixhub.bcbfixhub.controllers;

import bcbfixhub.bcbfixhub.BcbfixhubApplication;
import bcbfixhub.bcbfixhub.controllers.MainController.Product;
import bcbfixhub.bcbfixhub.utils.DBConnectionHelper;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.bson.Document;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class PaymentController extends BaseController implements Initializable {

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
    private BcbfixhubApplication application;

    private List<Product> cart;

    private static final double TAX_RATE = 0.08;

    @Override
    public void setApp(BcbfixhubApplication app) {
        super.setApp(app);
        this.application = app;
        loadCartSafely();
    }

    public void setCart(List<Product> cart) {
        this.cart = cart;
        loadCartSafely();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        paymentToggleGroup = new ToggleGroup();
        gcashToggle.setToggleGroup(paymentToggleGroup);
        paypalToggle.setToggleGroup(paymentToggleGroup);
        creditDebitToggle.setToggleGroup(paymentToggleGroup);

        paymentToggleGroup.selectedToggleProperty().addListener(
                (obs, oldToggle, newToggle) -> handlePaymentMethodChange(newToggle)
        );

        checkoutButton.setOnAction(e -> handleCheckoutSafely());
    }

    private void handlePaymentMethodChange(Toggle selectedToggle) {
        gcashBox.setVisible(false);
        paypalBox.setVisible(false);
        creditDebitBox.setVisible(false);

        if (selectedToggle == gcashToggle) gcashBox.setVisible(true);
        else if (selectedToggle == paypalToggle) paypalBox.setVisible(true);
        else if (selectedToggle == creditDebitToggle) creditDebitBox.setVisible(true);
    }

    private void handleCheckoutSafely() {
        String error = validateInputs();
        if (error != null) {
            showErrorPopup(error);
            return;
        }

        if (cart == null || cart.isEmpty()) {
            showErrorPopup("Your cart is empty.");
            return;
        }

        savePaymentToDatabase();
        updateProductStockSafely();

        cart.clear();
        showConfirmationPopup();
    }

    private String validateInputs() {
        Toggle selected = paymentToggleGroup.getSelectedToggle();
        if (selected == null) return "Please select a payment method.";

        if (selected == gcashToggle) {
            if (gcashNumberField.getText().isBlank() || gcashNameField.getText().isBlank())
                return "Please fill in all GCash fields.";
        } else if (selected == paypalToggle) {
            if (paypalEmailField.getText().isBlank())
                return "Please enter your PayPal email.";
        } else if (selected == creditDebitToggle) {
            if (cardNumberField.getText().isBlank()
                    || expiryDateField.getText().isBlank()
                    || cvvField.getText().isBlank())
                return "Please fill in all card fields.";
        }
        return null;
    }

    private void savePaymentToDatabase() {
        if (currentUser == null) return;

        try {
            MongoDatabase db = DBConnectionHelper.getInstance().getDatabase();
            MongoCollection<Document> collection = db.getCollection("payments");

            double subtotal = cart.stream().mapToDouble(Product::getPrice).sum();
            double tax = subtotal * TAX_RATE;
            double total = subtotal + tax;

            String date = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            Document doc = new Document()
                    .append("username", currentUser.getUsername())
                    .append("date", date)
                    .append("subtotal", subtotal)
                    .append("tax", tax)
                    .append("total", total);

            collection.insertOne(doc);
        } catch (MongoException e) {
            showErrorPopup("Failed to save payment.");
        }
    }

    private void updateProductStockSafely() {
        try {
            MongoDatabase db = DBConnectionHelper.getInstance().getDatabase();
            String[] categories = {"keyboard", "mouse", "memory", "storage", "monitor"};

            for (Product product : cart) {
                for (String category : categories) {
                    MongoCollection<Document> col = db.getCollection(category);
                    Document d = col.find(
                            new Document("brand", product.getBrand())
                                    .append("model", product.getModel())
                    ).first();

                    if (d != null) {
                        int stock = Integer.parseInt(d.getString("stock"));
                        col.updateOne(
                                new Document("_id", d.getObjectId("_id")),
                                new Document("$set",
                                        new Document("stock", String.valueOf(Math.max(stock - 1, 0))))
                        );
                        break;
                    }
                }
            }
        } catch (Exception ignored) {}
    }

    private void showErrorPopup(String message) {
        Alert a = new Alert(Alert.AlertType.ERROR, message);
        a.showAndWait();
    }

    private void showConfirmationPopup() {
        Alert a = new Alert(Alert.AlertType.INFORMATION,
                "Payment successful!\nThank you for shopping.");
        a.showAndWait();
        application.switchScene("home");
    }

    public void loadCartSafely() {
        if (cartItemsContainer == null) return;

        cartItemsContainer.getChildren().clear();
        double subtotal = 0;

        if (cart == null || cart.isEmpty()) {
            subtotalLabel.setText("PHP 0.00");
            taxLabel.setText("PHP 0.00");
            totalLabel.setText("PHP 0.00");
            return;
        }

        for (Product p : cart) {
            HBox row = new HBox(10);
            row.setPadding(new Insets(5));
            row.getChildren().addAll(
                    new Label(p.getBrand() + " " + p.getModel()),
                    new Label("PHP " + String.format("%.2f", p.getPrice()))
            );
            cartItemsContainer.getChildren().add(row);
            subtotal += p.getPrice();
        }

        double tax = subtotal * TAX_RATE;
        subtotalLabel.setText("PHP " + String.format("%.2f", subtotal));
        taxLabel.setText("PHP " + String.format("%.2f", tax));
        totalLabel.setText("PHP " + String.format("%.2f", subtotal + tax));
    }

    @FXML
    public void onBackToStore() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Cancel order?");
        Optional<ButtonType> res = alert.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.OK) {
            application.switchScene("home");
        }
    }
}
