package src.model;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private int cartID;
    private int userID;
    private List<CartItem> items;
    private double totalCost;

    // Default constructor
    public Cart() {
        this.items = new ArrayList<>();
    }

    // Constructor with fields
    public Cart(int cartID, int userID) {
        this.cartID = cartID;
        this.userID = userID;
        this.items = new ArrayList<>();
    }

    // Getters and Setters
    public int getCartID() {
        return cartID;
    }

    public void setCartID(int cartID) {
        this.cartID = cartID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
        calculateTotal();
    }

    public double getTotalCost() {
        return totalCost;
    }

    // Add item to cart
    public void addItem(CartItem item) {
        items.add(item);
        calculateTotal();
    }

    // Remove item from cart
    public void removeItem(int cartItemID) {
        items.removeIf(item -> item.getCartItemID() == cartItemID);
        calculateTotal();
    }

    // Calculate total cost
    public void calculateTotal() {
        totalCost = 0;
        for (CartItem item : items) {
            totalCost += item.getSubtotal();
        }
    }

    // Clear all items
    public void clearCart() {
        items.clear();
        totalCost = 0;
    }

    // Get item count
    public int getItemCount() {
        return items.size();
    }
}
