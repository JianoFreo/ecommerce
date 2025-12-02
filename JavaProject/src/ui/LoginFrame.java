package src.ui;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import src.db.DatabaseConnection;

public class LoginFrame extends JFrame {
    private JTextField txtUser;
    private JPasswordField txtPass;

    public LoginFrame() {
        setTitle("E-Commerce Management System");
        setSize(400, 250);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Login Panel
        JPanel loginPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        loginPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        loginPanel.add(new JLabel("Email:"));
        txtUser = new JTextField();
        loginPanel.add(txtUser);

        loginPanel.add(new JLabel("Password:"));
        txtPass = new JPasswordField();
        loginPanel.add(txtPass);

        JButton btnLogin = new JButton("Login");
        JButton btnRegister = new JButton("Register");
        loginPanel.add(btnLogin);
        loginPanel.add(btnRegister);

        btnLogin.addActionListener(e -> login());
        btnRegister.addActionListener(e -> {
            dispose();
            new RegistrationFrame();
        });

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
                
                // Open Facebook-style main frame
                new FacebookStyleFrame(user);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Login error: " + e.getMessage());
        }
    }
}
