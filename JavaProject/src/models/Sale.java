package src.models;

import java.sql.Timestamp;

public class Sale {
    private int saleID;
    private int orderID;
    private int productID;
    private String productTitle;
    private int quantity;
    private double unitPrice;
    private double totalPrice;
    private double discount;
    private Timestamp saleDate;

    public Sale() {}

    public Sale(int orderID, int productID, int quantity, double unitPrice, double discount) {
        this.orderID = orderID;
        this.productID = productID;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.discount = discount;
        this.totalPrice = (quantity * unitPrice) - discount;
    }

    // Getters and Setters
    public int getSaleID() { return saleID; }
    public void setSaleID(int saleID) { this.saleID = saleID; }

    public int getOrderID() { return orderID; }
    public void setOrderID(int orderID) { this.orderID = orderID; }

    public int getProductID() { return productID; }
    public void setProductID(int productID) { this.productID = productID; }

    public String getProductTitle() { return productTitle; }
    public void setProductTitle(String productTitle) { this.productTitle = productTitle; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public double getDiscount() { return discount; }
    public void setDiscount(double discount) { this.discount = discount; }

    public Timestamp getSaleDate() { return saleDate; }
    public void setSaleDate(Timestamp saleDate) { this.saleDate = saleDate; }

    public double getRevenue() {
        return totalPrice;
    }

    @Override
    public String toString() {
        return String.format("Sale[ID=%d, Product=%s, Qty=%d, Revenue=%.2f, Date=%s]",
                saleID, productTitle, quantity, totalPrice, saleDate);
    }
}
