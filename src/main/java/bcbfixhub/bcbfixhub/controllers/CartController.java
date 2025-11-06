package bcbfixhub.bcbfixhub.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font; // ADDED import

import java.net.URL;
import java.util.ResourceBundle;

public class CartController extends ScenesController implements Initializable {

    @FXML
    private Button backButton;

    @FXML
    private Button checkoutButton;

    @FXML
    private VBox cartItemsContainer;

    @FXML
    private Label emptyCartLabel;

    @FXML
    private Label subtotalLabel;

    @FXML
    private Label taxLabel;

    @FXML
    private Label totalLabel;

    // A field to hold the application instance
    private bcbfixhub.bcbfixhub.ScenesApplication application;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("CartController initialized.");

        // --- MODIFICATION: Add placeholder items ---

        // 1. Hide the "empty cart" label
        emptyCartLabel.setVisible(false);
        emptyCartLabel.setManaged(false); // Doesn't take up space

        // 2. Add placeholder items (matching the payment screen)
        cartItemsContainer.getChildren().add(
                createCartItemNode("Adobe Photoshop", "$11.00")
        );
        cartItemsContainer.getChildren().add(
                createCartItemNode("Adobe Illustrator", "$20.00")
        );

        // 3. Update summary with placeholder total
        updateSummary(31.00); // $11 + $20
    }

    // Override the setApplication method to capture the instance
    @Override
    public void setApplication(bcbfixhub.bcbfixhub.ScenesApplication application) {
        super.setApplication(application); // Calls parent's method
        this.application = application; // Saves the instance locally
    }

    /**
     * Handles the action of clicking the "Back to Store" button.
     */
    @FXML
    private void handleGoBack() {
        System.out.println("Back to store clicked.");
        application.switchTo("user-dashboard");
    }

    /**
     * Handles the action of clicking the "Proceed to Checkout" button.
     */
    @FXML
    private void handleCheckout() {
        System.out.println("Checkout clicked. Switching to payment scene.");
        // --- MODIFICATION: Switch to payment scene ---
        application.switchTo("payment");
    }

    /**
     * Updates the order summary.
     * @param subtotal The new subtotal amount.
     */
    private void updateSummary(double subtotal) {
        double tax = subtotal * 0.08; // 8% tax
        double total = subtotal + tax;

        subtotalLabel.setText(String.format("$%.2f", subtotal));
        taxLabel.setText(String.format("$%.2f", tax));
        totalLabel.setText(String.format("$%.2f", total));

        // Disable checkout if cart is empty
        checkoutButton.setDisable(subtotal == 0.0);
    }

    /**
     * Helper method to create a simple visual node for a cart item.
     * In a real app, this would be its own FXML component.
     */
    private HBox createCartItemNode(String productName, String price) {
        HBox itemBox = new HBox(20);
        itemBox.setAlignment(Pos.CENTER_LEFT);
        itemBox.setPadding(new Insets(10));
        itemBox.setStyle("-fx-border-color: #E0E0E0; -fx-border-width: 0 0 1 0;"); // Bottom border

        Label nameLabel = new Label(productName);
        nameLabel.setFont(new Font("System Bold", 16));

        Label priceLabel = new Label(price);
        priceLabel.setFont(new Font("System", 16));

        Button removeButton = new Button("Remove");
        removeButton.setStyle("-fx-background-color: #FF6B6B; -fx-text-fill: white; -fx-cursor: hand;");
        // You would add an action to this button to remove the item

        // Add spacer to push price and button
        HBox spacer = new HBox();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        itemBox.getChildren().addAll(nameLabel, spacer, priceLabel, removeButton);
        return itemBox;
    }
}