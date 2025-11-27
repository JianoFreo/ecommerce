package src.dao;

import java.sql.*;
import java.util.*;
import src.db.DatabaseConnection;
import src.model.Order;
import src.model.OrderItem;
import src.model.Cart;
import src.model.CartItem;

public class OrderDAO {

    // CREATE - Place order from cart
    public int createOrder(int userID, String shippingAddress, Cart cart) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            // Create order
            PreparedStatement orderPs = conn.prepareStatement(
                "INSERT INTO orders (userID, orderDate, status, totalAmount, shippingAddress) " +
                "VALUES (?, NOW(), 'Pending', ?, ?)",
                Statement.RETURN_GENERATED_KEYS);
            orderPs.setInt(1, userID);
            orderPs.setDouble(2, cart.getTotalCost());
            orderPs.setString(3, shippingAddress);
            orderPs.executeUpdate();
            
            ResultSet generatedKeys = orderPs.getGeneratedKeys();
            int orderID = -1;
            if (generatedKeys.next()) {
                orderID = generatedKeys.getInt(1);
            }
            
            // Add order items
            PreparedStatement itemPs = conn.prepareStatement(
                "INSERT INTO order_items (orderID, productID, price, quantity) VALUES (?, ?, ?, ?)");
            
            for (CartItem cartItem : cart.getItems()) {
                itemPs.setInt(1, orderID);
                itemPs.setInt(2, cartItem.getProductID());
                itemPs.setDouble(3, cartItem.getProductPrice());
                itemPs.setInt(4, cartItem.getQuantity());
                itemPs.executeUpdate();
                
                // Update product quantity
                PreparedStatement updateProductPs = conn.prepareStatement(
                    "UPDATE products SET quantity = quantity - ? WHERE id = ?");
                updateProductPs.setInt(1, cartItem.getQuantity());
                updateProductPs.setInt(2, cartItem.getProductID());
                updateProductPs.executeUpdate();
            }
            
            // Clear cart
            PreparedStatement clearCartPs = conn.prepareStatement(
                "DELETE FROM cart_items WHERE cartID = ?");
            clearCartPs.setInt(1, cart.getCartID());
            clearCartPs.executeUpdate();
            
            conn.commit(); // Commit transaction
            return orderID;
            
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback on error
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return -1;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // READ - Get all orders
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT o.*, u.name as userName FROM orders o " +
                "JOIN users u ON o.userID = u.userID ORDER BY o.orderDate DESC");
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Order order = new Order(
                    rs.getInt("orderID"),
                    rs.getInt("userID"),
                    rs.getTimestamp("orderDate"),
                    rs.getString("status"),
                    rs.getDouble("totalAmount"),
                    rs.getString("shippingAddress")
                );
                order.setUserName(rs.getString("userName"));
                loadOrderItems(order);
                orders.add(order);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return orders;
    }

    // READ - Get orders by user ID
    public List<Order> getOrdersByUserId(int userID) {
        List<Order> orders = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM orders WHERE userID=? ORDER BY orderDate DESC");
            ps.setInt(1, userID);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Order order = new Order(
                    rs.getInt("orderID"),
                    rs.getInt("userID"),
                    rs.getTimestamp("orderDate"),
                    rs.getString("status"),
                    rs.getDouble("totalAmount"),
                    rs.getString("shippingAddress")
                );
                loadOrderItems(order);
                orders.add(order);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return orders;
    }

    // READ - Get order by ID
    public Order getOrderById(int orderID) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT o.*, u.name as userName FROM orders o " +
                "JOIN users u ON o.userID = u.userID WHERE o.orderID=?");
            ps.setInt(1, orderID);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                Order order = new Order(
                    rs.getInt("orderID"),
                    rs.getInt("userID"),
                    rs.getTimestamp("orderDate"),
                    rs.getString("status"),
                    rs.getDouble("totalAmount"),
                    rs.getString("shippingAddress")
                );
                order.setUserName(rs.getString("userName"));
                loadOrderItems(order);
                return order;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Helper method to load order items
    private void loadOrderItems(Order order) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT oi.*, p.name as productName FROM order_items oi " +
                "JOIN products p ON oi.productID = p.id WHERE oi.orderID=?");
            ps.setInt(1, order.getOrderID());
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                OrderItem item = new OrderItem(
                    rs.getInt("orderItemID"),
                    rs.getInt("orderID"),
                    rs.getInt("productID"),
                    rs.getString("productName"),
                    rs.getDouble("price"),
                    rs.getInt("quantity")
                );
                order.addItem(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // UPDATE - Update order status
    public boolean updateOrderStatus(int orderID, String status) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "UPDATE orders SET status=? WHERE orderID=?");
            ps.setString(1, status);
            ps.setInt(2, orderID);
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // DELETE - Cancel/Delete order (only if status is Pending)
    public boolean cancelOrder(int orderID) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Check if order is pending
            PreparedStatement checkPs = conn.prepareStatement(
                "SELECT status FROM orders WHERE orderID=?");
            checkPs.setInt(1, orderID);
            ResultSet rs = checkPs.executeQuery();
            
            if (rs.next() && "Pending".equals(rs.getString("status"))) {
                // Restore product quantities
                PreparedStatement restorePs = conn.prepareStatement(
                    "UPDATE products p JOIN order_items oi ON p.id = oi.productID " +
                    "SET p.quantity = p.quantity + oi.quantity WHERE oi.orderID = ?");
                restorePs.setInt(1, orderID);
                restorePs.executeUpdate();
                
                // Delete order items
                PreparedStatement deleteItemsPs = conn.prepareStatement(
                    "DELETE FROM order_items WHERE orderID=?");
                deleteItemsPs.setInt(1, orderID);
                deleteItemsPs.executeUpdate();
                
                // Delete order
                PreparedStatement deleteOrderPs = conn.prepareStatement(
                    "DELETE FROM orders WHERE orderID=?");
                deleteOrderPs.setInt(1, orderID);
                deleteOrderPs.executeUpdate();
                
                conn.commit();
                return true;
            }
            
            conn.rollback();
            return false;
            
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Get order statistics
    public Map<String, Integer> getOrderStatistics() {
        Map<String, Integer> stats = new HashMap<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT status, COUNT(*) as count FROM orders GROUP BY status");
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                stats.put(rs.getString("status"), rs.getInt("count"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stats;
    }
}
