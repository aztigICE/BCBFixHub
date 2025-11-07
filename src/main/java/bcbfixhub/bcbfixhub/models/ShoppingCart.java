package bcbfixhub.bcbfixhub.models;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {

    private final List<Product> items = new ArrayList<>();

    // gets the list of products
    public List<Product> getItems() {
        return items;
    }

    // adds products to the shopping cart
    public void add(Product product) {
        items.add(product);
    }

    // removes the product from the shopping cart
    public void remove(Product product) {
        items.remove(product);
    }

    // clears the shopping cart
    public void clear() {
        items.clear();
    }

    // gets the total sum of the products
    public double getSubtotal() {
        return items.stream().mapToDouble(Product::getPrice).sum();
    }

    // products to add in the cart
    public static class Product {
        private final String brand;
        private final String model;
        private final double price;

        public Product(String brand, String model, double price) {
            this.brand = brand;
            this.model = model;
            this.price = price;
        }

        public String getBrand() { return brand; }
        public String getModel() { return model; }
        public double getPrice() { return price; }
    }
}
