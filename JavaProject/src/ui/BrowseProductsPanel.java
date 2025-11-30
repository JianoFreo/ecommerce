package src.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import src.dao.ProductDAO;
import src.model.Product;

public class BrowseProductsPanel extends JPanel {
    private JTable productTable;
    private DefaultTableModel tableModel;
    private ProductDAO productDAO;

    public BrowseProductsPanel() {
        this.productDAO = new ProductDAO();
        
        setLayout(new BorderLayout());

        // Table
        String[] columns = {"ID", "Name", "Description", "Price", "Stock", "Category"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        productTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(productTable);
        add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton btnRefresh = new JButton("Refresh");
        JButton btnViewDetails = new JButton("View Details");
        
        btnRefresh.addActionListener(e -> loadProducts());
        btnViewDetails.addActionListener(e -> viewProductDetails());
        
        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnViewDetails);
        add(buttonPanel, BorderLayout.SOUTH);

        loadProducts();
    }

    private void loadProducts() {
        tableModel.setRowCount(0);
        List<Product> products = productDAO.getAllProducts();
        
        for (Product product : products) {
            tableModel.addRow(new Object[]{
                product.getId(),
                product.getName(),
                product.getDescription(),
                String.format("$%.2f", product.getPrice()),
                product.getQuantity(),
                product.getCategoryName()
            });
        }
    }

    private void viewProductDetails() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product to view details.");
            return;
        }

        int productId = (int) tableModel.getValueAt(selectedRow, 0);
        String name = (String) tableModel.getValueAt(selectedRow, 1);
        String description = (String) tableModel.getValueAt(selectedRow, 2);
        String price = (String) tableModel.getValueAt(selectedRow, 3);
        int stock = (int) tableModel.getValueAt(selectedRow, 4);
        String category = (String) tableModel.getValueAt(selectedRow, 5);
        
        String details = String.format(
            "Product ID: %d\n" +
            "Name: %s\n" +
            "Category: %s\n" +
            "Price: %s\n" +
            "In Stock: %d\n\n" +
            "Description:\n%s",
            productId, name, category, price, stock, description
        );
        
        JOptionPane.showMessageDialog(this, details, 
            "Product Details", JOptionPane.INFORMATION_MESSAGE);
    }
}
