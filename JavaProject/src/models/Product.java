package src.models;

public class Product {
    private int productID;
    private String title;
    private String description;
    private double price;
    private int quantity;
    private int categoryID;
    private String imageUrl;
    private int lowStockThreshold;

    public Product() {
        this.lowStockThreshold = 10; // Default low stock threshold
    }

    public Product(int productID, String title, String description, double price, int quantity, int categoryID) {
        this.productID = productID;
        this.title = title;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.categoryID = categoryID;
        this.lowStockThreshold = 10;
    }

    // Getters and Setters
    public int getProductID() { return productID; }
    public void setProductID(int productID) { this.productID = productID; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getCategoryID() { return categoryID; }
    public void setCategoryID(int categoryID) { this.categoryID = categoryID; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public int getLowStockThreshold() { return lowStockThreshold; }
    public void setLowStockThreshold(int lowStockThreshold) { this.lowStockThreshold = lowStockThreshold; }

    public boolean isLowStock() {
        return quantity <= lowStockThreshold;
    }

    @Override
    public String toString() {
        return String.format("Product[ID=%d, Title=%s, Price=%.2f, Quantity=%d, LowStock=%s]",
                productID, title, price, quantity, isLowStock() ? "YES" : "NO");
    }
}
