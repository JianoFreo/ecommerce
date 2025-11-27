package src.ui; // Package declaration

import java.awt.*; // Import DB helper
import java.sql.*; // Import Swing UI classes
import javax.swing.*; // Import layout classes
import src.db.DatabaseConnection; // Import JDBC classes

public class LoginFrame extends JFrame { // Login window
    private JTextField txtUser; // Text field for username
    private JPasswordField txtPass; // Password field
    private JButton btnLogin; // Login button

    public LoginFrame() { // Constructor
        setTitle("Login - E-Commerce"); // Window title
        setSize(300, 180); // Window size
        setLayout(new GridLayout(3, 2)); // Layout with 3 rows, 2 columns
        setDefaultCloseOperation(EXIT_ON_CLOSE); // Close app when window closes
        setLocationRelativeTo(null); // Center window

        add(new JLabel("Email:")); // Label for email
        txtUser = new JTextField(); // Input field
        add(txtUser); // Add to layout

        add(new JLabel("Password:")); // Label for password
        txtPass = new JPasswordField(); // Input field
        add(txtPass);

        btnLogin = new JButton("Login"); // Create login button
        JButton btnRegister = new JButton("Register"); // Create register button
        add(btnLogin); // Add button to layout
        add(btnRegister); // Add register button

        btnLogin.addActionListener(e -> login()); // When clicked, call login method
        btnRegister.addActionListener(e -> {
            dispose();
            new RegistrationFrame();
        });

        setVisible(true); // Show window
    }

    private void login() { // Method to validate login
        String email = txtUser.getText(); // Get email text
        String password = String.valueOf(txtPass.getPassword()); // Get password text

        try (Connection conn = DatabaseConnection.getConnection()) { // Open DB connection
            PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM users WHERE email=? AND password=?"); // SQL query
            ps.setString(1, email); // Set email parameter
            ps.setString(2, password); // Set password parameter
            ResultSet rs = ps.executeQuery(); // Execute query

            if (rs.next()) { // If a row exists, login success
                // Create User object
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
                dispose(); // Close login window
                
                // Open appropriate dashboard based on role
                if ("Admin".equals(user.getRole())) {
                    new AdminDashboard(user); // Open admin dashboard
                } else {
                    new CustomerDashboard(user); // Open customer dashboard
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials!"); // Show error
            }
        } catch (Exception e) {
            e.printStackTrace(); // Print DB errors
            JOptionPane.showMessageDialog(this, "Login error: " + e.getMessage());
        }
    }
}
