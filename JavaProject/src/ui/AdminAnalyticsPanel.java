package src.ui;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.table.*;
import src.dao.CategoryDAO;
import src.dao.OrderDAO;
import src.dao.ProductDAO;
import src.model.Category;
import src.model.Order;
import src.model.Product;

public class AdminAnalyticsPanel extends JPanel {
    private ProductDAO productDAO;
    private OrderDAO orderDAO;
    private CategoryDAO categoryDAO;
    private JComboBox<String> cmbCategory;
    private JComboBox<String> cmbChartType;
    private JLabel lblTotalSales;
    private JLabel lblTotalRevenue;
    private JLabel lblTotalInventory;
    private JTable productsTable;
    private DefaultTableModel tableModel;
    private ChartPanel chartPanel;

    public AdminAnalyticsPanel() {
        this.productDAO = new ProductDAO();
        this.orderDAO = new OrderDAO();
        this.categoryDAO = new CategoryDAO();

        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(15, 30, 60));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top: Filter Panel
        JPanel filterPanel = createFilterPanel();
        add(filterPanel, BorderLayout.NORTH);

        // Middle: Stats Panel and Chart
        JPanel statsChartPanel = new JPanel(new BorderLayout(10, 10));
        statsChartPanel.setBackground(new Color(15, 30, 60));
        
        JPanel statsPanel = createStatsPanel();
        statsChartPanel.add(statsPanel, BorderLayout.NORTH);
        
        chartPanel = new ChartPanel();
        chartPanel.setPreferredSize(new Dimension(0, 250));
        statsChartPanel.add(chartPanel, BorderLayout.CENTER);
        
        add(statsChartPanel, BorderLayout.CENTER);

        // Bottom: Products Table
        JPanel tablePanel = createTablePanel();
        tablePanel.setPreferredSize(new Dimension(0, 200));
        add(tablePanel, BorderLayout.SOUTH);

        refreshAnalytics();
    }

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBackground(new Color(20, 40, 80));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JLabel titleLabel = new JLabel("📊 Sales & Inventory Analytics");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel);

        panel.add(new JLabel("Filter by Category:"));
        cmbCategory = new JComboBox<>();
        cmbCategory.addItem("All Categories");
        for (Category c : categoryDAO.getMainCategories()) {
            cmbCategory.addItem(c.getName());
        }
        cmbCategory.addActionListener(e -> refreshAnalytics());
        panel.add(cmbCategory);

        // Chart type selector
        panel.add(new JLabel("Chart Type:"));
        cmbChartType = new JComboBox<>(new String[]{"📊 Bar Chart", "🥧 Pie Chart", "📈 Line Chart"});
        cmbChartType.addActionListener(e -> refreshAnalytics());
        panel.add(cmbChartType);

        JButton btnRefresh = new JButton("🔄 Refresh");
        btnRefresh.setBackground(new Color(66, 133, 244));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFocusPainted(false);
        btnRefresh.addActionListener(e -> refreshAnalytics());
        panel.add(btnRefresh);

        return panel;
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 15, 0));
        panel.setBackground(new Color(15, 30, 60));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // Total Sales
        JPanel salesCard = createStatCard("Total Sales", "0", new Color(76, 175, 80));
        lblTotalSales = (JLabel) salesCard.getComponent(1);
        panel.add(salesCard);

        // Total Revenue
        JPanel revenueCard = createStatCard("Total Revenue", "₱0", new Color(33, 150, 243));
        lblTotalRevenue = (JLabel) revenueCard.getComponent(1);
        panel.add(revenueCard);

        // Total Inventory
        JPanel inventoryCard = createStatCard("Total Inventory", "0 units", new Color(255, 152, 0));
        lblTotalInventory = (JLabel) inventoryCard.getComponent(1);
        panel.add(inventoryCard);

        return panel;
    }

    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(color, 2));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        card.add(titleLabel, BorderLayout.NORTH);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 20));
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(JLabel.CENTER);
        valueLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(15, 30, 60));

        JLabel tableTitle = new JLabel("📦 Product Inventory & Sales");
        tableTitle.setFont(new Font("Arial", Font.BOLD, 12));
        tableTitle.setForeground(Color.WHITE);
        tableTitle.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel.add(tableTitle, BorderLayout.NORTH);

        String[] cols = {"ID", "Product Name", "Category", "Stock", "Price", "Cost", "Profit"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        productsTable = new JTable(tableModel);
        productsTable.setBackground(Color.WHITE);
        productsTable.setForeground(Color.BLACK);
        productsTable.getTableHeader().setBackground(new Color(33, 150, 243));
        productsTable.getTableHeader().setForeground(Color.WHITE);
        productsTable.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(productsTable);
        scrollPane.setPreferredSize(new Dimension(0, 200));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void refreshAnalytics() {
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

        List<Product> products = productDAO.getAllProducts();
        final Integer finalCategoryID = categoryID;
        List<Product> filteredProducts = products.stream()
            .filter(p -> finalCategoryID == null || p.getCategoryID() == finalCategoryID)
            .collect(Collectors.toList());

        // Calculate stats
        int totalSales = 0;
        double totalRevenue = 0;
        int totalInventory = 0;

        for (Product p : filteredProducts) {
            // Assume each product generates sales (in real app, query from orders)
            totalRevenue += p.getPrice() * p.getQuantity();
            totalInventory += p.getQuantity();
            totalSales++;
        }

        lblTotalSales.setText(String.valueOf(filteredProducts.size()));
        lblTotalRevenue.setText("₱" + String.format("%.0f", totalRevenue));
        lblTotalInventory.setText(totalInventory + " units");

        // Update chart based on selected type
        String chartType = (String) cmbChartType.getSelectedItem();
        if (chartType.contains("Pie")) {
            chartPanel.setPieChartData(filteredProducts);
        } else if (chartType.contains("Line")) {
            chartPanel.setLineChartData(orderDAO.getAllOrders());
        } else {
            chartPanel.setProducts(filteredProducts);
        }

        // Update table
        tableModel.setRowCount(0);
        for (Product p : filteredProducts) {
            double profit = p.getPrice() - p.getCostPrice();
            tableModel.addRow(new Object[]{
                p.getId(),
                p.getName(),
                p.getCategoryName(),
                p.getQuantity(),
                "₱" + String.format("%.0f", p.getPrice()),
                "₱" + String.format("%.0f", p.getCostPrice()),
                "₱" + String.format("%.0f", profit)
            });
        }
    }

    // Custom Chart Panel supporting Bar, Pie, and Line Charts
    private class ChartPanel extends JPanel {
        private List<Product> products = new ArrayList<>();
        private List<Order> orders = new ArrayList<>();
        private String chartMode = "bar"; // bar, pie, line

        public void setProducts(List<Product> products) {
            this.products = new ArrayList<>(products);
            this.chartMode = "bar";
            repaint();
        }

        public void setPieChartData(List<Product> products) {
            this.products = new ArrayList<>(products);
            this.chartMode = "pie";
            repaint();
        }

        public void setLineChartData(List<Order> orders) {
            this.orders = new ArrayList<>(orders);
            this.chartMode = "line";
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if ("pie".equals(chartMode)) {
                drawPieChart(g2d);
            } else if ("line".equals(chartMode)) {
                drawLineChart(g2d);
            } else {
                drawBarChart(g2d);
            }
        }

        private void drawBarChart(Graphics2D g2d) {
            if (products.isEmpty()) return;

            int width = getWidth();
            int height = getHeight();
            int margin = 50;
            int chartWidth = width - 2 * margin;
            int chartHeight = height - 2 * margin;

            // Draw background
            g2d.setColor(new Color(20, 40, 80));
            g2d.fillRect(0, 0, width, height);

            // Draw title
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            g2d.drawString("📊 Inventory by Product", margin, 25);

            // Draw chart area
            g2d.setColor(new Color(30, 60, 100));
            g2d.fillRect(margin, margin, chartWidth, chartHeight);
            g2d.setColor(new Color(66, 133, 244));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRect(margin, margin, chartWidth, chartHeight);

            // Find max value
            int maxQty = products.stream().mapToInt(Product::getQuantity).max().orElse(1);
            if (maxQty == 0) maxQty = 1;

            // Bar width
            int maxBars = Math.min(10, products.size());
            int barWidth = (chartWidth - 20) / maxBars;
            int spacing = barWidth / 4;
            int actualBarWidth = barWidth - spacing;

            // Draw bars
            for (int i = 0; i < maxBars; i++) {
                Product p = products.get(i);
                int qty = p.getQuantity();
                int barHeight = (int) ((double) qty / maxQty * (chartHeight - 40));

                int x = margin + 10 + i * barWidth;
                int y = margin + chartHeight - 20 - barHeight;

                // Draw bar
                g2d.setColor(new Color(76, 175, 80));
                g2d.fillRect(x, y, actualBarWidth, barHeight);
                g2d.setColor(new Color(66, 133, 244));
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRect(x, y, actualBarWidth, barHeight);

                // Draw label
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.PLAIN, 9));
                String label = p.getName().length() > 8 ? p.getName().substring(0, 8) : p.getName();
                int labelX = x + (actualBarWidth - g2d.getFontMetrics().stringWidth(label)) / 2;
                g2d.drawString(label, labelX, margin + chartHeight - 5);

                // Draw value
                String valueStr = String.valueOf(qty);
                int valueX = x + (actualBarWidth - g2d.getFontMetrics().stringWidth(valueStr)) / 2;
                g2d.drawString(valueStr, valueX, y - 5);
            }

            // Draw Y-axis labels
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.PLAIN, 10));
            for (int i = 0; i <= 5; i++) {
                int val = (maxQty * i) / 5;
                int yPos = margin + chartHeight - 20 - (i * (chartHeight - 40) / 5);
                g2d.drawString(String.valueOf(val), margin - 40, yPos + 4);
            }
        }

        private void drawPieChart(Graphics2D g2d) {
            if (products.isEmpty()) return;

            int width = getWidth();
            int height = getHeight();
            int centerX = width / 2;
            int centerY = height / 2;
            int radius = Math.min(width, height) / 3;

            // Draw background
            g2d.setColor(new Color(20, 40, 80));
            g2d.fillRect(0, 0, width, height);

            // Draw title
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            g2d.drawString("🥧 Inventory Distribution by Category", 20, 25);

            // Group products by category and sum quantities
            Map<String, Integer> categoryInventory = new HashMap<>();
            for (Product p : products) {
                String category = p.getCategoryName() != null ? p.getCategoryName() : "Other";
                categoryInventory.put(category, categoryInventory.getOrDefault(category, 0) + p.getQuantity());
            }

            // Calculate total
            int total = categoryInventory.values().stream().mapToInt(Integer::intValue).sum();
            if (total == 0) return;

            // Colors for pie slices
            Color[] colors = {
                new Color(76, 175, 80), new Color(33, 150, 243), new Color(255, 152, 0),
                new Color(244, 67, 54), new Color(156, 39, 176), new Color(0, 188, 212),
                new Color(205, 220, 57), new Color(63, 81, 181)
            };

            int colorIdx = 0;
            double currentAngle = 0;
            int sliceIndex = 0;

            for (Map.Entry<String, Integer> entry : categoryInventory.entrySet()) {
                double sliceAngle = 360.0 * entry.getValue() / total;
                g2d.setColor(colors[colorIdx % colors.length]);
                g2d.fillArc(centerX - radius, centerY - radius, radius * 2, radius * 2, (int) currentAngle, (int) sliceAngle);

                // Draw border
                g2d.setColor(new Color(20, 40, 80));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawArc(centerX - radius, centerY - radius, radius * 2, radius * 2, (int) currentAngle, (int) sliceAngle);

                // Draw label
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 11));
                double labelAngle = Math.toRadians(currentAngle + sliceAngle / 2);
                int labelX = (int) (centerX + Math.cos(labelAngle) * (radius + 40));
                int labelY = (int) (centerY + Math.sin(labelAngle) * (radius + 40));
                String label = entry.getKey() + ": " + entry.getValue();
                g2d.drawString(label, labelX - 30, labelY);

                currentAngle += sliceAngle;
                colorIdx++;
                sliceIndex++;
            }
        }

        private void drawLineChart(Graphics2D g2d) {
            if (orders.isEmpty()) return;

            int width = getWidth();
            int height = getHeight();
            int margin = 50;
            int chartWidth = width - 2 * margin;
            int chartHeight = height - 2 * margin;

            // Draw background
            g2d.setColor(new Color(20, 40, 80));
            g2d.fillRect(0, 0, width, height);

            // Draw title
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            g2d.drawString("📈 Revenue Over Time", margin, 25);

            // Group orders by date and sum revenue
            Map<String, Double> dailyRevenue = new HashMap<>();
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
            for (Order order : orders) {
                if (order.getOrderDate() != null) {
                    String dateKey = sdf.format(new Date(order.getOrderDate().getTime()));
                    dailyRevenue.put(dateKey, dailyRevenue.getOrDefault(dateKey, 0.0) + order.getTotalAmount());
                }
            }

            if (dailyRevenue.isEmpty()) {
                g2d.setColor(Color.WHITE);
                g2d.drawString("No order data available", margin + 20, margin + 100);
                return;
            }

            // Draw chart area
            g2d.setColor(new Color(30, 60, 100));
            g2d.fillRect(margin, margin, chartWidth, chartHeight);
            g2d.setColor(new Color(66, 133, 244));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRect(margin, margin, chartWidth, chartHeight);

            // Find max revenue
            double maxRevenue = dailyRevenue.values().stream().mapToDouble(Double::doubleValue).max().orElse(1);
            if (maxRevenue == 0) maxRevenue = 1;

            // Draw data points and lines
            List<String> dates = new ArrayList<>(dailyRevenue.keySet());
            dates.sort(String::compareTo);
            int maxPoints = Math.min(15, dates.size());
            int pointSpacing = (chartWidth - 40) / Math.max(1, maxPoints - 1);

            double[] xPoints = new double[maxPoints];
            double[] yPoints = new double[maxPoints];

            for (int i = 0; i < maxPoints; i++) {
                String date = dates.get(i);
                double revenue = dailyRevenue.get(date);
                int pointHeight = (int) ((revenue / maxRevenue) * (chartHeight - 40));

                xPoints[i] = margin + 20 + i * pointSpacing;
                yPoints[i] = margin + chartHeight - 20 - pointHeight;
            }

            // Draw line
            g2d.setColor(new Color(33, 150, 243));
            g2d.setStroke(new BasicStroke(2));
            for (int i = 0; i < maxPoints - 1; i++) {
                g2d.drawLine((int) xPoints[i], (int) yPoints[i], (int) xPoints[i + 1], (int) yPoints[i + 1]);
            }

            // Draw points
            for (int i = 0; i < maxPoints; i++) {
                g2d.setColor(new Color(76, 175, 80));
                g2d.fillOval((int) xPoints[i] - 4, (int) yPoints[i] - 4, 8, 8);
                g2d.setColor(Color.WHITE);
                g2d.drawOval((int) xPoints[i] - 4, (int) yPoints[i] - 4, 8, 8);

                // Draw date label
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.PLAIN, 8));
                g2d.drawString(dates.get(i), (int) xPoints[i] - 12, margin + chartHeight + 5);
            }

            // Draw Y-axis labels
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.PLAIN, 10));
            for (int i = 0; i <= 5; i++) {
                double val = (maxRevenue * i) / 5;
                int yPos = margin + chartHeight - 20 - (i * (chartHeight - 40) / 5);
                g2d.drawString("₱" + String.format("%.0f", val), margin - 50, yPos + 4);
            }
        }
    }
}
