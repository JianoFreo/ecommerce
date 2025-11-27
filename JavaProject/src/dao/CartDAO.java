package src.dao;

import java.sql.*;
import java.util.*;
import src.db.DatabaseConnection;
import src.model.Cart;
import src.model.CartItem;

public class CartDAO {

    // CREATE - Create cart for user (if not exists)
    public int createCart(int userID) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Check if cart already exists
            PreparedStatement checkPs = conn.prepareStatement(
                "SELECT cartID FROM carts WHERE userID=?");
            checkPs.setInt(1, userID);
            ResultSet rs = checkPs.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("cartID"); // Return existing cart ID
            }
            
            // Create new cart
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO carts (userID, totalCost) VALUES (?, 0)",
                Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, userID);
            ps.executeUpdate();
            
            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    // CREATE - Add item to cart
    public boolean addItemToCart(int cartID, int productID, int quantity) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Check if item already exists in cart
            PreparedStatement checkPs = conn.prepareStatement(
                "SELECT cartItemID, quantity FROM cart_items WHERE cartID=? AND productID=?");
            checkPs.setInt(1, cartID);
            checkPs.setInt(2, productID);
            ResultSet rs = checkPs.executeQuery();
            
            if (rs.next()) {
                // Update existing item quantity
                int newQuantity = rs.getInt("quantity") + quantity;
                PreparedStatement updatePs = conn.prepareStatement(
                    "UPDATE cart_items SET quantity=? WHERE cartItemID=?");
                updatePs.setInt(1, newQuantity);
                updatePs.setInt(2, rs.getInt("cartItemID"));
                updatePs.executeUpdate();
            } else {
                // Insert new item
                PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO cart_items (cartID, productID, quantity) VALUES (?, ?, ?)");
                ps.setInt(1, cartID);
                ps.setInt(2, productID);
                ps.setInt(3, quantity);
                ps.executeUpdate();
            }
            
            updateCartTotal(cartID);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // READ - Get cart by user ID
    public Cart getCartByUserId(int userID) {
        Cart cart = null;
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Get cart
            PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM carts WHERE userID=?");
            ps.setInt(1, userID);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                cart = new Cart(rs.getInt("cartID"), rs.getInt("userID"));
                
                // Get cart items
                PreparedStatement itemsPs = conn.prepareStatement(
                    "SELECT ci.*, p.name, p.price FROM cart_items ci " +
                    "JOIN products p ON ci.productID = p.id WHERE ci.cartID=?");
                itemsPs.setInt(1, cart.getCartID());
                ResultSet itemsRs = itemsPs.executeQuery();
                
                while (itemsRs.next()) {
                    CartItem item = new CartItem(
                        itemsRs.getInt("cartItemID"),
                        itemsRs.getInt("cartID"),
                        itemsRs.getInt("productID"),
                        itemsRs.getString("name"),
                        itemsRs.getDouble("price"),
                        itemsRs.getInt("quantity")
                    );
                    cart.addItem(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cart;
    }

    // UPDATE - Update item quantity in cart
    public boolean updateCartItemQuantity(int cartItemID, int quantity) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "UPDATE cart_items SET quantity=? WHERE cartItemID=?");
            ps.setInt(1, quantity);
            ps.setInt(2, cartItemID);
            ps.executeUpdate();
            
            // Get cartID to update total
            PreparedStatement getCartPs = conn.prepareStatement(
                "SELECT cartID FROM cart_items WHERE cartItemID=?");
            getCartPs.setInt(1, cartItemID);
            ResultSet rs = getCartPs.executeQuery();
            if (rs.next()) {
                updateCartTotal(rs.getInt("cartID"));
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // DELETE - Remove item from cart
    public boolean removeItemFromCart(int cartItemID) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Get cartID before deletion
            PreparedStatement getCartPs = conn.prepareStatement(
                "SELECT cartID FROM cart_items WHERE cartItemID=?");
            getCartPs.setInt(1, cartItemID);
            ResultSet rs = getCartPs.executeQuery();
            int cartID = -1;
            if (rs.next()) {
                cartID = rs.getInt("cartID");
            }
            
            // Delete item
            PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM cart_items WHERE cartItemID=?");
            ps.setInt(1, cartItemID);
            ps.executeUpdate();
            
            if (cartID != -1) {
                updateCartTotal(cartID);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // DELETE - Clear all items from cart
    public boolean clearCart(int cartID) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM cart_items WHERE cartID=?");
            ps.setInt(1, cartID);
            ps.executeUpdate();
            
            updateCartTotal(cartID);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Helper method to update cart total
    private void updateCartTotal(int cartID) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "UPDATE carts SET totalCost = " +
                "(SELECT COALESCE(SUM(ci.quantity * p.price), 0) FROM cart_items ci " +
                "JOIN products p ON ci.productID = p.id WHERE ci.cartID = ?) " +
                "WHERE cartID = ?");
            ps.setInt(1, cartID);
            ps.setInt(2, cartID);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
