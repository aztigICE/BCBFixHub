package bcbfixhub.bcbfixhub.controllers;

import bcbfixhub.bcbfixhub.models.ProductDBConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class ProductController extends ScenesController {

    // ✅ Must match fx:id="tabPane" in FXML
    @FXML private TabPane tabPane;

    // === Keyboard Tab ===
    @FXML private TableView<Product> tableView;
    @FXML private TableColumn<Product, String> collectionColumn;
    @FXML private TableColumn<Product, String> brandColumn;
    @FXML private TableColumn<Product, String> modelColumn;
    @FXML private TableColumn<Product, Double> priceColumn;
    @FXML private TextField collectionField;
    @FXML private TextField brandField;
    @FXML private TextField modelField;
    @FXML private TextField priceField;

    // === Mouse Tab ===
    @FXML private TableView<Product> tableView1;
    @FXML private TableColumn<Product, String> collectionColumn1;
    @FXML private TableColumn<Product, String> brandColumn1;
    @FXML private TableColumn<Product, String> modelColumn1;
    @FXML private TableColumn<Product, Double> priceColumn1;
    @FXML private TextField collectionField1;
    @FXML private TextField brandField1;
    @FXML private TextField modelField1;
    @FXML private TextField priceField1;

    // === Storage Tab ===
    @FXML private TableView<Product> tableView21;
    @FXML private TableColumn<Product, String> collectionColumn21;
    @FXML private TableColumn<Product, String> brandColumn21;
    @FXML private TableColumn<Product, String> modelColumn21;
    @FXML private TableColumn<Product, Double> priceColumn21;
    @FXML private TextField collectionField2;
    @FXML private TextField brandField2;
    @FXML private TextField modelField2;
    @FXML private TextField priceField2;

    // === Memory Tab ===
    @FXML private TableView<Product> tableView211;
    @FXML private TableColumn<Product, String> collectionColumn211;
    @FXML private TableColumn<Product, String> brandColumn211;
    @FXML private TableColumn<Product, String> modelColumn211;
    @FXML private TableColumn<Product, Double> priceColumn211;
    @FXML private TextField collectionField211;
    @FXML private TextField brandField211;
    @FXML private TextField modelField211;
    @FXML private TextField priceField211;

    // === Monitor Tab ===
    @FXML private TableView<Product> tableView2111;
    @FXML private TableColumn<Product, String> collectionColumn2111;
    @FXML private TableColumn<Product, String> brandColumn2111;
    @FXML private TableColumn<Product, String> modelColumn2111;
    @FXML private TableColumn<Product, Double> priceColumn2111;
    @FXML private TextField collectionField2111;
    @FXML private TextField brandField2111;
    @FXML private TextField modelField2111;
    @FXML private TextField priceField2111;

    // === Initialize All Tabs ===
    @FXML
    public void initialize() {
        setupTable(tableView, collectionColumn, brandColumn, modelColumn, priceColumn, "keyboard");
        setupTable(tableView1, collectionColumn1, brandColumn1, modelColumn1, priceColumn1, "mouse");
        setupTable(tableView21, collectionColumn21, brandColumn21, modelColumn21, priceColumn21, "storage");
        setupTable(tableView211, collectionColumn211, brandColumn211, modelColumn211, priceColumn211, "memory");
        setupTable(tableView2111, collectionColumn2111, brandColumn2111, modelColumn2111, priceColumn2111, "monitor");
    }

    // === Setup Table Columns and Data ===
    private void setupTable(TableView<Product> table, TableColumn<Product, String> collectionCol,
                            TableColumn<Product, String> brandCol, TableColumn<Product, String> modelCol,
                            TableColumn<Product, Double> priceCol, String collectionName) {

        collectionCol.setCellValueFactory(new PropertyValueFactory<>("collection"));
        brandCol.setCellValueFactory(new PropertyValueFactory<>("brand"));
        modelCol.setCellValueFactory(new PropertyValueFactory<>("model"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        table.getItems().setAll(fetchProducts(collectionName));
    }

    // === Fetch from MongoDB ===
    private List<Product> fetchProducts(String collectionName) {
        List<Product> products = new ArrayList<>();
        try {
            MongoCollection<Document> collection = ProductDBConnection.getDatabase().getCollection(collectionName);
            try (MongoCursor<Document> cursor = collection.find().iterator()) {
                while (cursor.hasNext()) {
                    Document doc = cursor.next();
                    String collectionType = doc.getString("collection");
                    String brand = doc.getString("brand");
                    String model = doc.getString("model");
                    Double price = doc.getDouble("price");
                    products.add(new Product(collectionType, brand, model, price));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }

    // === Add Product (works for all tabs) ===
    @FXML
    private void handleAddProduct() {
        if (tabPane == null) {
            showAlert(Alert.AlertType.ERROR, "TabPane is not connected in FXML!");
            return;
        }

        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        if (selectedTab == null) {
            showAlert(Alert.AlertType.WARNING, "No tab selected!");
            return;
        }

        String tabName = selectedTab.getText().toLowerCase();

        TextField collectionF = null, brandF = null, modelF = null, priceF = null;
        TableView<Product> currentTable = null;

        switch (tabName) {
            case "keyboard" -> { collectionF = collectionField; brandF = brandField; modelF = modelField; priceF = priceField; currentTable = tableView; }
            case "mouse" -> { collectionF = collectionField1; brandF = brandField1; modelF = modelField1; priceF = priceField1; currentTable = tableView1; }
            case "storage" -> { collectionF = collectionField2; brandF = brandField2; modelF = modelField2; priceF = priceField2; currentTable = tableView21; }
            case "memory" -> { collectionF = collectionField211; brandF = brandField211; modelF = modelField211; priceF = priceField211; currentTable = tableView211; }
            case "monitor" -> { collectionF = collectionField2111; brandF = brandField2111; modelF = modelField2111; priceF = priceField2111; currentTable = tableView2111; }
            default -> { showAlert(Alert.AlertType.ERROR, "Unknown tab: " + tabName); return; }
        }

        if (collectionF == null || brandF == null || modelF == null || priceF == null) {
            showAlert(Alert.AlertType.ERROR, "Input fields not found for tab: " + tabName);
            return;
        }

        String collection = collectionF.getText().trim();
        String brand = brandF.getText().trim();
        String model = modelF.getText().trim();
        String priceText = priceF.getText().trim();

        if (collection.isEmpty() || brand.isEmpty() || model.isEmpty() || priceText.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Please fill in all fields!");
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceText);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Price must be a valid number!");
            return;
        }

        try {
            MongoCollection<Document> collectionDB = ProductDBConnection.getDatabase().getCollection(tabName);
            Document newProduct = new Document("collection", collection)
                    .append("brand", brand)
                    .append("model", model)
                    .append("price", price);
            collectionDB.insertOne(newProduct);

            currentTable.getItems().add(new Product(collection, brand, model, price));

            collectionF.clear(); brandF.clear(); modelF.clear(); priceF.clear();

            showAlert(Alert.AlertType.INFORMATION, "Product added to " + tabName + " collection!");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error adding product: " + e.getMessage());
        }
    }

    // === Alerts ===
    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // === Inner Product Class ===
    public static class Product {
        private final String collection;
        private final String brand;
        private final String model;
        private final Double price;

        public Product(String collection, String brand, String model, Double price) {
            this.collection = collection;
            this.brand = brand;
            this.model = model;
            this.price = price;
        }

        public String getCollection() { return collection; }
        public String getBrand() { return brand; }
        public String getModel() { return model; }
        public Double getPrice() { return price; }
    }
}
