package src.ui;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.File;
import javax.imageio.ImageIO;
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

public class ShoppingPanelNew extends JPanel {
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
    private List<Product> allProductsCache;
    private JLabel totalLabel;
    private JTextField txtSearch;
    private JComboBox<String> cmbCategory;
    private JComboBox<String> cmbSortBy;
    private double discountAmount = 0;
    private JPanel sidebarPanel;
    private JLabel resultLabel;

    public ShoppingPanelNew(User user) {
        this.currentUser = user;
        this.productDAO = new ProductDAO();
        this.categoryDAO = new CategoryDAO();
        this.discountDAO = new DiscountCodeDAO();
        this.orderDAO = new OrderDAO();
        this.cart = new ArrayList<>();
        this.productCache = new ArrayList<>();

        setLayout(new BorderLayout(0, 0));
        setBackground(new Color(120, 100, 200));

        // Hero Banner
        add(createHeroBanner(), BorderLayout.NORTH);

        // Main content with sidebar and products
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setDividerLocation(220);
        split.setBackground(Color.WHITE);

        // Sidebar with filters
        sidebarPanel = createSidebar();
        split.setLeftComponent(new JScrollPane(sidebarPanel));

        // Right: Products and Cart
        JSplitPane rightSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        rightSplit.setDividerLocation(900);
        rightSplit.setBackground(Color.WHITE);

        // Product grid
        JPanel gridContainer = new JPanel(new BorderLayout(10, 10));
        gridContainer.setBackground(Color.WHITE);
        gridContainer.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 10));

        // Search and sort panel
        JPanel controlsPanel = createControlsPanel();
        gridContainer.add(controlsPanel, BorderLayout.NORTH);

        gridPanel = new JPanel(new GridLayout(0, 3, 20, 20));
        gridPanel.setBackground(Color.WHITE);
        JScrollPane gridScroll = new JScrollPane(gridPanel);
        gridScroll.getViewport().setBackground(Color.WHITE);
        gridContainer.add(gridScroll, BorderLayout.CENTER);

        rightSplit.setLeftComponent(gridContainer);

        // Cart panel
        JPanel cartPanel = createCartPanel();
        rightSplit.setRightComponent(cartPanel);

        split.setRightComponent(rightSplit);
        add(split, BorderLayout.CENTER);

        loadGrid();
    }

    private JPanel createHeroBanner() {
        JPanel banner = new JPanel() {
            private BufferedImage backgroundImage;
            
            {
                try {
                    File imgFile = new File("assets/Screenshot 2025-12-16 092424.png");
                    if (imgFile.exists()) {
                        backgroundImage = ImageIO.read(imgFile);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                    return;
                }
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int width = getWidth();
                int height = getHeight();

                // Dark gradient background
                GradientPaint gradient = new GradientPaint(0, 0, new Color(15, 20, 40), width, height, new Color(25, 35, 60));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, width, height);

                // Draw particle wave effect
                drawParticleWave(g2d, width, height);
            }

            private void drawParticleWave(Graphics2D g2d, int width, int height) {
                // Create wave pattern with particles
                long time = System.currentTimeMillis() / 50;
                
                for (int x = 0; x < width; x += 8) {
                    for (int y = 0; y < height; y += 8) {
                        double wave1 = Math.sin((x * 0.01 + time * 0.05)) * 30;
                        double wave2 = Math.cos((y * 0.01 + time * 0.03)) * 20;
                        
                        float distance = (float) Math.sqrt(wave1 * wave1 + wave2 * wave2);
                        float alpha = Math.max(0, 1 - distance / 50f);
                        
                        // Color gradient from purple to cyan
                        int r = (int) (150 + (distance / 50) * 100);
                        int g = (int) (100 + (distance / 50) * 150);
                        int b = 200;
                        
                        g2d.setColor(new Color(r, g, b, (int) (alpha * 150)));
                        g2d.fillOval(x + (int) wave1, y + (int) wave2, 4, 4);
                    }
                }
            }
        };
        
        banner.setLayout(new BorderLayout());
        banner.setBackground(new Color(15, 20, 40));
        banner.setPreferredSize(new Dimension(0, 180));

        JLabel title = new JLabel("JianoFreoTech");
        title.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 0));
        title.setOpaque(false);
        banner.add(title, BorderLayout.CENTER);

        return banner;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(120, 100, 200));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));

        // Logo
        BufferedImage logoImage = LogoRenderer.createLogo(80, 80);
        JLabel logoLabel = new JLabel(new ImageIcon(logoImage));
        logoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(logoLabel);
        sidebar.add(Box.createVerticalStrut(20));

        // Filter label
        JLabel filterLabel = new JLabel("Filter");
        filterLabel.setFont(new Font("Arial", Font.BOLD, 16));
        filterLabel.setForeground(Color.WHITE);
        filterLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(filterLabel);
        sidebar.add(Box.createVerticalStrut(15));

        // Category filter
        JLabel categoryLabel = new JLabel("Category");
        categoryLabel.setFont(new Font("Arial", Font.BOLD, 12));
        categoryLabel.setForeground(new Color(240, 240, 255));
        categoryLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(categoryLabel);

        cmbCategory = new JComboBox<>();
        cmbCategory.addItem("All Categories");
        for (Category c : categoryDAO.getMainCategories()) {
            cmbCategory.addItem(c.getName());
        }
        cmbCategory.addActionListener(e -> applyFilters());
        cmbCategory.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        cmbCategory.setBackground(new Color(100, 180, 220));
        cmbCategory.setForeground(Color.WHITE);
        sidebar.add(cmbCategory);
        sidebar.add(Box.createVerticalStrut(20));

        // Sort filter
        JLabel sortLabel = new JLabel("Sort By");
        sortLabel.setFont(new Font("Arial", Font.BOLD, 12));
        sortLabel.setForeground(new Color(240, 240, 255));
        sortLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(sortLabel);

        cmbSortBy = new JComboBox<>(new String[]{"Popular", "Price ↑", "Price ↓", "Name (A-Z)", "Stock ↑", "Stock ↓"});
        cmbSortBy.addActionListener(e -> applyFilters());
        cmbSortBy.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        cmbSortBy.setBackground(new Color(100, 180, 220));
        cmbSortBy.setForeground(Color.WHITE);
        sidebar.add(cmbSortBy);

        sidebar.add(Box.createVerticalGlue());
        return sidebar;
    }

    private JPanel createControlsPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setBackground(new Color(120, 100, 200));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        resultLabel = new JLabel("Loading products...");
        resultLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        resultLabel.setForeground(new Color(240, 240, 255));
        panel.add(resultLabel, BorderLayout.WEST);

        // Search
        txtSearch = new JTextField();
        txtSearch.setFont(new Font("Arial", Font.PLAIN, 12));
        txtSearch.setBorder(BorderFactory.createLineBorder(new Color(100, 180, 220), 1));
        txtSearch.setMaximumSize(new Dimension(200, 30));
        txtSearch.setBackground(Color.WHITE);
        txtSearch.setForeground(new Color(60, 60, 80));
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) {
                applyFilters();
            }
        });
        panel.add(txtSearch, BorderLayout.EAST);

        return panel;
    }

    private JPanel createCartPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 15, 15, 15));
        panel.setBackground(Color.WHITE);

        JLabel cartTitle = new JLabel("🛒 Your Cart");
        cartTitle.setFont(new Font("Arial", Font.BOLD, 14));
        cartTitle.setForeground(new Color(60, 60, 80));
        panel.add(cartTitle, BorderLayout.NORTH);

        String[] cols = {"Image", "Product", "Price", "Qty", "Subtotal"};
        cartModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        cartTable = new JTable(cartModel);
        cartTable.setRowHeight(60);
        cartTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        cartTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        cartTable.setDefaultRenderer(Object.class, new CartImageRenderer());
        cartTable.setBackground(Color.WHITE);
        cartTable.setGridColor(new Color(230, 230, 235));
        panel.add(new JScrollPane(cartTable), BorderLayout.CENTER);

        // Discount section
        JPanel discountPanel = new JPanel(new BorderLayout(5, 5));
        discountPanel.setBackground(Color.WHITE);
        discountPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 225)));

        JLabel discountLabel = new JLabel("Coupon Code:");
        discountLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        discountPanel.add(discountLabel, BorderLayout.WEST);

        JPanel inputPanel = new JPanel(new BorderLayout(5, 0));
        inputPanel.setBackground(Color.WHITE);
        JTextField txtCoupon = new JTextField(10);
        txtCoupon.setFont(new Font("Arial", Font.PLAIN, 11));
        txtCoupon.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 225), 1));

        JButton btnApply = new JButton("Apply");
        btnApply.setFont(new Font("Arial", Font.PLAIN, 10));
        btnApply.setBackground(new Color(66, 133, 244));
        btnApply.setForeground(Color.WHITE);
        btnApply.setFocusPainted(false);
        btnApply.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));

        JLabel statusLabel = new JLabel("");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 9));

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
                statusLabel.setText("❌ Invalid coupon");
                statusLabel.setForeground(Color.RED);
                updateTotal();
            }
        });

        inputPanel.add(txtCoupon, BorderLayout.CENTER);
        inputPanel.add(btnApply, BorderLayout.EAST);
        discountPanel.add(inputPanel, BorderLayout.CENTER);
        discountPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        // Checkout section
        JPanel checkoutPanel = new JPanel(new BorderLayout(10, 10));
        checkoutPanel.setBackground(Color.WHITE);
        checkoutPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 225)));
        checkoutPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        JButton btnRemove = new JButton("Remove");
        JButton btnClear = new JButton("Clear");
        btnRemove.setFont(new Font("Arial", Font.PLAIN, 11));
        btnClear.setFont(new Font("Arial", Font.PLAIN, 11));
        btnRemove.setBackground(new Color(200, 200, 210));
        btnClear.setBackground(new Color(200, 200, 210));
        btnRemove.setForeground(new Color(60, 60, 80));
        btnClear.setForeground(new Color(60, 60, 80));
        btnRemove.setFocusPainted(false);
        btnClear.setFocusPainted(false);
        btnRemove.addActionListener(e -> removeCart());
        btnClear.addActionListener(e -> clearCart());
        buttonPanel.add(btnRemove);
        buttonPanel.add(btnClear);

        totalLabel = new JLabel("Total: ₱0");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 14));
        totalLabel.setForeground(new Color(60, 60, 80));

        JButton btnOrder = new JButton("Place Order");
        btnOrder.setFont(new Font("Arial", Font.BOLD, 12));
        btnOrder.setBackground(new Color(66, 133, 244));
        btnOrder.setForeground(Color.WHITE);
        btnOrder.setFocusPainted(false);
        btnOrder.addActionListener(e -> checkout());

        checkoutPanel.add(buttonPanel, BorderLayout.NORTH);
        checkoutPanel.add(totalLabel, BorderLayout.WEST);
        checkoutPanel.add(btnOrder, BorderLayout.EAST);

        JPanel bottomPanel = new JPanel(new BorderLayout(0, 10));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        bottomPanel.add(discountPanel, BorderLayout.NORTH);
        bottomPanel.add(checkoutPanel, BorderLayout.SOUTH);

        panel.add(bottomPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void loadGrid() {
        List<Product> products = productDAO.getAllProducts();
        this.allProductsCache = new ArrayList<>(products);
        this.productCache = new ArrayList<>(products);
        displayProducts(productCache);
    }

    private JPanel createCard(Product p) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, new Color(240, 240, 245)));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Badge
        JLabel badge = new JLabel("NEW ARRIVAL");
        badge.setFont(new Font("Arial", Font.BOLD, 8));
        badge.setForeground(Color.WHITE);
        badge.setBackground(new Color(66, 133, 244));
        badge.setOpaque(true);
        badge.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
        JPanel badgePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        badgePanel.setBackground(Color.WHITE);
        badgePanel.add(badge);
        card.add(badgePanel, BorderLayout.NORTH);

        // Image
        try {
            byte[] imageData = p.getImageData();
            if (imageData != null && imageData.length > 0) {
                ImageIcon icon = new ImageIcon(imageData);
                Image scaledImg = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                JLabel imgLabel = new JLabel(new ImageIcon(scaledImg));
                imgLabel.setHorizontalAlignment(JLabel.CENTER);
                imgLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                card.add(imgLabel, BorderLayout.CENTER);
            } else {
                JLabel placeholder = new JLabel("📦");
                placeholder.setFont(new Font("Arial", Font.PLAIN, 40));
                placeholder.setHorizontalAlignment(JLabel.CENTER);
                placeholder.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
                card.add(placeholder, BorderLayout.CENTER);
            }
        } catch (Exception e) {
            JLabel placeholder = new JLabel("📦");
            placeholder.setFont(new Font("Arial", Font.PLAIN, 40));
            placeholder.setHorizontalAlignment(JLabel.CENTER);
            placeholder.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
            card.add(placeholder, BorderLayout.CENTER);
        }

        // Info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        JLabel nameLabel = new JLabel(p.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 12));
        nameLabel.setForeground(new Color(60, 60, 80));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        nameLabel.setMaximumSize(new Dimension(140, 30));
        infoPanel.add(nameLabel);

        JLabel priceLabel = new JLabel("₱" + String.format("%.0f", p.getPrice()));
        priceLabel.setFont(new Font("Arial", Font.BOLD, 13));
        priceLabel.setForeground(new Color(60, 60, 80));
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(priceLabel);

        card.add(infoPanel, BorderLayout.SOUTH);

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                showDetails(p);
            }
        });

        return card;
    }

    private void showDetails(Product p) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Product Details", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(Color.WHITE);

        try {
            byte[] imageData = p.getImageData();
            if (imageData != null && imageData.length > 0) {
                ImageIcon icon = new ImageIcon(imageData);
                Image scaledImg = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                JLabel imgLabel = new JLabel(new ImageIcon(scaledImg));
                panel.add(imgLabel, BorderLayout.WEST);
            } else {
                JLabel placeholder = new JLabel("📦");
                placeholder.setFont(new Font("Arial", Font.PLAIN, 60));
                placeholder.setHorizontalAlignment(JLabel.CENTER);
                panel.add(placeholder, BorderLayout.WEST);
            }
        } catch (Exception e) {
            JLabel placeholder = new JLabel("📦");
            placeholder.setFont(new Font("Arial", Font.PLAIN, 60));
            placeholder.setHorizontalAlignment(JLabel.CENTER);
            panel.add(placeholder, BorderLayout.WEST);
        }

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);

        JLabel nameLabel = new JLabel(p.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        nameLabel.setForeground(new Color(60, 60, 80));
        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(5));

        JLabel priceLabel = new JLabel("₱" + String.format("%.0f", p.getPrice()));
        priceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        priceLabel.setForeground(new Color(60, 60, 80));
        infoPanel.add(priceLabel);
        infoPanel.add(Box.createVerticalStrut(5));

        JLabel descLabel = new JLabel(p.getDescription());
        descLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        descLabel.setForeground(new Color(100, 100, 110));
        infoPanel.add(descLabel);
        infoPanel.add(Box.createVerticalStrut(10));

        String stockStatus = p.getQuantity() > 0 ? "✓ In Stock: " + p.getQuantity() + " units" : "✗ Out of Stock";
        JLabel stockLabel = new JLabel(stockStatus);
        stockLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        stockLabel.setForeground(new Color(100, 100, 110));
        infoPanel.add(stockLabel);

        infoPanel.add(Box.createVerticalGlue());

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        JButton btnAdd = new JButton("Add to Cart");
        btnAdd.setFont(new Font("Arial", Font.BOLD, 12));
        btnAdd.setBackground(new Color(66, 133, 244));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);
        btnAdd.addActionListener(e -> {
            addToCart(p);
            dialog.dispose();
        });

        JButton btnClose = new JButton("Close");
        btnClose.setFont(new Font("Arial", Font.PLAIN, 12));
        btnClose.setBackground(new Color(200, 200, 210));
        btnClose.setForeground(new Color(60, 60, 80));
        btnClose.setFocusPainted(false);
        btnClose.addActionListener(e -> dialog.dispose());

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnClose);
        infoPanel.add(buttonPanel);

        panel.add(infoPanel, BorderLayout.CENTER);
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void addToCart(Product p) {
        for (OrderItem item : cart) {
            if (item.getProductID() == p.getId()) {
                item.setQuantity(item.getQuantity() + 1);
                item.setSubtotal(item.getQuantity() * p.getPrice());
                updateCart();
                return;
            }
        }
        OrderItem item = new OrderItem();
        item.setProductID(p.getId());
        item.setProductName(p.getName());
        item.setPrice(p.getPrice());
        item.setQuantity(1);
        item.setSubtotal(p.getPrice());
        cart.add(item);
        updateCart();
    }

    private void removeCart() {
        int row = cartTable.getSelectedRow();
        if (row >= 0) {
            cart.remove(row);
            updateCart();
        }
    }

    private void clearCart() {
        cart.clear();
        updateCart();
    }

    private void updateCart() {
        cartModel.setRowCount(0);
        for (OrderItem item : cart) {
            cartModel.addRow(new Object[]{item.getProductID(), item.getProductName(), "₱" + item.getPrice(), item.getQuantity(), "₱" + item.getSubtotal()});
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
        public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
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
        double finalTotal = total - discountAmount;
        Order order = new Order(0, currentUser.getUserID(), null, "Pending", finalTotal, addr);
        order.setItems(new ArrayList<>(cart));
        if (orderDAO.createOrder(order)) {
            JOptionPane.showMessageDialog(this, "Order placed! Total: ₱" + String.format("%.0f", finalTotal));
            cart.clear();
            discountAmount = 0;
            updateCart();
            loadGrid();
        } else {
            JOptionPane.showMessageDialog(this, "Order failed!");
        }
    }

    private void applyFilters() {
        String keyword = txtSearch.getText().trim().toLowerCase();
        String selectedCategory = (String) cmbCategory.getSelectedItem();

        Integer categoryID = null;
        if (!selectedCategory.equals("All Categories")) {
            for (Category c : categoryDAO.getMainCategories()) {
                if (c.getName().equals(selectedCategory)) {
                    categoryID = c.getCategoryID();
                    break;
                }
            }
        }

        final Integer finalCategoryID = categoryID;
        List<Product> results = allProductsCache.stream()
            .filter(p -> (keyword.isEmpty() || p.getName().toLowerCase().contains(keyword) || p.getDescription().toLowerCase().contains(keyword)) &&
                        (finalCategoryID == null || p.getCategoryID() == finalCategoryID))
            .toList();

        String sortOption = (String) cmbSortBy.getSelectedItem();
        List<Product> sorted = applySorting(new ArrayList<>(results), sortOption);
        displayProducts(sorted);
    }

    private List<Product> applySorting(List<Product> products, String sortOption) {
        switch (sortOption) {
            case "Price ↑":
                return products.stream().sorted((a, b) -> Double.compare(a.getPrice(), b.getPrice())).toList();
            case "Price ↓":
                return products.stream().sorted((a, b) -> Double.compare(b.getPrice(), a.getPrice())).toList();
            case "Name (A-Z)":
                return products.stream().sorted((a, b) -> a.getName().compareTo(b.getName())).toList();
            case "Stock ↑":
                return products.stream().sorted((a, b) -> Integer.compare(a.getQuantity(), b.getQuantity())).toList();
            case "Stock ↓":
                return products.stream().sorted((a, b) -> Integer.compare(b.getQuantity(), a.getQuantity())).toList();
            default:
                return products;
        }
    }

    private void displayProducts(List<Product> products) {
        gridPanel.removeAll();
        this.productCache = new ArrayList<>(products);
        
        // Update result label
        if (resultLabel != null) {
            String keyword = txtSearch.getText().trim();
            if (keyword.isEmpty()) {
                resultLabel.setText(products.size() + " products available");
            } else {
                resultLabel.setText(products.size() + " results for '" + keyword + "'");
            }
        }
        
        for (Product p : products) {
            gridPanel.add(createCard(p));
        }
        gridPanel.revalidate();
        gridPanel.repaint();
    }
}
