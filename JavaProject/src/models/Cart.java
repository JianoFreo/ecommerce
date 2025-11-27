package src.models;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private int cartID;
    private int userID;
    private List<CartItem> items;
    private double totalCost;

    public Cart() {
        this.items = new ArrayList<>();
        this.totalCost = 0.0;
    }

    public Cart(int cartID, int userID) {
        this.cartID = cartID;
        this.userID = userID;
        this.items = new ArrayList<>();
        this.totalCost = 0.0;
    }

    // Getters and Setters
    public int getCartID() { return cartID; }
    public void setCartID(int cartID) { this.cartID = cartID; }

    public int getUserID() { return userID; }
    public void setUserID(int userID) { this.userID = userID; }

    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) { 
        this.items = items;
        calculateTotal();
    }

    public double getTotalCost() { return totalCost; }

    public void addItem(CartItem item) {
        // Check if product already exists in cart
        for (CartItem existingItem : items) {
            if (existingItem.getProductID() == item.getProductID()) {
                existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity());
                calculateTotal();
                return;
            }
        }
        items.add(item);
        calculateTotal();
    }

    public void removeItem(int productID) {
        items.removeIf(item -> item.getProductID() == productID);
        calculateTotal();
    }

    public void clearCart() {
        items.clear();
        totalCost = 0.0;
    }

    private void calculateTotal() {
        totalCost = items.stream()
                .mapToDouble(CartItem::getSubtotal)
                .sum();
    }

    @Override
    public String toString() {
        return String.format("Cart[ID=%d, UserID=%d, Items=%d, Total=%.2f]",
                cartID, userID, items.size(), totalCost);
    }
}
