package src.model; // Package declaration

public class Product { // Product class represents a product entity
    private int id; // Product ID
    private String name; // Product name
    private String description; // Product description
    private double price; // Product price
    private int quantity; // Product quantity
    private int categoryID; // Foreign key to category
    private String categoryName; // Category name (for display)

    public Product() {} // Default constructor (empty product)

    // Constructor for basic product (backward compatibility)
    public Product(int id, String name, double price, int quantity) { 
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    // Constructor with all fields
    public Product(int id, String name, String description, double price, int quantity, int categoryID) {
        this.id = id;
        this.name = name;
        this.description = description;
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

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                '}';
    }
}
