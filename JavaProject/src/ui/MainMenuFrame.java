package src.ui;

import java.awt.*;
import javax.swing.*;
import src.model.User;

public class MainMenuFrame extends JFrame {
    private User currentUser;

    public MainMenuFrame(User user) {
        this.currentUser = user;
        
        setTitle("JianoFreoTech - E-Commerce");
        setSize(1400, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Top Navigation Panel
        JPanel navPanel = new JPanel(new BorderLayout());
        navPanel.setBackground(Color.WHITE);
        navPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 225)));
        navPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        JLabel titleLabel = new JLabel("JianoFreoTech");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(60, 60, 80));
        navPanel.add(titleLabel, BorderLayout.WEST);

        JPanel rightNav = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        rightNav.setBackground(Color.WHITE);
        
        JLabel userLabel = new JLabel(user.getName() + " (" + user.getRole() + ")");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        userLabel.setForeground(new Color(100, 100, 110));
        rightNav.add(userLabel);

        JButton btnLogout = new JButton("Logout");
        btnLogout.setFont(new Font("Arial", Font.PLAIN, 11));
        btnLogout.setBackground(new Color(229, 57, 53));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        btnLogout.addActionListener(e -> logout());
        rightNav.add(btnLogout);

        navPanel.add(rightNav, BorderLayout.EAST);
        add(navPanel, BorderLayout.NORTH);

        // Content Panel based on user role
        if ("Admin".equals(currentUser.getRole())) {
            add(new AdminPanel(currentUser), BorderLayout.CENTER);
        } else {
            add(new CustomerPanel(currentUser), BorderLayout.CENTER);
        }

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
