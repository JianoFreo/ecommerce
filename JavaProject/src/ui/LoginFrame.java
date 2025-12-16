package src.ui;

import java.awt.*;
import java.awt.geom.*;
import java.sql.*;
import javax.swing.*;
import src.db.DatabaseConnection;

public class LoginFrame extends JFrame {
    private JTextField txtUser;
    private JPasswordField txtPass;

    public LoginFrame() {
        setTitle("JianoFreoTech - E-Commerce");
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Header
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int width = getWidth();
                int height = getHeight();

                // Dark gradient background
                GradientPaint gradient = new GradientPaint(0, 0, new Color(15, 20, 40), width, height, new Color(25, 35, 60));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, width, height);

                // Draw particle wave effect
                drawParticleWave(g2d, width, height);
            }

            private void drawParticleWave(Graphics2D g2d, int width, int height) {
                long time = System.currentTimeMillis() / 50;
                
                for (int x = 0; x < width; x += 8) {
                    for (int y = 0; y < height; y += 8) {
                        double wave1 = Math.sin((x * 0.01 + time * 0.05)) * 30;
                        double wave2 = Math.cos((y * 0.01 + time * 0.03)) * 20;
                        
                        float distance = (float) Math.sqrt(wave1 * wave1 + wave2 * wave2);
                        float alpha = Math.max(0, 1 - distance / 50f);
                        
                        int r = (int) (150 + (distance / 50) * 100);
                        int g = (int) (100 + (distance / 50) * 150);
                        int b = 200;
                        
                        g2d.setColor(new Color(r, g, b, (int) (alpha * 150)));
                        g2d.fillOval(x + (int) wave1, y + (int) wave2, 4, 4);
                    }
                }
            }
        };
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        headerPanel.setPreferredSize(new Dimension(0, 150));
        headerPanel.setOpaque(true);
        headerPanel.setBackground(new Color(15, 20, 40));

        JLabel titleLabel = new JLabel("JianoFreoTech");
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 42));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(titleLabel);

        add(headerPanel, BorderLayout.NORTH);

        // Login Panel
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
        loginPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        loginPanel.setBackground(Color.WHITE);
        
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        emailLabel.setForeground(new Color(60, 60, 80));
        loginPanel.add(emailLabel);
        txtUser = new JTextField();
        txtUser.setFont(new Font("Arial", Font.PLAIN, 12));
        txtUser.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 225), 1));
        txtUser.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        loginPanel.add(txtUser);
        loginPanel.add(Box.createVerticalStrut(15));

        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        passLabel.setForeground(new Color(60, 60, 80));
        loginPanel.add(passLabel);
        txtPass = new JPasswordField();
        txtPass.setFont(new Font("Arial", Font.PLAIN, 12));
        txtPass.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 225), 1));
        txtPass.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        loginPanel.add(txtPass);
        loginPanel.add(Box.createVerticalStrut(20));

        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton btnLogin = new JButton("Login");
        btnLogin.setFont(new Font("Arial", Font.BOLD, 12));
        btnLogin.setBackground(new Color(66, 133, 244));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnLogin.addActionListener(e -> login());
        buttonPanel.add(btnLogin);
        
        JButton btnRegister = new JButton("Create New Account");
        btnRegister.setFont(new Font("Arial", Font.PLAIN, 12));
        btnRegister.setBackground(new Color(200, 200, 210));
        btnRegister.setForeground(new Color(60, 60, 80));
        btnRegister.setFocusPainted(false);
        btnRegister.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnRegister.addActionListener(e -> {
            dispose();
            new RegistrationFrame();
        });
        buttonPanel.add(btnRegister);

        loginPanel.add(buttonPanel);
        add(loginPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    private void login() {
        String email = txtUser.getText();
        String password = String.valueOf(txtPass.getPassword());

        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM users WHERE email=? AND password=?");
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                src.model.User user = new src.model.User(
                    rs.getInt("userID"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("address"),
                    rs.getString("phoneNumber"),
                    rs.getString("role")
                );
                
                JOptionPane.showMessageDialog(this, "Login Successful! Welcome " + user.getName());
                dispose();
                
                // Open simple main menu
                new MainMenuFrame(user);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Login error: " + e.getMessage());
        }
    }
}
