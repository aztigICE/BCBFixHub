package bcbfixhub.bcbfixhub.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

import bcbfixhub.bcbfixhub.controllers.MainController.Product;

public class CartController extends BaseController {

    @FXML private VBox cartItemsContainer;
    @FXML private Label subtotalLabel;
    @FXML private Label taxLabel;
    @FXML private Label totalLabel;
    @FXML private Button backButton;
    @FXML private Button checkoutButton;

    private static final double TAX_RATE = 0.08;

    // Temporary local cart (since app-level cart is not guaranteed)
    private List<Product> cart;

    @FXML
    public void initialize() {
        // cart may be injected later; load when scene is shown
    }

    @Override
    public void onSceneShown() {
        loadCart();
    }

    @FXML
    private void handleGoBack() {
        showAlert(
                javafx.scene.control.Alert.AlertType.INFORMATION,
                "Back",
                "Back navigation not implemented."
        );
    }

    @FXML
    private void handleCheckout() {
        if (cart == null || cart.isEmpty()) {
            showAlert(
                    javafx.scene.control.Alert.AlertType.WARNING,
                    "Checkout",
                    "Your cart is empty."
            );
            return;
        }

        showAlert(
                javafx.scene.control.Alert.AlertType.INFORMATION,
                "Checkout",
                "Checkout not implemented."
        );
    }

    // This method can be called by whoever creates the scene
    public void setCart(List<Product> cart) {
        this.cart = cart;
        loadCart();
    }

    private void loadCart() {
        if (cartItemsContainer == null || cart == null) return;

        cartItemsContainer.getChildren().clear();
        double subtotal = 0.0;

        if (cart.isEmpty()) {
            Label emptyLabel = new Label("Your cart is empty.");
            emptyLabel.setPadding(new Insets(10));
            cartItemsContainer.getChildren().add(emptyLabel);
        } else {
            for (Product product : cart) {
                HBox itemBox = new HBox(10);
                itemBox.setPadding(new Insets(5));

                Label nameLabel = new Label(
                        product.getBrand() + " " + product.getModel()
                );
                nameLabel.setPrefWidth(200);

                Label priceLabel = new Label(
                        "₱" + String.format("%.2f", product.getPrice())
                );
                priceLabel.setPrefWidth(80);

                itemBox.getChildren().addAll(nameLabel, priceLabel);
                cartItemsContainer.getChildren().add(itemBox);

                subtotal += product.getPrice();
            }
        }

        double tax = subtotal * TAX_RATE;
        double total = subtotal + tax;

        subtotalLabel.setText("₱" + String.format("%.2f", subtotal));
        taxLabel.setText("₱" + String.format("%.2f", tax));
        totalLabel.setText("₱" + String.format("%.2f", total));
    }
}
