package src.ui;

import src.model.User;
import javax.swing.*;
import java.awt.*;

public class CustomerPanel extends JPanel {
    private User currentUser;
    private JTabbedPane tabbedPane;

    public CustomerPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout());

        // Tabbed Pane for customer features
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.addTab("Shop", new ShoppingPanel(user));
        tabbedPane.addTab("My Profile", new CustomerProfilePanel(user));
        tabbedPane.addTab("My Orders", new OrderHistoryPanel(user));
        
        add(tabbedPane, BorderLayout.CENTER);
    }
}
