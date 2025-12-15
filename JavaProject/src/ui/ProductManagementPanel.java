package src.ui;

import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
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
    private JLabel lblProfit, lblImageStatus;
    private JComboBox<Category> cmbCategory;
    private ProductDAO productDAO;
    private CategoryDAO categoryDAO;
    private String selectedImagePath = null;
    private Product selectedProduct = null;

    public ProductManagementPanel() {
        productDAO = new ProductDAO();
        categoryDAO = new CategoryDAO();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(new Color(245, 245, 245));

        // Top: Header + Search/Filter
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(245, 245, 245));
        JLabel header = new JLabel("📦 Product Management (Admin)");
        header.setFont(new Font("Arial", Font.BOLD, 18));
        JButton refreshBtn = new JButton("🔄 Refresh");
        refreshBtn.addActionListener(e -> loadGrid());
        JPanel headerRight = new JPanel();
        headerRight.add(refreshBtn);
        topPanel.add(header, BorderLayout.WEST);
        topPanel.add(headerRight, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Center: Product Grid (Amazon style)
        gridPanel = new JPanel(new GridLayout(0, 3, 15, 15));
        gridPanel.setBackground(new Color(245, 245, 245));
        gridPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JScrollPane gridScroll = new JScrollPane(gridPanel);
        gridScroll.setBackground(new Color(245, 245, 245));
        gridScroll.getViewport().setBackground(new Color(245, 245, 245));
        add(gridScroll, BorderLayout.CENTER);

        // Bottom: Form Panel for Add/Edit
        formPanel = createFormPanel();
        add(formPanel, BorderLayout.SOUTH);

        loadGrid();
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 4, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("➕ Add/Edit Product"));
        panel.setBackground(Color.WHITE);

        txtName = new JTextField();
        txtDesc = new JTextField();
        txtCostPrice = new JTextField();
        txtPrice = new JTextField();
        txtQty = new JTextField();
        cmbCategory = new JComboBox<>();
        loadCategories();

        panel.add(new JLabel("Name:"));
        panel.add(txtName);
        panel.add(new JLabel("Description:"));
        panel.add(txtDesc);

        panel.add(new JLabel("Cost Price:"));
        panel.add(txtCostPrice);
        panel.add(new JLabel("Selling Price:"));
        panel.add(txtPrice);

        panel.add(new JLabel("Profit:"));
        lblProfit = new JLabel("₱0.00");
        lblProfit.setFont(new Font("Arial", Font.BOLD, 12));
        lblProfit.setForeground(new Color(0, 150, 0));
        panel.add(lblProfit);
        panel.add(new JLabel("Quantity:"));
        panel.add(txtQty);

        panel.add(new JLabel("Category:"));
        panel.add(cmbCategory);
        panel.add(new JLabel("Image:"));
        JButton btnBrowse = new JButton("📁 Browse");
        btnBrowse.addActionListener(e -> browseImage());
        panel.add(btnBrowse);

        lblImageStatus = new JLabel("No image selected");
        lblImageStatus.setFont(new Font("Arial", Font.PLAIN, 10));
        panel.add(lblImageStatus);
        panel.add(new JLabel(""));

        // Buttons
        JButton btnAdd = new JButton("➕ Add");
        JButton btnUpdate = new JButton("✏️ Update");
        JButton btnClear = new JButton("Clear");
        
        btnAdd.addActionListener(e -> addProduct());
        btnUpdate.addActionListener(e -> updateProduct());
        btnClear.addActionListener(e -> clearForm());

        panel.add(btnAdd);
        panel.add(btnUpdate);
        panel.add(btnClear);
        panel.add(new JLabel(""));

        txtPrice.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) { calculateProfit(); }
        });
        txtCostPrice.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) { calculateProfit(); }
        });

        return panel;
    }

    private void loadGrid() {
        gridPanel.removeAll();
        List<Product> products = productDAO.getAllProducts();
        for (Product p : products) {
            gridPanel.add(createCard(p));
        }
        gridPanel.revalidate();
        gridPanel.repaint();
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

        if (p.getImageUrl() != null && !p.getImageUrl().isEmpty() && new File(p.getImageUrl()).exists()) {
            try {
                ImageIcon icon = new ImageIcon(p.getImageUrl());
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
        
        selectedImagePath = p.getImageUrl();
        lblImageStatus.setText(selectedImagePath != null ? "✓ " + new File(selectedImagePath).getName() : "No image");
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

            File imgFolder = new File("images");
            if (!imgFolder.exists()) imgFolder.mkdir();

            String fileName = System.currentTimeMillis() + "_" + src.getName();
            File dest = new File(imgFolder, fileName);
            Files.copy(src.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);

            selectedImagePath = "images/" + fileName;
            lblImageStatus.setText("✓ " + src.getName());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Upload failed: " + e.getMessage());
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
            if (selectedImagePath != null) p.setImageUrl(selectedImagePath);

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
            if (selectedImagePath != null) p.setImageUrl(selectedImagePath);

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
}
