package src.dao;

import java.sql.*;
import java.util.*;
import src.db.DatabaseConnection;
import src.model.Cart;
import src.model.CartItem;

/**
 * CartPersistenceDAO - Saves and loads cart state to/from database
 * Allows users to have persistent carts that survive logout/login
 */
public class CartPersistenceDAO {
    
    // Save cart to database
    public void saveCart(int userID, Cart cart) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Clear old cart items for this user
            PreparedStatement delPS = conn.prepareStatement("DELETE FROM saved_carts WHERE userID = ?");
            delPS.setInt(1, userID);
            delPS.executeUpdate();
            
            // Insert each cart item
            String sql = "INSERT INTO saved_carts (userID, productID, productName, quantity, price, subtotal, imageUrl, savedAt) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, NOW())";
            PreparedStatement ps = conn.prepareStatement(sql);
            
            for (CartItem item : cart.getItems()) {
                ps.setInt(1, userID);
                ps.setInt(2, item.getProductID());
                ps.setString(3, item.getProductName());
                ps.setInt(4, item.getQuantity());
                ps.setDouble(5, item.getPrice());
                ps.setDouble(6, item.getSubtotal());
                ps.setString(7, item.getImageUrl());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Load cart from database for a specific user
    public Cart loadCart(int userID) {
        Cart cart = new Cart();
        cart.setUserID(userID);
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM saved_carts WHERE userID = ? ORDER BY savedAt DESC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userID);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                CartItem item = new CartItem(
                    rs.getInt("productID"),
                    rs.getString("productName"),
                    rs.getDouble("price"),
                    rs.getInt("quantity")
                );
                item.setImageUrl(rs.getString("imageUrl"));
                cart.addItem(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cart;
    }
    
    // Clear saved cart for user
    public void clearSavedCart(int userID) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM saved_carts WHERE userID = ?");
            ps.setInt(1, userID);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
