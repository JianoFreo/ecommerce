package src.ui;

import src.model.User;
import javax.swing.*;
import java.awt.*;

public class CustomerProfilePanel extends JPanel {
    private User currentUser;

    public CustomerProfilePanel(User user) {
        this.currentUser = user;
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JLabel header = new JLabel("My Profile");
        header.setFont(new Font("Arial", Font.BOLD, 20));
        add(header, BorderLayout.NORTH);

        // Profile Information Panel
        JPanel profilePanel = new JPanel(new GridLayout(6, 2, 10, 10));
        profilePanel.setBorder(BorderFactory.createTitledBorder("Account Information"));

        // Profile fields (read-only)
        profilePanel.add(new JLabel("Name:"));
        JTextField txtName = new JTextField(currentUser.getName());
        txtName.setEditable(false);
        profilePanel.add(txtName);

        profilePanel.add(new JLabel("Email:"));
        JTextField txtEmail = new JTextField(currentUser.getEmail());
        txtEmail.setEditable(false);
        profilePanel.add(txtEmail);

        profilePanel.add(new JLabel("Phone Number:"));
        JTextField txtPhone = new JTextField(currentUser.getPhoneNumber());
        txtPhone.setEditable(false);
        profilePanel.add(txtPhone);

        profilePanel.add(new JLabel("Address:"));
        JTextField txtAddress = new JTextField(currentUser.getAddress());
        txtAddress.setEditable(false);
        profilePanel.add(txtAddress);

        profilePanel.add(new JLabel("Role:"));
        JTextField txtRole = new JTextField(currentUser.getRole());
        txtRole.setEditable(false);
        profilePanel.add(txtRole);

        profilePanel.add(new JLabel("User ID:"));
        JTextField txtUserID = new JTextField(String.valueOf(currentUser.getUserID()));
        txtUserID.setEditable(false);
        profilePanel.add(txtUserID);

        add(profilePanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnEdit = new JButton("Edit Profile");
        JButton btnChangePassword = new JButton("Change Password");
        btnEdit.addActionListener(e -> editProfile());
        btnChangePassword.addActionListener(e -> changePassword());
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnChangePassword);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void editProfile() {
        JOptionPane.showMessageDialog(this, "Edit profile feature coming soon!", "Edit Profile", JOptionPane.INFORMATION_MESSAGE);
    }

    private void changePassword() {
        JOptionPane.showMessageDialog(this, "Change password feature coming soon!", "Change Password", JOptionPane.INFORMATION_MESSAGE);
    }
}
