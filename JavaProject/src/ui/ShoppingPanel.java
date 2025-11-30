package src.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import src.dao.ProductDAO;
import src.dao.OrderDAO;
import src.model.Product;
import src.model.Order;
import src.model.OrderItem;
import src.model.User;

public class ShoppingPanel extends JPanel {
    private JTable productTable;
    private DefaultTableModel productTableModel;
    private JTable cartTable;
    private DefaultTableModel cartTableModel;
    private ProductDAO productDAO;
    private OrderDAO orderDAO;
    private User currentUser;
    private List<OrderItem> cartItems;
    private JLabel totalLabel;

    public ShoppingPanel(User user) {
        this.currentUser = user;
        this.productDAO = new ProductDAO();
        this.orderDAO = new OrderDAO();
        this.cartItems = new ArrayList<>();
        
        setLayout(new BorderLayout());

        // Split pane for products and cart
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        
        // Products Panel (Top)
        JPanel productsPanel = new JPanel(new BorderLayout());
        productsPanel.setBorder(BorderFactory.createTitledBorder("Available Products"));
        
        String[] productColumns = {"ID", "Name", "Price", "Stock", "Category"};
        productTableModel = new DefaultTableModel(productColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        productTable = new JTable(productTableModel);
        productsPanel.add(new JScrollPane(productTable), BorderLayout.CENTER);
        
        JPanel productButtonPanel = new JPanel();
        JButton btnAddToCart = new JButton("Add to Cart");
        JButton btnRefresh = new JButton("Refresh Products");
        btnAddToCart.addActionListener(e -> addToCart());
        btnRefresh.addActionListener(e -> loadProducts());
        productButtonPanel.add(btnAddToCart);
        productButtonPanel.add(btnRefresh);
        productsPanel.add(productButtonPanel, BorderLayout.SOUTH);
        
        // Cart Panel (Bottom)
        JPanel cartPanel = new JPanel(new BorderLayout());
        cartPanel.setBorder(BorderFactory.createTitledBorder("Shopping Cart"));
        
        String[] cartColumns = {"Product", "Price", "Quantity", "Subtotal"};
        cartTableModel = new DefaultTableModel(cartColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        cartTable = new JTable(cartTableModel);
        cartPanel.add(new JScrollPane(cartTable), BorderLayout.CENTER);
        
        JPanel cartButtonPanel = new JPanel(new BorderLayout());
        JPanel leftButtons = new JPanel();
        JButton btnRemove = new JButton("Remove Item");
        JButton btnClear = new JButton("Clear Cart");
        btnRemove.addActionListener(e -> removeFromCart());
        btnClear.addActionListener(e -> clearCart());
        leftButtons.add(btnRemove);
        leftButtons.add(btnClear);
        
        JPanel rightPanel = new JPanel();
        totalLabel = new JLabel("Total: $0.00");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 14));
        JButton btnCheckout = new JButton("Place Order");
        btnCheckout.addActionListener(e -> checkout());
        rightPanel.add(totalLabel);
        rightPanel.add(btnCheckout);
        
        cartButtonPanel.add(leftButtons, BorderLayout.WEST);
        cartButtonPanel.add(rightPanel, BorderLayout.EAST);
        cartPanel.add(cartButtonPanel, BorderLayout.SOUTH);
        
        splitPane.setTopComponent(productsPanel);
        splitPane.setBottomComponent(cartPanel);
        splitPane.setDividerLocation(300);
        
        add(splitPane, BorderLayout.CENTER);
        
        loadProducts();
    }

    private void loadProducts() {
        productTableModel.setRowCount(0);
        List<Product> products = productDAO.getAllProducts();
        
        for (Product product : products) {
            if (product.getQuantity() > 0) { // Only show in-stock products
                productTableModel.addRow(new Object[]{
                    product.getId(),
                    product.getName(),
                    String.format("$%.2f", product.getPrice()),
                    product.getQuantity(),
                    product.getCategoryName()
                });
            }
        }
    }

    private void addToCart() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product to add to cart.");
            return;
        }

        int productId = (int) productTableModel.getValueAt(selectedRow, 0);
        String productName = (String) productTableModel.getValueAt(selectedRow, 1);
        String priceStr = (String) productTableModel.getValueAt(selectedRow, 2);
        double price = Double.parseDouble(priceStr.replace("$", ""));
        int availableStock = (int) productTableModel.getValueAt(selectedRow, 3);
        
        String qtyStr = JOptionPane.showInputDialog(this, "Enter quantity:", "1");
        if (qtyStr == null || qtyStr.trim().isEmpty()) {
            return;
        }
        
        try {
            int quantity = Integer.parseInt(qtyStr);
            if (quantity <= 0) {
                JOptionPane.showMessageDialog(this, "Quantity must be greater than 0.");
                return;
            }
            if (quantity > availableStock) {
                JOptionPane.showMessageDialog(this, "Only " + availableStock + " items available in stock.");
                return;
            }
            
            // Check if product already in cart
            boolean found = false;
            for (OrderItem item : cartItems) {
                if (item.getProductID() == productId) {
                    int newQty = item.getQuantity() + quantity;
                    if (newQty > availableStock) {
                        JOptionPane.showMessageDialog(this, "Cannot add more. Stock limit reached.");
                        return;
                    }
                    item.setQuantity(newQty);
                    item.setSubtotal(price * newQty);
                    found = true;
                    break;
                }
            }
            
            if (!found) {
                OrderItem item = new OrderItem(0, 0, productId, productName, price, quantity);
                cartItems.add(item);
            }
            
            updateCartDisplay();
            JOptionPane.showMessageDialog(this, "Added to cart!");
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid quantity.");
        }
    }

    private void removeFromCart() {
        int selectedRow = cartTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item to remove.");
            return;
        }
        
        cartItems.remove(selectedRow);
        updateCartDisplay();
    }

    private void clearCart() {
        if (cartItems.isEmpty()) {
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Clear all items from cart?", "Confirm", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            cartItems.clear();
            updateCartDisplay();
        }
    }

    private void updateCartDisplay() {
        cartTableModel.setRowCount(0);
        double total = 0;
        
        for (OrderItem item : cartItems) {
            cartTableModel.addRow(new Object[]{
                item.getProductName(),
                String.format("$%.2f", item.getPrice()),
                item.getQuantity(),
                String.format("$%.2f", item.getSubtotal())
            });
            total += item.getSubtotal();
        }
        
        totalLabel.setText(String.format("Total: $%.2f", total));
    }

    private void checkout() {
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Your cart is empty.");
            return;
        }
        
        String address = JOptionPane.showInputDialog(this, 
            "Enter shipping address:", currentUser.getAddress());
        
        if (address == null || address.trim().isEmpty()) {
            return;
        }
        
        double total = cartItems.stream().mapToDouble(OrderItem::getSubtotal).sum();
        
        Order order = new Order(0, currentUser.getUserID(), null, "Pending", total, address);
        order.setItems(new ArrayList<>(cartItems));
        
        boolean success = orderDAO.createOrder(order);
        
        if (success) {
            JOptionPane.showMessageDialog(this, 
                "Order placed successfully!\nTotal: $" + String.format("%.2f", total));
            cartItems.clear();
            updateCartDisplay();
            loadProducts(); // Refresh to show updated stock
        } else {
            JOptionPane.showMessageDialog(this, 
                "Failed to place order. Please try again.");
        }
    }
}
