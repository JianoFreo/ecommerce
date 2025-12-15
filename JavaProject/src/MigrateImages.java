import java.io.File;
import java.nio.file.Files;
import java.sql.*;

public class MigrateImages {
    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/ecommercedb", "root", "root")) {
            
            // Get all products with imageUrl
            String selectSql = "SELECT id, imageUrl FROM products WHERE imageUrl IS NOT NULL AND imageUrl != ''";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(selectSql)) {
                
                while (rs.next()) {
                    int productId = rs.getInt("id");
                    String imageUrl = rs.getString("imageUrl");
                    
                    // Extract filename from path (e.g., "images/1765813314740_Screenshot.png")
                    String filename = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
                    File imageFile = new File("images", filename);
                    
                    if (imageFile.exists()) {
                        byte[] imageBytes = Files.readAllBytes(imageFile.toPath());
                        
                        String updateSql = "UPDATE products SET imageData=? WHERE id=?";
                        try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                            ps.setBytes(1, imageBytes);
                            ps.setInt(2, productId);
                            ps.executeUpdate();
                            System.out.println("✓ Product " + productId + ": " + filename);
                        }
                    } else {
                        System.out.println("✗ File not found: " + imageFile.getAbsolutePath());
                    }
                }
            }
            System.out.println("Migration complete!");
        }
    }
}
