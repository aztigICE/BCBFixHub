package bcbfixhub.bcbfixhub.controllers;

import bcbfixhub.bcbfixhub.BcbfixhubApplication;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

import bcbfixhub.bcbfixhub.controllers.MainController.Product;

public class CartController extends BaseController {

    @FXML private VBox cartItemsContainer;
    @FXML private Label subtotalLabel;
    @FXML private Label taxLabel;
    @FXML private Label totalLabel;
    @FXML private Button backButton;
    @FXML private Button checkoutButton;

    private BcbfixhubApplication application;
    private static final double TAX_RATE = 0.08;

    @Override
    public void setApplication(BcbfixhubApplication application) {
        super.setApplication(application);
        this.application = application;
        loadCart();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (application != null) loadCart();
    }

    @FXML
    private void handleGoBack() {
        if (application != null) {
            application.switchTo("user-dashboard"); // Or whichever scene is the previous screen
        }
    }

    @FXML
    private void handleCheckout() {
        if (application != null) {
            if (application.getCart().isEmpty()) {
                System.out.println("Cart is empty!");
                return;
            }
            application.switchTo("payment");
        }
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

            Label priceLabel = new Label("$" + String.format("%.2f", product.getPrice()));
            priceLabel.setPrefWidth(80);

            itemBox.getChildren().addAll(nameLabel, priceLabel);
            cartItemsContainer.getChildren().add(itemBox);

            subtotal += product.getPrice();
        }

        double tax = subtotal * TAX_RATE;
        double total = subtotal + tax;

        subtotalLabel.setText("$" + String.format("%.2f", subtotal));
        taxLabel.setText("$" + String.format("%.2f", tax));
        totalLabel.setText("$" + String.format("%.2f", total));
    }
}
