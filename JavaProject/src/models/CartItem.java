package src.models;

public class CartItem {
    private int cartItemID;
    private int cartID;
    private int productID;
    private String productTitle;
    private int quantity;
    private double price;
    private double subtotal;

    public CartItem() {}

    public CartItem(int productID, String productTitle, int quantity, double price) {
        this.productID = productID;
        this.productTitle = productTitle;
        this.quantity = quantity;
        this.price = price;
        this.subtotal = quantity * price;
    }

    // Getters and Setters
    public int getCartItemID() { return cartItemID; }
    public void setCartItemID(int cartItemID) { this.cartItemID = cartItemID; }

    public int getCartID() { return cartID; }
    public void setCartID(int cartID) { this.cartID = cartID; }

    public int getProductID() { return productID; }
    public void setProductID(int productID) { this.productID = productID; }

    public String getProductTitle() { return productTitle; }
    public void setProductTitle(String productTitle) { this.productTitle = productTitle; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { 
        this.quantity = quantity;
        this.subtotal = quantity * price;
    }

    public double getPrice() { return price; }
    public void setPrice(double price) { 
        this.price = price;
        this.subtotal = quantity * price;
    }

    public double getSubtotal() { return subtotal; }

    @Override
    public String toString() {
        return String.format("CartItem[Product=%s, Qty=%d, Price=%.2f, Subtotal=%.2f]",
                productTitle, quantity, price, subtotal);
    }
}
