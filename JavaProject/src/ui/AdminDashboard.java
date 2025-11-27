package src.ui;

import java.awt.*;
import javax.swing.*;
import src.models.User;

public class AdminDashboard extends JFrame {
    private User currentUser;
    private JLabel lblWelcome;
    
    public AdminDashboard(User user) {
        this.currentUser = user;
        
        setTitle("Admin Dashboard - Sales & Inventory System");
        setSize(800, 600);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Top Panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        lblWelcome = new JLabel("Welcome, " + currentUser.getName() + " (Admin)");
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(lblWelcome, BorderLayout.WEST);
        
        JButton btnLogout = new JButton("Logout");
        btnLogout.addActionListener(e -> logout());
        topPanel.add(btnLogout, BorderLayout.EAST);
        
        // Menu Panel
        JPanel menuPanel = new JPanel(new GridLayout(6, 1, 10, 10));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JButton btnProducts = new JButton("Product Management");
        JButton btnOrders = new JButton("Order Management");
        JButton btnCategories = new JButton("Category Management");
        JButton btnUsers = new JButton("User Management");
        JButton btnLowStock = new JButton("Low Stock Alert");
        JButton btnReports = new JButton("Sales Reports");
        
        btnProducts.addActionListener(e -> openProductManagement());
        btnOrders.addActionListener(e -> openOrderManagement());
        btnCategories.addActionListener(e -> openCategoryManagement());
        btnUsers.addActionListener(e -> openUserManagement());
        btnLowStock.addActionListener(e -> openLowStockView());
        btnReports.addActionListener(e -> openSalesReports());
        
        menuPanel.add(btnProducts);
        menuPanel.add(btnOrders);
        menuPanel.add(btnCategories);
        menuPanel.add(btnUsers);
        menuPanel.add(btnLowStock);
        menuPanel.add(btnReports);
        
        // Center content
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(menuPanel, BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        
        setVisible(true);
    }
    
    private void openProductManagement() {
        new ProductManagementFrame(currentUser);
    }
    
    private void openOrderManagement() {
        JOptionPane.showMessageDialog(this, "Order Management - Coming Soon!");
    }
    
    private void openCategoryManagement() {
        JOptionPane.showMessageDialog(this, "Category Management - Coming Soon!");
    }
    
    private void openUserManagement() {
        JOptionPane.showMessageDialog(this, "User Management - Coming Soon!");
    }
    
    private void openLowStockView() {
        JOptionPane.showMessageDialog(this, "Low Stock Alert - Coming Soon!");
    }
    
    private void openSalesReports() {
        JOptionPane.showMessageDialog(this, "Sales Reports - Coming Soon!");
    }
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to logout?", 
            "Confirm Logout", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame();
        }
    }
}
