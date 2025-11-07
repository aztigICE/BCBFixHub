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
        MongoDatabase db = MongoDBConnectionManager.getDatabase(DATABASE_NAME);

        // Populate categories
        List<String> collectionList = new ArrayList<>();
        for (String name : db.listCollectionNames()) collectionList.add(name);
        var collections = FXCollections.observableArrayList(collectionList);
        collections.add(0, "All");
        categoryChoiceBox.setItems(collections);
        categoryChoiceBox.setValue("All");

        categoryChoiceBox.setOnAction(e -> {
            String selected = categoryChoiceBox.getValue();
            if ("All".equals(selected)) loadAllProducts();
            else loadProductsFromMongoDB(selected);
        });

        updateCartButtonText();
        loadAllProducts();
    }

    private void loadAllProducts() {
        catalogTilePane.getChildren().clear();
        MongoDatabase db = MongoDBConnectionManager.getDatabase(DATABASE_NAME);
        for (String collectionName : db.listCollectionNames()) {
            loadProductsFromMongoDB(collectionName);
        }
    }

    private void loadProductsFromMongoDB(String collectionName) {
        try {
            MongoCollection<Document> collection = MongoDBConnectionManager
                    .getDatabase(DATABASE_NAME)
                    .getCollection(collectionName);

            for (Document doc : collection.find()) {
                String stock = doc.getString("stock");
                String brand = doc.getString("brand");
                String model = doc.getString("model");
                Double price = doc.getDouble("price");

                Product product = new Product(stock, brand, model, price);
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
        card.setStyle("-fx-background-color: #FFF8E7; -fx-background-radius: 8; -fx-border-color: #D1B48C; -fx-border-radius: 8;");

        VBox imageBox = new VBox();
        imageBox.setPrefSize(180, 150);
        imageBox.setStyle("-fx-background-color: #E0CBAF; -fx-background-radius: 5;");

        Label nameLabel = new Label(product.getBrand() + " " + product.getModel());
        Label stockLabel = new Label("Stock: " + product.getStock());
        Label priceLabel = new Label("₱" + String.format("%.2f", product.getPrice()));

        Button addToCartButton = new Button("Add to Cart");
        addToCartButton.setOnAction(e -> handleAddToCart(product));

        card.getChildren().addAll(imageBox, nameLabel, stockLabel, priceLabel, addToCartButton);
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
        } else cartButton.setText("Cart (0)");
    }

    @FXML private void handleGoToCart() {
        if (application != null) application.switchTo("cart");
    }

    @FXML private void handleGoToAccount() {
        if (application != null) application.switchTo("account");
    }

    public static class Product {
        private String stock, brand, model;
        private Double price;

        public Product(String stock, String brand, String model, Double price) {
            this.stock = stock; this.brand = brand; this.model = model; this.price = price;
        }

        public String getStock() { return stock; }
        public String getBrand() { return brand; }
        public String getModel() { return model; }
        public Double getPrice() { return price; }
    }
}
