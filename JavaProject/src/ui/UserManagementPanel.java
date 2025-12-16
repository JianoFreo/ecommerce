package src.ui;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;
import src.dao.UserDAO;
import src.model.User;

public class UserManagementPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtName, txtEmail, txtPassword, txtAddress, txtPhone;
    private JComboBox<String> cmbRole;
    private UserDAO userDAO;

    public UserManagementPanel() {
        userDAO = new UserDAO();
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(new Color(200, 220, 240));

        JLabel header = new JLabel("User Management");
        header.setFont(new Font("Arial", Font.BOLD, 20));
        add(header, BorderLayout.NORTH);

        model = new DefaultTableModel(
            new String[]{"ID", "Name", "Email", "Address", "Phone", "Role"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        loadUsers();
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridLayout(7, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("User Details"));

        txtName = new JTextField();
        txtEmail = new JTextField();
        txtPassword = new JTextField();
        txtAddress = new JTextField();
        txtPhone = new JTextField();
        cmbRole = new JComboBox<>(new String[]{"Customer", "Admin"});

        formPanel.add(new JLabel("Name:"));
        formPanel.add(txtName);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(txtEmail);
        formPanel.add(new JLabel("Password:"));
        formPanel.add(txtPassword);
        formPanel.add(new JLabel("Address:"));
        formPanel.add(txtAddress);
        formPanel.add(new JLabel("Phone:"));
        formPanel.add(txtPhone);
        formPanel.add(new JLabel("Role:"));
        formPanel.add(cmbRole);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnAdd = new JButton("Add User");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        JButton btnClear = new JButton("Clear");

        btnAdd.addActionListener(e -> addUser());
        btnUpdate.addActionListener(e -> updateUser());
        btnDelete.addActionListener(e -> deleteUser());
        btnClear.addActionListener(e -> clearForm());

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);

        formPanel.add(buttonPanel);
        add(formPanel, BorderLayout.SOUTH);

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    txtName.setText(model.getValueAt(row, 1).toString());
                    txtEmail.setText(model.getValueAt(row, 2).toString());
                    txtAddress.setText(model.getValueAt(row, 3).toString());
                    txtPhone.setText(model.getValueAt(row, 4).toString());
                    cmbRole.setSelectedItem(model.getValueAt(row, 5).toString());
                    txtPassword.setText(""); // Don't show password
                }
            }
        });
    }

    private void loadUsers() {
        model.setRowCount(0);
        List<User> users = userDAO.getAllUsers();
        for (User u : users) {
            model.addRow(new Object[]{
                u.getUserID(), u.getName(), u.getEmail(), 
                u.getAddress(), u.getPhoneNumber(), u.getRole()
            });
        }
    }

    private void addUser() {
        User user = new User(0, txtName.getText(), txtEmail.getText(),
            txtPassword.getText(), txtAddress.getText(), 
            txtPhone.getText(), (String) cmbRole.getSelectedItem());
        
        if (userDAO.registerUser(user)) {
            JOptionPane.showMessageDialog(this, "User added!");
            refresh();
        } else {
            JOptionPane.showMessageDialog(this, "Error adding user!");
        }
    }

    private void updateUser() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a user!");
            return;
        }
        
        int id = (int) model.getValueAt(row, 0);
        String password = txtPassword.getText().isEmpty() ? 
            userDAO.getUserById(id).getPassword() : txtPassword.getText();
        
        User user = new User(id, txtName.getText(), txtEmail.getText(),
            password, txtAddress.getText(), txtPhone.getText(), 
            (String) cmbRole.getSelectedItem());
        
        if (userDAO.updateUser(user)) {
            JOptionPane.showMessageDialog(this, "User updated!");
            refresh();
        }
    }

    private void deleteUser() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a user!");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Delete this user?", "Confirm", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            int id = (int) model.getValueAt(row, 0);
            if (userDAO.deleteUser(id)) {
                JOptionPane.showMessageDialog(this, "User deleted!");
                refresh();
            }
        }
    }

    private void clearForm() {
        txtName.setText("");
        txtEmail.setText("");
        txtPassword.setText("");
        txtAddress.setText("");
        txtPhone.setText("");
        cmbRole.setSelectedIndex(0);
        table.clearSelection();
    }

    private void refresh() {
        loadUsers();
        clearForm();
    }
}
