-- Create Database
CREATE DATABASE IF NOT EXISTS is2bdb;
USE is2bdb;

-- Users Table
CREATE TABLE IF NOT EXISTS users (
    userID INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    address VARCHAR(255),
    phoneNumber VARCHAR(20),
    role VARCHAR(20) DEFAULT 'Customer',
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Categories Table
CREATE TABLE IF NOT EXISTS categories (
    categoryID INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    parentCategoryID INT DEFAULT 0,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Products Table
CREATE TABLE IF NOT EXISTS products (
    productID INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    quantity INT DEFAULT 0,
    categoryID INT,
    imageUrl VARCHAR(255),
    lowStockThreshold INT DEFAULT 10,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (categoryID) REFERENCES categories(categoryID) ON DELETE SET NULL
);

-- Orders Table
CREATE TABLE IF NOT EXISTS orders (
    orderID INT AUTO_INCREMENT PRIMARY KEY,
    userID INT NOT NULL,
    orderDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) DEFAULT 'Pending',
    totalAmount DECIMAL(10, 2) NOT NULL,
    shippingAddress TEXT,
    FOREIGN KEY (userID) REFERENCES users(userID) ON DELETE CASCADE
);

-- Order Items Table
CREATE TABLE IF NOT EXISTS order_items (
    orderItemID INT AUTO_INCREMENT PRIMARY KEY,
    orderID INT NOT NULL,
    productID INT NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (orderID) REFERENCES orders(orderID) ON DELETE CASCADE,
    FOREIGN KEY (productID) REFERENCES products(productID) ON DELETE CASCADE
);

-- Cart Table
CREATE TABLE IF NOT EXISTS cart (
    cartID INT AUTO_INCREMENT PRIMARY KEY,
    userID INT NOT NULL,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (userID) REFERENCES users(userID) ON DELETE CASCADE
);

-- Cart Items Table
CREATE TABLE IF NOT EXISTS cart_items (
    cartItemID INT AUTO_INCREMENT PRIMARY KEY,
    cartID INT NOT NULL,
    productID INT NOT NULL,
    quantity INT NOT NULL,
    FOREIGN KEY (cartID) REFERENCES cart(cartID) ON DELETE CASCADE,
    FOREIGN KEY (productID) REFERENCES products(productID) ON DELETE CASCADE
);

-- Reviews Table
CREATE TABLE IF NOT EXISTS reviews (
    reviewID INT AUTO_INCREMENT PRIMARY KEY,
    productID INT NOT NULL,
    userID INT NOT NULL,
    rating INT CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    reviewDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (productID) REFERENCES products(productID) ON DELETE CASCADE,
    FOREIGN KEY (userID) REFERENCES users(userID) ON DELETE CASCADE
);

-- Suppliers Table
CREATE TABLE IF NOT EXISTS suppliers (
    supplierID INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    contactPerson VARCHAR(100),
    email VARCHAR(100),
    phone VARCHAR(20),
    address TEXT,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Sales Table
CREATE TABLE IF NOT EXISTS sales (
    saleID INT AUTO_INCREMENT PRIMARY KEY,
    orderID INT NOT NULL,
    productID INT NOT NULL,
    quantity INT NOT NULL,
    unitPrice DECIMAL(10, 2) NOT NULL,
    totalPrice DECIMAL(10, 2) NOT NULL,
    discount DECIMAL(10, 2) DEFAULT 0,
    saleDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (orderID) REFERENCES orders(orderID) ON DELETE CASCADE,
    FOREIGN KEY (productID) REFERENCES products(productID) ON DELETE CASCADE
);

-- Insert Sample Admin User (password: admin123)
INSERT INTO users (name, email, password, role) VALUES 
('Admin User', 'admin@system.com', 'admin123', 'Admin');

-- Insert Sample Categories
INSERT INTO categories (name, description) VALUES
('Electronics', 'Electronic devices and gadgets'),
('Clothing', 'Fashion and apparel'),
('Food', 'Food and beverages'),
('Books', 'Books and magazines'),
('Home & Garden', 'Home improvement and garden supplies');

-- Insert Sample Products
INSERT INTO products (title, description, price, quantity, categoryID, lowStockThreshold) VALUES
('Laptop', 'High-performance laptop', 999.99, 50, 1, 10),
('Smartphone', 'Latest smartphone model', 699.99, 100, 1, 15),
('T-Shirt', 'Cotton t-shirt', 19.99, 200, 2, 20),
('Coffee', 'Premium coffee beans', 12.99, 150, 3, 30),
('Novel', 'Bestselling novel', 14.99, 75, 4, 10);
