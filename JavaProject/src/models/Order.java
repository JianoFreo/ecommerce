package src.models;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private int orderID;
    private int userID;
    private Timestamp orderDate;
    private String status; // Pending, Processing, Shipped, Delivered, Cancelled
    private double totalAmount;
    private String shippingAddress;
    private List<OrderItem> items;

    public Order() {
        this.items = new ArrayList<>();
        this.status = "Pending";
    }

    public Order(int orderID, int userID, String status, double totalAmount, String shippingAddress) {
        this.orderID = orderID;
        this.userID = userID;
        this.status = status;
        this.totalAmount = totalAmount;
        this.shippingAddress = shippingAddress;
        this.items = new ArrayList<>();
    }

    // Getters and Setters
    public int getOrderID() { return orderID; }
    public void setOrderID(int orderID) { this.orderID = orderID; }

    public int getUserID() { return userID; }
    public void setUserID(int userID) { this.userID = userID; }

    public Timestamp getOrderDate() { return orderDate; }
    public void setOrderDate(Timestamp orderDate) { this.orderDate = orderDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }

    public void addItem(OrderItem item) {
        this.items.add(item);
    }

    @Override
    public String toString() {
        return String.format("Order[ID=%d, UserID=%d, Status=%s, Total=%.2f, Date=%s]",
                orderID, userID, status, totalAmount, orderDate);
    }
}
