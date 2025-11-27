package src.ui;

import java.awt.*;
import javax.swing.*;
import src.dao.UserDAO;
import src.models.User;

public class LoginFrame extends JFrame {
    private JTextField txtEmail;
    private JPasswordField txtPass;
    private JButton btnLogin;
    private JButton btnRegister;
    private UserDAO userDAO;

    public LoginFrame() {
        userDAO = new UserDAO();
        
        setTitle("Login - Sales & Inventory System");
        setSize(400, 250);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Title Panel
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("Sales & Inventory Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titlePanel.add(titleLabel);
        
        // Form Panel
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        
        formPanel.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        formPanel.add(txtEmail);

        formPanel.add(new JLabel("Password:"));
        txtPass = new JPasswordField();
        formPanel.add(txtPass);
        
        btnLogin = new JButton("Login");
        btnRegister = new JButton("Register");
        formPanel.add(btnLogin);
        formPanel.add(btnRegister);

        // Add panels
        add(titlePanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        
        // Action listeners
        btnLogin.addActionListener(e -> login());
        btnRegister.addActionListener(e -> openRegisterFrame());
        
        // Enter key to login
        txtPass.addActionListener(e -> login());

        setVisible(true);
    }

    private void login() {
        String email = txtEmail.getText().trim();
        String password = String.valueOf(txtPass.getPassword());
        
        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter email and password!", 
                "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        User user = userDAO.authenticateUser(email, password);
        
        if (user != null) {
            JOptionPane.showMessageDialog(this, "Login Successful!\nWelcome, " + user.getName(), 
                "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            
            if (user.isAdmin()) {
                new AdminDashboard(user);
            } else {
                new CustomerDashboard(user);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Invalid email or password!", 
                "Login Failed", JOptionPane.ERROR_MESSAGE);
            txtPass.setText("");
        }
    }
    
    private void openRegisterFrame() {
        new RegisterFrame(this);
        setVisible(false);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame());
    }
}
