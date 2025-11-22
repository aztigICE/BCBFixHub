package bcbfixhub.bcbfixhub.controllers;

import bcbfixhub.bcbfixhub.utils.DBConnectionHelper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import org.bson.Document;

import java.util.*;

public class ProductController extends ScenesController {

    @FXML private TabPane tabPane;

    // === TableViews ===
    @FXML private TableView<Product> tableView;
    @FXML private TableView<Product> tableView1;
    @FXML private TableView<Product> tableView21;
    @FXML private TableView<Product> tableView211;
    @FXML private TableView<Product> tableView2111;

    // === TextFields ===
    @FXML private TextField stockField, brandField, modelField, priceField, imageField;
    @FXML private TextField stockField1, brandField1, modelField1, priceField1, imageField1;
    @FXML private TextField stockField2, brandField2, modelField2, priceField2, imageField2;
    @FXML private TextField stockField211, brandField211, modelField211, priceField211, imageField211;
    @FXML private TextField stockField2111, brandField2111, modelField2111, priceField2111, imageField2111;

    // Maps for dynamic access
    private final Map<String, TableView<Product>> tables = new HashMap<>();
    private final Map<String, TextField[]> fields = new HashMap<>();

    @FXML
    public void initialize() {
        // tabs per category
        tables.put("keyboard", tableView);
        tables.put("mouse", tableView1);
        tables.put("storage", tableView21);
        tables.put("memory", tableView211);
        tables.put("monitor", tableView2111);

        // sets up the fields for the information
        fields.put("keyboard", new TextField[]{stockField, brandField, modelField, priceField, imageField});
        fields.put("mouse", new TextField[]{stockField1, brandField1, modelField1, priceField1, imageField1});
        fields.put("storage", new TextField[]{stockField2, brandField2, modelField2, priceField2, imageField2});
        fields.put("memory", new TextField[]{stockField211, brandField211, modelField211, priceField211, imageField211});
        fields.put("monitor", new TextField[]{stockField2111, brandField2111, modelField2111, priceField2111, imageField2111});

        // Setup tables dynamically
        tables.forEach((tabName, table) -> setupTable(table, tabName));
    }

    // === Setup Table Columns and Load Data ===
    private void setupTable(TableView<Product> table, String collectionName) {
        table.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("stock"));
        table.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("brand"));
        table.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("model"));
        table.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("price"));
        table.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("imageName")); // new column

        table.getItems().setAll(fetchProducts(collectionName));
    }

    // fetches the products from mongoDB
    private List<Product> fetchProducts(String collectionName) {
        List<Product> products = new ArrayList<>();
        MongoDatabase db = DBConnectionHelper.getDatabase("Product-Details");
        MongoCollection<Document> collection = db.getCollection(collectionName);

        for (Document doc : collection.find()) {
            products.add(new Product(
                    doc.getString("stock"),
                    doc.getString("brand"),
                    doc.getString("model"),
                    doc.getDouble("price"),
                    doc.getString("imageName") // fetch imageName
            ));
        }
        return products;
    }

    // adds product
    @FXML
    private void handleAddProduct() {
        String activeTab = getActiveCollectionName();
        TableView<Product> table = tables.get(activeTab);
        TextField[] f = fields.get(activeTab);
        if (table == null || f == null) return;

        String stock = f[0].getText().trim();
        String brand = f[1].getText().trim();
        String model = f[2].getText().trim();
        String priceText = f[3].getText().trim();
        String imageName = f[4].getText().trim(); // get imageName

        if (stock.isEmpty() || brand.isEmpty() || model.isEmpty() || priceText.isEmpty() || imageName.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Please fill in all fields!");
            return;
        }

        double price;
        try { price = Double.parseDouble(priceText); }
        catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Price must be a valid number!");
            return;
        }

        MongoCollection<Document> collection = DBConnectionHelper.getDatabase("Product-Details")
                .getCollection(activeTab);
        Document newProduct = new Document("stock", stock)
                .append("brand", brand)
                .append("model", model)
                .append("price", price)
                .append("imageName", imageName);
        collection.insertOne(newProduct);

        table.getItems().add(new Product(stock, brand, model, price, imageName));
        Arrays.stream(f).forEach(TextField::clear);
        showAlert(Alert.AlertType.INFORMATION, "Product added to " + activeTab + "!");
    }

    // edits the product
    @FXML
    private void handleEditProduct() {
        Product selected = getSelectedProduct();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No product selected!");
            return;
        }

        String activeTab = getActiveCollectionName();
        TextField[] f = fields.get(activeTab);

        TextField stockField = new TextField(selected.getStock());
        TextField brandField = new TextField(selected.getBrand());
        TextField modelField = new TextField(selected.getModel());
        TextField priceField = new TextField(String.valueOf(selected.getPrice()));
        TextField imageField = new TextField(selected.getImageName());

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.addRow(0, new Label("Stock:"), stockField);
        grid.addRow(1, new Label("Brand:"), brandField);
        grid.addRow(2, new Label("Model:"), modelField);
        grid.addRow(3, new Label("Price:"), priceField);
        grid.addRow(4, new Label("Image Name:"), imageField);

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Product");
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    String stock = stockField.getText().trim();
                    String brand = brandField.getText().trim();
                    String model = modelField.getText().trim();
                    double price = Double.parseDouble(priceField.getText().trim());
                    String imageName = imageField.getText().trim();

                    if (stock.isEmpty() || brand.isEmpty() || model.isEmpty() || imageName.isEmpty()) {
                        showAlert(Alert.AlertType.WARNING, "All fields must be filled!");
                        return;
                    }

                    MongoCollection<Document> collection = DBConnectionHelper.getDatabase("Product-Details")
                            .getCollection(activeTab);
                    collection.updateOne(new Document("model", selected.getModel()),
                            new Document("$set", new Document("stock", stock)
                                    .append("brand", brand)
                                    .append("model", model)
                                    .append("price", price)
                                    .append("imageName", imageName)));

                    selected.setStock(stock); selected.setBrand(brand);
                    selected.setModel(model); selected.setPrice(price);
                    selected.setImageName(imageName);
                    tables.get(activeTab).refresh();
                    showAlert(Alert.AlertType.INFORMATION, "Product updated!");
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Price must be a number!");
                }
            }
        });
    }

    // deletes the product
    @FXML
    private void handleDeleteProduct() {
        Product selected = getSelectedProduct();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No product selected!");
            return;
        }

        String activeTab = getActiveCollectionName();
        TableView<Product> table = tables.get(activeTab);

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setHeaderText(null);
        confirm.setContentText("Delete \"" + selected.getModel() + "\"?");
        confirm.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.OK) {
                MongoCollection<Document> collection = DBConnectionHelper.getDatabase("Product-Details")
                        .getCollection(activeTab);
                collection.deleteOne(new Document("model", selected.getModel()));
                table.getItems().remove(selected);
                showAlert(Alert.AlertType.INFORMATION, "Product deleted!");
            }
        });
    }

    // gets the selected product to edit or delete
    private Product getSelectedProduct() {
        String activeTab = getActiveCollectionName();
        TableView<Product> table = tables.get(activeTab);
        return table == null ? null : table.getSelectionModel().getSelectedItem();
    }

    // gets the active collection name from mongodb
    private String getActiveCollectionName() {
        if (tabPane == null || tabPane.getSelectionModel().getSelectedItem() == null)
            return "keyboard"; // default fallback
        return tabPane.getSelectionModel().getSelectedItem().getText().toLowerCase();
    }

    // shows alerts
    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // goes back to admin tools
    public void backAdmin(ActionEvent event) {
        app.switchTo("admin");
    }

    // === Inner Product Class ===
    public static class Product {
        private String stock, brand, model, imageName;
        private Double price;

        public Product(String stock, String brand, String model, Double price, String imageName) {
            this.stock = stock; this.brand = brand; this.model = model; this.price = price;
            this.imageName = imageName;
        }

        public String getStock() { return stock; }
        public String getBrand() { return brand; }
        public String getModel() { return model; }
        public Double getPrice() { return price; }
        public String getImageName() { return imageName; }

        public void setStock(String stock) { this.stock = stock; }
        public void setBrand(String brand) { this.brand = brand; }
        public void setModel(String model) { this.model = model; }
        public void setPrice(Double price) { this.price = price; }
        public void setImageName(String imageName) { this.imageName = imageName; }
    }
}
