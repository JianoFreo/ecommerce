package src.ui;

import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.util.List;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import src.dao.CategoryDAO;
import src.dao.ProductDAO;
import src.model.Category;
import src.model.Product;

public class ProductManagementPanel extends JPanel {
    private JPanel gridPanel;
    private JPanel formPanel;
    private JTextField txtName, txtDesc, txtPrice, txtQty, txtCostPrice;
    private JTextField txtSearch;
    private JComboBox<Category> cmbCategory;
    private JComboBox<String> cmbFilterCategory;
    private JLabel lblProfit, lblImageStatus;
    private ProductDAO productDAO;
    private CategoryDAO categoryDAO;
    private String selectedImagePath = null;
    private Product selectedProduct = null;
    private JLabel lowStockBadge;

    public ProductManagementPanel() {
        productDAO = new ProductDAO();
        categoryDAO = new CategoryDAO();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(new Color(245, 245, 245));

        // Top: Header + Search/Filter
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        // Center: Grid left + Form right (SplitPane)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        
        JPanel gridPanel_wrapper = new JPanel(new BorderLayout());
        gridPanel = new JPanel(new GridLayout(0, 3, 15, 15));
        gridPanel.setBackground(new Color(245, 245, 245));
        gridPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JScrollPane gridScroll = new JScrollPane(gridPanel);
        gridScroll.setBackground(new Color(245, 245, 245));
        gridScroll.getViewport().setBackground(new Color(245, 245, 245));
        gridPanel_wrapper.add(gridScroll, BorderLayout.CENTER);
        
        formPanel = createFormPanel();
        
        splitPane.setLeftComponent(gridPanel_wrapper);
        splitPane.setRightComponent(formPanel);
        splitPane.setDividerLocation(700);
        splitPane.setResizeWeight(0.7);
        
        add(splitPane, BorderLayout.CENTER);

        loadGrid();
    }

    private JPanel createFormPanel() {
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        outerPanel.setBackground(new Color(245, 245, 245));

        // Title
        JLabel title = new JLabel("➕ Add/Edit Product");
        title.setFont(new Font("Arial", Font.BOLD, 14));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        outerPanel.add(title, BorderLayout.NORTH);

        // Form panel (vertical layout)
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Product Details"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridwidth = 2;

        txtName = new JTextField(20);
        txtDesc = new JTextField(20);
        txtCostPrice = new JTextField(20);
        txtPrice = new JTextField(20);
        txtQty = new JTextField(20);
        cmbCategory = new JComboBox<>();
        loadCategories();

        int row = 0;

        // Name
        gbc.gridy = row++;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        panel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(txtName, gbc);

        // Description
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.weightx = 0;
        panel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(txtDesc, gbc);

        // Cost Price
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.weightx = 0;
        panel.add(new JLabel("Cost Price:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(txtCostPrice, gbc);

        // Selling Price
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.weightx = 0;
        panel.add(new JLabel("Selling Price:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(txtPrice, gbc);

        // Profit
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.weightx = 0;
        panel.add(new JLabel("Profit:"), gbc);
        lblProfit = new JLabel("₱0.00");
        lblProfit.setFont(new Font("Arial", Font.BOLD, 12));
        lblProfit.setForeground(new Color(0, 150, 0));
        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(lblProfit, gbc);

        // Quantity
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.weightx = 0;
        panel.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(txtQty, gbc);

        // Category
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.weightx = 0;
        panel.add(new JLabel("Category:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(cmbCategory, gbc);

        // Image Browse
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.weightx = 0;
        panel.add(new JLabel("Image:"), gbc);
        JButton btnBrowse = new JButton("📁 Browse");
        btnBrowse.addActionListener(e -> browseImage());
        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(btnBrowse, gbc);

        // Image Status
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.weightx = 0;
        panel.add(new JLabel("Status:"), gbc);
        lblImageStatus = new JLabel("No image");
        lblImageStatus.setFont(new Font("Arial", Font.PLAIN, 10));
        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(lblImageStatus, gbc);

        // Buttons
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JPanel btnPanel = new JPanel(new GridLayout(1, 3, 5, 0));
        btnPanel.setBackground(Color.WHITE);
        
        JButton btnAdd = new JButton("➕ Add");
        btnAdd.addActionListener(e -> addProduct());
        btnPanel.add(btnAdd);

        JButton btnUpdate = new JButton("✏️ Update");
        btnUpdate.addActionListener(e -> updateProduct());
        btnPanel.add(btnUpdate);

        JButton btnClear = new JButton("Clear");
        btnClear.addActionListener(e -> clearForm());
        btnPanel.add(btnClear);

        panel.add(btnPanel, gbc);

        txtPrice.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) { calculateProfit(); }
        });
        txtCostPrice.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) { calculateProfit(); }
        });

        // Wrap in scroll pane
        JScrollPane scroll = new JScrollPane(panel);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        outerPanel.add(scroll, BorderLayout.CENTER);

        return outerPanel;
    }

    private void loadGrid() {
        gridPanel.removeAll();
        List<Product> products = productDAO.getAllProducts();
        for (Product p : products) {
            gridPanel.add(createCard(p));
        }
        gridPanel.revalidate();
        gridPanel.repaint();
        updateLowStockBadge();
    }

    private JPanel createCard(Product p) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setPreferredSize(new Dimension(220, 340));
        card.setMaximumSize(new Dimension(220, 340));
        card.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        card.setBackground(Color.WHITE);

        // Image
        JPanel imgPanel = new JPanel(new BorderLayout());
        imgPanel.setBackground(new Color(240, 240, 240));
        imgPanel.setPreferredSize(new Dimension(220, 180));
        JLabel imgLabel = new JLabel();
        imgLabel.setHorizontalAlignment(JLabel.CENTER);
        imgLabel.setVerticalAlignment(JLabel.CENTER);

        if (p.getImageData() != null && p.getImageData().length > 0) {
            try {
                ImageIcon icon = new ImageIcon(p.getImageData());
                Image img = icon.getImage().getScaledInstance(210, 170, Image.SCALE_SMOOTH);
                imgLabel.setIcon(new ImageIcon(img));
            } catch (Exception e) {
                imgLabel.setText("📷");
                imgLabel.setFont(new Font("Arial", Font.BOLD, 40));
            }
        } else {
            imgLabel.setText("📷");
            imgLabel.setFont(new Font("Arial", Font.BOLD, 40));
        }
        imgPanel.add(imgLabel, BorderLayout.CENTER);
        card.add(imgPanel, BorderLayout.NORTH);

        // Info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        infoPanel.setBackground(Color.WHITE);

        JLabel nameLabel = new JLabel(p.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 12));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel priceLabel = new JLabel("₱" + String.format("%.0f", p.getPrice()));
        priceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        priceLabel.setForeground(new Color(200, 0, 0));
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        double profit = p.getPrice() - p.getCostPrice();
        JLabel profitLabel = new JLabel("Profit: ₱" + String.format("%.0f", profit));
        profitLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        profitLabel.setForeground(new Color(0, 150, 0));
        profitLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel stockLabel = new JLabel("Stock: " + p.getQuantity());
        stockLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        stockLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(3));
        infoPanel.add(priceLabel);
        infoPanel.add(Box.createVerticalStrut(2));
        infoPanel.add(profitLabel);
        infoPanel.add(Box.createVerticalStrut(2));
        infoPanel.add(stockLabel);
        infoPanel.add(Box.createVerticalStrut(5));

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        btnPanel.setMaximumSize(new Dimension(200, 30));
        btnPanel.setBackground(Color.WHITE);
        JButton btnEdit = new JButton("✏️ Edit");
        JButton btnDelete = new JButton("🗑️ Delete");
        btnEdit.setFont(new Font("Arial", Font.PLAIN, 11));
        btnDelete.setFont(new Font("Arial", Font.PLAIN, 11));
        btnEdit.addActionListener(e -> selectProduct(p));
        btnDelete.addActionListener(e -> deleteProduct(p));
        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);

        infoPanel.add(btnPanel);
        card.add(infoPanel, BorderLayout.CENTER);

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                card.setBorder(BorderFactory.createLineBorder(new Color(100, 150, 255), 2));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                card.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
            }
        });

        return card;
    }

    private void selectProduct(Product p) {
        selectedProduct = p;
        txtName.setText(p.getName());
        txtDesc.setText(p.getDescription() != null ? p.getDescription() : "");
        txtCostPrice.setText(String.valueOf(p.getCostPrice()));
        txtPrice.setText(String.valueOf(p.getPrice()));
        txtQty.setText(String.valueOf(p.getQuantity()));
        
        for (int i = 0; i < cmbCategory.getItemCount(); i++) {
            Category cat = (Category) cmbCategory.getItemAt(i);
            if (cat.getCategoryID() == p.getCategoryID()) {
                cmbCategory.setSelectedIndex(i);
                break;
            }
        }
        
        // Store image data if available
        if (p.getImageData() != null && p.getImageData().length > 0) {
            selectedImagePath = "BINARY:" + java.util.Base64.getEncoder().encodeToString(p.getImageData());
            lblImageStatus.setText("✓ Image loaded");
        } else {
            selectedImagePath = null;
            lblImageStatus.setText("No image");
        }
        calculateProfit();
        formPanel.scrollRectToVisible(formPanel.getBounds());
    }

    private void browseImage() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("Images", "jpg", "jpeg", "png", "gif", "bmp"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            uploadImage(fc.getSelectedFile().getAbsolutePath());
        }
    }

    private void uploadImage(String path) {
        try {
            File src = new File(path);
            if (!src.exists()) {
                JOptionPane.showMessageDialog(this, "File not found!");
                return;
            }

            // Read image file bytes
            byte[] imageBytes = Files.readAllBytes(src.toPath());
            
            // Store in selectedImagePath as bytes for database
            // Using a marker to indicate this is binary data
            selectedImagePath = "BINARY:" + java.util.Base64.getEncoder().encodeToString(imageBytes);
            lblImageStatus.setText("✓ " + src.getName());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Upload failed: " + e.getMessage());
        }
    }
    
    // Helper method to extract image bytes from selectedImagePath
    private byte[] getImageBytesFromSelectedPath() {
        if (selectedImagePath == null || !selectedImagePath.startsWith("BINARY:")) {
            return null;
        }
        try {
            String base64 = selectedImagePath.substring(7); // Remove "BINARY:" prefix
            return java.util.Base64.getDecoder().decode(base64);
        } catch (Exception e) {
            return null;
        }
    }

    private void calculateProfit() {
        try {
            double cost = Double.parseDouble(txtCostPrice.getText().isEmpty() ? "0" : txtCostPrice.getText());
            double sell = Double.parseDouble(txtPrice.getText().isEmpty() ? "0" : txtPrice.getText());
            double profit = sell - cost;
            lblProfit.setText(String.format("₱%.2f", profit));
            lblProfit.setForeground(profit >= 0 ? new Color(0, 150, 0) : new Color(200, 0, 0));
        } catch (Exception ex) {
            lblProfit.setText("₱0.00");
        }
    }

    private void loadCategories() {
        List<Category> categories = categoryDAO.getAllCategories();
        for (Category cat : categories) {
            cmbCategory.addItem(cat);
        }
    }

    private void addProduct() {
        try {
            if (txtName.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter product name!");
                return;
            }
            
            Category cat = (Category) cmbCategory.getSelectedItem();
            if (cat == null) {
                JOptionPane.showMessageDialog(this, "Select a category!");
                return;
            }

            Product p = new Product(
                0, txtName.getText(), txtDesc.getText(),
                Double.parseDouble(txtCostPrice.getText().isEmpty() ? "0" : txtCostPrice.getText()),
                Double.parseDouble(txtPrice.getText().isEmpty() ? "0" : txtPrice.getText()),
                Integer.parseInt(txtQty.getText().isEmpty() ? "0" : txtQty.getText()),
                cat.getCategoryID()
            );
            if (selectedImagePath != null) p.setImageData(getImageBytesFromSelectedPath());

            productDAO.addProduct(p);
            JOptionPane.showMessageDialog(this, "✓ Product added!");
            refresh();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void updateProduct() {
        try {
            if (selectedProduct == null) {
                JOptionPane.showMessageDialog(this, "Select a product to edit!");
                return;
            }

            Category cat = (Category) cmbCategory.getSelectedItem();
            if (cat == null) {
                JOptionPane.showMessageDialog(this, "Select a category!");
                return;
            }

            Product p = new Product(
                selectedProduct.getId(), txtName.getText(), txtDesc.getText(),
                Double.parseDouble(txtCostPrice.getText().isEmpty() ? "0" : txtCostPrice.getText()),
                Double.parseDouble(txtPrice.getText().isEmpty() ? "0" : txtPrice.getText()),
                Integer.parseInt(txtQty.getText().isEmpty() ? "0" : txtQty.getText()),
                cat.getCategoryID()
            );
            // Use new image if uploaded, otherwise keep old image
            if (selectedImagePath != null) {
                p.setImageData(getImageBytesFromSelectedPath());
            } else if (selectedProduct.getImageData() != null) {
                p.setImageData(selectedProduct.getImageData());
            }

            productDAO.updateProduct(p);
            JOptionPane.showMessageDialog(this, "✓ Product updated!");
            refresh();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void deleteProduct(Product p) {
        if (JOptionPane.showConfirmDialog(this, "Delete '" + p.getName() + "'?") == 0) {
            productDAO.deleteProduct(p.getId());
            JOptionPane.showMessageDialog(this, "✓ Product deleted!");
            loadGrid();
        }
    }

    private void clearForm() {
        selectedProduct = null;
        txtName.setText("");
        txtDesc.setText("");
        txtCostPrice.setText("");
        txtPrice.setText("");
        txtQty.setText("");
        lblProfit.setText("₱0.00");
        lblImageStatus.setText("No image selected");
        selectedImagePath = null;
        if (cmbCategory.getItemCount() > 0) cmbCategory.setSelectedIndex(0);
    }

    private void refresh() {
        loadGrid();
        clearForm();
    }

    private void updateLowStockBadge() {
        List<Product> lowStockProducts = productDAO.getLowStockProducts();
        if (lowStockProducts.isEmpty()) {
            lowStockBadge.setText("");
        } else {
            lowStockBadge.setText("⚠️ " + lowStockProducts.size() + " LOW STOCK");
            lowStockBadge.setBackground(new Color(220, 53, 69));
            lowStockBadge.setOpaque(true);
            lowStockBadge.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        }
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // Left: Title + Low stock badge
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftPanel.setBackground(new Color(245, 245, 245));
        JLabel header = new JLabel("📦 Product Management (Admin)");
        header.setFont(new Font("Arial", Font.BOLD, 18));
        leftPanel.add(header);
        lowStockBadge = new JLabel();
        lowStockBadge.setFont(new Font("Arial", Font.BOLD, 11));
        lowStockBadge.setForeground(Color.WHITE);
        updateLowStockBadge();
        lowStockBadge.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lowStockBadge.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                showLowStockDialog();
            }
        });
        leftPanel.add(lowStockBadge);
        panel.add(leftPanel, BorderLayout.WEST);

        // Center: Search bar
        JPanel searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setBackground(new Color(245, 245, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 5, 0, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.weightx = 0;
        searchPanel.add(new JLabel("Search:"), gbc);
        txtSearch = new JTextField(20);
        txtSearch.setToolTipText("Search by product name");
        gbc.gridx = 1;
        gbc.weightx = 2;
        searchPanel.add(txtSearch, gbc);

        JButton btnSearch = new JButton("🔎");
        btnSearch.addActionListener(e -> performAdminSearch());
        gbc.gridx = 2;
        gbc.weightx = 0;
        searchPanel.add(btnSearch, gbc);

        gbc.gridx = 3;
        searchPanel.add(new JLabel("Category:"), gbc);
        cmbFilterCategory = new JComboBox<>();
        cmbFilterCategory.addItem("All Categories");
        for (Category c : categoryDAO.getMainCategories()) {
            cmbFilterCategory.addItem(c.getName());
        }
        cmbFilterCategory.addActionListener(e -> applyAdminFilters());
        gbc.gridx = 4;
        gbc.weightx = 1;
        searchPanel.add(cmbFilterCategory, gbc);

        panel.add(searchPanel, BorderLayout.CENTER);

        // Right: Refresh button
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(new Color(245, 245, 245));
        JButton refreshBtn = new JButton("🔄 Refresh");
        refreshBtn.addActionListener(e -> {
            txtSearch.setText("");
            cmbFilterCategory.setSelectedIndex(0);
            loadGrid();
        });
        rightPanel.add(refreshBtn);
        panel.add(rightPanel, BorderLayout.EAST);

        return panel;
    }

    private void performAdminSearch() {
        String keyword = txtSearch.getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            loadGrid();
            return;
        }
        List<Product> allProducts = productDAO.getAllProducts();
        List<Product> results = allProducts.stream()
            .filter(p -> p.getName().toLowerCase().contains(keyword) || 
                        p.getDescription().toLowerCase().contains(keyword))
            .toList();
        displayProducts(results);
    }

    private void applyAdminFilters() {
        String keyword = txtSearch.getText().trim().toLowerCase();
        Integer categoryID = null;
        if (!cmbFilterCategory.getSelectedItem().equals("All Categories")) {
            for (Category c : categoryDAO.getMainCategories()) {
                if (c.getName().equals(cmbFilterCategory.getSelectedItem())) {
                    categoryID = c.getCategoryID();
                    break;
                }
            }
        }

        List<Product> allProducts = productDAO.getAllProducts();
        final Integer finalCategoryID = categoryID;
        List<Product> results = allProducts.stream()
            .filter(p -> (keyword.isEmpty() || p.getName().toLowerCase().contains(keyword) || p.getDescription().toLowerCase().contains(keyword)) &&
                        (finalCategoryID == null || p.getCategoryID() == finalCategoryID))
            .toList();
        displayProducts(results);
    }

    private void displayProducts(List<Product> products) {
        gridPanel.removeAll();
        for (Product p : products) {
            gridPanel.add(createCard(p));
        }
        gridPanel.revalidate();
        gridPanel.repaint();
        updateLowStockBadge();
    }

    private void showLowStockDialog() {
        List<Product> lowStock = productDAO.getLowStockProducts();
        if (lowStock.isEmpty()) {
            JOptionPane.showMessageDialog(this, "✓ All products are in stock!", "Low Stock Report", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Create dialog
        JDialog dialog = new JDialog();
        dialog.setTitle("⚠️ Low Stock Products");
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        // Table with low stock products
        String[] cols = {"Product", "Stock", "Category"};
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        for (Product p : lowStock) {
            model.addRow(new Object[]{
                p.getName(),
                p.getQuantity() + " units",
                p.getCategoryName() != null ? p.getCategoryName() : "N/A"
            });
        }

        JTable table = new JTable(model);
        table.setRowHeight(25);
        table.getColumnModel().getColumn(0).setPreferredWidth(250);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel info = new JLabel("Found " + lowStock.size() + " product(s) with stock < 10 units");
        info.setFont(new Font("Arial", Font.BOLD, 12));
        content.add(info, BorderLayout.NORTH);
        content.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(e -> dialog.dispose());
        JPanel btnPanel = new JPanel();
        btnPanel.add(btnClose);
        content.add(btnPanel, BorderLayout.SOUTH);

        dialog.add(content);
        dialog.setVisible(true);
    }
}
