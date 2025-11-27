# Sales & Inventory Management System

A comprehensive CRUD-based Java application for managing sales and inventory operations with both console and GUI interfaces.

## Features

- **User Management**: Registration, login, and role-based access (Admin/Customer)
- **Product Management**: Full CRUD operations with low stock alerts
- **Order Management**: Place orders, track status, and view history
- **Category Management**: Organize products by categories
- **Inventory Tracking**: Real-time stock monitoring
- **Sales Reporting**: Track sales and revenue
- **Dual Interface**: Console-based and GUI (Swing) applications

## Prerequisites

### Required Software
1. **Java Development Kit (JDK)**: Version 8 or higher
   - Download from: https://www.oracle.com/java/technologies/downloads/
   
2. **MySQL Server**: Version 5.7 or higher
   - Download from: https://dev.mysql.com/downloads/mysql/
   
3. **MySQL JDBC Driver** (mysql-connector-java)
   - Download from: https://dev.mysql.com/downloads/connector/j/

### IDE (Optional but Recommended)
- IntelliJ IDEA, Eclipse, NetBeans, or VS Code with Java extensions

## Installation Steps

### Step 1: Install MySQL Server

1. Install MySQL Server on your machine
2. Set root password as `root` (or update `DatabaseConnection.java` with your password)
3. Start MySQL service

### Step 2: Create Database

1. Open MySQL Command Line or MySQL Workbench
2. Run the following commands:

```sql
CREATE DATABASE is2bdb;
USE is2bdb;
```

3. Execute the `database_schema.sql` file to create all tables:

```sql
SOURCE path/to/database_schema.sql;
```

Or copy and paste the entire schema from `database_schema.sql`

### Step 3: Add MySQL JDBC Driver

#### Option A: Using IDE
1. Download `mysql-connector-java-8.0.xx.jar`
2. Add to project libraries:
   - **IntelliJ**: File → Project Structure → Libraries → + → Java → Select JAR
   - **Eclipse**: Right-click project → Build Path → Configure Build Path → Add External JARs
   - **NetBeans**: Right-click Libraries → Add JAR/Folder

#### Option B: Manual Classpath
Place the JAR in a `lib` folder and compile with:
```bash
javac -cp "lib/mysql-connector-java-8.0.xx.jar" src/**/*.java
java -cp ".:lib/mysql-connector-java-8.0.xx.jar" src.Main
```

### Step 4: Update Database Configuration

Edit `src/db/DatabaseConnection.java` if needed:

```java
private static final String URL = "jdbc:mysql://localhost:3306/is2bdb";
private static final String USER = "root";
private static final String PASSWORD = "your_password_here";
```

## Project Structure

```
JavaProject/
├── src/
│   ├── db/
│   │   └── DatabaseConnection.java
│   ├── models/
│   │   ├── User.java
│   │   ├── Product.java
│   │   ├── Order.java
│   │   ├── OrderItem.java
│   │   ├── Category.java
│   │   ├── Cart.java
│   │   ├── CartItem.java
│   │   ├── Review.java
│   │   ├── Supplier.java
│   │   └── Sale.java
│   ├── dao/
│   │   ├── UserDAO.java
│   │   ├── ProductDAO.java
│   │   ├── OrderDAO.java
│   │   └── CategoryDAO.java
│   ├── utils/
│   │   └── InputValidator.java
│   ├── ui/
│   │   ├── LoginFrame.java
│   │   ├── RegisterFrame.java
│   │   ├── AdminDashboard.java
│   │   ├── CustomerDashboard.java
│   │   ├── ProductManagementFrame.java
│   │   └── ProductBrowseFrame.java
│   └── Main.java
├── database_schema.sql
└── README.md
```

## Running the Application

### Console Mode

```bash
# Compile
javac -cp "lib/*" src/**/*.java

# Run
java -cp ".:lib/*" src.Main
```

### GUI Mode

```bash
# Run LoginFrame
java -cp ".:lib/*" src.ui.LoginFrame
```

Or run from your IDE:
- Console: Run `Main.java`
- GUI: Run `LoginFrame.java`

## Default Login Credentials

### Admin Account
- **Email**: admin@system.com
- **Password**: admin123

### Test Customer (Create via Registration)
- Use the registration form to create customer accounts

## Common Errors and Solutions

### Error 1: ClassNotFoundException - MySQL Driver

**Error Message:**
```
java.lang.ClassNotFoundException: com.mysql.cj.jdbc.Driver
```

**Solution:**
- Ensure MySQL Connector JAR is added to classpath
- Verify JAR file is not corrupted
- Download latest version from MySQL website

### Error 2: SQLException - Access Denied

**Error Message:**
```
java.sql.SQLException: Access denied for user 'root'@'localhost'
```

**Solution:**
- Verify MySQL username and password in `DatabaseConnection.java`
- Check if MySQL server is running
- Grant necessary permissions to user

### Error 3: Communications Link Failure

**Error Message:**
```
com.mysql.cj.jdbc.exceptions.CommunicationsException
```

**Solution:**
- Ensure MySQL server is running
- Check if port 3306 is not blocked by firewall
- Verify database name `is2bdb` exists
- Try: `CREATE DATABASE IF NOT EXISTS is2bdb;`

### Error 4: Table doesn't exist

**Error Message:**
```
Table 'is2bdb.users' doesn't exist
```

**Solution:**
- Run the `database_schema.sql` script
- Verify you're connected to the correct database
- Check table names match exactly (case-sensitive on Linux)

### Error 5: Package does not exist

**Error Message:**
```
error: package src.models does not exist
```

**Solution:**
- Ensure all source files are in correct directories
- Check package declarations match folder structure
- Compile from project root directory
- Clean and rebuild project in IDE

### Error 6: Cannot find symbol

**Error Message:**
```
error: cannot find symbol
```

**Solution:**
- Ensure all required classes are created
- Check import statements
- Verify class names and method names
- Rebuild project

## Testing the Database Connection

Run this test class to verify database connectivity:

```java
// TestConnection.java
package src;

import src.db.DatabaseConnection;
import java.sql.Connection;

public class TestConnection {
    public static void main(String[] args) {
        System.out.println("Testing database connection...");
        if (DatabaseConnection.testConnection()) {
            System.out.println("✓ Connection successful!");
        } else {
            System.out.println("✗ Connection failed!");
            System.out.println("\nTroubleshooting steps:");
            System.out.println("1. Check if MySQL server is running");
            System.out.println("2. Verify database 'is2bdb' exists");
            System.out.println("3. Check username and password");
            System.out.println("4. Ensure MySQL JDBC driver is in classpath");
        }
    }
}
```

## Features Overview

### Admin Features
- Add, edit, delete products
- View all orders and update status
- Manage categories
- View user accounts
- Monitor low stock products
- Generate sales reports

### Customer Features
- Browse products
- Search products
- Place orders
- View order history
- Update profile
- Shopping cart (coming soon)

## Database Schema

### Main Tables
- **users**: User accounts and authentication
- **products**: Product inventory
- **categories**: Product categories
- **orders**: Customer orders
- **order_items**: Order details
- **cart**: Shopping cart
- **cart_items**: Cart items
- **reviews**: Product reviews
- **suppliers**: Supplier information
- **sales**: Sales transactions

## API Documentation

### ProductDAO Methods
```java
- createProduct(Product product): boolean
- getProductById(int id): Product
- getAllProducts(): List<Product>
- updateProduct(Product product): boolean
- deleteProduct(int id): boolean
- getLowStockProducts(): List<Product>
- searchProducts(String keyword): List<Product>
- updateStock(int productId, int quantity): boolean
```

### UserDAO Methods
```java
- createUser(User user): boolean
- getUserById(int id): User
- getAllUsers(): List<User>
- updateUser(User user): boolean
- deleteUser(int id): boolean
- authenticateUser(String email, String password): User
- emailExists(String email): boolean
- updatePassword(int userId, String newPassword): boolean
```

## Future Enhancements

- [ ] Password encryption (BCrypt/SHA-256)
- [ ] Email verification
- [ ] Two-factor authentication
- [ ] Shopping cart persistence
- [ ] Payment gateway integration
- [ ] Invoice generation (PDF)
- [ ] Advanced reporting and analytics
- [ ] Product image upload
- [ ] Discount codes and promotions
- [ ] Multi-currency support

## Troubleshooting Compilation

### Windows
```cmd
set CLASSPATH=.;lib\mysql-connector-java-8.0.33.jar
javac src\db\*.java
javac src\models\*.java
javac src\dao\*.java
javac src\utils\*.java
javac src\ui\*.java
javac src\Main.java
java src.Main
```

### Linux/Mac
```bash
export CLASSPATH=.:lib/mysql-connector-java-8.0.33.jar
javac src/db/*.java
javac src/models/*.java
javac src/dao/*.java
javac src/utils/*.java
javac src/ui/*.java
javac src/Main.java
java src.Main
```

## Support

For issues and questions:
1. Check error messages carefully
2. Verify database connection
3. Ensure all dependencies are installed
4. Check package structure matches folder structure
5. Review this README for common solutions

## License

This project is created for educational purposes as part of the TUP OOP course requirements.

## Project Deadline

**Tentative Deadline**: December 9, 2025

Ensure all core functionalities are complete and tested before submission.
