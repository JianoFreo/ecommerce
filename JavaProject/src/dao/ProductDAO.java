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

    // Retrieve all products from the database
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>(); // Create empty list
        try (Connection conn = DatabaseConnection.getConnection()) { // Open DB connection
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM products"); // Prepare SQL query
            ResultSet rs = ps.executeQuery(); // Execute query and get result set
            while (rs.next()) { // Iterate through rows
                products.add(new Product( // Create Product object from row
                    rs.getInt("id"), // Get id column
                    rs.getString("name"), // Get name column
                    rs.getDouble("price"), // Get price column
                    rs.getInt("quantity") // Get quantity column
                ));
            }
        } catch (Exception e) { // Catch SQL errors
            e.printStackTrace(); // Print error
        }
        return products; // Return list of products
    }

    // Add a product to the database
    public void addProduct(Product product) {
        try (Connection conn = DatabaseConnection.getConnection()) { // Open DB connection
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO products (name, price, quantity) VALUES (?, ?, ?)"); // SQL insert with placeholders
            ps.setString(1, product.getName()); // Set name
            ps.setDouble(2, product.getPrice()); // Set price
            ps.setInt(3, product.getQuantity()); // Set quantity
            ps.executeUpdate(); // Execute insert
        } catch (Exception e) {
            e.printStackTrace(); // Print error
        }
    }

    // Update existing product
    public void updateProduct(Product product) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "UPDATE products SET name=?, price=?, quantity=? WHERE id=?"); // SQL update
            ps.setString(1, product.getName()); // Set name
            ps.setDouble(2, product.getPrice()); // Set price
            ps.setInt(3, product.getQuantity()); // Set quantity
            ps.setInt(4, product.getId()); // Set id to update
            ps.executeUpdate(); // Execute update
        } catch (Exception e) {
            e.printStackTrace(); // Print error
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
