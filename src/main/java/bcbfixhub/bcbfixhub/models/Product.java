package bcbfixhub.bcbfixhub.models;

public class Product {
    private int id;
    private String stock; // changed from int → String
    private String brand;
    private String model;
    private double price;

    public Product(int id, String stock, String brand, String model, double price) {
        this.id = id;
        this.stock = stock;
        this.brand = brand;
        this.model = model;
        this.price = price;
    }

    // === Getters ===
    public int getId() {
        return id;
    }

    public String getStock() {
        return stock;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public double getPrice() {
        return price;
    }

    // === Setters ===
    public void setId(int id) {
        this.id = id;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
