package src.models;

public class OrderItem {
    private int orderItemID;
    private int orderID;
    private int productID;
    private String productTitle;
    private int quantity;
    private double price;
    private double subtotal;

    public OrderItem() {}

    public OrderItem(int productID, String productTitle, int quantity, double price) {
        this.productID = productID;
        this.productTitle = productTitle;
        this.quantity = quantity;
        this.price = price;
        this.subtotal = quantity * price;
    }

    // Getters and Setters
    public int getOrderItemID() { return orderItemID; }
    public void setOrderItemID(int orderItemID) { this.orderItemID = orderItemID; }

    public int getOrderID() { return orderID; }
    public void setOrderID(int orderID) { this.orderID = orderID; }

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
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    @Override
    public String toString() {
        return String.format("OrderItem[Product=%s, Qty=%d, Price=%.2f, Subtotal=%.2f]",
                productTitle, quantity, price, subtotal);
    }
}
