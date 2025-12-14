package src.ui;

import java.awt.*;
import javax.swing.*;
import src.model.User;

public class MainMenuFrame extends JFrame {
    private User currentUser;

    public MainMenuFrame(User user) {
        this.currentUser = user;
        
        setTitle("E-Commerce Product Management");
        setSize(1200, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Top Navigation Panel
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        navPanel.setBackground(new Color(240, 240, 240));
        JLabel navLabel = new JLabel("Navigation");
        navLabel.setFont(new Font("Arial", Font.BOLD, 14));
        navPanel.add(navLabel);
        navPanel.add(Box.createHorizontalStrut(20));
        JLabel accountLabel = new JLabel("Account");
        accountLabel.setFont(new Font("Arial", Font.BOLD, 14));
        navPanel.add(accountLabel);
        add(navPanel, BorderLayout.NORTH);

        // Content Panel based on user role
        if ("Admin".equals(currentUser.getRole())) {
            add(new AdminPanel(currentUser), BorderLayout.CENTER);
        } else {
            add(new CustomerPanel(currentUser), BorderLayout.CENTER);
        }

        // Footer with logout
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JButton btnLogout = new JButton("Logout");
        btnLogout.addActionListener(e -> logout());
        footerPanel.add(btnLogout);
        add(footerPanel, BorderLayout.SOUTH);

        setVisible(true);
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
