package src.ui;

import src.dao.*;
import src.model.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class CustomerDashboard extends JFrame {
    private User currentUser;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private ProductDAO productDAO;
    private CartDAO cartDAO;
    private OrderDAO orderDAO;
    private ReviewDAO reviewDAO;
    private Cart currentCart;

    public CustomerDashboard(User user) {
        this.currentUser = user;
        this.productDAO = new ProductDAO();
        this.cartDAO = new CartDAO();
        this.orderDAO = new OrderDAO();
        this.reviewDAO = new ReviewDAO();
        
        // Get or create cart for user
        this.currentCart = cartDAO.getCartByUserId(user.getUserID());
        if (this.currentCart == null) {
            int cartID = cartDAO.createCart(user.getUserID());
            this.currentCart = new Cart(cartID, user.getUserID());
        }
        
        setTitle("E-Commerce - Welcome " + user.getName());
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        createMenuBar();

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(createShopPanel(), "SHOP");
        mainPanel.add(createCartPanel(), "CART");
        mainPanel.add(createOrdersPanel(), "ORDERS");
        mainPanel.add(createProfilePanel(), "PROFILE");

        add(mainPanel);
        cardLayout.show(mainPanel, "SHOP");
        
        setVisible(true);
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu shopMenu = new JMenu("Shop");
        JMenuItem browseItem = new JMenuItem("Browse Products");
        browseItem.addActionListener(e -> cardLayout.show(mainPanel, "SHOP"));
        shopMenu.add(browseItem);
        menuBar.add(shopMenu);

        JMenu cartMenu = new JMenu("Cart");
        JMenuItem viewCartItem = new JMenuItem("View Cart");
        viewCartItem.addActionListener(e -> {
            refreshCart();
            cardLayout.show(mainPanel, "CART");
        });
        cartMenu.add(viewCartItem);
        menuBar.add(cartMenu);

        JMenu ordersMenu = new JMenu("Orders");
        JMenuItem myOrdersItem = new JMenuItem("My Orders");
        myOrdersItem.addActionListener(e -> {
            refreshOrders();
            cardLayout.show(mainPanel, "ORDERS");
        });
        ordersMenu.add(myOrdersItem);
        menuBar.add(ordersMenu);

        JMenu accountMenu = new JMenu("Account");
        JMenuItem profileItem = new JMenuItem("My Profile");
        profileItem.addActionListener(e -> cardLayout.show(mainPanel, "PROFILE"));
        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });
        accountMenu.add(profileItem);
        accountMenu.add(logoutItem);
        menuBar.add(accountMenu);

        setJMenuBar(menuBar);
    }

    private JPanel createShopPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel header = new JLabel("Browse Products");
        header.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(header, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Name", "Description", "Price", "Stock", "Category"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        
        List<Product> products = productDAO.getAllProducts();
        for (Product p : products) {
            model.addRow(new Object[]{
                p.getId(), p.getName(), p.getDescription(),
                String.format("₱%.2f", p.getPrice()), p.getQuantity(),
                p.getCategoryName()
            });
        }

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout());
        JButton btnAddToCart = new JButton("Add to Cart");
        JButton btnViewReviews = new JButton("View Reviews");
        JButton btnRefresh = new JButton("Refresh");

        btnAddToCart.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Select a product!");
                return;
            }
            
            int productID = (int) model.getValueAt(row, 0);
            String productName = model.getValueAt(row, 1).toString();
            int stock = Integer.parseInt(model.getValueAt(row, 4).toString());
            
            if (stock <= 0) {
                JOptionPane.showMessageDialog(this, "Product out of stock!");
                return;
            }
            
            String qtyStr = JOptionPane.showInputDialog(this, "Enter quantity:", "1");
            if (qtyStr != null) {
                try {
                    int qty = Integer.parseInt(qtyStr);
                    if (qty <= 0 || qty > stock) {
                        JOptionPane.showMessageDialog(this, "Invalid quantity!");
                        return;
                    }
                    
                    if (cartDAO.addItemToCart(currentCart.getCartID(), productID, qty)) {
                        JOptionPane.showMessageDialog(this, 
                            productName + " added to cart!");
                        currentCart = cartDAO.getCartByUserId(currentUser.getUserID());
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid quantity!");
                }
            }
        });

        btnViewReviews.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Select a product!");
                return;
            }
            int productID = (int) model.getValueAt(row, 0);
            showProductReviews(productID);
        });

        btnRefresh.addActionListener(e -> {
            model.setRowCount(0);
            List<Product> prods = productDAO.getAllProducts();
            for (Product p : prods) {
                model.addRow(new Object[]{
                    p.getId(), p.getName(), p.getDescription(),
                    String.format("₱%.2f", p.getPrice()), p.getQuantity(),
                    p.getCategoryName()
                });
            }
        });

        actionPanel.add(btnAddToCart);
        actionPanel.add(btnViewReviews);
        actionPanel.add(btnRefresh);
        panel.add(actionPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createCartPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel header = new JLabel("Shopping Cart");
        header.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(header, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(
            new String[]{"Item ID", "Product", "Price", "Quantity", "Subtotal"}, 0);
        JTable table = new JTable(model);
        
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        JLabel lblTotal = new JLabel("Total: ₱0.00", SwingConstants.RIGHT);
        lblTotal.setFont(new Font("Arial", Font.BOLD, 16));
        bottomPanel.add(lblTotal, BorderLayout.NORTH);

        JPanel actionPanel = new JPanel(new FlowLayout());
        JButton btnCheckout = new JButton("Checkout");
        JButton btnRemove = new JButton("Remove Item");
        JButton btnClear = new JButton("Clear Cart");

        btnCheckout.addActionListener(e -> checkout());
        
        btnRemove.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Select an item!");
                return;
            }
            int itemID = (int) model.getValueAt(row, 0);
            if (cartDAO.removeItemFromCart(itemID)) {
                JOptionPane.showMessageDialog(this, "Item removed!");
                refreshCart();
            }
        });

        btnClear.addActionListener(e -> {
            if (cartDAO.clearCart(currentCart.getCartID())) {
                JOptionPane.showMessageDialog(this, "Cart cleared!");
                refreshCart();
            }
        });

        actionPanel.add(btnCheckout);
        actionPanel.add(btnRemove);
        actionPanel.add(btnClear);
        bottomPanel.add(actionPanel, BorderLayout.CENTER);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        // Store references for refresh
        panel.putClientProperty("table", table);
        panel.putClientProperty("model", model);
        panel.putClientProperty("lblTotal", lblTotal);

        refreshCartDisplay(panel);
        return panel;
    }

    private void refreshCart() {
        currentCart = cartDAO.getCartByUserId(currentUser.getUserID());
        Component[] components = mainPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                if (comp.getClientProperty("table") != null) {
                    refreshCartDisplay((JPanel) comp);
                }
            }
        }
    }

    private void refreshCartDisplay(JPanel panel) {
        DefaultTableModel model = (DefaultTableModel) panel.getClientProperty("model");
        JLabel lblTotal = (JLabel) panel.getClientProperty("lblTotal");
        
        if (model != null && currentCart != null) {
            model.setRowCount(0);
            currentCart = cartDAO.getCartByUserId(currentUser.getUserID());
            
            if (currentCart != null) {
                for (CartItem item : currentCart.getItems()) {
                    model.addRow(new Object[]{
                        item.getCartItemID(),
                        item.getProductName(),
                        String.format("₱%.2f", item.getProductPrice()),
                        item.getQuantity(),
                        String.format("₱%.2f", item.getSubtotal())
                    });
                }
                lblTotal.setText(String.format("Total: ₱%.2f", currentCart.getTotalCost()));
            }
        }
    }

    private void checkout() {
        if (currentCart == null || currentCart.getItems().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty!");
            return;
        }

        String address = JOptionPane.showInputDialog(this, 
            "Enter shipping address:", currentUser.getAddress());
        
        if (address != null && !address.trim().isEmpty()) {
            int orderID = orderDAO.createOrder(currentUser.getUserID(), address, currentCart);
            if (orderID > 0) {
                JOptionPane.showMessageDialog(this, 
                    "Order placed successfully! Order ID: " + orderID);
                currentCart = cartDAO.getCartByUserId(currentUser.getUserID());
                refreshCart();
            } else {
                JOptionPane.showMessageDialog(this, "Order failed!");
            }
        }
    }

    private JPanel createOrdersPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel header = new JLabel("My Orders");
        header.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(header, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(
            new String[]{"Order ID", "Date", "Status", "Total", "Items"}, 0);
        JTable table = new JTable(model);
        
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout());
        JButton btnViewDetails = new JButton("View Details");
        JButton btnAddReview = new JButton("Add Review");
        JButton btnRefresh = new JButton("Refresh");

        btnViewDetails.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                int orderID = (int) model.getValueAt(row, 0);
                showOrderDetails(orderID);
            }
        });

        btnAddReview.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                int orderID = (int) model.getValueAt(row, 0);
                addReviewDialog(orderID);
            }
        });

        btnRefresh.addActionListener(e -> refreshOrders());

        actionPanel.add(btnViewDetails);
        actionPanel.add(btnAddReview);
        actionPanel.add(btnRefresh);
        panel.add(actionPanel, BorderLayout.SOUTH);

        panel.putClientProperty("table", table);
        panel.putClientProperty("model", model);

        refreshOrdersDisplay(panel);
        return panel;
    }

    private void refreshOrders() {
        Component[] components = mainPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                if (comp.getClientProperty("model") != null) {
                    refreshOrdersDisplay((JPanel) comp);
                }
            }
        }
    }

    private void refreshOrdersDisplay(JPanel panel) {
        DefaultTableModel model = (DefaultTableModel) panel.getClientProperty("model");
        if (model != null) {
            model.setRowCount(0);
            List<Order> orders = orderDAO.getOrdersByUserId(currentUser.getUserID());
            for (Order o : orders) {
                model.addRow(new Object[]{
                    o.getOrderID(),
                    o.getOrderDate(),
                    o.getStatus(),
                    String.format("₱%.2f", o.getTotalAmount()),
                    o.getItemCount()
                });
            }
        }
    }

    private void showOrderDetails(int orderID) {
        Order order = orderDAO.getOrderById(orderID);
        StringBuilder details = new StringBuilder();
        details.append("Order ID: ").append(order.getOrderID()).append("\n");
        details.append("Date: ").append(order.getOrderDate()).append("\n");
        details.append("Status: ").append(order.getStatus()).append("\n");
        details.append("Shipping: ").append(order.getShippingAddress()).append("\n\n");
        details.append("Items:\n");
        
        for (OrderItem item : order.getItems()) {
            details.append(String.format("  • %s x %d = ₱%.2f\n",
                item.getProductName(), item.getQuantity(), item.getSubtotal()));
        }
        
        details.append(String.format("\nTotal: ₱%.2f", order.getTotalAmount()));
        JOptionPane.showMessageDialog(this, details.toString(), 
            "Order Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private void addReviewDialog(int orderID) {
        Order order = orderDAO.getOrderById(orderID);
        if (order == null || order.getItems().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No items to review!");
            return;
        }

        JComboBox<String> productCombo = new JComboBox<>();
        for (OrderItem item : order.getItems()) {
            productCombo.addItem(item.getProductID() + " - " + item.getProductName());
        }

        JComboBox<Integer> ratingCombo = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
        ratingCombo.setSelectedItem(5);
        
        JTextArea txtComment = new JTextArea(5, 20);
        
        JPanel reviewPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        reviewPanel.add(new JLabel("Product:"));
        reviewPanel.add(productCombo);
        reviewPanel.add(new JLabel("Rating (1-5):"));
        reviewPanel.add(ratingCombo);
        reviewPanel.add(new JLabel("Comment:"));
        reviewPanel.add(new JScrollPane(txtComment));

        int result = JOptionPane.showConfirmDialog(this, reviewPanel, 
            "Add Review", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String selected = (String) productCombo.getSelectedItem();
            int productID = Integer.parseInt(selected.split(" - ")[0]);
            
            Review review = new Review();
            review.setProductID(productID);
            review.setUserID(currentUser.getUserID());
            review.setRating((Integer) ratingCombo.getSelectedItem());
            review.setComment(txtComment.getText());

            if (reviewDAO.addReview(review)) {
                JOptionPane.showMessageDialog(this, "Review added!");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add review!");
            }
        }
    }

    private void showProductReviews(int productID) {
        List<Review> reviews = reviewDAO.getReviewsByProductId(productID);
        double avgRating = reviewDAO.getAverageRating(productID);
        
        StringBuilder msg = new StringBuilder();
        msg.append(String.format("Average Rating: %.1f/5.0 (%d reviews)\n\n", 
            avgRating, reviews.size()));
        
        if (reviews.isEmpty()) {
            msg.append("No reviews yet.");
        } else {
            for (Review r : reviews) {
                msg.append(r.getStarDisplay()).append(" - ").append(r.getUserName()).append("\n");
                msg.append(r.getComment()).append("\n\n");
            }
        }
        
        JOptionPane.showMessageDialog(this, msg.toString(), 
            "Product Reviews", JOptionPane.INFORMATION_MESSAGE);
    }

    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel header = new JLabel("My Profile");
        header.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(header, BorderLayout.NORTH);

        JPanel infoPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        infoPanel.add(new JLabel("Name:"));
        infoPanel.add(new JLabel(currentUser.getName()));
        infoPanel.add(new JLabel("Email:"));
        infoPanel.add(new JLabel(currentUser.getEmail()));
        infoPanel.add(new JLabel("Address:"));
        infoPanel.add(new JLabel(currentUser.getAddress()));
        infoPanel.add(new JLabel("Phone:"));
        infoPanel.add(new JLabel(currentUser.getPhoneNumber()));
        infoPanel.add(new JLabel("Role:"));
        infoPanel.add(new JLabel(currentUser.getRole()));

        panel.add(infoPanel, BorderLayout.CENTER);

        return panel;
    }
}
