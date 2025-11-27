package src.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import src.db.DatabaseConnection;
import src.models.Order;
import src.models.OrderItem;

public class OrderDAO {
    
    // Create Order
    public boolean createOrder(Order order) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Insert order
            String orderSql = "INSERT INTO orders (userID, status, totalAmount, shippingAddress) VALUES (?, ?, ?, ?)";
            PreparedStatement orderStmt = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);
            
            orderStmt.setInt(1, order.getUserID());
            orderStmt.setString(2, order.getStatus());
            orderStmt.setDouble(3, order.getTotalAmount());
            orderStmt.setString(4, order.getShippingAddress());
            
            orderStmt.executeUpdate();
            
            ResultSet rs = orderStmt.getGeneratedKeys();
            if (rs.next()) {
                int orderID = rs.getInt(1);
                order.setOrderID(orderID);
                
                // Insert order items
                String itemSql = "INSERT INTO order_items (orderID, productID, quantity, price, subtotal) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement itemStmt = conn.prepareStatement(itemSql);
                
                for (OrderItem item : order.getItems()) {
                    itemStmt.setInt(1, orderID);
                    itemStmt.setInt(2, item.getProductID());
                    itemStmt.setInt(3, item.getQuantity());
                    itemStmt.setDouble(4, item.getPrice());
                    itemStmt.setDouble(5, item.getSubtotal());
                    itemStmt.addBatch();
                }
                
                itemStmt.executeBatch();
            }
            
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
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
        return false;
    }
    
    // Read - Get by ID
    public Order getOrderById(int orderID) {
        String sql = "SELECT * FROM orders WHERE orderID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, orderID);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Order order = extractOrderFromResultSet(rs);
                order.setItems(getOrderItems(orderID));
                return order;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Read - Get All
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders ORDER BY orderDate DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Order order = extractOrderFromResultSet(rs);
                order.setItems(getOrderItems(order.getOrderID()));
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }
    
    // Get Orders by User
    public List<Order> getOrdersByUser(int userID) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE userID = ? ORDER BY orderDate DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Order order = extractOrderFromResultSet(rs);
                order.setItems(getOrderItems(order.getOrderID()));
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }
    
    // Update Order Status
    public boolean updateOrderStatus(int orderID, String status) {
        String sql = "UPDATE orders SET status = ? WHERE orderID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            stmt.setInt(2, orderID);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Delete
    public boolean deleteOrder(int orderID) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Delete order items first
            String deleteItems = "DELETE FROM order_items WHERE orderID = ?";
            PreparedStatement itemStmt = conn.prepareStatement(deleteItems);
            itemStmt.setInt(1, orderID);
            itemStmt.executeUpdate();
            
            // Delete order
            String deleteOrder = "DELETE FROM orders WHERE orderID = ?";
            PreparedStatement orderStmt = conn.prepareStatement(deleteOrder);
            orderStmt.setInt(1, orderID);
            orderStmt.executeUpdate();
            
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
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
        return false;
    }
    
    // Get Order Items
    private List<OrderItem> getOrderItems(int orderID) {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT oi.*, p.title FROM order_items oi JOIN products p ON oi.productID = p.productID WHERE oi.orderID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, orderID);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                OrderItem item = new OrderItem();
                item.setOrderItemID(rs.getInt("orderItemID"));
                item.setOrderID(rs.getInt("orderID"));
                item.setProductID(rs.getInt("productID"));
                item.setProductTitle(rs.getString("title"));
                item.setQuantity(rs.getInt("quantity"));
                item.setPrice(rs.getDouble("price"));
                item.setSubtotal(rs.getDouble("subtotal"));
                items.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }
    
    // Helper method
    private Order extractOrderFromResultSet(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setOrderID(rs.getInt("orderID"));
        order.setUserID(rs.getInt("userID"));
        order.setOrderDate(rs.getTimestamp("orderDate"));
        order.setStatus(rs.getString("status"));
        order.setTotalAmount(rs.getDouble("totalAmount"));
        order.setShippingAddress(rs.getString("shippingAddress"));
        return order;
    }
}
