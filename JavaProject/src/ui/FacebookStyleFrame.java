package src.ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;
import src.dao.ProductDAO;
import src.dao.OrderDAO;
import src.model.Product;
import src.model.User;
import src.model.Order;
import src.model.OrderItem;
import java.util.ArrayList;

public class FacebookStyleFrame extends JFrame {
    private User currentUser;
    private ProductDAO productDAO;
    private OrderDAO orderDAO;
    private JPanel productsPanel;
    private List<OrderItem> cartItems;
    private JLabel cartCountLabel;
    private JTextArea cartSummaryArea;

    public FacebookStyleFrame(User user) {
        this.currentUser = user;
        this.productDAO = new ProductDAO();
        this.orderDAO = new OrderDAO();
        this.cartItems = new ArrayList<>();
        
        setTitle("E-Commerce - " + user.getName());
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Top Navigation Bar
        add(createNavBar(), BorderLayout.NORTH);
        
        // Main Content Area
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(700);
        
        // Left - Product Feed
        JScrollPane feedScroll = new JScrollPane(createProductFeed());
        feedScroll.getVerticalScrollBar().setUnitIncrement(16);
        splitPane.setLeftComponent(feedScroll);
        
        // Right - Shopping Cart Sidebar
        splitPane.setRightComponent(createCartSidebar());
        
        add(splitPane, BorderLayout.CENTER);
        
        setVisible(true);
    }

    private JPanel createNavBar() {
        JPanel navBar = new JPanel(new BorderLayout());
        navBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Left side
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel logo = new JLabel("E-Commerce System");
        leftPanel.add(logo);
        
        // Right side
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        cartCountLabel = new JLabel("Cart (0)");
        JLabel userLabel = new JLabel(currentUser.getName() + " (" + currentUser.getRole() + ")");
        
        JButton menuBtn = new JButton("Menu");
        menuBtn.addActionListener(e -> showMenu());
        
        rightPanel.add(cartCountLabel);
        rightPanel.add(userLabel);
        rightPanel.add(menuBtn);
        
        navBar.add(leftPanel, BorderLayout.WEST);
        navBar.add(rightPanel, BorderLayout.EAST);
        
        return navBar;
    }

    private JPanel createProductFeed() {
        productsPanel = new JPanel();
        productsPanel.setLayout(new BoxLayout(productsPanel, BoxLayout.Y_AXIS));
        
        loadProducts();
        
        return productsPanel;
    }

    private void loadProducts() {
        productsPanel.removeAll();
        List<Product> products = productDAO.getAllProducts();
        
        for (Product product : products) {
            if (product.getQuantity() > 0) {
                productsPanel.add(createProductCard(product));
                productsPanel.add(Box.createVerticalStrut(15));
            }
        }
        
        productsPanel.revalidate();
        productsPanel.repaint();
    }

    private JPanel createProductCard(Product product) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        card.setMaximumSize(new Dimension(680, 150));
        
        // Product Info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        
        JLabel nameLabel = new JLabel(product.getName());
        
        JLabel descLabel = new JLabel("<html>" + product.getDescription() + "</html>");
        
        JLabel categoryLabel = new JLabel("Category: " + product.getCategoryName());
        
        // Price info with markup
        JPanel pricePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        
        JLabel costLabel = new JLabel(String.format("Cost: $%.2f", product.getCostPrice()));
        
        JLabel priceLabel = new JLabel(String.format("Selling Price: $%.2f", product.getPrice()));
        
        JLabel profitLabel = new JLabel(String.format("Profit: $%.2f (%.1f%% markup)", 
            product.getProfit(), product.getProfitMargin()));
        
        pricePanel.add(costLabel);
        pricePanel.add(new JLabel("|"));
        pricePanel.add(priceLabel);
        pricePanel.add(new JLabel("|"));
        pricePanel.add(profitLabel);
        
        JLabel stockLabel = new JLabel("In stock: " + product.getQuantity());
        
        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(descLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(categoryLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(pricePanel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(stockLabel);
        
        // Action buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton addToCartBtn = new JButton("Add to Cart");
        addToCartBtn.addActionListener(e -> addToCart(product));
        
        actionPanel.add(addToCartBtn);
        
        card.add(infoPanel, BorderLayout.CENTER);
        card.add(actionPanel, BorderLayout.SOUTH);
        
        return card;
    }

    private JPanel createCartSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.GRAY));
        
        JLabel title = new JLabel("Shopping Cart");
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        cartSummaryArea = new JTextArea();
        cartSummaryArea.setEditable(false);
        cartSummaryArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JScrollPane scrollPane = new JScrollPane(cartSummaryArea);
        
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton checkoutBtn = new JButton("Checkout");
        checkoutBtn.addActionListener(e -> checkout());
        
        JButton clearBtn = new JButton("Clear Cart");
        clearBtn.addActionListener(e -> clearCart());
        
        JButton ordersBtn = new JButton("My Orders");
        ordersBtn.addActionListener(e -> showOrders());
        
        buttonPanel.add(checkoutBtn);
        buttonPanel.add(clearBtn);
        buttonPanel.add(ordersBtn);
        
        sidebar.add(title, BorderLayout.NORTH);
        sidebar.add(scrollPane, BorderLayout.CENTER);
        sidebar.add(buttonPanel, BorderLayout.SOUTH);
        
        updateCartDisplay();
        
        return sidebar;
    }

    private void addToCart(Product product) {
        String qtyStr = JOptionPane.showInputDialog(this, "Enter quantity:", "1");
        if (qtyStr == null || qtyStr.trim().isEmpty()) return;
        
        try {
            int quantity = Integer.parseInt(qtyStr);
            if (quantity <= 0 || quantity > product.getQuantity()) {
                JOptionPane.showMessageDialog(this, "Invalid quantity!");
                return;
            }
            
            // Check if already in cart
            boolean found = false;
            for (OrderItem item : cartItems) {
                if (item.getProductID() == product.getId()) {
                    item.setQuantity(item.getQuantity() + quantity);
                    found = true;
                    break;
                }
            }
            
            if (!found) {
                OrderItem item = new OrderItem(0, 0, product.getId(), 
                    product.getName(), product.getPrice(), quantity);
                cartItems.add(item);
            }
            
            updateCartDisplay();
            JOptionPane.showMessageDialog(this, "Added to cart!");
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid quantity!");
        }
    }

    private void updateCartDisplay() {
        cartCountLabel.setText("Cart (" + cartItems.size() + ")");
        
        StringBuilder sb = new StringBuilder();
        double total = 0;
        
        for (OrderItem item : cartItems) {
            sb.append(item.getProductName()).append("\n");
            sb.append("  Qty: ").append(item.getQuantity());
            sb.append(" x $").append(String.format("%.2f", item.getPrice()));
            sb.append(" = $").append(String.format("%.2f", item.getSubtotal())).append("\n\n");
            total += item.getSubtotal();
        }
        
        if (cartItems.isEmpty()) {
            sb.append("Your cart is empty");
        } else {
            sb.append("─────────────────\n");
            sb.append("TOTAL: $").append(String.format("%.2f", total));
        }
        
        cartSummaryArea.setText(sb.toString());
    }

    private void checkout() {
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty!");
            return;
        }
        
        String address = JOptionPane.showInputDialog(this, "Enter shipping address:", currentUser.getAddress());
        if (address == null || address.trim().isEmpty()) return;
        
        double total = cartItems.stream().mapToDouble(OrderItem::getSubtotal).sum();
        Order order = new Order(0, currentUser.getUserID(), null, "Pending", total, address);
        order.setItems(new ArrayList<>(cartItems));
        
        if (orderDAO.createOrder(order)) {
            JOptionPane.showMessageDialog(this, "Order placed successfully!\nTotal: $" + String.format("%.2f", total));
            cartItems.clear();
            updateCartDisplay();
            loadProducts(); // Refresh stock
        } else {
            JOptionPane.showMessageDialog(this, "Failed to place order!");
        }
    }

    private void clearCart() {
        if (!cartItems.isEmpty()) {
            cartItems.clear();
            updateCartDisplay();
        }
    }

    private void showOrders() {
        new JFrame("My Orders") {{
            setSize(900, 600);
            setLocationRelativeTo(FacebookStyleFrame.this);
            add(new OrderHistoryPanel(currentUser));
            setVisible(true);
        }};
    }

    private void showMenu() {
        JPopupMenu menu = new JPopupMenu();
        
        if ("Admin".equals(currentUser.getRole())) {
            menu.add(createMenuItem("Manage Products", e -> openProductManagement()));
            menu.add(createMenuItem("Manage Categories", e -> openCategoryManagement()));
            menu.add(createMenuItem("Manage Users", e -> openUserManagement()));
            menu.add(createMenuItem("Manage Orders", e -> openOrderManagement()));
            menu.add(createMenuItem("Manage Reviews", e -> openReviewManagement()));
            menu.addSeparator();
        }
        
        menu.add(createMenuItem("My Profile", e -> showProfile()));
        menu.add(createMenuItem("Refresh Products", e -> loadProducts()));
        menu.addSeparator();
        menu.add(createMenuItem("Logout", e -> logout()));
        
        menu.show(this, getWidth() - 150, 60);
    }

    private JMenuItem createMenuItem(String text, java.awt.event.ActionListener action) {
        JMenuItem item = new JMenuItem(text);
        item.addActionListener(action);
        return item;
    }

    private void openProductManagement() {
        new JFrame("Product Management") {{
            setSize(900, 600);
            setLocationRelativeTo(FacebookStyleFrame.this);
            add(new ProductManagementPanel());
            setVisible(true);
        }};
    }

    private void openCategoryManagement() {
        new JFrame("Category Management") {{
            setSize(800, 500);
            setLocationRelativeTo(FacebookStyleFrame.this);
            add(new CategoryManagementPanel());
            setVisible(true);
        }};
    }

    private void openUserManagement() {
        new JFrame("User Management") {{
            setSize(900, 600);
            setLocationRelativeTo(FacebookStyleFrame.this);
            add(new UserManagementPanel());
            setVisible(true);
        }};
    }

    private void openOrderManagement() {
        new JFrame("Order Management") {{
            setSize(900, 600);
            setLocationRelativeTo(FacebookStyleFrame.this);
            add(new OrderManagementPanel());
            setVisible(true);
        }};
    }

    private void openReviewManagement() {
        new JFrame("Review Management") {{
            setSize(900, 600);
            setLocationRelativeTo(FacebookStyleFrame.this);
            add(new ReviewManagementPanel());
            setVisible(true);
        }};
    }

    private void showProfile() {
        String profile = "Name: " + currentUser.getName() + "\n" +
                        "Email: " + currentUser.getEmail() + "\n" +
                        "Phone: " + currentUser.getPhoneNumber() + "\n" +
                        "Address: " + currentUser.getAddress();
        JOptionPane.showMessageDialog(this, profile, "My Profile", JOptionPane.INFORMATION_MESSAGE);
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Logout?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame();
        }
    }
}
