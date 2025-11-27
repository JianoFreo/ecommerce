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

    // Create
    public boolean createProduct(Product product) {
        String sql = "INSERT INTO products (title, description, price, quantity, categoryID, imageUrl, lowStockThreshold) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, product.getTitle());
            stmt.setString(2, product.getDescription());
            stmt.setDouble(3, product.getPrice());
            stmt.setInt(4, product.getQuantity());
            stmt.setInt(5, product.getCategoryID());
            stmt.setString(6, product.getImageUrl());
            stmt.setInt(7, product.getLowStockThreshold());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    product.setProductID(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Read - Get by ID
    public Product getProductById(int productID) {
        String sql = "SELECT * FROM products WHERE productID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, productID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractProductFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Read - Get All
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products ORDER BY productID";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                products.add(extractProductFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    // Update
    public boolean updateProduct(Product product) {
        String sql = "UPDATE products SET title=?, description=?, price=?, quantity=?, categoryID=?, imageUrl=?, lowStockThreshold=? WHERE productID=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, product.getTitle());
            stmt.setString(2, product.getDescription());
            stmt.setDouble(3, product.getPrice());
            stmt.setInt(4, product.getQuantity());
            stmt.setInt(5, product.getCategoryID());
            stmt.setString(6, product.getImageUrl());
            stmt.setInt(7, product.getLowStockThreshold());
            stmt.setInt(8, product.getProductID());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Delete
    public boolean deleteProduct(int productID) {
        String sql = "DELETE FROM products WHERE productID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, productID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Get Low Stock Products
    public List<Product> getLowStockProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE quantity <= lowStockThreshold ORDER BY quantity ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                products.add(extractProductFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    // Search Products
    public List<Product> searchProducts(String keyword) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE title LIKE ? OR description LIKE ? ORDER BY title";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                products.add(extractProductFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    // Get Products by Category
    public List<Product> getProductsByCategory(int categoryID) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE categoryID = ? ORDER BY title";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, categoryID);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                products.add(extractProductFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    // Update Stock
    public boolean updateStock(int productID, int quantity) {
        String sql = "UPDATE products SET quantity = quantity + ? WHERE productID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, quantity);
            stmt.setInt(2, productID);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Helper method
    private Product extractProductFromResultSet(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setProductID(rs.getInt("productID"));
        product.setTitle(rs.getString("title"));
        product.setDescription(rs.getString("description"));
        product.setPrice(rs.getDouble("price"));
        product.setQuantity(rs.getInt("quantity"));
        product.setCategoryID(rs.getInt("categoryID"));
        product.setImageUrl(rs.getString("imageUrl"));
        product.setLowStockThreshold(rs.getInt("lowStockThreshold"));
        return product;
    }
}
