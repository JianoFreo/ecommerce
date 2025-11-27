package src.ui;

import src.dao.ProductDAO;
import src.dao.CategoryDAO;
import src.model.Product;
import src.model.Category;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class ProductManagementPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtName, txtDesc, txtPrice, txtQty;
    private JComboBox<Category> cmbCategory;
    private ProductDAO productDAO;
    private CategoryDAO categoryDAO;

    public ProductManagementPanel() {
        productDAO = new ProductDAO();
        categoryDAO = new CategoryDAO();
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Header
        JLabel header = new JLabel("Product Management");
        header.setFont(new Font("Arial", Font.BOLD, 20));
        add(header, BorderLayout.NORTH);

        // Table setup
        model = new DefaultTableModel(
            new String[]{"ID", "Name", "Description", "Price", "Quantity", "Category"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        loadProducts();
        
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Product Details"));

        txtName = new JTextField();
        txtDesc = new JTextField();
        txtPrice = new JTextField();
        txtQty = new JTextField();
        cmbCategory = new JComboBox<>();
        loadCategories();

        formPanel.add(new JLabel("Name:"));
        formPanel.add(txtName);
        formPanel.add(new JLabel("Description:"));
        formPanel.add(txtDesc);
        formPanel.add(new JLabel("Price:"));
        formPanel.add(txtPrice);
        formPanel.add(new JLabel("Quantity:"));
        formPanel.add(txtQty);
        formPanel.add(new JLabel("Category:"));
        formPanel.add(cmbCategory);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnAdd = new JButton("Add Product");
        JButton btnUpdate = new JButton("Update Product");
        JButton btnDelete = new JButton("Delete Product");
        JButton btnClear = new JButton("Clear Form");
        JButton btnLowStock = new JButton("View Low Stock");

        btnAdd.addActionListener(e -> addProduct());
        btnUpdate.addActionListener(e -> updateProduct());
        btnDelete.addActionListener(e -> deleteProduct());
        btnClear.addActionListener(e -> clearForm());
        btnLowStock.addActionListener(e -> showLowStock());

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);
        buttonPanel.add(btnLowStock);

        formPanel.add(buttonPanel);

        add(formPanel, BorderLayout.SOUTH);

        // Table selection listener
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    txtName.setText(model.getValueAt(row, 1).toString());
                    txtDesc.setText(model.getValueAt(row, 2).toString());
                    txtPrice.setText(model.getValueAt(row, 3).toString());
                    txtQty.setText(model.getValueAt(row, 4).toString());
                    String categoryName = model.getValueAt(row, 5).toString();
                    for (int i = 0; i < cmbCategory.getItemCount(); i++) {
                        if (cmbCategory.getItemAt(i).getName().equals(categoryName)) {
                            cmbCategory.setSelectedIndex(i);
                            break;
                        }
                    }
                }
            }
        });
    }

    private void loadCategories() {
        cmbCategory.removeAllItems();
        List<Category> categories = categoryDAO.getAllCategories();
        for (Category cat : categories) {
            cmbCategory.addItem(cat);
        }
    }

    private void loadProducts() {
        model.setRowCount(0);
        List<Product> products = productDAO.getAllProducts();
        for (Product p : products) {
            model.addRow(new Object[]{
                p.getId(),
                p.getName(),
                p.getDescription(),
                String.format("%.2f", p.getPrice()),
                p.getQuantity(),
                p.getCategoryName() != null ? p.getCategoryName() : "N/A"
            });
        }
    }

    private void addProduct() {
        try {
            Category selectedCategory = (Category) cmbCategory.getSelectedItem();
            if (selectedCategory == null) {
                JOptionPane.showMessageDialog(this, "Please select a category!");
                return;
            }

            Product product = new Product(
                0,
                txtName.getText(),
                txtDesc.getText(),
                Double.parseDouble(txtPrice.getText()),
                Integer.parseInt(txtQty.getText()),
                selectedCategory.getCategoryID()
            );

            productDAO.addProduct(product);
            JOptionPane.showMessageDialog(this, "Product added successfully!");
            refresh();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid price or quantity!");
        }
    }

    private void updateProduct() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a product to update!");
            return;
        }

        try {
            Category selectedCategory = (Category) cmbCategory.getSelectedItem();
            int id = (int) model.getValueAt(row, 0);
            
            Product product = new Product(
                id,
                txtName.getText(),
                txtDesc.getText(),
                Double.parseDouble(txtPrice.getText()),
                Integer.parseInt(txtQty.getText()),
                selectedCategory.getCategoryID()
            );

            productDAO.updateProduct(product);
            JOptionPane.showMessageDialog(this, "Product updated successfully!");
            refresh();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid price or quantity!");
        }
    }

    private void deleteProduct() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a product to delete!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete this product?", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int id = (int) model.getValueAt(row, 0);
            productDAO.deleteProduct(id);
            JOptionPane.showMessageDialog(this, "Product deleted successfully!");
            refresh();
        }
    }

    private void showLowStock() {
        List<Product> lowStockProducts = productDAO.getLowStockProducts();
        if (lowStockProducts.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No low stock products found!");
            return;
        }

        StringBuilder message = new StringBuilder("Low Stock Products (< 10 items):\n\n");
        for (Product p : lowStockProducts) {
            message.append(String.format("• %s - Quantity: %d\n", p.getName(), p.getQuantity()));
        }

        JOptionPane.showMessageDialog(this, message.toString(), 
            "Low Stock Alert", JOptionPane.WARNING_MESSAGE);
    }

    private void clearForm() {
        txtName.setText("");
        txtDesc.setText("");
        txtPrice.setText("");
        txtQty.setText("");
        if (cmbCategory.getItemCount() > 0) {
            cmbCategory.setSelectedIndex(0);
        }
        table.clearSelection();
    }

    private void refresh() {
        loadProducts();
        clearForm();
    }
}
