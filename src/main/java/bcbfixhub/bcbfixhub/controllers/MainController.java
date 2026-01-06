package bcbfixhub.bcbfixhub.controllers;

import com.mongodb.client.MongoCollection;
import javafx.animation.PauseTransition;
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

public class MainController extends BaseController implements Initializable {

    @FXML private ChoiceBox<String> categoryChoiceBox;
    @FXML private TextField searchBar;
    @FXML private TilePane catalogTilePane;
    @FXML private Button cartButton;
    @FXML private Button accountButton;

    private final Map<String, Image> imageCache = new HashMap<>();
    private final PauseTransition searchDelay = new PauseTransition(Duration.millis(400));

    // local cart (NO dependency on BcbfixhubApplication methods)
    private final List<Product> cart = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        categoryChoiceBox.setItems(FXCollections.observableArrayList(
                "All", "keyboard", "mouse", "memory", "storage", "monitor"
        ));
        categoryChoiceBox.setValue("All");

        categoryChoiceBox.setOnAction(e -> refreshProducts());

        searchBar.textProperty().addListener((obs, o, n) -> {
            searchDelay.setOnFinished(ev -> handleSearchAsync(n));
            searchDelay.playFromStart();
        });

        updateCartButtonText();
        loadAllProductsAsync();
    }

    @Override
    public void onSceneShown() {
        updateCartButtonText();
        refreshProducts();
    }

    private void loadAllProductsAsync() {
        catalogTilePane.getChildren().clear();
        for (String cat : new String[]{"keyboard","mouse","memory","storage","monitor"}) {
            loadProductsFromMongoDBAsync(cat);
        }
    }

    private void loadProductsFromMongoDBAsync(String collectionName) {
        Task<List<Product>> task = new Task<>() {
            @Override protected List<Product> call() {
                return fetchProducts(collectionName);
            }
        };

        task.setOnSucceeded(e -> {
            if (!"All".equals(categoryChoiceBox.getValue())) {
                catalogTilePane.getChildren().clear();
            }
            task.getValue().forEach(p ->
                    catalogTilePane.getChildren().add(createProductCard(p)));
        });

        new Thread(task).start();
    }

    private List<Product> fetchProducts(String collectionName) {
        List<Product> products = new ArrayList<>();
        try {
            MongoCollection<Document> collection =
                    database.getCollection(collectionName);

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

    private VBox createProductCard(Product product) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(200,300);

        ImageView imageView =
                new ImageView(getCachedImage(product.getImageName()));
        imageView.setFitWidth(180);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);

        Label name = new Label(product.getBrand()+" "+product.getModel());
        Label stock = new Label("Stock: "+product.getStock());
        Label price = new Label("₱"+String.format("%.2f",product.getPrice()));

        Button add = new Button("Add to Cart");
        add.setOnAction(e -> {
            cart.add(product);
            updateCartButtonText();
        });

        VBox.setMargin(add,new Insets(10,0,0,0));
        card.getChildren().addAll(imageView,name,stock,price,add);
        return card;
    }

    private Image getCachedImage(String imageName) {
        if (imageName == null) return getPlaceholderImage();
        if (imageCache.containsKey(imageName)) return imageCache.get(imageName);

        for (String folder : new String[]{"keyboard","mouse","memory","storage","monitor"}) {
            for (String ext : new String[]{".jpg",".png"}) {
                String path =
                        "bcbfixhub/bcbfixhub/product_images/"+folder+"/"+imageName+ext;
                try (var s = getClass().getClassLoader().getResourceAsStream(path)) {
                    if (s != null) {
                        Image img = new Image(s);
                        imageCache.put(imageName,img);
                        return img;
                    }
                } catch (Exception ignored) {}
            }
        }
        return getPlaceholderImage();
    }

    private Image getPlaceholderImage() {
        try (var s = getClass().getClassLoader()
                .getResourceAsStream("bcbfixhub/bcbfixhub/product_images/placeholder.png")) {
            return s != null ? new Image(s) : new Image("https://via.placeholder.com/180");
        } catch (Exception e) {
            return new Image("https://via.placeholder.com/180");
        }
    }

    private void updateCartButtonText() {
        cartButton.setText("Cart (" + cart.size() + ")");
    }

    @FXML
    private void handleGoToCart() {}

    @FXML
    private void handleGoToAccount() {}

    private void handleSearchAsync(String query) {
        Task<List<Product>> task = new Task<>() {
            @Override protected List<Product> call() {
                return performSearch(query);
            }
        };

        task.setOnSucceeded(e -> {
            catalogTilePane.getChildren().clear();
            task.getValue().forEach(p ->
                    catalogTilePane.getChildren().add(createProductCard(p)));
        });

        new Thread(task).start();
    }

    private List<Product> performSearch(String query) {
        List<Product> results = new ArrayList<>();
        if (query == null || query.isBlank()) {
            refreshProducts();
            return results;
        }

        query = query.toLowerCase();
        try {
            for (String cat : new String[]{"keyboard","mouse","memory","storage","monitor"}) {
                for (Document d : database.getCollection(cat).find()) {
                    String b = d.getString("brand");
                    String m = d.getString("model");
                    if (b != null && m != null &&
                            (b.toLowerCase().contains(query) ||
                                    m.toLowerCase().contains(query))) {

                        results.add(new Product(
                                d.getString("stock"),
                                b,
                                m,
                                d.getDouble("price"),
                                d.getString("imageName")
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
        if ("All".equals(categoryChoiceBox.getValue())) {
            loadAllProductsAsync();
        } else {
            loadProductsFromMongoDBAsync(categoryChoiceBox.getValue());
        }
    }

    public static class Product {
        private final String stock, brand, model, imageName;
        private final Double price;

        public Product(String stock, String brand, String model,
                       Double price, String imageName) {
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
