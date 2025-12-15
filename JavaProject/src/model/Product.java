package src.model; // Package declaration

public class Product { // Product class represents a product entity
    private int id; // Product ID
    private String name; // Product name
    private String description; // Product description
    private double price; // Product selling price
    private double costPrice; // Product cost price (supplier price)
    private int quantity; // Product quantity
    private int categoryID; // Foreign key to category
    private String categoryName; // Category name (for display)
    private byte[] imageData; // Image binary data

    public Product() {} // Default constructor (empty product)

    // Constructor for basic product (backward compatibility)
    public Product(int id, String name, double price, int quantity) { 
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.costPrice = 0;
    }

    // Constructor with all fields
    public Product(int id, String name, String description, double price, int quantity, int categoryID) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.categoryID = categoryID;
        this.costPrice = 0;
    }

    // Constructor with cost price and selling price
    public Product(int id, String name, String description, double costPrice, double price, int quantity, int categoryID) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.costPrice = costPrice;
        this.price = price;
        this.quantity = quantity;
        this.categoryID = categoryID;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public double getCostPrice() { return costPrice; }
    public void setCostPrice(double costPrice) { this.costPrice = costPrice; }

    public double getProfit() { 
        return price - costPrice; 
    }

    public double getProfitMargin() {
        if (costPrice == 0) return 0;
        return ((price - costPrice) / costPrice) * 100;
    }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getCategoryID() { return categoryID; }
    public void setCategoryID(int categoryID) { this.categoryID = categoryID; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    // Check if product is low in stock (less than 10 items)
    public boolean isLowStock() {
        return quantity < 10;
    }
    
    public byte[] getImageData() { return imageData; }
    public void setImageData(byte[] imageData) { this.imageData = imageData; }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", costPrice=" + costPrice +
                ", sellingPrice=" + price +
                ", profit=" + getProfit() +
                ", quantity=" + quantity +
                '}';
    }
}
