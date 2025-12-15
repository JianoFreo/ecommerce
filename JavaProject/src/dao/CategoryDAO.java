package src.dao;

import java.sql.*;
import java.util.*;
import src.db.DatabaseConnection;
import src.model.Category;

public class CategoryDAO {

    // CREATE - Add new category
    public boolean addCategory(Category category) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO categories (name, description) VALUES (?, ?)");
            ps.setString(1, category.getName());
            ps.setString(2, category.getDescription());
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // READ - Get all categories
    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM categories");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                categories.add(new Category(
                    rs.getInt("categoryID"),
                    rs.getString("name"),
                    rs.getString("description")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return categories;
    }

    // READ - Get category by ID
    public Category getCategoryById(int categoryID) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM categories WHERE categoryID=?");
            ps.setInt(1, categoryID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Category(
                    rs.getInt("categoryID"),
                    rs.getString("name"),
                    rs.getString("description")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // UPDATE - Update category
    public boolean updateCategory(Category category) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "UPDATE categories SET name=?, description=? WHERE categoryID=?");
            ps.setString(1, category.getName());
            ps.setString(2, category.getDescription());
            ps.setInt(3, category.getCategoryID());
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // DELETE - Delete category
    public boolean deleteCategory(int categoryID) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM categories WHERE categoryID=?");
            ps.setInt(1, categoryID);
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get main categories (no parent)
    public List<Category> getMainCategories() {
        List<Category> categories = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM categories WHERE parentCategoryID IS NULL ORDER BY name";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                categories.add(new Category(
                    rs.getInt("categoryID"),
                    rs.getString("name"),
                    rs.getString("description")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return categories;
    }
    
    // Get subcategories of a parent
    public List<Category> getSubcategories(int parentCategoryID) {
        List<Category> categories = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM categories WHERE parentCategoryID = ? ORDER BY name";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, parentCategoryID);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                categories.add(new Category(
                    rs.getInt("categoryID"),
                    rs.getString("name"),
                    rs.getString("description")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return categories;
    }
}
