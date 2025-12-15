package src.dao;

import java.sql.*;
import java.util.*;
import src.db.DatabaseConnection;
import src.model.Product;

/**
 * ProductSearchDAO - Search and filter products by various criteria
 * Supports text search, price range, category, rating, and sorting
 */
public class ProductSearchDAO {
    
    // Search products by name/description
    public List<Product> searchProducts(String keyword) {
        List<Product> products = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT p.*, c.name as categoryName FROM products p " +
                        "LEFT JOIN categories c ON p.categoryID = c.categoryID " +
                        "WHERE p.name LIKE ? OR p.description LIKE ? " +
                        "ORDER BY p.name";
            PreparedStatement ps = conn.prepareStatement(sql);
            String searchTerm = "%" + keyword + "%";
            ps.setString(1, searchTerm);
            ps.setString(2, searchTerm);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                products.add(createProductFromRS(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }
    
    // Filter by price range
    public List<Product> filterByPriceRange(double minPrice, double maxPrice) {
        List<Product> products = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT p.*, c.name as categoryName FROM products p " +
                        "LEFT JOIN categories c ON p.categoryID = c.categoryID " +
                        "WHERE p.price BETWEEN ? AND ? " +
                        "ORDER BY p.price";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setDouble(1, minPrice);
            ps.setDouble(2, maxPrice);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                products.add(createProductFromRS(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }
    
    // Filter by category
    public List<Product> filterByCategory(int categoryID) {
        List<Product> products = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT p.*, c.name as categoryName FROM products p " +
                        "LEFT JOIN categories c ON p.categoryID = c.categoryID " +
                        "WHERE p.categoryID = ? " +
                        "ORDER BY p.name";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, categoryID);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                products.add(createProductFromRS(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }
    
    // Filter by minimum rating
    public List<Product> filterByRating(double minRating) {
        List<Product> products = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT p.*, c.name as categoryName FROM products p " +
                        "LEFT JOIN categories c ON p.categoryID = c.categoryID " +
                        "WHERE p.averageRating >= ? " +
                        "ORDER BY p.averageRating DESC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setDouble(1, minRating);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                products.add(createProductFromRS(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }
    
    // Advanced search with multiple filters
    public List<Product> advancedSearch(String keyword, Integer categoryID, Double minPrice, Double maxPrice, Double minRating) {
        List<Product> products = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            StringBuilder sql = new StringBuilder(
                "SELECT p.*, c.name as categoryName FROM products p " +
                "LEFT JOIN categories c ON p.categoryID = c.categoryID WHERE 1=1"
            );
            
            List<Object> params = new ArrayList<>();
            
            if (keyword != null && !keyword.isEmpty()) {
                sql.append(" AND (p.name LIKE ? OR p.description LIKE ?)");
                String searchTerm = "%" + keyword + "%";
                params.add(searchTerm);
                params.add(searchTerm);
            }
            
            if (categoryID != null) {
                sql.append(" AND p.categoryID = ?");
                params.add(categoryID);
            }
            
            if (minPrice != null && maxPrice != null) {
                sql.append(" AND p.price BETWEEN ? AND ?");
                params.add(minPrice);
                params.add(maxPrice);
            }
            
            if (minRating != null) {
                sql.append(" AND p.averageRating >= ?");
                params.add(minRating);
            }
            
            sql.append(" ORDER BY p.name");
            
            PreparedStatement ps = conn.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof String) {
                    ps.setString(i + 1, (String) param);
                } else if (param instanceof Integer) {
                    ps.setInt(i + 1, (Integer) param);
                } else if (param instanceof Double) {
                    ps.setDouble(i + 1, (Double) param);
                }
            }
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                products.add(createProductFromRS(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }
    
    // Sort products by price (ascending)
    public List<Product> sortByPriceAsc() {
        List<Product> products = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT p.*, c.name as categoryName FROM products p " +
                        "LEFT JOIN categories c ON p.categoryID = c.categoryID " +
                        "ORDER BY p.price ASC";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                products.add(createProductFromRS(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }
    
    // Sort products by price (descending)
    public List<Product> sortByPriceDesc() {
        List<Product> products = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT p.*, c.name as categoryName FROM products p " +
                        "LEFT JOIN categories c ON p.categoryID = c.categoryID " +
                        "ORDER BY p.price DESC";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                products.add(createProductFromRS(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }
    
    // Sort by rating (highest first)
    public List<Product> sortByRating() {
        List<Product> products = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT p.*, c.name as categoryName FROM products p " +
                        "LEFT JOIN categories c ON p.categoryID = c.categoryID " +
                        "ORDER BY p.averageRating DESC";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                products.add(createProductFromRS(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }
    
    private Product createProductFromRS(ResultSet rs) throws SQLException {
        Product product = new Product(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("description"),
            rs.getDouble("costPrice"),
            rs.getDouble("price"),
            rs.getInt("quantity"),
            rs.getInt("categoryID")
        );
        product.setCategoryName(rs.getString("categoryName"));
        product.setImageData(rs.getBytes("imageData"));
        product.setAverageRating(rs.getDouble("averageRating"));
        product.setTotalReviews(rs.getInt("totalReviews"));
        return product;
    }
}
