package bcbfixhub.bcbfixhub.models;

public class Product {
    private int id;
    private String stock; // changed from int → String
    private String brand;
    private String model;
    private double price;
    private String imageName; // new field for product image

    public Product(int id, String stock, String brand, String model, double price, String imageName) {
        this.id = id;
        this.stock = stock;
        this.brand = brand;
        this.model = model;
        this.price = price;
        this.imageName = imageName;
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

    public String getImageName() {
        return imageName;
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

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }



    /**
     * Convenience method to get the full path for the product image.
     */
    public String getImagePath() {
        return "resources/bcbfixhub/bcbfixhub/product_images/" + imageName;
    }
}
