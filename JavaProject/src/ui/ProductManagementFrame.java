package src.ui;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import src.dao.ProductDAO;
import src.models.Product;
import src.models.User;

public class ProductManagementFrame extends JFrame {
    private User currentUser;
    private ProductDAO productDAO;
    private JTable productTable;
    private DefaultTableModel tableModel;
    private JButton btnAdd, btnEdit, btnDelete, btnRefresh;
    
    public ProductManagementFrame(User user) {
        this.currentUser = user;
        this.productDAO = new ProductDAO();
        
        setTitle("Product Management");
        setSize(900, 600);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        
        // Top Panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblTitle = new JLabel("Product Management");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(lblTitle);
        
        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnAdd = new JButton("Add Product");
        btnEdit = new JButton("Edit Product");
        btnDelete = new JButton("Delete Product");
        btnRefresh = new JButton("Refresh");
        
        btnAdd.addActionListener(e -> addProduct());
        btnEdit.addActionListener(e -> editProduct());
        btnDelete.addActionListener(e -> deleteProduct());
        btnRefresh.addActionListener(e -> loadProducts());
        
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);
        
        // Table
        String[] columns = {"ID", "Title", "Description", "Price", "Quantity", "Category ID", "Low Stock"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        productTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(productTable);
        
        add(topPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);
        add(scrollPane, BorderLayout.CENTER);
        
        loadProducts();
        setVisible(true);
    }
    
    private void loadProducts() {
        tableModel.setRowCount(0);
        List<Product> products = productDAO.getAllProducts();
        
        for (Product product : products) {
            Object[] row = {
                product.getProductID(),
                product.getTitle(),
                product.getDescription(),
                String.format("$%.2f", product.getPrice()),
                product.getQuantity(),
                product.getCategoryID(),
                product.isLowStock() ? "YES" : "NO"
            };
            tableModel.addRow(row);
        }
    }
    
    private void addProduct() {
        JTextField txtTitle = new JTextField();
        JTextField txtDescription = new JTextField();
        JTextField txtPrice = new JTextField();
        JTextField txtQuantity = new JTextField();
        JTextField txtCategory = new JTextField();
        JTextField txtThreshold = new JTextField("10");
        
        Object[] message = {
            "Title:", txtTitle,
            "Description:", txtDescription,
            "Price:", txtPrice,
            "Quantity:", txtQuantity,
            "Category ID:", txtCategory,
            "Low Stock Threshold:", txtThreshold
        };
        
        int option = JOptionPane.showConfirmDialog(this, message, "Add Product", JOptionPane.OK_CANCEL_OPTION);
        
        if (option == JOptionPane.OK_OPTION) {
            try {
                Product product = new Product();
                product.setTitle(txtTitle.getText());
                product.setDescription(txtDescription.getText());
                product.setPrice(Double.parseDouble(txtPrice.getText()));
                product.setQuantity(Integer.parseInt(txtQuantity.getText()));
                product.setCategoryID(Integer.parseInt(txtCategory.getText()));
                product.setLowStockThreshold(Integer.parseInt(txtThreshold.getText()));
                
                if (productDAO.createProduct(product)) {
                    JOptionPane.showMessageDialog(this, "Product added successfully!");
                    loadProducts();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add product!");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid input format!");
            }
        }
    }
    
    private void editProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product to edit!");
            return;
        }
        
        int productId = (int) tableModel.getValueAt(selectedRow, 0);
        Product product = productDAO.getProductById(productId);
        
        if (product == null) {
            JOptionPane.showMessageDialog(this, "Product not found!");
            return;
        }
        
        JTextField txtTitle = new JTextField(product.getTitle());
        JTextField txtDescription = new JTextField(product.getDescription());
        JTextField txtPrice = new JTextField(String.valueOf(product.getPrice()));
        JTextField txtQuantity = new JTextField(String.valueOf(product.getQuantity()));
        JTextField txtCategory = new JTextField(String.valueOf(product.getCategoryID()));
        
        Object[] message = {
            "Title:", txtTitle,
            "Description:", txtDescription,
            "Price:", txtPrice,
            "Quantity:", txtQuantity,
            "Category ID:", txtCategory
        };
        
        int option = JOptionPane.showConfirmDialog(this, message, "Edit Product", JOptionPane.OK_CANCEL_OPTION);
        
        if (option == JOptionPane.OK_OPTION) {
            try {
                product.setTitle(txtTitle.getText());
                product.setDescription(txtDescription.getText());
                product.setPrice(Double.parseDouble(txtPrice.getText()));
                product.setQuantity(Integer.parseInt(txtQuantity.getText()));
                product.setCategoryID(Integer.parseInt(txtCategory.getText()));
                
                if (productDAO.updateProduct(product)) {
                    JOptionPane.showMessageDialog(this, "Product updated successfully!");
                    loadProducts();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update product!");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid input format!");
            }
        }
    }
    
    private void deleteProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product to delete!");
            return;
        }
        
        int productId = (int) tableModel.getValueAt(selectedRow, 0);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete this product?", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (productDAO.deleteProduct(productId)) {
                JOptionPane.showMessageDialog(this, "Product deleted successfully!");
                loadProducts();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete product!");
            }
        }
    }
}
