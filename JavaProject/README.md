# E-Commerce Sales and Inventory Management System

## 📋 Project Overview

A comprehensive CRUD-based Java desktop application for managing sales and inventory operations in an e-commerce platform. Built with Java Swing for the UI and MySQL for data persistence, this system provides complete management capabilities for products, orders, users, categories, reviews, and shopping carts.

**Project Deadline:** December 9, 2025

---

## 🎯 Features

### Core Functionality
- ✅ **Complete CRUD Operations** for all entities
- ✅ **User Authentication** with role-based access (Admin/Customer)
- ✅ **Product Management** with categories and inventory tracking
- ✅ **Order Processing** with status tracking
- ✅ **Shopping Cart** functionality
- ✅ **Review System** with ratings (1-5 stars)
- ✅ **Low Stock Alerts** for inventory management

### User Roles

#### Admin Dashboard
- Manage products, categories, users, orders, and reviews
- View comprehensive statistics
- Monitor low stock items
- Update order statuses
- Complete CRUD operations on all entities

#### Customer Dashboard
- Browse products by category
- Add items to shopping cart
- Place orders with shipping address
- View order history and status
- Write product reviews and ratings
- View profile information

---

## 🏗️ Architecture

### Project Structure
```
JavaProject/
├── src/
│   ├── Main.java                 # Application entry point
│   ├── model/                    # Data models (POJOs)
│   │   ├── User.java
│   │   ├── Product.java
│   │   ├── Category.java
│   │   ├── Order.java
│   │   ├── OrderItem.java
│   │   ├── Cart.java
│   │   ├── CartItem.java
│   │   └── Review.java
│   ├── dao/                      # Data Access Objects
│   │   ├── UserDAO.java
│   │   ├── ProductDAO.java
│   │   ├── CategoryDAO.java
│   │   ├── OrderDAO.java
│   │   ├── CartDAO.java
│   │   └── ReviewDAO.java
│   ├── db/                       # Database connection
│   │   └── DatabaseConnection.java
│   └── ui/                       # User Interface
│       ├── LoginFrame.java
│       ├── RegistrationFrame.java
│       ├── AdminDashboard.java
│       ├── CustomerDashboard.java
│       ├── ProductManagementPanel.java
│       ├── CategoryManagementPanel.java
│       ├── OrderManagementPanel.java
│       ├── UserManagementPanel.java
│       └── ReviewManagementPanel.java
├── lib/                          # External libraries
│   └── mysql-connector-java.jar
├── database_schema.sql           # Complete database schema
└── README.md                     # This file
```

### Design Patterns Used
- **DAO Pattern**: Separation of data access logic
- **MVC Pattern**: Model-View-Controller separation
- **Singleton Pattern**: Database connection management

---

## 📊 Database Schema

### Tables Overview

1. **users** - User accounts (customers and admins)
2. **products** - Product inventory with pricing
3. **categories** - Product categorization
4. **carts** - User shopping carts
5. **cart_items** - Items in shopping carts
6. **orders** - Customer orders
7. **order_items** - Items in orders
8. **reviews** - Product reviews and ratings

### Key Relationships
- Products belong to Categories (Many-to-One)
- Users have one Cart (One-to-One)
- Carts contain CartItems (One-to-Many)
- Users place Orders (One-to-Many)
- Orders contain OrderItems (One-to-Many)
- Users write Reviews for Products (Many-to-Many)

---

## 🚀 Setup Instructions

### Prerequisites
- Java Development Kit (JDK) 8 or higher
- MySQL Server 5.7 or higher
- MySQL Connector/J (JDBC Driver)

### Database Setup

1. **Create Database:**
   ```sql
   CREATE DATABASE is2bdb;
   USE is2bdb;
   ```

2. **Import Schema:**
   ```bash
   mysql -u root -p is2bdb < database_schema.sql
   ```
   
   Or execute the `database_schema.sql` file in your MySQL client.

3. **Verify Installation:**
   - Check that all tables are created
   - Sample data should be loaded automatically

### Application Configuration

1. **Update Database Credentials:**
   
   Edit `src/db/DatabaseConnection.java`:
   ```java
   private static final String URL = "jdbc:mysql://localhost:3306/is2bdb";
   private static final String USER = "root";
   private static final String PASSWORD = "your_password";
   ```

2. **Add MySQL Connector:**
   - Download MySQL Connector/J from [MySQL website](https://dev.mysql.com/downloads/connector/j/)
   - Place `mysql-connector-java-x.x.x.jar` in the `lib/` folder
   - Add to classpath when compiling/running

### Compilation and Execution

#### Using Command Line:

**Compile:**
```bash
javac -cp ".;lib/mysql-connector-java-x.x.x.jar" -d bin src/**/*.java src/*.java
```

**Run:**
```bash
java -cp "bin;lib/mysql-connector-java-x.x.x.jar" src.Main
```

#### Using IDE (Eclipse/IntelliJ):

1. Import project as Java project
2. Add `mysql-connector-java.jar` to build path
3. Run `Main.java`

---

## 👥 Default Users

After running the schema, you can login with:

### Admin Account
- **Email:** admin@ecommerce.com
- **Password:** admin123

### Customer Account
- **Email:** john@example.com
- **Password:** user123

**⚠️ Note:** In production, passwords should be hashed using BCrypt or similar algorithms.

---

## 🔑 Key Features Implementation

### 1. User Management (CRUD)
- ✅ Registration with email validation
- ✅ Login with role-based routing
- ✅ Profile management
- ✅ Admin can manage all users

### 2. Product Management (CRUD)
- ✅ Add/Edit/Delete products
- ✅ Category assignment
- ✅ Inventory tracking
- ✅ Low stock alerts (< 10 items)
- ✅ Product descriptions and pricing

### 3. Order Management (CRUD)
- ✅ Create orders from cart
- ✅ Order status tracking (Pending → Processing → Shipped → Delivered)
- ✅ Order history for customers
- ✅ Admin can update order status
- ✅ Automatic inventory deduction

### 4. Shopping Cart (CRUD)
- ✅ Add/Remove items
- ✅ Update quantities
- ✅ Persistent cart (saved to database)
- ✅ Real-time total calculation
- ✅ Clear cart functionality

### 5. Category Management (CRUD)
- ✅ Create/Edit/Delete categories
- ✅ Product filtering by category
- ✅ Category descriptions

### 6. Review System (CRUD)
- ✅ Add reviews with ratings (1-5 stars)
- ✅ Edit/Delete own reviews
- ✅ View product reviews
- ✅ Average rating calculation
- ✅ Admin moderation

---

## 🎨 User Interface

### Admin Dashboard
- **Statistics Overview:** Total products, orders, users, low stock items
- **Product Management:** Full CRUD with category selection
- **Order Management:** View all orders, update status
- **User Management:** Manage all user accounts
- **Review Management:** Moderate customer reviews
- **Category Management:** Organize products

### Customer Dashboard
- **Shop:** Browse products, view details, add to cart
- **Cart:** View cart, modify items, checkout
- **Orders:** View order history and status
- **Profile:** View personal information
- **Reviews:** Rate and review purchased products

---

## 📈 Advanced Features

### Implemented Improvements

1. **Low Stock Alerts**
   - Automatic detection of products with quantity < 10
   - Alert notification in admin dashboard
   - View low stock products button

2. **Transaction Management**
   - Order creation uses database transactions
   - Automatic inventory deduction
   - Rollback on errors

3. **Data Integrity**
   - Foreign key constraints
   - Cascade deletions where appropriate
   - Input validation

4. **User Experience**
   - Intuitive navigation with menu bars
   - Card layout for panel switching
   - Real-time data updates
   - Confirmation dialogs for critical actions

---

## 🔧 Technical Details

### Technologies Used
- **Language:** Java 8+
- **GUI Framework:** Swing
- **Database:** MySQL 5.7+
- **JDBC Driver:** MySQL Connector/J
- **Design Pattern:** DAO, MVC

### Database Features
- Views for common queries
- Stored procedures for complex operations
- Triggers for automatic calculations
- Indexes for performance optimization

---

## 🐛 Troubleshooting

### Common Issues

**1. ClassNotFoundException: com.mysql.cj.jdbc.Driver**
- Solution: Ensure MySQL Connector JAR is in classpath

**2. Access denied for user**
- Solution: Update database credentials in DatabaseConnection.java

**3. Table doesn't exist**
- Solution: Run database_schema.sql to create all tables

**4. Low stock alert not showing**
- Solution: Ensure products table has records with quantity < 10

---

## 📝 Future Enhancements

### Suggested Improvements
- [ ] Password hashing (BCrypt)
- [ ] Email notifications for orders
- [ ] Product image upload
- [ ] Advanced search and filtering
- [ ] Sales reports and analytics
- [ ] Discount codes/promotions
- [ ] Export data to PDF/Excel
- [ ] Multi-language support
- [ ] Payment gateway integration
- [ ] Supplier management

---

## 👨‍💻 Development Guidelines

### Adding New Features

1. **Create Model Class** in `src/model/`
2. **Create DAO Class** in `src/dao/` with CRUD methods
3. **Update Database Schema** in `database_schema.sql`
4. **Create UI Panel** in `src/ui/`
5. **Integrate with Dashboard**

### Code Style
- Follow Java naming conventions
- Add comments for complex logic
- Use meaningful variable names
- Implement error handling
- Validate user inputs

---

## 📚 Documentation

### Class Descriptions

#### Models
- **User:** Represents system users (customers/admins)
- **Product:** Product inventory items
- **Category:** Product categorization
- **Order/OrderItem:** Customer orders and items
- **Cart/CartItem:** Shopping cart functionality
- **Review:** Product reviews with ratings

#### DAOs
Each DAO provides:
- `add()` / `create()` - INSERT operations
- `getAll()` / `getById()` - SELECT operations
- `update()` - UPDATE operations
- `delete()` - DELETE operations

---

## 🤝 Contributing

This is an academic project. For improvements:
1. Fork the repository
2. Create feature branch
3. Commit changes
4. Submit pull request

---

## 📄 License

This project is created for educational purposes as part of the OOP curriculum.

---

## ✅ Project Checklist

- [x] User Class with CRUD
- [x] Product Class with CRUD
- [x] Category Class with CRUD
- [x] Order Class with CRUD
- [x] Cart Class with CRUD
- [x] Review Class with CRUD
- [x] Admin Dashboard
- [x] Customer Dashboard
- [x] Login/Registration System
- [x] Low Stock Alerts
- [x] Database Schema
- [x] Sample Data
- [x] Documentation

---

## 📞 Support

For questions or issues:
- Review the troubleshooting section
- Check database connection settings
- Verify all files are in correct directories
- Ensure MySQL service is running

---

**Project Status:** ✅ Complete - Ready for Submission

**Last Updated:** November 28, 2025

**Deadline:** December 9, 2025
