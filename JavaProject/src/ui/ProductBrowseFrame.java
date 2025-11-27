package src.ui;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import src.dao.ProductDAO;
import src.models.Product;
import src.models.User;

public class ProductBrowseFrame extends JFrame {
    private User currentUser;
    private ProductDAO productDAO;
    private JPanel productPanel;
    private JTextField txtSearch;
    
    public ProductBrowseFrame(User user) {
        this.currentUser = user;
        this.productDAO = new ProductDAO();
        
        setTitle("Browse Products");
        setSize(800, 600);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        
        // Top Panel with Search
        JPanel topPanel = new JPanel(new FlowLayout());
        JLabel lblSearch = new JLabel("Search:");
        txtSearch = new JTextField(20);
        JButton btnSearch = new JButton("Search");
        
        btnSearch.addActionListener(e -> searchProducts());
        txtSearch.addActionListener(e -> searchProducts());
        
        topPanel.add(lblSearch);
        topPanel.add(txtSearch);
        topPanel.add(btnSearch);
        
        // Products Panel
        productPanel = new JPanel();
        productPanel.setLayout(new BoxLayout(productPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(productPanel);
        
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        
        loadProducts();
        setVisible(true);
    }
    
    private void loadProducts() {
        productPanel.removeAll();
        List<Product> products = productDAO.getAllProducts();
        
        for (Product product : products) {
            productPanel.add(createProductCard(product));
        }
        
        productPanel.revalidate();
        productPanel.repaint();
    }
    
    private void searchProducts() {
        String keyword = txtSearch.getText().trim();
        productPanel.removeAll();
        
        List<Product> products = keyword.isEmpty() ? 
            productDAO.getAllProducts() : 
            productDAO.searchProducts(keyword);
        
        for (Product product : products) {
            productPanel.add(createProductCard(product));
        }
        
        productPanel.revalidate();
        productPanel.repaint();
    }
    
    private JPanel createProductCard(Product product) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setMaximumSize(new Dimension(750, 100));
        
        // Product Info
        JPanel infoPanel = new JPanel(new GridLayout(3, 1));
        JLabel lblTitle = new JLabel(product.getTitle());
        lblTitle.setFont(new Font("Arial", Font.BOLD, 14));
        JLabel lblPrice = new JLabel(String.format("Price: $%.2f", product.getPrice()));
        JLabel lblStock = new JLabel("In Stock: " + product.getQuantity());
        
        infoPanel.add(lblTitle);
        infoPanel.add(lblPrice);
        infoPanel.add(lblStock);
        
        // Order Button
        JButton btnOrder = new JButton("Order");
        btnOrder.addActionListener(e -> orderProduct(product));
        
        card.add(infoPanel, BorderLayout.CENTER);
        card.add(btnOrder, BorderLayout.EAST);
        
        return card;
    }
    
    private void orderProduct(Product product) {
        String quantityStr = JOptionPane.showInputDialog(this, 
            "Enter quantity for " + product.getTitle() + ":", 
            "1");
        
        if (quantityStr != null) {
            try {
                int quantity = Integer.parseInt(quantityStr);
                if (quantity > product.getQuantity()) {
                    JOptionPane.showMessageDialog(this, "Insufficient stock!");
                } else if (quantity > 0) {
                    JOptionPane.showMessageDialog(this, 
                        "Added to cart (Feature coming soon!)");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid quantity!");
            }
        }
    }
}
