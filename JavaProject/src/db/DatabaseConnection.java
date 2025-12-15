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
}
