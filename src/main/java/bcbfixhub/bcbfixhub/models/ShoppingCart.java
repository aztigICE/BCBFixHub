package bcbfixhub.bcbfixhub.models;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {

    private final List<Product> items = new ArrayList<>();

    public List<Product> getItems() {
        return items;
    }

    public void add(Product product) {
        items.add(product);
    }

    public void remove(Product product) {
        items.remove(product);
    }

    public void clear() {
        items.clear();
    }

    public double getSubtotal() {
        return items.stream().mapToDouble(Product::getPrice).sum();
    }

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
