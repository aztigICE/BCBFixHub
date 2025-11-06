package bcbfixhub.bcbfixhub.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController extends ScenesController implements Initializable {

    @FXML
    private ChoiceBox<String> categoryChoiceBox;

    @FXML
    private TextField searchBar;

    @FXML
    private TilePane catalogTilePane;

    @FXML
    private Button cartButton; // Reference for the cart button

    @FXML
    private Button accountButton; // ADDED

    private int cartItemCount = 0; // Counter for cart items

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
        // Populate category choice box
        categoryChoiceBox.setItems(FXCollections.observableArrayList(
                "All Categories", "Design", "Development", "Productivity"
        ));
        categoryChoiceBox.setValue("All Categories");

        // Set initial cart button text
        updateCartButtonText();

        // Add placeholder items to the catalog
        // In a real app, you'd load this from a database
        catalogTilePane.getChildren().add(
                createProductCard("Adobe Photoshop", "Design", "$11.00", "https://placehold.co/200x150/E8D8B3/363636?text=Photoshop")
        );
        catalogTilePane.getChildren().add(
                createProductCard("Adobe Illustrator", "Design", "$20.00", "https://placehold.co/200x150/E8D8B3/363636?text=Illustrator")
        );
        catalogTilePane.getChildren().add(
                createProductCard("Visual Studio Code", "Development", "Free", "https://placehold.co/200x150/E8D8B3/363636?text=VS+Code")
        );
        catalogTilePane.getChildren().add(
                createProductCard("Microsoft Office", "Productivity", "$99.00", "https://placehold.co/200x150/E8D8B3/363636?text=Office")
        );

        // You can add listeners here for search and category filtering
        // e.g., searchBar.textProperty().addListener(...)
    }

    /**
     * Helper method to create a placeholder product card.
     * In a real app, this would be a separate FXML component.
     * @param productName The name of the product
     * @param category The product category
     * @param price The product price
     * @param imageUrl A placeholder image URL
     * @return A VBox representing the product card
     */
    private VBox createProductCard(String productName, String category, String price, String imageUrl) {
        VBox card = new VBox();
        card.getStyleClass().add("product-card");
        card.setPrefSize(200, 300); // Fixed size for cards
        card.setSpacing(10);
        card.setAlignment(Pos.CENTER);

        // Placeholder for image
        VBox imageBox = new VBox();
        imageBox.setPrefSize(180, 150);
        imageBox.setStyle("-fx-background-color: #E0CBAF; -fx-background-radius: 5;");
        // In a real app, you'd use an ImageView here:
        // ImageView imageView = new ImageView(new Image(imageUrl));

        Label nameLabel = new Label(productName);
        nameLabel.getStyleClass().add("product-name");

        Label categoryLabel = new Label(category);
        categoryLabel.getStyleClass().add("product-category");

        Label priceLabel = new Label(price);
        priceLabel.getStyleClass().add("product-price");

        Button addToCartButton = new Button("Add to Cart");
        addToCartButton.getStyleClass().add("add-to-cart-button");

        // Add action to the button
        addToCartButton.setOnAction(event -> handleAddToCart());

        card.getChildren().addAll(imageBox, nameLabel, categoryLabel, priceLabel, addToCartButton);
        VBox.setMargin(addToCartButton, new Insets(10, 0, 0, 0)); // Add space above button

        return card;
    }

    /**
     * Handles adding an item to the cart.
     */
    private void handleAddToCart() {
        cartItemCount++;
        updateCartButtonText();
        System.out.println("Item added to cart. Total items: " + cartItemCount);
        // In a real app, you'd add the specific item to a cart model/list
    }

    /**
     * Updates the cart button text to show the current item count.
     */
    private void updateCartButtonText() {
        cartButton.setText("Cart (" + cartItemCount + ")");
    }

    /**
     * Placeholder method for handling the cart button click.
     * This is where you would navigate to the cart screen.
     */
    @FXML
    private void handleGoToCart() {
        System.out.println("Go to Cart button clicked!");
        // Switch to the cart scene
        application.switchTo("cart");
    }

    /**
     * ADDED: Handles switching to the account view.
     */
    @FXML
    private void handleGoToAccount() {
        System.out.println("Account button clicked!");
        application.switchTo("account");
    }
}