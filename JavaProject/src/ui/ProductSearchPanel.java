package src.ui;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import src.dao.CategoryDAO;
import src.model.Category;
import src.model.Product;

/**
 * ProductSearchPanel - Product search and filtering interface
 * Allows customers to search, filter, and sort products
 */
public class ProductSearchPanel extends JPanel {
    private CategoryDAO categoryDAO;
    private JPanel gridPanel;
    private JTextField txtSearch;
    private JComboBox<String> cmbCategory, cmbSort;
    private JSlider sliderMinPrice, sliderMaxPrice;
    private JSlider sliderMinRating;
    private JLabel lblPriceRange, lblRatingRange;

    public ProductSearchPanel() {
        categoryDAO = new CategoryDAO();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(new Color(245, 245, 245));

        // Top: Search and Filter Panel
        add(createFilterPanel(), BorderLayout.NORTH);

        // Center: Product Grid
        gridPanel = new JPanel(new GridLayout(0, 3, 15, 15));
        gridPanel.setBackground(new Color(245, 245, 245));
        JScrollPane gridScroll = new JScrollPane(gridPanel);
        gridScroll.getViewport().setBackground(new Color(245, 245, 245));
        add(gridScroll, BorderLayout.CENTER);

        displayAllProducts();
    }

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("🔍 Search & Filter"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Search box
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        panel.add(new JLabel("Search:"), gbc);
        txtSearch = new JTextField(20);
        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(txtSearch, gbc);
        JButton btnSearch = new JButton("🔎 Search");
        btnSearch.addActionListener(e -> performSearch());
        gbc.gridx = 2;
        gbc.weightx = 0;
        panel.add(btnSearch, gbc);

        // Category filter
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        panel.add(new JLabel("Category:"), gbc);
        cmbCategory = new JComboBox<>();
        cmbCategory.addItem("All Categories");
        for (Category c : categoryDAO.getMainCategories()) {
            cmbCategory.addItem(c.getName());
        }
        cmbCategory.addActionListener(e -> applyFilters());
        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(cmbCategory, gbc);

        // Sort
        gbc.gridx = 2;
        gbc.weightx = 0;
        panel.add(new JLabel("Sort By:"), gbc);
        cmbSort = new JComboBox<>(new String[]{"Default", "Price Low to High", "Price High to Low", "Rating"});
        cmbSort.addActionListener(e -> applySorting());
        gbc.gridx = 3;
        gbc.weightx = 1;
        panel.add(cmbSort, gbc);

        // Price range slider
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        panel.add(new JLabel("Price Range:"), gbc);
        
        JPanel pricePanel = new JPanel(new GridLayout(1, 2, 10, 0));
        sliderMinPrice = new JSlider(0, 50000, 0);
        sliderMaxPrice = new JSlider(0, 50000, 50000);
        lblPriceRange = new JLabel("₱0 - ₱50000");
        lblPriceRange.setFont(new Font("Arial", Font.BOLD, 11));
        
        sliderMinPrice.addChangeListener(e -> {
            if (sliderMinPrice.getValue() > sliderMaxPrice.getValue()) {
                sliderMinPrice.setValue(sliderMaxPrice.getValue());
            }
            updatePriceLabel();
            applyFilters();
        });
        sliderMaxPrice.addChangeListener(e -> {
            if (sliderMaxPrice.getValue() < sliderMinPrice.getValue()) {
                sliderMaxPrice.setValue(sliderMinPrice.getValue());
            }
            updatePriceLabel();
            applyFilters();
        });
        
        pricePanel.add(sliderMinPrice);
        pricePanel.add(sliderMaxPrice);
        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(pricePanel, gbc);
        gbc.gridx = 2;
        gbc.gridwidth = 2;
        panel.add(lblPriceRange, gbc);
        gbc.gridwidth = 1;

        // Rating filter
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        panel.add(new JLabel("Min Rating:"), gbc);
        sliderMinRating = new JSlider(0, 50, 0); // 0-5 stars * 10
        lblRatingRange = new JLabel("⭐ 0.0");
        lblRatingRange.setFont(new Font("Arial", Font.BOLD, 11));
        sliderMinRating.addChangeListener(e -> {
            updateRatingLabel();
            applyFilters();
        });
        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(sliderMinRating, gbc);
        gbc.gridx = 2;
        panel.add(lblRatingRange, gbc);

        // Clear button
        JButton btnClear = new JButton("Clear All");
        btnClear.addActionListener(e -> clearFilters());
        gbc.gridx = 3;
        gbc.gridy = 3;
        gbc.weightx = 0;
        panel.add(btnClear, gbc);

        return panel;
    }

    private void performSearch() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            displayAllProducts();
            return;
        }
        // Stub - would need ProductDAO integration
        displayProducts(new ArrayList<>());
    }

    private void applyFilters() {
        // Stub - would need ProductDAO integration
        displayProducts(new ArrayList<>());
    }

    private void applySorting() {
        // Stub - would need ProductDAO integration
        displayAllProducts();
    }

    private void clearFilters() {
        txtSearch.setText("");
        cmbCategory.setSelectedIndex(0);
        cmbSort.setSelectedIndex(0);
        sliderMinPrice.setValue(0);
        sliderMaxPrice.setValue(50000);
        sliderMinRating.setValue(0);
        displayAllProducts();
    }

    private void updatePriceLabel() {
        lblPriceRange.setText("₱" + sliderMinPrice.getValue() + " - ₱" + sliderMaxPrice.getValue());
    }

    private void updateRatingLabel() {
        double rating = sliderMinRating.getValue() / 10.0;
        lblRatingRange.setText("⭐ " + String.format("%.1f", rating));
    }

    private void displayAllProducts() {
        // Stub - would need ProductDAO integration
        displayProducts(new ArrayList<>());
    }

    private void displayProducts(List<Product> products) {
        gridPanel.removeAll();
        if (products.isEmpty()) {
            JLabel noResults = new JLabel("No products found");
            noResults.setHorizontalAlignment(JLabel.CENTER);
            noResults.setFont(new Font("Arial", Font.PLAIN, 14));
            gridPanel.add(noResults);
        } else {
            for (Product p : products) {
                gridPanel.add(createProductCard(p));
            }
        }
        gridPanel.revalidate();
        gridPanel.repaint();
    }

    private JPanel createProductCard(Product p) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setPreferredSize(new Dimension(220, 340));
        card.setMaximumSize(new Dimension(220, 340));
        card.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        card.setBackground(Color.WHITE);

        // Image
        JPanel imgPanel = new JPanel(new BorderLayout());
        imgPanel.setBackground(new Color(240, 240, 240));
        imgPanel.setPreferredSize(new Dimension(220, 180));
        JLabel imgLabel = new JLabel("📷");
        imgLabel.setHorizontalAlignment(JLabel.CENTER);
        imgLabel.setFont(new Font("Arial", Font.BOLD, 40));
        imgPanel.add(imgLabel, BorderLayout.CENTER);
        card.add(imgPanel, BorderLayout.NORTH);

        // Info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JLabel nameLabel = new JLabel(p.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 12));
        infoPanel.add(nameLabel);

        JLabel priceLabel = new JLabel("₱" + String.format("%.2f", p.getPrice()));
        priceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        priceLabel.setForeground(new Color(220, 53, 69));
        infoPanel.add(priceLabel);

        JLabel ratingLabel = new JLabel("⭐ " + String.format("%.1f", p.getAverageRating()) + " (" + p.getTotalReviews() + ")");
        ratingLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        infoPanel.add(ratingLabel);

        String stockText = p.getQuantity() > 0 ? "✓ Stock: " + p.getQuantity() : "✗ Out of Stock";
        JLabel stockLabel = new JLabel(stockText);
        stockLabel.setFont(new Font("Arial", Font.BOLD, 10));
        stockLabel.setForeground(p.getQuantity() > 0 ? new Color(0, 150, 0) : new Color(220, 53, 69));
        infoPanel.add(stockLabel);

        card.add(infoPanel, BorderLayout.CENTER);
        return card;
    }
}
