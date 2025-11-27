package src.model; // Package declaration

public class Product { // Product class represents a product entity
    private int id; // Product ID
    private String name; // Product name
    private double price; // Product price
    private int quantity; // Product quantity

    public Product() {} // Default constructor (empty product)

    public Product(int id, String name, double price, int quantity) { // Constructor with parameters
        this.id = id; // Assign id
        this.name = name; // Assign name
        this.price = price; // Assign price
        this.quantity = quantity; // Assign quantity
    }

    // Getters and setters allow controlled access to private fields (encapsulation)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
