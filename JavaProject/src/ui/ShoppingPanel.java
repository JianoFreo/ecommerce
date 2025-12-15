package src.ui;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import src.dao.OrderDAO;
import src.dao.ProductDAO;
import src.model.Order;
import src.model.OrderItem;
import src.model.Product;
import src.model.User;

public class ShoppingPanel extends JPanel {
    private JPanel gridPanel;
    private JTable cartTable;
    private DefaultTableModel cartModel;
    private ProductDAO productDAO;
    private OrderDAO orderDAO;
    private User currentUser;
    private List<OrderItem> cart;
    private List<Product> productCache;
    private JLabel totalLabel;

    public ShoppingPanel(User user) {
        this.currentUser = user;
        this.productDAO = new ProductDAO();
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
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(245, 245, 245));
        JLabel title = new JLabel("🛍️ Products");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(e -> loadGrid());
        JPanel refreshPanel = new JPanel();
        refreshPanel.add(refresh);
        headerPanel.add(title, BorderLayout.WEST);
        headerPanel.add(refreshPanel, BorderLayout.EAST);
        leftPanel.add(headerPanel, BorderLayout.NORTH);

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
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
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
        rightPanel.add(cartBottomPanel, BorderLayout.SOUTH);

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

        if (p.getImageUrl() != null && !p.getImageUrl().isEmpty() && new File(p.getImageUrl()).exists()) {
            try {
                ImageIcon icon = new ImageIcon(p.getImageUrl());
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
        
        if (p.getImageUrl() != null && !p.getImageUrl().isEmpty() && new File(p.getImageUrl()).exists()) {
            try {
                ImageIcon icon = new ImageIcon(p.getImageUrl());
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
                item.setImageUrl(p.getImageUrl());
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
                item.getImageUrl(),
                item.getProductName(),
                "₱" + String.format("%.0f", item.getPrice()),
                item.getQuantity(),
                "₱" + String.format("%.0f", item.getSubtotal())
            });
            total += item.getSubtotal();
        }
        totalLabel.setText(String.format("Total: ₱%.0f", total));
    }

    private class CartImageRenderer extends javax.swing.table.DefaultTableCellRenderer {
        @Override
        public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBackground(isSelected ? new Color(200, 220, 255) : Color.WHITE);
            
            if (value != null && column == 0) {
                String imageUrl = value.toString();
                JLabel imgLabel = new JLabel();
                imgLabel.setHorizontalAlignment(JLabel.CENTER);
                imgLabel.setVerticalAlignment(JLabel.CENTER);
                
                if (!imageUrl.isEmpty() && new File(imageUrl).exists()) {
                    try {
                        ImageIcon icon = new ImageIcon(imageUrl);
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
}
