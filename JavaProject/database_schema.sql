-- E-Commerce Sales and Inventory Database Schema
-- Database: is2bdb
-- Created for CRUD Java Application Project

-- Drop tables if they exist (for clean installation)
DROP TABLE IF EXISTS reviews;
DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS cart_items;
DROP TABLE IF EXISTS carts;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS users;

-- =============================================
-- Table: users
-- Description: Stores user information including customers and admins
-- =============================================
CREATE TABLE users (
    userID INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL, -- Should be hashed in production
    address VARCHAR(255),
    phoneNumber VARCHAR(20),
    role ENUM('Customer', 'Admin') DEFAULT 'Customer',
    createdDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_role (role)
);

-- =============================================
-- Table: categories
-- Description: Product categories for organization
-- =============================================
CREATE TABLE categories (
    categoryID INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    createdDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =============================================
-- Table: products
-- Description: Product inventory with pricing and stock information
-- =============================================
CREATE TABLE products (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL CHECK (price >= 0),
    quantity INT NOT NULL DEFAULT 0 CHECK (quantity >= 0),
    categoryID INT,
    createdDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    lastModified TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (categoryID) REFERENCES categories(categoryID) ON DELETE SET NULL,
    INDEX idx_category (categoryID),
    INDEX idx_price (price),
    INDEX idx_quantity (quantity)
);

-- =============================================
-- Table: carts
-- Description: Shopping carts for users
-- =============================================
CREATE TABLE carts (
    cartID INT PRIMARY KEY AUTO_INCREMENT,
    userID INT NOT NULL,
    totalCost DECIMAL(10, 2) DEFAULT 0,
    createdDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    lastModified TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (userID) REFERENCES users(userID) ON DELETE CASCADE,
    UNIQUE KEY unique_user_cart (userID),
    INDEX idx_user (userID)
);

-- =============================================
-- Table: cart_items
-- Description: Items in shopping carts
-- =============================================
CREATE TABLE cart_items (
    cartItemID INT PRIMARY KEY AUTO_INCREMENT,
    cartID INT NOT NULL,
    productID INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1 CHECK (quantity > 0),
    addedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cartID) REFERENCES carts(cartID) ON DELETE CASCADE,
    FOREIGN KEY (productID) REFERENCES products(id) ON DELETE CASCADE,
    UNIQUE KEY unique_cart_product (cartID, productID),
    INDEX idx_cart (cartID),
    INDEX idx_product (productID)
);

-- =============================================
-- Table: orders
-- Description: Customer orders
-- =============================================
CREATE TABLE orders (
    orderID INT PRIMARY KEY AUTO_INCREMENT,
    userID INT NOT NULL,
    orderDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('Pending', 'Processing', 'Shipped', 'Delivered', 'Cancelled') DEFAULT 'Pending',
    totalAmount DECIMAL(10, 2) NOT NULL,
    shippingAddress VARCHAR(255) NOT NULL,
    lastModified TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (userID) REFERENCES users(userID) ON DELETE CASCADE,
    INDEX idx_user (userID),
    INDEX idx_status (status),
    INDEX idx_date (orderDate)
);

-- =============================================
-- Table: order_items
-- Description: Items within orders
-- =============================================
CREATE TABLE order_items (
    orderItemID INT PRIMARY KEY AUTO_INCREMENT,
    orderID INT NOT NULL,
    productID INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL, -- Price at time of order
    quantity INT NOT NULL CHECK (quantity > 0),
    FOREIGN KEY (orderID) REFERENCES orders(orderID) ON DELETE CASCADE,
    FOREIGN KEY (productID) REFERENCES products(id) ON DELETE RESTRICT,
    INDEX idx_order (orderID),
    INDEX idx_product (productID)
);

-- =============================================
-- Table: reviews
-- Description: Product reviews and ratings
-- =============================================
CREATE TABLE reviews (
    reviewID INT PRIMARY KEY AUTO_INCREMENT,
    productID INT NOT NULL,
    userID INT NOT NULL,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    reviewDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    lastModified TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (productID) REFERENCES products(id) ON DELETE CASCADE,
    FOREIGN KEY (userID) REFERENCES users(userID) ON DELETE CASCADE,
    UNIQUE KEY unique_user_product_review (userID, productID),
    INDEX idx_product (productID),
    INDEX idx_user (userID),
    INDEX idx_rating (rating)
);

-- =============================================
-- Insert Sample Data
-- =============================================

-- Sample Users (password: "admin123" and "user123" - should be hashed in production)
INSERT INTO users (name, email, password, address, phoneNumber, role) VALUES
('Admin User', 'admin@ecommerce.com', 'admin123', '123 Admin St, Manila', '09171234567', 'Admin'),
('John Doe', 'john@example.com', 'user123', '456 Customer Ave, Quezon City', '09187654321', 'Customer'),
('Jane Smith', 'jane@example.com', 'user123', '789 Buyer Blvd, Makati', '09191122334', 'Customer');

-- Sample Categories
INSERT INTO categories (name, description) VALUES
('Electronics', 'Electronic devices and accessories'),
('Clothing', 'Fashion and apparel items'),
('Books', 'Physical and digital books'),
('Home & Garden', 'Home improvement and gardening supplies'),
('Sports', 'Sports equipment and accessories');

-- Sample Products
INSERT INTO products (name, description, price, quantity, categoryID) VALUES
('Laptop Computer', 'High-performance laptop with 16GB RAM', 45000.00, 15, 1),
('Wireless Mouse', 'Ergonomic wireless mouse with USB receiver', 850.00, 50, 1),
('T-Shirt', 'Cotton t-shirt available in multiple colors', 399.00, 100, 2),
('Jeans', 'Denim jeans, slim fit', 1299.00, 75, 2),
('Programming Book', 'Learn Java Programming - Comprehensive Guide', 1500.00, 30, 3),
('Garden Tools Set', 'Complete set of gardening tools', 2500.00, 20, 4),
('Basketball', 'Official size basketball', 899.00, 8, 5),
('Running Shoes', 'Comfortable running shoes for athletes', 3500.00, 25, 5);

-- Sample Reviews
INSERT INTO reviews (productID, userID, rating, comment) VALUES
(1, 2, 5, 'Excellent laptop! Very fast and reliable.'),
(1, 3, 4, 'Good performance but a bit pricey.'),
(2, 2, 5, 'Perfect mouse for my work setup.'),
(3, 3, 5, 'Great quality t-shirt, fits perfectly!'),
(5, 2, 5, 'Best programming book I have read!');

-- =============================================
-- Useful Views
-- =============================================

-- View: Low Stock Products (quantity < 10)
CREATE OR REPLACE VIEW low_stock_products AS
SELECT p.id, p.name, p.quantity, c.name as category
FROM products p
LEFT JOIN categories c ON p.categoryID = c.categoryID
WHERE p.quantity < 10
ORDER BY p.quantity ASC;

-- View: Product Ratings Summary
CREATE OR REPLACE VIEW product_ratings AS
SELECT 
    p.id as productID,
    p.name as productName,
    COUNT(r.reviewID) as reviewCount,
    ROUND(AVG(r.rating), 2) as averageRating
FROM products p
LEFT JOIN reviews r ON p.id = r.productID
GROUP BY p.id, p.name;

-- View: Order Summary
CREATE OR REPLACE VIEW order_summary AS
SELECT 
    o.orderID,
    u.name as customerName,
    o.orderDate,
    o.status,
    o.totalAmount,
    COUNT(oi.orderItemID) as itemCount
FROM orders o
JOIN users u ON o.userID = u.userID
LEFT JOIN order_items oi ON o.orderID = oi.orderID
GROUP BY o.orderID, u.name, o.orderDate, o.status, o.totalAmount
ORDER BY o.orderDate DESC;

-- =============================================
-- Stored Procedures
-- =============================================

DELIMITER //

-- Procedure: Get Product Stock Status
CREATE PROCEDURE GetStockStatus(IN prod_id INT)
BEGIN
    SELECT 
        id,
        name,
        quantity,
        CASE 
            WHEN quantity = 0 THEN 'Out of Stock'
            WHEN quantity < 10 THEN 'Low Stock'
            ELSE 'In Stock'
        END as stockStatus
    FROM products
    WHERE id = prod_id;
END //

-- Procedure: Get Sales Report by Date Range
CREATE PROCEDURE GetSalesReport(IN start_date DATE, IN end_date DATE)
BEGIN
    SELECT 
        DATE(o.orderDate) as orderDate,
        COUNT(DISTINCT o.orderID) as totalOrders,
        SUM(o.totalAmount) as totalRevenue,
        AVG(o.totalAmount) as averageOrderValue
    FROM orders o
    WHERE DATE(o.orderDate) BETWEEN start_date AND end_date
        AND o.status != 'Cancelled'
    GROUP BY DATE(o.orderDate)
    ORDER BY orderDate DESC;
END //

DELIMITER ;

-- =============================================
-- Triggers
-- =============================================

DELIMITER //

-- Trigger: Prevent negative inventory
CREATE TRIGGER prevent_negative_inventory
BEFORE UPDATE ON products
FOR EACH ROW
BEGIN
    IF NEW.quantity < 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Product quantity cannot be negative';
    END IF;
END //

-- Trigger: Update cart total when items change
CREATE TRIGGER update_cart_total_after_item_insert
AFTER INSERT ON cart_items
FOR EACH ROW
BEGIN
    UPDATE carts 
    SET totalCost = (
        SELECT COALESCE(SUM(ci.quantity * p.price), 0)
        FROM cart_items ci
        JOIN products p ON ci.productID = p.id
        WHERE ci.cartID = NEW.cartID
    )
    WHERE cartID = NEW.cartID;
END //

CREATE TRIGGER update_cart_total_after_item_update
AFTER UPDATE ON cart_items
FOR EACH ROW
BEGIN
    UPDATE carts 
    SET totalCost = (
        SELECT COALESCE(SUM(ci.quantity * p.price), 0)
        FROM cart_items ci
        JOIN products p ON ci.productID = p.id
        WHERE ci.cartID = NEW.cartID
    )
    WHERE cartID = NEW.cartID;
END //

CREATE TRIGGER update_cart_total_after_item_delete
AFTER DELETE ON cart_items
FOR EACH ROW
BEGIN
    UPDATE carts 
    SET totalCost = (
        SELECT COALESCE(SUM(ci.quantity * p.price), 0)
        FROM cart_items ci
        JOIN products p ON ci.productID = p.id
        WHERE ci.cartID = OLD.cartID
    )
    WHERE cartID = OLD.cartID;
END //

DELIMITER ;

-- =============================================
-- Indexes for Performance Optimization
-- =============================================

-- Additional composite indexes
CREATE INDEX idx_order_user_date ON orders(userID, orderDate);
CREATE INDEX idx_order_status_date ON orders(status, orderDate);
CREATE INDEX idx_product_category_price ON products(categoryID, price);

-- Full-text search indexes (for product search)
-- ALTER TABLE products ADD FULLTEXT INDEX ft_product_search (name, description);

-- =============================================
-- Grant Permissions (adjust as needed)
-- =============================================
-- GRANT ALL PRIVILEGES ON is2bdb.* TO 'root'@'localhost';
-- FLUSH PRIVILEGES;

-- =============================================
-- Database Setup Complete
-- =============================================
SELECT 'Database schema created successfully!' as Status;
