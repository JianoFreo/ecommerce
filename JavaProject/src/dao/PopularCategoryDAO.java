package src.dao;

import java.sql.*;
import java.util.*;
import src.db.DatabaseConnection;

/**
 * PopularCategoryDAO - Tracks category usage and popularity
 * Updates whenever a user views or purchases products in a category
 */
public class PopularCategoryDAO {
    
    // Increment view count for a category
    public void recordCategoryView(int categoryID) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Check if category tracking exists
            String checkSQL = "SELECT * FROM category_popularity WHERE categoryID = ?";
            PreparedStatement checkPS = conn.prepareStatement(checkSQL);
            checkPS.setInt(1, categoryID);
            ResultSet rs = checkPS.executeQuery();
            
            if (rs.next()) {
                // Update existing record
                String sql = "UPDATE category_popularity SET viewCount = viewCount + 1, lastViewed = NOW() WHERE categoryID = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, categoryID);
                ps.executeUpdate();
            } else {
                // Create new record
                String sql = "INSERT INTO category_popularity (categoryID, viewCount, purchaseCount, lastViewed) VALUES (?, 1, 0, NOW())";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, categoryID);
                ps.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Increment purchase count for a category
    public void recordCategoryPurchase(int categoryID) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "UPDATE category_popularity SET purchaseCount = purchaseCount + 1 WHERE categoryID = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, categoryID);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Get top N popular categories by view count
    public List<Map<String, Object>> getTopPopularCategories(int limit) {
        List<Map<String, Object>> results = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT c.categoryID, c.name, cp.viewCount, cp.purchaseCount " +
                        "FROM categories c " +
                        "LEFT JOIN category_popularity cp ON c.categoryID = cp.categoryID " +
                        "WHERE c.parentCategoryID IS NULL " +
                        "ORDER BY cp.viewCount DESC LIMIT ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("categoryID", rs.getInt("categoryID"));
                row.put("name", rs.getString("name"));
                row.put("viewCount", rs.getInt("viewCount"));
                row.put("purchaseCount", rs.getInt("purchaseCount"));
                results.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }
    
    // Get top N popular categories by purchase count
    public List<Map<String, Object>> getTopPurchasedCategories(int limit) {
        List<Map<String, Object>> results = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT c.categoryID, c.name, cp.viewCount, cp.purchaseCount " +
                        "FROM categories c " +
                        "LEFT JOIN category_popularity cp ON c.categoryID = cp.categoryID " +
                        "WHERE c.parentCategoryID IS NULL " +
                        "ORDER BY cp.purchaseCount DESC LIMIT ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("categoryID", rs.getInt("categoryID"));
                row.put("name", rs.getString("name"));
                row.put("viewCount", rs.getInt("viewCount"));
                row.put("purchaseCount", rs.getInt("purchaseCount"));
                results.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }
}
