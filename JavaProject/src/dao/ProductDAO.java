package src.dao; // Package declaration
/**A DAO is a design pattern in Java used to separate the database logic (SQL queries, 
 * CRUD operations) from the business logic or user interface.

In simpler terms:

The DAO acts as a “middleman” between your Java code and your database. */
import java.sql.*; // Import Product class
import java.util.*; // Import DatabaseConnection class
import src.db.DatabaseConnection; // Import JDBC classes
import src.model.Product; // Import List, ArrayList

public class ProductDAO { // DAO class for database operations

    // Retrieve all products from the database with category info
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT p.*, c.name as categoryName FROM products p " +
                        "LEFT JOIN categories c ON p.categoryID = c.categoryID";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
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
                products.add(product);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }

    // Get products by category
    public List<Product> getProductsByCategory(int categoryID) {
        List<Product> products = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT p.*, c.name as categoryName FROM products p " +
                "LEFT JOIN categories c ON p.categoryID = c.categoryID WHERE p.categoryID=?");
            ps.setInt(1, categoryID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
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
                products.add(product);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }

    // Get low stock products (quantity < 10)
    public List<Product> getLowStockProducts() {
        List<Product> products = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT p.*, c.name as categoryName FROM products p " +
                "LEFT JOIN categories c ON p.categoryID = c.categoryID WHERE p.quantity < 10");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
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
                products.add(product);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }

    // Add a product to the database
    public void addProduct(Product product) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO products (name, description, costPrice, price, quantity, categoryID) VALUES (?, ?, ?, ?, ?, ?)");
            ps.setString(1, product.getName());
            ps.setString(2, product.getDescription());
            ps.setDouble(3, product.getCostPrice());
            ps.setDouble(4, product.getPrice());
            ps.setInt(5, product.getQuantity());
            ps.setInt(6, product.getCategoryID());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Update existing product
    public void updateProduct(Product product) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "UPDATE products SET name=?, description=?, costPrice=?, price=?, quantity=?, categoryID=? WHERE id=?");
            ps.setString(1, product.getName());
            ps.setString(2, product.getDescription());
            ps.setDouble(3, product.getCostPrice());
            ps.setDouble(4, product.getPrice());
            ps.setInt(5, product.getQuantity());
            ps.setInt(6, product.getCategoryID());
            ps.setInt(7, product.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Delete product by id
    public void deleteProduct(int id) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM products WHERE id=?"); // SQL delete
            ps.setInt(1, id); // Set id
            ps.executeUpdate(); // Execute delete
        } catch (Exception e) {
            e.printStackTrace(); // Print error
        }
    }
}
