package src.ui;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import src.dao.CategoryDAO;
import src.dao.DiscountCodeDAO;
import src.dao.OrderDAO;
import src.dao.ProductDAO;
import src.model.Category;
import src.model.DiscountCode;
import src.model.Order;
import src.model.OrderItem;
import src.model.Product;
import src.model.User;

public class ShoppingPanel extends JPanel {
    private JPanel gridPanel;
    private JTable cartTable;
    private DefaultTableModel cartModel;
    private ProductDAO productDAO;
    private CategoryDAO categoryDAO;
    private DiscountCodeDAO discountDAO;
    private OrderDAO orderDAO;
    private User currentUser;
    private List<OrderItem> cart;
    private List<Product> productCache;
    private JLabel totalLabel;
    private JTextField txtSearch;
    private JComboBox<String> cmbCategory;
    private double discountAmount = 0;

    public ShoppingPanel(User user) {
        this.currentUser = user;
        this.productDAO = new ProductDAO();
        this.categoryDAO = new CategoryDAO();
        this.discountDAO = new DiscountCodeDAO();
        this.orderDAO = new OrderDAO();
        this.cart = new ArrayList<>();
        this.productCache = new ArrayList<>();

        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 245, 245));

        // Split: Grid left, Cart right
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        // Left: Product Grid
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(new Color(245, 245, 245));
        
        // Top: Search Bar
        JPanel searchPanel = createSearchPanel();
        leftPanel.add(searchPanel, BorderLayout.NORTH);

        // Product Grid
        gridPanel = new JPanel(new GridLayout(0, 3, 15, 15));
        gridPanel.setBackground(new Color(245, 245, 245));
        gridPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JScrollPane gridScroll = new JScrollPane(gridPanel);
        gridScroll.setBackground(new Color(245, 245, 245));
        gridScroll.getViewport().setBackground(new Color(245, 245, 245));
        leftPanel.add(gridScroll, BorderLayout.CENTER);

        // Right: Shopping Cart
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        rightPanel.setBackground(Color.WHITE);

        JLabel cartTitle = new JLabel("🛒 Your Cart");
        cartTitle.setFont(new Font("Arial", Font.BOLD, 14));
        rightPanel.add(cartTitle, BorderLayout.NORTH);

        String[] cols = {"Image", "Product", "Price", "Qty", "Subtotal"};
        cartModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        cartTable = new JTable(cartModel);
        cartTable.setRowHeight(60);
        cartTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        cartTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        cartTable.setDefaultRenderer(Object.class, new CartImageRenderer());
        rightPanel.add(new JScrollPane(cartTable), BorderLayout.CENTER);

        // Bottom: Discount and Checkout Panel
        JPanel bottomRightPanel = new JPanel(new BorderLayout(10, 10));
        bottomRightPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        bottomRightPanel.setBackground(Color.WHITE);

        // Discount Code Panel
        JPanel discountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        discountPanel.setBackground(Color.WHITE);
        discountPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JLabel discountLabel = new JLabel("Coupon Code:");
        JTextField txtCoupon = new JTextField(12);
        JButton btnApply = new JButton("Apply");
        JLabel statusLabel = new JLabel("");
        discountPanel.add(discountLabel);
        discountPanel.add(txtCoupon);
        discountPanel.add(btnApply);
        discountPanel.add(statusLabel);
        bottomRightPanel.add(discountPanel, BorderLayout.NORTH);

        btnApply.addActionListener(e -> {
            String code = txtCoupon.getText().trim();
            if (code.isEmpty()) {
                statusLabel.setText("Enter a coupon code");
                statusLabel.setForeground(Color.RED);
                return;
            }
            DiscountCode discount = discountDAO.getDiscountCode(code);
            if (discount != null) {
                double total = calculateTotal();
                discountAmount = discount.calculateDiscount(total);
                statusLabel.setText("✓ Coupon applied!");
                statusLabel.setForeground(new Color(0, 150, 0));
                updateTotal();
            } else {
                discountAmount = 0;
                statusLabel.setText("❌ Invalid or expired coupon");
                statusLabel.setForeground(Color.RED);
                updateTotal();
            }
        });

        // Checkout buttons panel
        JPanel cartBottomPanel = new JPanel(new BorderLayout());
        JPanel cartButtonsLeft = new JPanel();
        JButton btnRemove = new JButton("Remove");
        JButton btnClear = new JButton("Clear");
        btnRemove.addActionListener(e -> removeCart());
        btnClear.addActionListener(e -> clearCart());
        cartButtonsLeft.add(btnRemove);
        cartButtonsLeft.add(btnClear);

        JPanel cartButtonsRight = new JPanel(new BorderLayout(10, 0));
        totalLabel = new JLabel("Total: ₱0");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 14));
        totalLabel.setForeground(new Color(200, 0, 0));
        JButton btnOrder = new JButton("Place Order");
        btnOrder.setFont(new Font("Arial", Font.BOLD, 12));
        btnOrder.setBackground(new Color(255, 153, 0));
        btnOrder.setForeground(Color.WHITE);
        btnOrder.addActionListener(e -> checkout());
        cartButtonsRight.add(totalLabel, BorderLayout.WEST);
        cartButtonsRight.add(btnOrder, BorderLayout.EAST);

        cartBottomPanel.add(cartButtonsLeft, BorderLayout.WEST);
        cartBottomPanel.add(cartButtonsRight, BorderLayout.EAST);
        bottomRightPanel.add(cartBottomPanel, BorderLayout.SOUTH);
        
        rightPanel.add(bottomRightPanel, BorderLayout.SOUTH);

        split.setLeftComponent(leftPanel);
        split.setRightComponent(rightPanel);
        split.setDividerLocation(700);
        split.setResizeWeight(0.65);

        add(split, BorderLayout.CENTER);
        loadGrid();
    }

    private void loadGrid() {
        gridPanel.removeAll();
        productCache = productDAO.getAllProducts();
        for (Product p : productCache) {
            gridPanel.add(createCard(p));
        }
        gridPanel.revalidate();
        gridPanel.repaint();
    }

    private JPanel createCard(Product p) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setPreferredSize(new Dimension(220, 350));
        card.setMaximumSize(new Dimension(220, 350));
        card.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        card.setBackground(Color.WHITE);

        // Image
        JPanel imgPanel = new JPanel(new BorderLayout());
        imgPanel.setBackground(new Color(240, 240, 240));
        imgPanel.setPreferredSize(new Dimension(220, 220));
        JLabel imgLabel = new JLabel();
        imgLabel.setHorizontalAlignment(JLabel.CENTER);
        imgLabel.setVerticalAlignment(JLabel.CENTER);

        if (p.getImageData() != null && p.getImageData().length > 0) {
            try {
                ImageIcon icon = new ImageIcon(p.getImageData());
                Image img = icon.getImage().getScaledInstance(210, 210, Image.SCALE_SMOOTH);
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
        priceLabel.setFont(new Font("Arial", Font.BOLD, 18));
        priceLabel.setForeground(new Color(200, 0, 0));
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        String stock = p.getQuantity() > 0 ? "✓ Stock: " + p.getQuantity() : "✗ Out of Stock";
        Color stockCol = p.getQuantity() > 0 ? new Color(0, 150, 0) : Color.RED;
        JLabel stockLabel = new JLabel(stock);
        stockLabel.setFont(new Font("Arial", Font.BOLD, 11));
        stockLabel.setForeground(stockCol);
        stockLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(3));
        infoPanel.add(priceLabel);
        infoPanel.add(Box.createVerticalStrut(3));
        infoPanel.add(stockLabel);

        // Buttons Panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        btnPanel.setMaximumSize(new Dimension(200, 40));
        btnPanel.setBackground(Color.WHITE);
        
        JButton btnDetails = new JButton("ℹ️ Details");
        btnDetails.setFont(new Font("Arial", Font.PLAIN, 10));
        btnDetails.addActionListener(e -> showDetails(p));
        btnPanel.add(btnDetails);
        
        JButton btnCart = new JButton("🛒 Add");
        btnCart.setFont(new Font("Arial", Font.PLAIN, 10));
        btnCart.setEnabled(p.getQuantity() > 0);
        btnCart.addActionListener(e -> addToCart(p));
        btnPanel.add(btnCart);
        
        infoPanel.add(Box.createVerticalStrut(5));
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

    private void showDetails(Product p) {
        JDialog dialog = new JDialog();
        dialog.setTitle("Product Details - " + p.getName());
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setModal(true);
        
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(Color.WHITE);
        
        // Image
        JPanel imgPanel = new JPanel(new BorderLayout());
        imgPanel.setBackground(new Color(240, 240, 240));
        imgPanel.setPreferredSize(new Dimension(150, 150));
        JLabel imgLabel = new JLabel();
        imgLabel.setHorizontalAlignment(JLabel.CENTER);
        imgLabel.setVerticalAlignment(JLabel.CENTER);
        
        if (p.getImageData() != null && p.getImageData().length > 0) {
            try {
                ImageIcon icon = new ImageIcon(p.getImageData());
                Image img = icon.getImage().getScaledInstance(140, 140, Image.SCALE_SMOOTH);
                imgLabel.setIcon(new ImageIcon(img));
            } catch (Exception e) {
                imgLabel.setText("📷");
                imgLabel.setFont(new Font("Arial", Font.BOLD, 50));
            }
        } else {
            imgLabel.setText("📷");
            imgLabel.setFont(new Font("Arial", Font.BOLD, 50));
        }
        imgPanel.add(imgLabel, BorderLayout.CENTER);
        panel.add(imgPanel, BorderLayout.WEST);
        
        // Details
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBackground(Color.WHITE);
        
        JLabel nameLabel = new JLabel("Product: " + p.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        detailsPanel.add(nameLabel);
        detailsPanel.add(Box.createVerticalStrut(10));
        
        JLabel categoryLabel = new JLabel("Category: " + (p.getCategoryName() != null ? p.getCategoryName() : "N/A"));
        categoryLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        detailsPanel.add(categoryLabel);
        detailsPanel.add(Box.createVerticalStrut(5));
        
        JLabel priceLabel = new JLabel("Price: ₱" + String.format("%.2f", p.getPrice()));
        priceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        priceLabel.setForeground(new Color(200, 0, 0));
        detailsPanel.add(priceLabel);
        detailsPanel.add(Box.createVerticalStrut(5));
        
        String stockStatus = p.getQuantity() > 0 ? "✓ In Stock: " + p.getQuantity() + " units" : "✗ Out of Stock";
        Color stockColor = p.getQuantity() > 0 ? new Color(0, 150, 0) : Color.RED;
        JLabel stockLabel = new JLabel(stockStatus);
        stockLabel.setFont(new Font("Arial", Font.BOLD, 12));
        stockLabel.setForeground(stockColor);
        detailsPanel.add(stockLabel);
        detailsPanel.add(Box.createVerticalStrut(10));
        
        JLabel descLabel = new JLabel("Description:");
        descLabel.setFont(new Font("Arial", Font.BOLD, 12));
        detailsPanel.add(descLabel);
        
        String desc = p.getDescription() != null && !p.getDescription().isEmpty() ? p.getDescription() : "No description available";
        JTextArea descArea = new JTextArea(desc);
        descArea.setFont(new Font("Arial", Font.PLAIN, 11));
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setEditable(false);
        descArea.setBackground(new Color(245, 245, 245));
        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setPreferredSize(new Dimension(300, 100));
        detailsPanel.add(descScroll);
        
        panel.add(detailsPanel, BorderLayout.CENTER);
        
        // Button
        JPanel btnPanel = new JPanel();
        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(e -> dialog.dispose());
        JButton btnAddCart = new JButton("🛒 Add to Cart");
        btnAddCart.setEnabled(p.getQuantity() > 0);
        btnAddCart.addActionListener(e -> {
            addToCart(p);
            dialog.dispose();
        });
        btnPanel.add(btnAddCart);
        btnPanel.add(btnClose);
        panel.add(btnPanel, BorderLayout.SOUTH);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void addToCart(Product p) {
        String qty = JOptionPane.showInputDialog(this, "Qty:", "1");
        if (qty == null || qty.isEmpty()) return;
        try {
            int q = Integer.parseInt(qty);
            if (q <= 0 || q > p.getQuantity()) {
                JOptionPane.showMessageDialog(this, "Invalid quantity!");
                return;
            }
            boolean found = false;
            for (OrderItem item : cart) {
                if (item.getProductID() == p.getId()) {
                    int newQty = item.getQuantity() + q;
                    if (newQty > p.getQuantity()) {
                        JOptionPane.showMessageDialog(this, "Max: " + p.getQuantity());
                        return;
                    }
                    item.setQuantity(newQty);
                    item.setSubtotal(p.getPrice() * newQty);
                    found = true;
                    break;
                }
            }
            if (!found) {
                OrderItem item = new OrderItem(0, 0, p.getId(), p.getName(), p.getPrice(), q);
                item.setImageData(p.getImageData());
                cart.add(item);
            }
            updateCart();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid number!");
        }
    }

    private void removeCart() {
        int row = cartTable.getSelectedRow();
        if (row >= 0) {
            cart.remove(row);
            updateCart();
        }
    }

    private void clearCart() {
        if (!cart.isEmpty() && JOptionPane.showConfirmDialog(this, "Clear?") == 0) {
            cart.clear();
            updateCart();
        }
    }

    private void updateCart() {
        cartModel.setRowCount(0);
        double total = 0;
        for (OrderItem item : cart) {
            cartModel.addRow(new Object[]{
                item.getImageData(),
                item.getProductName(),
                "₱" + String.format("%.0f", item.getPrice()),
                item.getQuantity(),
                "₱" + String.format("%.0f", item.getSubtotal())
            });
            total += item.getSubtotal();
        }
        updateTotal();
    }

    private double calculateTotal() {
        double total = 0;
        for (OrderItem item : cart) {
            total += item.getSubtotal();
        }
        return total;
    }

    private void updateTotal() {
        double total = calculateTotal();
        double finalTotal = total - discountAmount;
        if (discountAmount > 0) {
            totalLabel.setText("Total: ₱" + String.format("%.0f", finalTotal) + " (Saved ₱" + String.format("%.0f", discountAmount) + ")");
        } else {
            totalLabel.setText("Total: ₱" + String.format("%.0f", finalTotal));
        }
    }

    private class CartImageRenderer extends javax.swing.table.DefaultTableCellRenderer {
        @Override
        public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBackground(isSelected ? new Color(200, 220, 255) : Color.WHITE);
            
            if (value != null && column == 0 && value instanceof byte[]) {
                byte[] imageData = (byte[]) value;
                JLabel imgLabel = new JLabel();
                imgLabel.setHorizontalAlignment(JLabel.CENTER);
                imgLabel.setVerticalAlignment(JLabel.CENTER);
                
                if (imageData.length > 0) {
                    try {
                        ImageIcon icon = new ImageIcon(imageData);
                        Image img = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                        imgLabel.setIcon(new ImageIcon(img));
                    } catch (Exception e) {
                        imgLabel.setText("📷");
                        imgLabel.setFont(new Font("Arial", Font.BOLD, 20));
                    }
                } else {
                    imgLabel.setText("📷");
                    imgLabel.setFont(new Font("Arial", Font.BOLD, 20));
                }
                panel.add(imgLabel, BorderLayout.CENTER);
                return panel;
            }
            
            JLabel label = new JLabel(value != null ? value.toString() : "");
            label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            label.setBackground(isSelected ? new Color(200, 220, 255) : Color.WHITE);
            label.setOpaque(true);
            return label;
        }
    }

    private void checkout() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart empty!");
            return;
        }
        String addr = JOptionPane.showInputDialog(this, "Address:", currentUser.getAddress());
        if (addr == null || addr.isEmpty()) return;
        double total = cart.stream().mapToDouble(OrderItem::getSubtotal).sum();
        Order order = new Order(0, currentUser.getUserID(), null, "Pending", total, addr);
        order.setItems(new ArrayList<>(cart));
        if (orderDAO.createOrder(order)) {
            JOptionPane.showMessageDialog(this, "Order placed! Total: ₱" + String.format("%.0f", total));
            cart.clear();
            updateCart();
            loadGrid();
        } else {
            JOptionPane.showMessageDialog(this, "Order failed!");
        }
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 5, 3, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel title = new JLabel("🛍️ Products");
        title.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        panel.add(title, gbc);

        // Search box
        gbc.gridx = 1;
        gbc.weightx = 2;
        txtSearch = new JTextField(20);
        txtSearch.setToolTipText("Search products...");
        panel.add(txtSearch, gbc);

        JButton btnSearch = new JButton("🔎 Search");
        btnSearch.addActionListener(e -> performSearch());
        gbc.gridx = 2;
        gbc.weightx = 0;
        panel.add(btnSearch, gbc);

        // Category filter
        gbc.gridx = 3;
        gbc.weightx = 0;
        panel.add(new JLabel("Category:"), gbc);

        cmbCategory = new JComboBox<>();
        cmbCategory.addItem("All Categories");
        for (Category c : categoryDAO.getMainCategories()) {
            cmbCategory.addItem(c.getName());
        }
        cmbCategory.addActionListener(e -> applyFilters());
        gbc.gridx = 4;
        gbc.weightx = 1;
        panel.add(cmbCategory, gbc);

        JButton btnRefresh = new JButton("🔄 Refresh");
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            cmbCategory.setSelectedIndex(0);
            loadGrid();
        });
        gbc.gridx = 5;
        gbc.weightx = 0;
        panel.add(btnRefresh, gbc);

        return panel;
    }

    private void performSearch() {
        String keyword = txtSearch.getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            loadGrid();
            return;
        }
        List<Product> results = productCache.stream()
            .filter(p -> p.getName().toLowerCase().contains(keyword) || 
                        p.getDescription().toLowerCase().contains(keyword))
            .toList();
        displayProducts(results);
    }

    private void applyFilters() {
        String keyword = txtSearch.getText().trim().toLowerCase();
        Integer categoryID = null;
        if (!cmbCategory.getSelectedItem().equals("All Categories")) {
            for (Category c : categoryDAO.getMainCategories()) {
                if (c.getName().equals(cmbCategory.getSelectedItem())) {
                    categoryID = c.getCategoryID();
                    break;
                }
            }
        }

        final Integer finalCategoryID = categoryID;
        List<Product> results = productCache.stream()
            .filter(p -> (keyword.isEmpty() || p.getName().toLowerCase().contains(keyword) || p.getDescription().toLowerCase().contains(keyword)) &&
                        (finalCategoryID == null || p.getCategoryID() == finalCategoryID))
            .toList();
        displayProducts(results);
    }

    private void displayProducts(List<Product> products) {
        gridPanel.removeAll();
        productCache = new ArrayList<>(products);
        for (Product p : products) {
            gridPanel.add(createCard(p));
        }
        gridPanel.revalidate();
        gridPanel.repaint();
    }
}

