package src.ui;

import src.dao.*;
import src.model.User;
import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {
    private User currentUser;
    private JPanel mainPanel;
    private CardLayout cardLayout;

    public AdminDashboard(User user) {
        this.currentUser = user;
        
        setTitle("Admin Dashboard - E-Commerce System");
        setSize(1200, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create menu bar
        createMenuBar();

        // Create main panel with CardLayout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Add panels
        mainPanel.add(createDashboardPanel(), "DASHBOARD");
        mainPanel.add(new ProductManagementPanel(), "PRODUCTS");
        mainPanel.add(new CategoryManagementPanel(), "CATEGORIES");
        mainPanel.add(new OrderManagementPanel(), "ORDERS");
        mainPanel.add(new UserManagementPanel(), "USERS");
        mainPanel.add(new ReviewManagementPanel(), "REVIEWS");

        add(mainPanel);
        
        // Show dashboard by default
        cardLayout.show(mainPanel, "DASHBOARD");
        
        setVisible(true);
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // Dashboard Menu
        JMenu dashboardMenu = new JMenu("Dashboard");
        JMenuItem homeItem = new JMenuItem("Home");
        homeItem.addActionListener(e -> cardLayout.show(mainPanel, "DASHBOARD"));
        dashboardMenu.add(homeItem);
        menuBar.add(dashboardMenu);

        // Products Menu
        JMenu productsMenu = new JMenu("Products");
        JMenuItem manageProducts = new JMenuItem("Manage Products");
        manageProducts.addActionListener(e -> cardLayout.show(mainPanel, "PRODUCTS"));
        JMenuItem manageCategories = new JMenuItem("Manage Categories");
        manageCategories.addActionListener(e -> cardLayout.show(mainPanel, "CATEGORIES"));
        productsMenu.add(manageProducts);
        productsMenu.add(manageCategories);
        menuBar.add(productsMenu);

        // Orders Menu
        JMenu ordersMenu = new JMenu("Orders");
        JMenuItem manageOrders = new JMenuItem("Manage Orders");
        manageOrders.addActionListener(e -> cardLayout.show(mainPanel, "ORDERS"));
        ordersMenu.add(manageOrders);
        menuBar.add(ordersMenu);

        // Users Menu
        JMenu usersMenu = new JMenu("Users");
        JMenuItem manageUsers = new JMenuItem("Manage Users");
        manageUsers.addActionListener(e -> cardLayout.show(mainPanel, "USERS"));
        usersMenu.add(manageUsers);
        menuBar.add(usersMenu);

        // Reviews Menu
        JMenu reviewsMenu = new JMenu("Reviews");
        JMenuItem manageReviews = new JMenuItem("Manage Reviews");
        manageReviews.addActionListener(e -> cardLayout.show(mainPanel, "REVIEWS"));
        reviewsMenu.add(manageReviews);
        menuBar.add(reviewsMenu);

        // Account Menu
        JMenu accountMenu = new JMenu("Account");
        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });
        accountMenu.add(logoutItem);
        menuBar.add(accountMenu);

        setJMenuBar(menuBar);
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JLabel headerLabel = new JLabel("Admin Dashboard - Welcome " + currentUser.getName());
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(headerLabel, BorderLayout.NORTH);

        // Statistics Panel
        JPanel statsPanel = new JPanel(new GridLayout(2, 3, 15, 15));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Get statistics
        ProductDAO productDAO = new ProductDAO();
        OrderDAO orderDAO = new OrderDAO();
        UserDAO userDAO = new UserDAO();
        ReviewDAO reviewDAO = new ReviewDAO();

        int totalProducts = productDAO.getAllProducts().size();
        int lowStockProducts = productDAO.getLowStockProducts().size();
        int totalOrders = orderDAO.getAllOrders().size();
        int totalUsers = userDAO.getAllUsers().size();
        int totalReviews = reviewDAO.getAllReviews().size();

        statsPanel.add(createStatCard("Total Products", String.valueOf(totalProducts), new Color(52, 152, 219)));
        statsPanel.add(createStatCard("Low Stock Items", String.valueOf(lowStockProducts), new Color(231, 76, 60)));
        statsPanel.add(createStatCard("Total Orders", String.valueOf(totalOrders), new Color(46, 204, 113)));
        statsPanel.add(createStatCard("Total Users", String.valueOf(totalUsers), new Color(155, 89, 182)));
        statsPanel.add(createStatCard("Total Reviews", String.valueOf(totalReviews), new Color(241, 196, 15)));
        statsPanel.add(createStatCard("Admin Panel", "Active", new Color(52, 73, 94)));

        panel.add(statsPanel, BorderLayout.CENTER);

        // Quick Actions Panel
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton refreshBtn = new JButton("Refresh Statistics");
        refreshBtn.setFont(new Font("Arial", Font.PLAIN, 14));
        refreshBtn.addActionListener(e -> {
            dispose();
            new AdminDashboard(currentUser);
        });
        actionsPanel.add(refreshBtn);

        panel.add(actionsPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));

        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 36));
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }
}
