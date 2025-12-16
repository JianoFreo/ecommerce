package src.ui;

import java.awt.*;
import javax.swing.*;
import src.dao.UserDAO;
import src.model.User;

public class RegistrationFrame extends JFrame {
    private JTextField txtName, txtEmail, txtAddress, txtPhone;
    private JPasswordField txtPassword, txtConfirmPassword;
    private JButton btnRegister, btnCancel;
    private UserDAO userDAO;

    public RegistrationFrame() {
        userDAO = new UserDAO();
        
        setTitle("User Registration - E-Commerce");
        setSize(400, 450);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Header
        JLabel headerLabel = new JLabel("Create New Account", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        add(headerLabel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(new Color(200, 220, 240));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        txtName = new JTextField();
        txtEmail = new JTextField();
        txtPassword = new JPasswordField();
        txtConfirmPassword = new JPasswordField();
        txtAddress = new JTextField();
        txtPhone = new JTextField();

        formPanel.add(new JLabel("Full Name:"));
        formPanel.add(txtName);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(txtEmail);
        formPanel.add(new JLabel("Password:"));
        formPanel.add(txtPassword);
        formPanel.add(new JLabel("Confirm Password:"));
        formPanel.add(txtConfirmPassword);
        formPanel.add(new JLabel("Address:"));
        formPanel.add(txtAddress);
        formPanel.add(new JLabel("Phone Number:"));
        formPanel.add(txtPhone);

        add(formPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        btnRegister = new JButton("Register");
        btnCancel = new JButton("Cancel");

        btnRegister.addActionListener(e -> register());
        btnCancel.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });

        buttonPanel.add(btnRegister);
        buttonPanel.add(btnCancel);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void register() {
        // Validation
        if (txtName.getText().trim().isEmpty() || 
            txtEmail.getText().trim().isEmpty() || 
            txtPassword.getPassword().length == 0) {
            JOptionPane.showMessageDialog(this, "Please fill all required fields!");
            return;
        }

        String password = String.valueOf(txtPassword.getPassword());
        String confirmPassword = String.valueOf(txtConfirmPassword.getPassword());

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match!");
            return;
        }

        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this, "Password must be at least 6 characters!");
            return;
        }

        // Check if email already exists
        User existingUser = userDAO.getUserByEmail(txtEmail.getText());
        if (existingUser != null) {
            JOptionPane.showMessageDialog(this, "Email already registered!");
            return;
        }

        // Create new user
        User newUser = new User(
            0,
            txtName.getText(),
            txtEmail.getText(),
            password, // In production, hash this password
            txtAddress.getText(),
            txtPhone.getText(),
            "Customer" // Default role
        );

        if (userDAO.registerUser(newUser)) {
            JOptionPane.showMessageDialog(this, 
                "Registration successful! You can now login.", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            dispose();
            new LoginFrame();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Registration failed! Please try again.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
