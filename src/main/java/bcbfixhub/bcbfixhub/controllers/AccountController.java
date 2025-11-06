package bcbfixhub.bcbfixhub.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos; // ADDED
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane; // ADDED
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.net.URL;
import java.util.ResourceBundle;

public class AccountController extends ScenesController implements Initializable {

    @FXML
    private VBox ordersContainer;
    @FXML
    private Button storeButton;
    @FXML
    private Button logoutButton;

    // --- ADDED: FXML references for Cart Panel ---
    @FXML
    private VBox cartItemsContainer;
    @FXML
    private Label emptyCartLabel;
    @FXML
    private Button checkoutButton;
    @FXML
    private ScrollPane cartScrollPane; // Added for correct layout

    // A field to hold the application instance
    private bcbfixhub.bcbfixhub.ScenesApplication application;

    // Override the setApplication method to capture the instance
    @Override
    public void setApplication(bcbfixhub.bcbfixhub.ScenesApplication application) {
        super.setApplication(application); // Calls parent's method
        this.application = application; // Saves the instance locally
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Populate placeholder orders
        populateOrders();

        // Populate placeholder cart
        populateCart();
    }

    /**
     * Populates the Order History panel.
     */
    private void populateOrders() {
        ordersContainer.getChildren().add(
                createOrderCard(
                        "Order #19283",
                        "2025-11-05",
                        "$31.00",
                        "Adobe Photoshop, Adobe Illustrator"
                )
        );
        ordersContainer.getChildren().add(
                createOrderCard(
                        "Order #19201",
                        "2025-10-21",
                        "$99.00",
                        "Microsoft Office"
                )
        );
        ordersContainer.getChildren().add(
                createOrderCard(
                        "Order #19155",
                        "2025-10-15",
                        "Free",
                        "Visual Studio Code"
                )
        );
    }

    /**
     * ADDED: Populates the Current Cart panel.
     */
    private void populateCart() {
        // Set to true to test the "empty" state
        boolean cartIsEmpty = false;

        // Toggle visibility based on cart state
        emptyCartLabel.setVisible(cartIsEmpty);
        emptyCartLabel.setManaged(cartIsEmpty);
        checkoutButton.setVisible(!cartIsEmpty);
        checkoutButton.setManaged(!cartIsEmpty);
        cartScrollPane.setVisible(!cartIsEmpty);
        cartScrollPane.setManaged(!cartIsEmpty);

        if (!cartIsEmpty) {
            // Add the same placeholder items from the cart screen
            cartItemsContainer.getChildren().add(
                    createCartItemNode("Adobe Photoshop", "$11.00")
            );
            cartItemsContainer.getChildren().add(
                    createCartItemNode("Adobe Illustrator", "$20.00")
            );
        }
    }

    /**
     * ADDED: Handles switching to the Store/Dashboard view.
     */
    @FXML
    private void handleGoToStore() {
        System.out.println("Store button clicked.");
        application.switchTo("user-dashboard");
    }

    /**
     * ADDED: Handles logging the user out (placeholder).
     */
    @FXML
    private void handleLogout() {
        // In a real app, you would clear the user session/token here
        System.out.println("Logout button clicked.");
        application.switchTo("login");
    }

    /**
     * ADDED: Handles switching to the payment screen.
     */
    @FXML
    private void handleGoToCheckout() {
        System.out.println("Checkout button on account page clicked.");
        application.switchTo("payment");
    }

    /**
     * Helper method to create a visual node for a placeholder order.
     */
    private VBox createOrderCard(String orderId, String date, String total, String items) {
        // ... (This method is unchanged from your file) ...
        VBox card = new VBox(10);
        card.getStyleClass().add("order-card");
        card.setPadding(new Insets(15));

        HBox header = new HBox();
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label orderIdLabel = new Label(orderId);
        orderIdLabel.getStyleClass().add("order-id");

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        Label dateLabel = new Label("Date: " + date);
        dateLabel.getStyleClass().add("order-date");
        header.getChildren().addAll(orderIdLabel, spacer, dateLabel);

        VBox details = new VBox(5);
        Label itemsLabel = new Label("Items: " + items);
        itemsLabel.getStyleClass().add("order-items");

        Label totalLabel = new Label("Total: " + total);
        totalLabel.getStyleClass().add("order-total");

        details.getChildren().addAll(itemsLabel, totalLabel);

        card.getChildren().addAll(header, details);
        return card;
    }

    /**
     * ADDED: Copied from CartController to create cart item nodes.
     */
    private HBox createCartItemNode(String productName, String price) {
        HBox itemBox = new HBox(20);
        itemBox.setAlignment(Pos.CENTER_LEFT);
        itemBox.setPadding(new Insets(10));
        itemBox.setStyle("-fx-border-color: #E0E0E0; -fx-border-width: 0 0 1 0;"); // Bottom border

        Label nameLabel = new Label(productName);
        nameLabel.setFont(new Font("System Bold", 14)); // Slightly smaller font

        Label priceLabel = new Label(price);
        priceLabel.setFont(new Font("System", 14)); // Slightly smaller font

        Button removeButton = new Button("X"); // Smaller remove button
        removeButton.setStyle("-fx-background-color: #FF6B6B; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 10px;");
        // You would add an action to this button

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        itemBox.getChildren().addAll(nameLabel, spacer, priceLabel, removeButton);
        return itemBox;
    }
}