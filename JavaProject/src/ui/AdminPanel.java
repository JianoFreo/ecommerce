package src.ui;

import java.awt.*;
import javax.swing.*;
import src.model.User;

public class AdminPanel extends JPanel {
    private User currentUser;
    private JTabbedPane tabbedPane;

    public AdminPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBackground(new Color(240, 240, 245));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("JianoFreoTech Admin");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(60, 60, 80));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JLabel userLabel = new JLabel("Welcome, " + user.getName());
        userLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        userLabel.setForeground(new Color(100, 100, 110));
        headerPanel.add(userLabel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Tabbed Pane for all management panels
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setBackground(Color.WHITE);
        tabbedPane.setForeground(new Color(60, 60, 80));
        tabbedPane.setFont(new Font("Arial", Font.PLAIN, 11));
        tabbedPane.addTab("Products", new ProductManagementPanel());
        tabbedPane.addTab("Categories", new CategoryManagementPanel());
        tabbedPane.addTab("Users", new UserManagementPanel());
        tabbedPane.addTab("Orders", new OrderManagementPanel());
        tabbedPane.addTab("Reviews", new ReviewManagementPanel());
        tabbedPane.addTab("Coupons", new AdminCouponPanel());
        tabbedPane.addTab("Analytics", new AdminAnalyticsPanel());
        
        add(tabbedPane, BorderLayout.CENTER);
    }
}
