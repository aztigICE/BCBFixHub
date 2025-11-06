package bcbfixhub.bcbfixhub.controllers;

import bcbfixhub.bcbfixhub.models.ProductDBConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
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
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import org.bson.Document;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainController extends ScenesController implements Initializable {

    @FXML private ChoiceBox<String> categoryChoiceBox;
    @FXML private TextField searchBar;
    @FXML private TilePane catalogTilePane;
    @FXML private Button cartButton;

    private int cartItemCount = 0;
    private final List<Product> cart = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        MongoDatabase db = ProductDBConnection.getDatabase();

        // Load collection names for category filter
        List<String> collectionList = new ArrayList<>();
        for (String name : db.listCollectionNames()) collectionList.add(name);
        var collections = FXCollections.observableArrayList(collectionList);
        collections.add(0, "All");
        categoryChoiceBox.setItems(collections);
        categoryChoiceBox.setValue("All");

        // Category selection listener
        categoryChoiceBox.setOnAction(e -> {
            String selected = categoryChoiceBox.getValue();
            if (selected.equals("All")) loadAllProductsAsTiles();
            else loadProductsFromMongoDBAsTiles(selected);
        });

        updateCartButtonText();
        loadAllProductsAsTiles();
    }

    // Load all products from all collections
    private void loadAllProductsAsTiles() {
        catalogTilePane.getChildren().clear();
        for (String collectionName : ProductDBConnection.getDatabase().listCollectionNames()) {
            loadProductsFromMongoDBAsTiles(collectionName);
        }
    }

    // Load products from a single collection
    private void loadProductsFromMongoDBAsTiles(String collectionName) {
        try {
            MongoCollection<Document> collection = ProductDBConnection.getDatabase().getCollection(collectionName);
            try (MongoCursor<Document> cursor = collection.find().iterator()) {
                while (cursor.hasNext()) {
                    Document doc = cursor.next();
                    String stock = doc.getString("stock");
                    String brand = doc.getString("brand");
                    String model = doc.getString("model");
                    Double price = doc.getDouble("price");
                    Product product = new Product(stock, brand, model, price);
                    catalogTilePane.getChildren().add(createProductCard(product));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Create a visual product card
    private VBox createProductCard(Product product) {
        VBox card = new VBox();
        card.setSpacing(10);
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(200, 250);
        card.setStyle("-fx-background-color: #FFF8E7; -fx-background-radius: 8; -fx-border-color: #D1B48C; -fx-border-radius: 8;");

        Label brandLabel = new Label(product.getBrand());
        brandLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        Label modelLabel = new Label(product.getModel());
        Label stockLabel = new Label("Stock: " + product.getStock());
        Label priceLabel = new Label("₱" + String.format("%.2f", product.getPrice()));

        Button addToCartButton = new Button("Add to Cart");
        addToCartButton.setOnAction(e -> handleAddToCart(product));

        card.getChildren().addAll(brandLabel, modelLabel, stockLabel, priceLabel, addToCartButton);
        VBox.setMargin(addToCartButton, new Insets(10, 0, 0, 0));
        return card;
    }

    // Add product to cart
    private void handleAddToCart(Product product) {
        cart.add(product);
        cartItemCount++;
        updateCartButtonText();
        System.out.println("Added to cart: " + product.getBrand() + " " + product.getModel());
    }

    private void updateCartButtonText() {
        cartButton.setText("Cart (" + cartItemCount + ")");
    }

    @FXML
    private void handleGoToCart() {
        if (app != null) app.switchTo("cart");
    }

    // Inner Product class (reused from ProductController)
    public static class Product {
        private String stock;
        private String brand;
        private String model;
        private Double price;

        public Product(String stock, String brand, String model, Double price) {
            this.stock = stock;
            this.brand = brand;
            this.model = model;
            this.price = price;
        }

        public String getStock() { return stock; }
        public String getBrand() { return brand; }
        public String getModel() { return model; }
        public Double getPrice() { return price; }

        public void setStock(String stock) { this.stock = stock; }
        public void setBrand(String brand) { this.brand = brand; }
        public void setModel(String model) { this.model = model; }
        public void setPrice(Double price) { this.price = price; }
    }
}
