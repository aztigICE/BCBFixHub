package bcbfixhub.bcbfixhub.controllers;

import bcbfixhub.bcbfixhub.ScenesApplication;
import bcbfixhub.bcbfixhub.utils.MongoDBConnectionManager;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import org.bson.Document;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController extends ScenesController implements Initializable {

    @FXML private ChoiceBox<String> categoryChoiceBox;
    @FXML private TextField searchBar;
    @FXML private TilePane catalogTilePane;
    @FXML private Button cartButton;
    @FXML private Button accountButton;

    private ScenesApplication application;
    private static final String DATABASE_NAME = "Product-Details";

    @Override
    public void setApplication(ScenesApplication application) {
        super.setApplication(application);
        this.application = application;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Hardcoded category list — corresponds to MongoDB collection names
        var collections = FXCollections.observableArrayList(
                "All", "keyboard", "mouse", "memory", "storage", "monitor"
        );
        categoryChoiceBox.setItems(collections);
        categoryChoiceBox.setValue("All");

        categoryChoiceBox.setOnAction(e -> {
            String selected = categoryChoiceBox.getValue();
            if ("All".equals(selected)) {
                loadAllProducts();
            } else {
                loadProductsFromMongoDB(selected);
            }
        });

        updateCartButtonText();
        loadAllProducts();
    }

    private void loadAllProducts() {
        catalogTilePane.getChildren().clear(); // keep this here to start fresh
        String[] categories = {"keyboard", "mouse", "memory", "storage", "monitor"};
        for (String category : categories) {
            loadProductsFromMongoDB(category);
        }
    }

    private void loadProductsFromMongoDB(String collectionName) {
        try {
            // ✅ clear only when switching individual category
            catalogTilePane.getChildren().clear();

            MongoCollection<Document> collection = MongoDBConnectionManager
                    .getDatabase(DATABASE_NAME)
                    .getCollection(collectionName);

            for (Document doc : collection.find()) {
                String stock = doc.getString("stock");
                String brand = doc.getString("brand");
                String model = doc.getString("model");
                Double price = doc.getDouble("price");
                String imageName = doc.getString("imageName");

                Product product = new Product(stock, brand, model, price, imageName);
                catalogTilePane.getChildren().add(createProductCard(product));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private VBox createProductCard(Product product) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(200, 300);
        card.setStyle("""
            -fx-background-color: #FFF8E7;
            -fx-background-radius: 8;
            -fx-border-color: #D1B48C;
            -fx-border-radius: 8;
        """);

        // --- Image ---
        if (product.getImageName() != null && !product.getImageName().isEmpty()) {
            ImageView imageView = new ImageView();
            imageView.setFitWidth(180);
            imageView.setFitHeight(150);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);

            String[] folders = {"keyboard", "mouse", "memory", "storage", "monitor"};
            String[] extensions = {".jpg", ".png"};

            boolean imageLoaded = false;
            for (String folder : folders) {
                for (String ext : extensions) {
                    String path = "bcbfixhub/bcbfixhub/product_images/" + folder + "/" + product.getImageName() + ext;
                    try (var stream = getClass().getClassLoader().getResourceAsStream(path)) {
                        if (stream != null) {
                            imageView.setImage(new Image(stream));
                            card.getChildren().add(imageView);
                            imageLoaded = true;
                            break; // stop after finding first valid image
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (imageLoaded) break;
            }
        }

        Label nameLabel = new Label(product.getBrand() + " " + product.getModel());
        Label stockLabel = new Label("Stock: " + product.getStock());
        Label priceLabel = new Label("₱" + String.format("%.2f", product.getPrice()));

        Button addToCartButton = new Button("Add to Cart");
        addToCartButton.setOnAction(e -> handleAddToCart(product));

        card.getChildren().addAll(nameLabel, stockLabel, priceLabel, addToCartButton);
        VBox.setMargin(addToCartButton, new Insets(10, 0, 0, 0));

        return card;
    }

    private void handleAddToCart(Product product) {
        if (application != null) {
            application.getCart().add(product);
            updateCartButtonText();
        }
    }

    private void updateCartButtonText() {
        if (application != null) {
            int count = application.getCart().size();
            cartButton.setText("Cart (" + count + ")");
        } else {
            cartButton.setText("Cart (0)");
        }
    }

    @FXML private void handleGoToCart() {
        if (application != null) application.switchTo("cart");
    }

    @FXML private void handleGoToAccount() {
        if (application != null) application.switchTo("account");
    }

    public static class Product {
        private final String stock, brand, model, imageName;
        private final Double price;

        public Product(String stock, String brand, String model, Double price, String imageName) {
            this.stock = stock;
            this.brand = brand;
            this.model = model;
            this.price = price;
            this.imageName = imageName;
        }

        public String getStock() { return stock; }
        public String getBrand() { return brand; }
        public String getModel() { return model; }
        public Double getPrice() { return price; }
        public String getImageName() { return imageName; }
    }
}
