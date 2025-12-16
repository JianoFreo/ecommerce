package src.ui;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;
import src.dao.CategoryDAO;
import src.model.Category;

public class CategoryManagementPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtName, txtDesc;
    private CategoryDAO categoryDAO;

    public CategoryManagementPanel() {
        categoryDAO = new CategoryDAO();
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(new Color(200, 220, 240));

        // Header
        JLabel header = new JLabel("Category Management");
        header.setFont(new Font("Arial", Font.BOLD, 20));
        add(header, BorderLayout.NORTH);

        // Table
        model = new DefaultTableModel(new String[]{"ID", "Name", "Description"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        loadCategories();
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Form
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Category Details"));

        txtName = new JTextField();
        txtDesc = new JTextField();

        formPanel.add(new JLabel("Name:"));
        formPanel.add(txtName);
        formPanel.add(new JLabel("Description:"));
        formPanel.add(txtDesc);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnAdd = new JButton("Add");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        JButton btnClear = new JButton("Clear");

        btnAdd.addActionListener(e -> addCategory());
        btnUpdate.addActionListener(e -> updateCategory());
        btnDelete.addActionListener(e -> deleteCategory());
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
                    txtDesc.setText(model.getValueAt(row, 2).toString());
                }
            }
        });
    }

    private void loadCategories() {
        model.setRowCount(0);
        List<Category> categories = categoryDAO.getAllCategories();
        for (Category c : categories) {
            model.addRow(new Object[]{c.getCategoryID(), c.getName(), c.getDescription()});
        }
    }

    private void addCategory() {
        Category category = new Category(0, txtName.getText(), txtDesc.getText());
        if (categoryDAO.addCategory(category)) {
            JOptionPane.showMessageDialog(this, "Category added!");
            refresh();
        }
    }

    private void updateCategory() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a category!");
            return;
        }
        int id = (int) model.getValueAt(row, 0);
        Category category = new Category(id, txtName.getText(), txtDesc.getText());
        if (categoryDAO.updateCategory(category)) {
            JOptionPane.showMessageDialog(this, "Category updated!");
            refresh();
        }
    }

    private void deleteCategory() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a category!");
            return;
        }
        int id = (int) model.getValueAt(row, 0);
        if (categoryDAO.deleteCategory(id)) {
            JOptionPane.showMessageDialog(this, "Category deleted!");
            refresh();
        }
    }

    private void clearForm() {
        txtName.setText("");
        txtDesc.setText("");
        table.clearSelection();
    }

    private void refresh() {
        loadCategories();
        clearForm();
    }
}
