package bcbfixhub.bcbfixhub.controllers;

import bcbfixhub.bcbfixhub.ScenesApplication;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.net.URL;
import java.util.ResourceBundle;

public class CartController extends ScenesController implements Initializable {

    @FXML private Button backButton;
    @FXML private Button checkoutButton;
    @FXML private VBox cartItemsContainer;
    @FXML private Label emptyCartLabel;
    @FXML private Label subtotalLabel;
    @FXML private Label taxLabel;
    @FXML private Label totalLabel;

    private ScenesApplication application;

    @Override
    public void setApplication(ScenesApplication application) {
        super.setApplication(application);
        this.application = application;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Do nothing initially
    }

    public void loadCart() {
        cartItemsContainer.getChildren().clear();

        if (application == null || application.getCart().isEmpty()) {
            emptyCartLabel.setVisible(true);
            emptyCartLabel.setManaged(true);
            checkoutButton.setDisable(true);
            subtotalLabel.setText("₱0.00");
            taxLabel.setText("₱0.00");
            totalLabel.setText("₱0.00");
            return;
        }

        emptyCartLabel.setVisible(false);
        emptyCartLabel.setManaged(false);
        checkoutButton.setDisable(false);

        double subtotal = 0;

        for (Object obj : application.getCart()) {
            if (obj instanceof MainController.Product product) {
                HBox itemBox = createCartItemNode(product);
                cartItemsContainer.getChildren().add(itemBox);
                subtotal += product.getPrice();
            }
        }

        updateSummary(subtotal);
    }

    private HBox createCartItemNode(MainController.Product product) {
        HBox itemBox = new HBox(20);
        itemBox.setAlignment(Pos.CENTER_LEFT);
        itemBox.setPadding(new Insets(10));
        itemBox.setStyle("-fx-border-color: #E0E0E0; -fx-border-width: 0 0 1 0;");

        Label nameLabel = new Label(product.getBrand() + " " + product.getModel());
        nameLabel.setFont(new Font("System Bold", 16));

        Label priceLabel = new Label("₱" + String.format("%.2f", product.getPrice()));
        priceLabel.setFont(new Font(16));

        Button removeButton = new Button("Remove");
        removeButton.setStyle("-fx-background-color: #FF6B6B; -fx-text-fill: white;");
        removeButton.setOnAction(e -> {
            application.getCart().remove(product);
            loadCart();
        });

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        itemBox.getChildren().addAll(nameLabel, spacer, priceLabel, removeButton);
        return itemBox;
    }

    private void updateSummary(double subtotal) {
        double tax = subtotal * 0.08;
        double total = subtotal + tax;

        subtotalLabel.setText("₱" + String.format("%.2f", subtotal));
        taxLabel.setText("₱" + String.format("%.2f", tax));
        totalLabel.setText("₱" + String.format("%.2f", total));

        checkoutButton.setDisable(subtotal == 0);
    }

    @FXML private void handleGoBack() {
        if (application != null) application.switchTo("user-dashboard");
    }

    @FXML private void handleCheckout() {
        if (application != null) {
            // TODO: Decrease stock in MongoDB if needed
            application.getCart().clear();
            loadCart();
        }
    }
}
