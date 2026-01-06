package bcbfixhub.bcbfixhub.controllers;

import bcbfixhub.bcbfixhub.utils.DBConnectionHelper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import org.bson.Document;

import java.text.DecimalFormat;
import java.util.List;

import bcbfixhub.bcbfixhub.controllers.MainController.Product;

public class AccountController extends BaseController {

    @FXML private VBox ordersContainer;
    @FXML private VBox cartItemsContainer;
    @FXML private Label emptyCartLabel;
    @FXML private Button checkoutButton;
    @FXML private ScrollPane cartScrollPane;
    @FXML private Button storeButton;
    @FXML private Button logoutButton;

    private static final String PAYMENT_DB = "Payment-Details";

    // Injected state
    private String userEmail;
    private List<Product> cart;

    @FXML
    public void initialize() {
        // Scene is wired; data comes later
    }

    @Override
    public void onSceneShown() {
        loadOrdersFromDB();
        populateCart();
    }

    /* =========================
       External injection hooks
       ========================= */

    public void setUserEmail(String email) {
        this.userEmail = email;
        loadOrdersFromDB();
    }

    public void setCart(List<Product> cart) {
        this.cart = cart;
        populateCart();
    }

    /* =========================
       Orders
       ========================= */

    private void loadOrdersFromDB() {
        if (ordersContainer == null) return;

        ordersContainer.getChildren().clear();

        if (userEmail == null || userEmail.isBlank()) {
            Label msg = new Label("Please log in to view order history.");
            msg.setPadding(new Insets(10));
            ordersContainer.getChildren().add(msg);
            return;
        }

        MongoDatabase db = DBConnectionHelper.getInstance().getDatabase();
        MongoCollection<Document> collection = db.getCollection("payments");

        var payments = collection.find(new Document("username", userEmail));
        boolean hasOrders = false;

        for (Document doc : payments) {
            hasOrders = true;

            String id = doc.getObjectId("_id").toString();
            double total = doc.getDouble("total");
            Object dateObj = doc.get("date");
            String date = (dateObj != null) ? dateObj.toString() : "Unknown date";

            VBox orderCard = new VBox(5);
            orderCard.setPadding(new Insets(10));
            orderCard.setStyle(
                    "-fx-background-color: #fefcf6;" +
                            "-fx-border-color: #d9c9a3;" +
                            "-fx-background-radius: 8;" +
                            "-fx-border-radius: 8;"
            );

            orderCard.getChildren().addAll(
                    new Label("Order ID: " + id),
                    new Label("Date: " + date),
                    new Label("Total: ₱" + new DecimalFormat("#,##0.00").format(total))
            );

            ordersContainer.getChildren().add(orderCard);
        }

        if (!hasOrders) {
            Label noOrders = new Label("No orders found.");
            noOrders.setPadding(new Insets(10));
            ordersContainer.getChildren().add(noOrders);
        }
    }

    /* =========================
       Cart
       ========================= */

    private void populateCart() {
        if (cartItemsContainer == null) return;

        cartItemsContainer.getChildren().clear();

        if (cart == null || cart.isEmpty()) {
            emptyCartLabel.setVisible(true);
            cartScrollPane.setVisible(false);
            checkoutButton.setVisible(false);
            return;
        }

        emptyCartLabel.setVisible(false);
        cartScrollPane.setVisible(true);
        checkoutButton.setVisible(true);

        for (Product p : cart) {
            HBox row = new HBox(15);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(10));

            Label name = new Label(p.getBrand() + " " + p.getModel());
            name.setFont(new Font("System Bold", 13));
            HBox.setHgrow(name, Priority.ALWAYS);

            Label price = new Label("₱" + String.format("%.2f", p.getPrice()));

            Button remove = new Button("X");
            remove.setStyle("-fx-background-color: #ff6666; -fx-text-fill: white;");
            remove.setOnAction(e -> {
                cart.remove(p);
                populateCart();
            });

            row.getChildren().addAll(name, price, remove);
            cartItemsContainer.getChildren().add(row);
        }
    }

    /* =========================
       UI Actions
       ========================= */

    @FXML
    private void handleGoToStore() {
        app.switchScene("store");
    }

    @FXML
    private void handleLogout() {
        userEmail = null;
        cart = null;
        app.switchScene("login");
    }

    @FXML
    private void handleGoToCheckout() {
        app.switchScene("payment");
    }

    /* =========================
       Alert helper
       ========================= */

    protected void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
