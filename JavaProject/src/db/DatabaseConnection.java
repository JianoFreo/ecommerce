package src.db;

import java.sql.*;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/ecommercedb";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            ensureImageUrlColumn(conn);
            ensureCartPersistenceTable(conn);
            ensureDiscountCodesTable(conn);
            ensureCategoryHierarchyColumn(conn);
            ensureCategoryPopularityTable(conn);
            ensureProductAttributesColumns(conn);
            return conn;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void ensureImageUrlColumn(Connection conn) {
        try {
            String checkSql = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='products' AND TABLE_SCHEMA='ecommercedb' AND COLUMN_NAME='imageUrl'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(checkSql);
            
            if (!rs.next()) {
                String alterSql = "ALTER TABLE products ADD COLUMN imageUrl VARCHAR(500)";
                stmt.execute(alterSql);
                System.out.println("✓ imageUrl column added");
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            // Column might already exist, ignore
        }
    }

    private static void ensureCartPersistenceTable(Connection conn) {
        try {
            String sql = "CREATE TABLE IF NOT EXISTS saved_carts (" +
                        "savedCartID INT AUTO_INCREMENT PRIMARY KEY," +
                        "userID INT NOT NULL," +
                        "productID INT NOT NULL," +
                        "productName VARCHAR(255)," +
                        "quantity INT," +
                        "price DOUBLE," +
                        "subtotal DOUBLE," +
                        "imageUrl VARCHAR(500)," +
                        "savedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "FOREIGN KEY(userID) REFERENCES users(userID) ON DELETE CASCADE" +
                        ")";
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
            System.out.println("✓ saved_carts table ready");
            stmt.close();
        } catch (Exception e) {
            // Table might already exist
        }
    }

    private static void ensureDiscountCodesTable(Connection conn) {
        try {
            String sql = "CREATE TABLE IF NOT EXISTS discount_codes (" +
                        "discountID INT AUTO_INCREMENT PRIMARY KEY," +
                        "code VARCHAR(50) UNIQUE NOT NULL," +
                        "description VARCHAR(255)," +
                        "discountType ENUM('PERCENTAGE', 'FIXED')," +
                        "discountValue DOUBLE," +
                        "minPurchase DOUBLE DEFAULT 0," +
                        "expiryDate TIMESTAMP," +
                        "maxUses INT DEFAULT 0," +
                        "timesUsed INT DEFAULT 0," +
                        "isActive TINYINT(1) DEFAULT 1," +
                        "createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                        ")";
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
            System.out.println("✓ discount_codes table ready");
            stmt.close();
        } catch (Exception e) {
            // Table might already exist
        }
    }

    private static void ensureCategoryHierarchyColumn(Connection conn) {
        try {
            String checkSql = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='categories' AND TABLE_SCHEMA='ecommercedb' AND COLUMN_NAME='parentCategoryID'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(checkSql);
            
            if (!rs.next()) {
                String alterSql = "ALTER TABLE categories ADD COLUMN parentCategoryID INT";
                stmt.execute(alterSql);
                System.out.println("✓ parentCategoryID column added");
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            // Column might already exist
        }
    }

    private static void ensureCategoryPopularityTable(Connection conn) {
        try {
            String sql = "CREATE TABLE IF NOT EXISTS category_popularity (" +
                        "popularityID INT AUTO_INCREMENT PRIMARY KEY," +
                        "categoryID INT NOT NULL," +
                        "viewCount INT DEFAULT 0," +
                        "purchaseCount INT DEFAULT 0," +
                        "lastViewed TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                        "UNIQUE(categoryID)," +
                        "FOREIGN KEY(categoryID) REFERENCES categories(categoryID) ON DELETE CASCADE" +
                        ")";
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
            System.out.println("✓ category_popularity table ready");
            stmt.close();
        } catch (Exception e) {
            // Table might already exist
        }
    }

    private static void ensureProductAttributesColumns(Connection conn) {
        try {
            String checkSql = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='products' AND TABLE_SCHEMA='ecommercedb'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(checkSql);
            
            java.util.Set<String> columns = new java.util.HashSet<>();
            while (rs.next()) {
                columns.add(rs.getString("COLUMN_NAME"));
            }
            
            if (!columns.contains("averageRating")) {
                stmt.execute("ALTER TABLE products ADD COLUMN averageRating DOUBLE DEFAULT 0");
                System.out.println("✓ averageRating column added");
            }
            if (!columns.contains("totalReviews")) {
                stmt.execute("ALTER TABLE products ADD COLUMN totalReviews INT DEFAULT 0");
                System.out.println("✓ totalReviews column added");
            }
            
            rs.close();
            stmt.close();
        } catch (Exception e) {
            // Columns might already exist
        }
    }
}
