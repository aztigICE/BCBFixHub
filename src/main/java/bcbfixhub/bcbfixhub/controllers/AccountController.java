package bcbfixhub.bcbfixhub.controllers;

import bcbfixhub.bcbfixhub.BcbfixhubApplication;
import bcbfixhub.bcbfixhub.controllers.MainController.Product;
import bcbfixhub.bcbfixhub.utils.DBConnectionHelper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import org.bson.Document;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

public class AccountController extends ScenesController implements Initializable {

    @FXML private VBox ordersContainer;
    @FXML private VBox cartItemsContainer;
    @FXML private Label emptyCartLabel;
    @FXML private Button checkoutButton;
    @FXML private ScrollPane cartScrollPane;
    @FXML private Button storeButton;
    @FXML private Button logoutButton;

    private BcbfixhubApplication application;
    private static final String PAYMENT_DB = "Payment-Details";

    @Override
    public void setApplication(BcbfixhubApplication application) {
        super.setApplication(application);
        this.application = application;

        // Ensure these load AFTER the app reference is set
        javafx.application.Platform.runLater(() -> {
            loadOrdersFromDB();
            populateCart();
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Leave empty — we now load data in setApplication() after app is known
    }

    /** Load order history from MongoDB for the logged-in user. */
    private void loadOrdersFromDB() {
        ordersContainer.getChildren().clear();

        if (application == null || application.getLoggedInUser() == null) {
            Label msg = new Label("Please log in to view order history.");
            msg.setPadding(new Insets(10));
            ordersContainer.getChildren().add(msg);
            return;
        }

        MongoDatabase db = DBConnectionHelper.getDatabase(PAYMENT_DB);
        MongoCollection<Document> collection = db.getCollection("payments");

        String userEmail = application.getLoggedInUser().getEmail();
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
            orderCard.setStyle("-fx-background-color: #fefcf6; -fx-border-color: #d9c9a3; -fx-background-radius: 8; -fx-border-radius: 8;");
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

    /** Populate cart from shared cart list in BcbfixhubApplication */
    public void populateCart() {
        cartItemsContainer.getChildren().clear();

        if (application == null || application.getCart().isEmpty()) {
            emptyCartLabel.setVisible(true);
            cartScrollPane.setVisible(false);
            checkoutButton.setVisible(false);
            return;
        }

        emptyCartLabel.setVisible(false);
        cartScrollPane.setVisible(true);
        checkoutButton.setVisible(true);

        for (Product p : application.getCart()) {
            HBox row = new HBox(15);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(10, 10, 10, 10));

            Label name = new Label(p.getBrand() + " " + p.getModel());
            name.setFont(new Font("System Bold", 13));

            Label price = new Label("₱" + String.format("%.2f", p.getPrice()));
            HBox.setHgrow(name, Priority.ALWAYS);

            Button remove = new Button("X");
            remove.setStyle("-fx-background-color: #ff6666; -fx-text-fill: white;");
            remove.setOnAction(e -> {
                application.getCart().remove(p);
                populateCart();
            });

            row.getChildren().addAll(name, price, remove);
            cartItemsContainer.getChildren().add(row);
        }
    }

    @FXML
    private void handleGoToStore() {
        if (application != null) application.switchTo("user-dashboard");
    }

    @FXML
    private void handleLogout() {
        if (application != null) {
            application.setLoggedInUser(null);
            application.switchTo("login");
        }
    }

    @FXML
    private void handleGoToCheckout() {
        if (application != null) application.switchTo("payment");
    }
}
