# E-Commerce Sales and Inventory Management System


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

## Database Schema

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
### User Interface

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

## Advanced Features

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

#### DAOs
Each DAO provides:
- `add()` / `create()` - INSERT operations
- `getAll()` / `getById()` - SELECT operations
- `update()` - UPDATE operations
- `delete()` - DELETE operations

---
