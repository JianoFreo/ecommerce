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
        setBackground(new Color(200, 220, 240));

        // Tabbed Pane for all management panels
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
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
