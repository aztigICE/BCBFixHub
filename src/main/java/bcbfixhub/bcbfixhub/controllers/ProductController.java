package bcbfixhub.bcbfixhub.controllers;

import bcbfixhub.bcbfixhub.models.ProductDBConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.bson.Document;
import javafx.scene.layout.GridPane;


import java.util.ArrayList;
import java.util.List;

public class ProductController {

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

    @FXML
    private void handleEditProduct() {
        Product selectedProduct = getSelectedProduct();
        if (selectedProduct == null) {
            showAlert("No Selection", "Please select a product to edit.");
            return;
        }

        // Create text fields pre-filled with existing data
        TextField collectionField = new TextField(selectedProduct.getCollection());
        TextField brandField = new TextField(selectedProduct.getBrand());
        TextField modelField = new TextField(selectedProduct.getModel());
        TextField priceField = new TextField(String.valueOf(selectedProduct.getPrice()));

        // Create a GridPane layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 20, 10, 20));
        grid.addRow(0, new Label("Collection:"), collectionField);
        grid.addRow(1, new Label("Brand:"), brandField);
        grid.addRow(2, new Label("Model:"), modelField);
        grid.addRow(3, new Label("Price:"), priceField);

        // Create and configure dialog
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Product");
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // Get updated values
                    String newCollection = collectionField.getText().trim();
                    String newBrand = brandField.getText().trim();
                    String newModel = modelField.getText().trim();
                    double newPrice = Double.parseDouble(priceField.getText().trim());

                    // Validate inputs
                    if (newCollection.isEmpty() || newBrand.isEmpty() || newModel.isEmpty()) {
                        showAlert("Validation Error", "All fields must be filled!");
                        return;
                    }

                    // Update MongoDB
                    String activeCollection = getActiveCollectionName();
                    MongoCollection<Document> collection = ProductDBConnection.getDatabase().getCollection(activeCollection);
                    Document filter = new Document("model", selectedProduct.getModel());
                    Document update = new Document("$set", new Document("collection", newCollection)
                            .append("brand", newBrand)
                            .append("model", newModel)
                            .append("price", newPrice));
                    collection.updateOne(filter, update);

                    // Update TableView
                    selectedProduct.setCollection(newCollection);
                    selectedProduct.setBrand(newBrand);
                    selectedProduct.setModel(newModel);
                    selectedProduct.setPrice(newPrice);

                    // Refresh the current table
                    switch (activeCollection) {
                        case "keyboard" -> tableView.refresh();
                        case "mouse" -> tableView1.refresh();
                        case "storage" -> tableView21.refresh();
                        case "memory" -> tableView211.refresh();
                        case "monitor" -> tableView2111.refresh();
                    }

                    showAlert("Success", "Product updated successfully!");

                } catch (NumberFormatException e) {
                    showAlert("Error", "Price must be a valid number!");
                } catch (Exception e) {
                    showAlert("Error", "Database update failed: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private Product getSelectedProduct() {
        String tabName = tabPane.getSelectionModel().getSelectedItem().getText().toLowerCase();
        return switch (tabName) {
            case "keyboard" -> tableView.getSelectionModel().getSelectedItem();
            case "mouse" -> tableView1.getSelectionModel().getSelectedItem();
            case "storage" -> tableView21.getSelectionModel().getSelectedItem();
            case "memory" -> tableView211.getSelectionModel().getSelectedItem();
            case "monitor" -> tableView2111.getSelectionModel().getSelectedItem();
            default -> null;
        };
    }


    private String getActiveCollectionName() {
        if (tabPane == null || tabPane.getSelectionModel().getSelectedItem() == null)
            return "Product"; // fallback

        return tabPane.getSelectionModel().getSelectedItem().getText().toLowerCase();
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
        private String collection;
        private String brand;
        private String model;
        private Double price;

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

        public void setCollection(String collection) { this.collection = collection; }
        public void setBrand(String brand) { this.brand = brand; }
        public void setModel(String model) { this.model = model; }
        public void setPrice(Double price) { this.price = price; }
    }

}
