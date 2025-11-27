package src.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import src.db.DatabaseConnection;
import src.models.Category;

public class CategoryDAO {
    
    // Create
    public boolean createCategory(Category category) {
        String sql = "INSERT INTO categories (name, description, parentCategoryID) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, category.getName());
            stmt.setString(2, category.getDescription());
            stmt.setInt(3, category.getParentCategoryID());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    category.setCategoryID(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Read - Get by ID
    public Category getCategoryById(int categoryID) {
        String sql = "SELECT * FROM categories WHERE categoryID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, categoryID);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractCategoryFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Read - Get All
    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM categories ORDER BY name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                categories.add(extractCategoryFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }
    
    // Update
    public boolean updateCategory(Category category) {
        String sql = "UPDATE categories SET name=?, description=?, parentCategoryID=? WHERE categoryID=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, category.getName());
            stmt.setString(2, category.getDescription());
            stmt.setInt(3, category.getParentCategoryID());
            stmt.setInt(4, category.getCategoryID());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Delete
    public boolean deleteCategory(int categoryID) {
        String sql = "DELETE FROM categories WHERE categoryID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, categoryID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Helper method
    private Category extractCategoryFromResultSet(ResultSet rs) throws SQLException {
        Category category = new Category();
        category.setCategoryID(rs.getInt("categoryID"));
        category.setName(rs.getString("name"));
        category.setDescription(rs.getString("description"));
        category.setParentCategoryID(rs.getInt("parentCategoryID"));
        return category;
    }
}
