package src;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import src.db.DatabaseConnection;

public class TestConnection {
    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("  Database Connection Test Utility");
        System.out.println("==========================================\n");
        
        // Test 1: Basic Connection
        System.out.println("Test 1: Testing basic database connection...");
        if (DatabaseConnection.testConnection()) {
            System.out.println("✓ Connection successful!\n");
        } else {
            System.out.println("✗ Connection failed!\n");
            printTroubleshootingSteps();
            return;
        }
        
        // Test 2: Query Database
        System.out.println("Test 2: Querying database information...");
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            if (conn != null) {
                Statement stmt = conn.createStatement();
                
                // Get database version
                ResultSet rs = stmt.executeQuery("SELECT VERSION()");
                if (rs.next()) {
                    System.out.println("✓ MySQL Version: " + rs.getString(1));
                }
                
                // List tables
                rs = stmt.executeQuery("SHOW TABLES");
                System.out.println("\n✓ Tables in database:");
                boolean hasTables = false;
                while (rs.next()) {
                    System.out.println("  - " + rs.getString(1));
                    hasTables = true;
                }
                
                if (!hasTables) {
                    System.out.println("  ⚠ No tables found! Please run database_schema.sql");
                }
                
                System.out.println("\n✓ All tests passed successfully!");
                
            }
        } catch (Exception e) {
            System.out.println("✗ Error during testing:");
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        
        System.out.println("\n==========================================");
        System.out.println("Active connections: " + DatabaseConnection.getActiveConnections());
        System.out.println("==========================================");
    }
    
    private static void printTroubleshootingSteps() {
        System.out.println("\n⚠ Troubleshooting Steps:");
        System.out.println("─────────────────────────────────────────");
        System.out.println("1. ✓ Check if MySQL server is running");
        System.out.println("   - Windows: Services → MySQL → Status");
        System.out.println("   - Linux: sudo systemctl status mysql");
        System.out.println("   - Mac: mysql.server status");
        System.out.println();
        System.out.println("2. ✓ Verify database 'is2bdb' exists");
        System.out.println("   - Login to MySQL: mysql -u root -p");
        System.out.println("   - Run: SHOW DATABASES;");
        System.out.println("   - If not exists: CREATE DATABASE is2bdb;");
        System.out.println();
        System.out.println("3. ✓ Check username and password");
        System.out.println("   - Default: username=root, password=root");
        System.out.println("   - Update in DatabaseConnection.java if different");
        System.out.println();
        System.out.println("4. ✓ Ensure MySQL JDBC driver is in classpath");
        System.out.println("   - File: mysql-connector-java-8.0.xx.jar");
        System.out.println("   - Location: Project libraries or lib folder");
        System.out.println();
        System.out.println("5. ✓ Check firewall settings");
        System.out.println("   - Ensure port 3306 is not blocked");
        System.out.println("─────────────────────────────────────────");
    }
}
