package bcbfixhub.bcbfixhub.controllers;

import bcbfixhub.bcbfixhub.BcbfixhubApplication;
import bcbfixhub.bcbfixhub.utils.DBConnectionHelper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
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
import javafx.util.Duration;
import org.bson.Document;

import java.net.URL;
import java.util.*;

public class MainController extends ScenesController implements Initializable {

    @FXML private ChoiceBox<String> categoryChoiceBox;
    @FXML private TextField searchBar;
    @FXML private TilePane catalogTilePane;
    @FXML private Button cartButton;
    @FXML private Button accountButton;

    private BcbfixhubApplication application;
    private static final String DATABASE_NAME = "Product-Details";

    private final Map<String, Image> imageCache = new HashMap<>();
    private final PauseTransition searchDelay = new PauseTransition(Duration.millis(400)); // debounce delay

    @Override
    public void setApplication(BcbfixhubApplication application) {
        super.setApplication(application);
        this.application = application;

        // Update cart + reload stock whenever the scene is shown
        Platform.runLater(() -> {
            updateCartButtonText();
            refreshProducts();
        });
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        var collections = FXCollections.observableArrayList(
                "All", "keyboard", "mouse", "memory", "storage", "monitor"
        );
        categoryChoiceBox.setItems(collections);
        categoryChoiceBox.setValue("All");
        categoryChoiceBox.setStyle("-fx-mark-color: black; -fx-text-fill: black;");

        categoryChoiceBox.setOnAction(e -> {
            String selected = categoryChoiceBox.getValue();
            if ("All".equals(selected)) {
                loadAllProductsAsync();
            } else {
                loadProductsFromMongoDBAsync(selected);
            }
        });

        // Debounced search
        searchBar.textProperty().addListener((obs, oldValue, newValue) -> {
            searchDelay.setOnFinished(e -> handleSearchAsync(newValue));
            searchDelay.playFromStart();
        });

        updateCartButtonText();
        loadAllProductsAsync();
    }

    //  Async load for all categories
    private void loadAllProductsAsync() {
        catalogTilePane.getChildren().clear();
        String[] categories = {"keyboard", "mouse", "memory", "storage", "monitor"};
        for (String category : categories) {
            loadProductsFromMongoDBAsync(category);
        }
    }

    // Async load single category
    private void loadProductsFromMongoDBAsync(String collectionName) {
        Task<List<Product>> task = new Task<>() {
            @Override
            protected List<Product> call() {
                return fetchProducts(collectionName);
            }
        };

        task.setOnSucceeded(e -> Platform.runLater(() -> {
            if (!"All".equals(categoryChoiceBox.getValue())) {
                catalogTilePane.getChildren().clear(); // Clear only for single category
            }
            for (Product product : task.getValue()) {
                catalogTilePane.getChildren().add(createProductCard(product));
            }
        }));

        new Thread(task, "ProductLoader-" + collectionName).start();
    }

    //  Background fetch products
    private List<Product> fetchProducts(String collectionName) {
        List<Product> products = new ArrayList<>();
        try {
            MongoCollection<Document> collection = DBConnectionHelper
                    .getDatabase(DATABASE_NAME)
                    .getCollection(collectionName);

            for (Document doc : collection.find()) {
                products.add(new Product(
                        doc.getString("stock"),
                        doc.getString("brand"),
                        doc.getString("model"),
                        doc.getDouble("price"),
                        doc.getString("imageName")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }

    //  UI product card creation
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

        ImageView imageView = new ImageView(getCachedImage(product.getImageName()));
        imageView.setFitWidth(180);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        card.getChildren().add(imageView);

        Label nameLabel = new Label(product.getBrand() + " " + product.getModel());
        Label stockLabel = new Label("Stock: " + product.getStock());
        Label priceLabel = new Label("₱" + String.format("%.2f", product.getPrice()));

        Button addToCartButton = new Button("Add to Cart");
        addToCartButton.setOnAction(e -> handleAddToCart(product));
        VBox.setMargin(addToCartButton, new Insets(10, 0, 0, 0));

        card.getChildren().addAll(nameLabel, stockLabel, priceLabel, addToCartButton);
        return card;
    }

    //  Cached image loading
    private Image getCachedImage(String imageName) {
        if (imageName == null || imageName.isEmpty()) return getPlaceholderImage();

        if (imageCache.containsKey(imageName)) {
            return imageCache.get(imageName);
        }

        String[] folders = {"keyboard", "mouse", "memory", "storage", "monitor"};
        String[] extensions = {".jpg", ".png"};

        for (String folder : folders) {
            for (String ext : extensions) {
                String path = "bcbfixhub/bcbfixhub/product_images/" + folder + "/" + imageName + ext;
                try (var stream = getClass().getClassLoader().getResourceAsStream(path)) {
                    if (stream != null) {
                        Image img = new Image(stream);
                        imageCache.put(imageName, img);
                        return img;
                    }
                } catch (Exception ignored) {}
            }
        }
        return getPlaceholderImage();
    }

    private Image getPlaceholderImage() {
        try (var stream = getClass().getClassLoader()
                .getResourceAsStream("bcbfixhub/bcbfixhub/product_images/placeholder.png")) {
            return (stream != null) ? new Image(stream) : new Image("https://via.placeholder.com/180");
        } catch (Exception e) {
            return new Image("https://via.placeholder.com/180");
        }
    }

    private void handleAddToCart(Product product) {
        if (application != null) {
            application.getCart().add(product);
            updateCartButtonText();
        }
    }

    private void updateCartButtonText() {
        Platform.runLater(() -> {
            if (application != null) {
                int count = application.getCart().size();
                cartButton.setText("Cart (" + count + ")");
            } else {
                cartButton.setText("Cart (0)");
            }
        });
    }

    @FXML private void handleGoToCart() {
        if (application != null) application.switchTo("cart");
    }

    @FXML private void handleGoToAccount() {
        if (application != null) application.switchTo("account");
    }

    //  Product class
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

    // Async search
    private void handleSearchAsync(String query) {
        Task<List<Product>> task = new Task<>() {
            @Override
            protected List<Product> call() {
                return performSearch(query);
            }
        };

        task.setOnSucceeded(e -> Platform.runLater(() -> {
            catalogTilePane.getChildren().clear();
            for (Product product : task.getValue()) {
                catalogTilePane.getChildren().add(createProductCard(product));
            }
        }));

        new Thread(task, "SearchThread").start();
    }

    // Background search logic
    private List<Product> performSearch(String query) {
        List<Product> results = new ArrayList<>();
        if (query == null || query.trim().isEmpty()) {
            if ("All".equals(categoryChoiceBox.getValue())) {
                for (String cat : new String[]{"keyboard", "mouse", "memory", "storage", "monitor"})
                    results.addAll(fetchProducts(cat));
            } else {
                results.addAll(fetchProducts(categoryChoiceBox.getValue()));
            }
            return results;
        }

        query = query.toLowerCase();
        String[] categories = "All".equals(categoryChoiceBox.getValue())
                ? new String[]{"keyboard", "mouse", "memory", "storage", "monitor"}
                : new String[]{categoryChoiceBox.getValue()};

        try {
            MongoDatabase db = DBConnectionHelper.getDatabase(DATABASE_NAME);
            for (String cat : categories) {
                MongoCollection<Document> collection = db.getCollection(cat);
                for (Document doc : collection.find()) {
                    String brand = doc.getString("brand");
                    String model = doc.getString("model");
                    if (brand != null && model != null &&
                            (brand.toLowerCase().contains(query) || model.toLowerCase().contains(query))) {
                        results.add(new Product(
                                doc.getString("stock"),
                                brand,
                                model,
                                doc.getDouble("price"),
                                doc.getString("imageName")
                        ));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return results;
    }

    public void refreshProducts() {
        Platform.runLater(() -> {
            String selected = categoryChoiceBox.getValue();
            if ("All".equals(selected)) {
                loadAllProductsAsync();
            } else {
                loadProductsFromMongoDBAsync(selected);
            }
        });
    }
}
