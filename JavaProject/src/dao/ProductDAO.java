package src.dao;

import java.sql.*;
import java.util.*;
import src.db.DatabaseConnection;
import src.model.Product;

public class ProductDAO {

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT p.*, c.name as categoryName FROM products p LEFT JOIN categories c ON p.categoryID = c.categoryID";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Product product = new Product(rs.getInt("id"), rs.getString("name"), rs.getString("description"),
                        rs.getDouble("costPrice"), rs.getDouble("price"), rs.getInt("quantity"), rs.getInt("categoryID"));
                product.setCategoryName(rs.getString("categoryName"));
                product.setImageUrl(rs.getString("imageUrl"));
                products.add(product);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }

    public List<Product> getProductsByCategory(int categoryID) {
        List<Product> products = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT p.*, c.name as categoryName FROM products p LEFT JOIN categories c ON p.categoryID = c.categoryID WHERE p.categoryID=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, categoryID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Product product = new Product(rs.getInt("id"), rs.getString("name"), rs.getString("description"),
                        rs.getDouble("costPrice"), rs.getDouble("price"), rs.getInt("quantity"), rs.getInt("categoryID"));
                product.setCategoryName(rs.getString("categoryName"));
                product.setImageUrl(rs.getString("imageUrl"));
                products.add(product);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }

    public List<Product> getLowStockProducts() {
        List<Product> products = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT p.*, c.name as categoryName FROM products p LEFT JOIN categories c ON p.categoryID = c.categoryID WHERE p.quantity < 10";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Product product = new Product(rs.getInt("id"), rs.getString("name"), rs.getString("description"),
                        rs.getDouble("costPrice"), rs.getDouble("price"), rs.getInt("quantity"), rs.getInt("categoryID"));
                product.setCategoryName(rs.getString("categoryName"));
                product.setImageUrl(rs.getString("imageUrl"));
                products.add(product);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }

    public void addProduct(Product product) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO products (name, description, costPrice, price, quantity, categoryID, imageUrl) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, product.getName());
            ps.setString(2, product.getDescription());
            ps.setDouble(3, product.getCostPrice());
            ps.setDouble(4, product.getPrice());
            ps.setInt(5, product.getQuantity());
            ps.setInt(6, product.getCategoryID());
            ps.setString(7, product.getImageUrl());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateProduct(Product product) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "UPDATE products SET name=?, description=?, costPrice=?, price=?, quantity=?, categoryID=?, imageUrl=? WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, product.getName());
            ps.setString(2, product.getDescription());
            ps.setDouble(3, product.getCostPrice());
            ps.setDouble(4, product.getPrice());
            ps.setInt(5, product.getQuantity());
            ps.setInt(6, product.getCategoryID());
            ps.setString(7, product.getImageUrl());
            ps.setInt(8, product.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteProduct(int id) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "DELETE FROM products WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
