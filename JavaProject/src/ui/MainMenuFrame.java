package src.ui;

import java.awt.*;
import javax.swing.*;
import src.model.User;

public class MainMenuFrame extends JFrame {
    private User currentUser;

    public MainMenuFrame(User user) {
        this.currentUser = user;
        
        setTitle("E-Commerce System - " + user.getName());
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.add(new JLabel("E-Commerce Management System"));
        add(titlePanel, BorderLayout.NORTH);

        // Menu Panel
        JPanel menuPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Admin menus
        if ("Admin".equals(currentUser.getRole())) {
            menuPanel.add(createMenuButton("Manage Products", e -> openProductManagement()));
            menuPanel.add(createMenuButton("Manage Categories", e -> openCategoryManagement()));
            menuPanel.add(createMenuButton("Manage Users", e -> openUserManagement()));
            menuPanel.add(createMenuButton("Manage Orders", e -> openOrderManagement()));
            menuPanel.add(createMenuButton("Manage Reviews", e -> openReviewManagement()));
            menuPanel.add(createMenuButton("Browse & Shop", e -> openShopping()));
        } else {
            // Customer menus
            menuPanel.add(createMenuButton("Browse & Shop", e -> openShopping()));
            menuPanel.add(createMenuButton("My Orders", e -> openOrderHistory()));
            menuPanel.add(createMenuButton("My Profile", e -> showProfile()));
        }

        menuPanel.add(createMenuButton("Logout", e -> logout()));

        add(menuPanel, BorderLayout.CENTER);

        // Status Bar
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.add(new JLabel("Logged in as: " + user.getName() + " (" + user.getRole() + ")"));
        add(statusPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private JButton createMenuButton(String text, java.awt.event.ActionListener action) {
        JButton button = new JButton(text);
        button.addActionListener(action);
        return button;
    }

    private void openProductManagement() {
        new JFrame("Product Management") {{
            setSize(900, 600);
            setLocationRelativeTo(null);
            add(new ProductManagementPanel());
            setVisible(true);
        }};
    }

    private void openCategoryManagement() {
        new JFrame("Category Management") {{
            setSize(800, 500);
            setLocationRelativeTo(null);
            add(new CategoryManagementPanel());
            setVisible(true);
        }};
    }

    private void openUserManagement() {
        new JFrame("User Management") {{
            setSize(900, 600);
            setLocationRelativeTo(null);
            add(new UserManagementPanel());
            setVisible(true);
        }};
    }

    private void openOrderManagement() {
        new JFrame("Order Management") {{
            setSize(900, 600);
            setLocationRelativeTo(null);
            add(new OrderManagementPanel());
            setVisible(true);
        }};
    }

    private void openReviewManagement() {
        new JFrame("Review Management") {{
            setSize(900, 600);
            setLocationRelativeTo(null);
            add(new ReviewManagementPanel());
            setVisible(true);
        }};
    }

    private void openShopping() {
        new JFrame("Browse & Shop") {{
            setSize(1000, 700);
            setLocationRelativeTo(null);
            add(new ShoppingPanel(currentUser));
            setVisible(true);
        }};
    }

    private void openOrderHistory() {
        new JFrame("My Order History") {{
            setSize(800, 500);
            setLocationRelativeTo(null);
            add(new OrderHistoryPanel(currentUser));
            setVisible(true);
        }};
    }

    private void showProfile() {
        String profile = "Name: " + currentUser.getName() + "\n" +
                        "Email: " + currentUser.getEmail() + "\n" +
                        "Phone: " + currentUser.getPhoneNumber() + "\n" +
                        "Address: " + currentUser.getAddress();
        JOptionPane.showMessageDialog(this, profile, "My Profile", JOptionPane.INFORMATION_MESSAGE);
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to logout?", 
            "Logout", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame();
        }
    }
}
