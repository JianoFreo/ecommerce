package src.ui;

import java.awt.*;
import javax.swing.*;
import src.models.User;

public class CustomerDashboard extends JFrame {
    private User currentUser;
    private JLabel lblWelcome;
    
    public CustomerDashboard(User user) {
        this.currentUser = user;
        
        setTitle("Customer Dashboard - Sales & Inventory System");
        setSize(700, 500);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Top Panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        lblWelcome = new JLabel("Welcome, " + currentUser.getName());
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(lblWelcome, BorderLayout.WEST);
        
        JButton btnLogout = new JButton("Logout");
        btnLogout.addActionListener(e -> logout());
        topPanel.add(btnLogout, BorderLayout.EAST);
        
        // Menu Panel
        JPanel menuPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JButton btnBrowse = new JButton("Browse Products");
        JButton btnOrders = new JButton("My Orders");
        JButton btnProfile = new JButton("Update Profile");
        JButton btnCart = new JButton("Shopping Cart");
        
        btnBrowse.addActionListener(e -> browseProducts());
        btnOrders.addActionListener(e -> viewMyOrders());
        btnProfile.addActionListener(e -> updateProfile());
        btnCart.addActionListener(e -> viewCart());
        
        menuPanel.add(btnBrowse);
        menuPanel.add(btnCart);
        menuPanel.add(btnOrders);
        menuPanel.add(btnProfile);
        
        // Center content
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(menuPanel, BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        
        setVisible(true);
    }
    
    private void browseProducts() {
        new ProductBrowseFrame(currentUser);
    }
    
    private void viewMyOrders() {
        JOptionPane.showMessageDialog(this, "My Orders - Coming Soon!");
    }
    
    private void updateProfile() {
        JOptionPane.showMessageDialog(this, "Update Profile - Coming Soon!");
    }
    
    private void viewCart() {
        JOptionPane.showMessageDialog(this, "Shopping Cart - Coming Soon!");
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
