package src.ui;

import java.awt.*;
import javax.swing.*;
import src.dao.UserDAO;
import src.models.User;
import src.utils.InputValidator;

public class RegisterFrame extends JFrame {
    private JTextField txtName, txtEmail, txtPhone, txtAddress;
    private JPasswordField txtPassword, txtConfirmPassword;
    private JButton btnRegister, btnCancel;
    private UserDAO userDAO;
    private LoginFrame loginFrame;

    public RegisterFrame(LoginFrame loginFrame) {
        this.loginFrame = loginFrame;
        this.userDAO = new UserDAO();
        
        setTitle("Register - Sales & Inventory System");
        setSize(450, 450);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Title
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("User Registration");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titlePanel.add(titleLabel);
        
        // Form Panel
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        
        formPanel.add(new JLabel("Full Name:"));
        txtName = new JTextField();
        formPanel.add(txtName);
        
        formPanel.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        formPanel.add(txtEmail);
        
        formPanel.add(new JLabel("Password:"));
        txtPassword = new JPasswordField();
        formPanel.add(txtPassword);
        
        formPanel.add(new JLabel("Confirm Password:"));
        txtConfirmPassword = new JPasswordField();
        formPanel.add(txtConfirmPassword);
        
        formPanel.add(new JLabel("Phone:"));
        txtPhone = new JTextField();
        formPanel.add(txtPhone);
        
        formPanel.add(new JLabel("Address:"));
        txtAddress = new JTextField();
        formPanel.add(txtAddress);
        
        btnRegister = new JButton("Register");
        btnCancel = new JButton("Cancel");
        formPanel.add(btnRegister);
        formPanel.add(btnCancel);
        
        add(titlePanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        
        // Action listeners
        btnRegister.addActionListener(e -> register());
        btnCancel.addActionListener(e -> {
            dispose();
            loginFrame.setVisible(true);
        });
        
        setVisible(true);
    }
    
    private void register() {
        String name = txtName.getText().trim();
        String email = txtEmail.getText().trim();
        String password = String.valueOf(txtPassword.getPassword());
        String confirmPassword = String.valueOf(txtConfirmPassword.getPassword());
        String phone = txtPhone.getText().trim();
        String address = txtAddress.getText().trim();
        
        // Validation
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields!", 
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!InputValidator.isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Invalid email format!", 
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match!", 
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this, "Password must be at least 6 characters!", 
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (userDAO.emailExists(email)) {
            JOptionPane.showMessageDialog(this, "Email already registered!", 
                "Registration Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Create user
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password); // Should hash in production
        user.setPhoneNumber(phone);
        user.setAddress(address);
        user.setRole("Customer");
        
        if (userDAO.createUser(user)) {
            JOptionPane.showMessageDialog(this, 
                "Registration successful! You can now login.", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            loginFrame.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Registration failed! Please try again.", 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
