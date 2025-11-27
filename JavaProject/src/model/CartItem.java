package src.model;

public class CartItem {
    private int cartItemID;
    private int cartID;
    private int productID;
    private String productName;
    private double productPrice;
    private int quantity;
    private double subtotal;

    // Default constructor
    public CartItem() {}

    // Constructor with all fields
    public CartItem(int cartItemID, int cartID, int productID, String productName, 
                    double productPrice, int quantity) {
        this.cartItemID = cartItemID;
        this.cartID = cartID;
        this.productID = productID;
        this.productName = productName;
        this.productPrice = productPrice;
        this.quantity = quantity;
        this.subtotal = productPrice * quantity;
    }

    // Getters and Setters
    public int getCartItemID() {
        return cartItemID;
    }

    public void setCartItemID(int cartItemID) {
        this.cartItemID = cartItemID;
    }

    public int getCartID() {
        return cartID;
    }

    public void setCartID(int cartID) {
        this.cartID = cartID;
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
        this.subtotal = productPrice * quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        this.subtotal = productPrice * quantity;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void calculateSubtotal() {
        this.subtotal = productPrice * quantity;
    }
}
