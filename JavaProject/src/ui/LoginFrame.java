package src.ui; // Package declaration

import src.db.DatabaseConnection; // Import DB helper
import javax.swing.*; // Import Swing UI classes
import java.awt.*; // Import layout classes
import java.sql.*; // Import JDBC classes

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

        add(new JLabel("Username:")); // Label for username
        txtUser = new JTextField(); // Input field
        add(txtUser); // Add to layout

        add(new JLabel("Password:")); // Label for password
        txtPass = new JPasswordField(); // Input field
        add(txtPass);

        btnLogin = new JButton("Login"); // Create login button
        add(new JLabel()); // Empty placeholder
        add(btnLogin); // Add button to layout

        btnLogin.addActionListener(e -> login()); // When clicked, call login method

        setVisible(true); // Show window
    }

    private void login() { // Method to validate login
        String username = txtUser.getText(); // Get username text
        String password = String.valueOf(txtPass.getPassword()); // Get password text

        try (Connection conn = DatabaseConnection.getConnection()) { // Open DB connection
            PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM users WHERE username=? AND password=?"); // SQL query
            ps.setString(1, username); // 1 – This refers to the position of the parameter in the SQL query.
            ps.setString(2, password); // Set password parameter
            ResultSet rs = ps.executeQuery(); // Execute query

            if (rs.next()) { // If a row exists, login success
                JOptionPane.showMessageDialog(this, "Login Successful!"); // Show success message
                dispose(); // Close login window
                new ProductFrame(); // Open product management window
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials!"); // Show error
            }
        } catch (Exception e) {
            e.printStackTrace(); // Print DB errors
        }
    }
}
