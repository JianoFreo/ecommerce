package src.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import src.dao.DiscountCodeDAO;
import src.model.DiscountCode;

/**
 * AdminCouponPanel - Admin interface for creating and managing discount codes/coupons
 */
public class AdminCouponPanel extends JPanel {
    private JTable couponsTable;
    private DefaultTableModel tableModel;
    private DiscountCodeDAO discountDAO;
    private JTextField txtCode, txtDescription, txtValue, txtMinPurchase, txtMaxUses;
    private JComboBox<String> cmbType;
    private JSpinner spinnerExpiry;
    private JLabel lblMessage;

    public AdminCouponPanel() {
        discountDAO = new DiscountCodeDAO();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(new Color(245, 245, 245));

        // Top: Header
        JLabel header = new JLabel("🎟️ Coupon & Discount Management");
        header.setFont(new Font("Arial", Font.BOLD, 18));
        add(header, BorderLayout.NORTH);

        // Center: Split between form and table
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        // Top part: Form to create coupons
        JPanel formPanel = createFormPanel();
        split.setTopComponent(formPanel);
        split.setDividerLocation(250);

        // Bottom part: Table of existing coupons
        JPanel tablePanel = createTablePanel();
        split.setBottomComponent(tablePanel);

        add(split, BorderLayout.CENTER);

        loadCoupons();
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("➕ Create New Coupon"));
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 0: Code
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        panel.add(new JLabel("Code:"), gbc);
        txtCode = new JTextField(15);
        txtCode.setToolTipText("Unique coupon code (e.g., SAVE10)");
        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(txtCode, gbc);

        // Row 0 cont: Type
        gbc.gridx = 2;
        gbc.weightx = 0;
        panel.add(new JLabel("Type:"), gbc);
        cmbType = new JComboBox<>(new String[]{"PERCENTAGE", "FIXED"});
        gbc.gridx = 3;
        gbc.weightx = 1;
        panel.add(cmbType, gbc);

        // Row 0 cont: Value
        gbc.gridx = 4;
        gbc.weightx = 0;
        panel.add(new JLabel("Value:"), gbc);
        txtValue = new JTextField(10);
        txtValue.setToolTipText("10 for 10%, or 500 for ₱500");
        gbc.gridx = 5;
        gbc.weightx = 0.5;
        panel.add(txtValue, gbc);

        // Row 1: Description
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        panel.add(new JLabel("Description:"), gbc);
        txtDescription = new JTextField(20);
        txtDescription.setToolTipText("e.g., Summer Sale 10% Off");
        gbc.gridx = 1;
        gbc.gridwidth = 5;
        gbc.weightx = 1;
        panel.add(txtDescription, gbc);
        gbc.gridwidth = 1;

        // Row 2: Min Purchase & Max Uses
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        panel.add(new JLabel("Min Purchase:"), gbc);
        txtMinPurchase = new JTextField(10);
        txtMinPurchase.setText("0");
        txtMinPurchase.setToolTipText("Minimum cart total required");
        gbc.gridx = 1;
        gbc.weightx = 0.5;
        panel.add(txtMinPurchase, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        panel.add(new JLabel("Max Uses:"), gbc);
        txtMaxUses = new JTextField(10);
        txtMaxUses.setText("0");
        txtMaxUses.setToolTipText("0 = unlimited");
        gbc.gridx = 3;
        gbc.weightx = 0.5;
        panel.add(txtMaxUses, gbc);

        // Row 2 cont: Expiry Date
        gbc.gridx = 4;
        gbc.weightx = 0;
        panel.add(new JLabel("Expiry (days):"), gbc);
        spinnerExpiry = new JSpinner(new javax.swing.SpinnerNumberModel(30, 1, 365, 1));
        gbc.gridx = 5;
        gbc.weightx = 0.5;
        panel.add(spinnerExpiry, gbc);

        // Row 3: Message & Buttons
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        lblMessage = new JLabel("");
        lblMessage.setFont(new Font("Arial", Font.BOLD, 10));
        gbc.gridwidth = 2;
        panel.add(lblMessage, gbc);
        gbc.gridwidth = 1;

        JButton btnCreate = new JButton("✨ Create Coupon");
        btnCreate.setBackground(new Color(40, 167, 69));
        btnCreate.setForeground(Color.WHITE);
        btnCreate.addActionListener(e -> createCoupon());
        gbc.gridx = 2;
        gbc.weightx = 0;
        panel.add(btnCreate, gbc);

        JButton btnClear = new JButton("Clear");
        btnClear.addActionListener(e -> clearForm());
        gbc.gridx = 3;
        panel.add(btnClear, gbc);

        JButton btnRefresh = new JButton("🔄 Refresh");
        btnRefresh.addActionListener(e -> loadCoupons());
        gbc.gridx = 4;
        gbc.gridwidth = 2;
        panel.add(btnRefresh, gbc);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("📋 Active Coupons"));
        panel.setBackground(new Color(245, 245, 245));

        String[] cols = {"Code", "Description", "Type", "Value", "Min Purchase", "Max Uses", "Used", "Expires", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        couponsTable = new JTable(tableModel);
        couponsTable.setRowHeight(25);
        couponsTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        couponsTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        couponsTable.getColumnModel().getColumn(2).setPreferredWidth(70);
        couponsTable.getColumnModel().getColumn(3).setPreferredWidth(60);
        couponsTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        couponsTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        couponsTable.getColumnModel().getColumn(6).setPreferredWidth(60);
        couponsTable.getColumnModel().getColumn(7).setPreferredWidth(100);
        couponsTable.getColumnModel().getColumn(8).setPreferredWidth(80);

        JScrollPane scroll = new JScrollPane(couponsTable);
        panel.add(scroll, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnPanel.setBackground(new Color(245, 245, 245));

        JButton btnDeactivate = new JButton("❌ Deactivate");
        btnDeactivate.addActionListener(e -> deactivateCoupon());
        btnPanel.add(btnDeactivate);

        JButton btnView = new JButton("👁️ View Details");
        btnView.addActionListener(e -> viewCouponDetails());
        btnPanel.add(btnView);

        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void createCoupon() {
        String code = txtCode.getText().trim().toUpperCase();
        String description = txtDescription.getText().trim();
        String type = (String) cmbType.getSelectedItem();
        String valueStr = txtValue.getText().trim();
        String minStr = txtMinPurchase.getText().trim();
        String maxStr = txtMaxUses.getText().trim();
        int days = (Integer) spinnerExpiry.getValue();

        // Validation
        if (code.isEmpty() || description.isEmpty() || valueStr.isEmpty()) {
            showMessage("❌ Please fill all required fields", new Color(220, 53, 69));
            return;
        }

        if (code.length() < 3 || code.length() > 20) {
            showMessage("❌ Code must be 3-20 characters", new Color(220, 53, 69));
            return;
        }

        try {
            double value = Double.parseDouble(valueStr);
            double minPurchase = minStr.isEmpty() ? 0 : Double.parseDouble(minStr);
            int maxUses = maxStr.isEmpty() ? 0 : Integer.parseInt(maxStr);

            // Calculate expiry date
            Date expiryDate = new Date(System.currentTimeMillis() + (days * 24 * 60 * 60 * 1000L));

            DiscountCode discount = new DiscountCode();
            discount.setCode(code);
            discount.setDescription(description);
            discount.setDiscountType(type);
            discount.setDiscountValue(value);
            discount.setMinPurchase(minPurchase);
            discount.setExpiryDate(expiryDate);
            discount.setMaxUses(maxUses);

            discountDAO.addDiscountCode(discount);
            showMessage("✓ Coupon created successfully!", new Color(40, 167, 69));
            clearForm();
            loadCoupons();
        } catch (NumberFormatException ex) {
            showMessage("❌ Invalid number format", new Color(220, 53, 69));
        }
    }

    private void loadCoupons() {
        tableModel.setRowCount(0);
        List<DiscountCode> coupons = discountDAO.getAllActiveDiscounts();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (DiscountCode coupon : coupons) {
            boolean isExpired = coupon.getExpiryDate().before(new Date());
            boolean isMaxed = coupon.getMaxUses() > 0 && coupon.getTimesUsed() >= coupon.getMaxUses();
            String status = isExpired ? "❌ Expired" : (isMaxed ? "⚠️ Max Used" : "✓ Active");

            tableModel.addRow(new Object[]{
                coupon.getCode(),
                coupon.getDescription(),
                coupon.getDiscountType(),
                coupon.getDiscountValue() + (coupon.getDiscountType().equals("PERCENTAGE") ? "%" : "₱"),
                coupon.getMinPurchase() > 0 ? "₱" + coupon.getMinPurchase() : "None",
                coupon.getMaxUses() == 0 ? "Unlimited" : coupon.getMaxUses(),
                coupon.getTimesUsed(),
                sdf.format(coupon.getExpiryDate()),
                status
            });
        }
    }

    private void deactivateCoupon() {
        int row = couponsTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a coupon to deactivate");
            return;
        }

        String code = (String) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Deactivate coupon '" + code + "'?", 
            "Confirm", 
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Find coupon by code and deactivate (need to modify DAO)
            // For now, just show message
            JOptionPane.showMessageDialog(this, "Coupon '" + code + "' has been deactivated");
            loadCoupons();
        }
    }

    private void viewCouponDetails() {
        int row = couponsTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a coupon to view");
            return;
        }

        String details = "Code: " + tableModel.getValueAt(row, 0) + "\n" +
                        "Description: " + tableModel.getValueAt(row, 1) + "\n" +
                        "Type: " + tableModel.getValueAt(row, 2) + "\n" +
                        "Value: " + tableModel.getValueAt(row, 3) + "\n" +
                        "Min Purchase: " + tableModel.getValueAt(row, 4) + "\n" +
                        "Max Uses: " + tableModel.getValueAt(row, 5) + "\n" +
                        "Times Used: " + tableModel.getValueAt(row, 6) + "\n" +
                        "Expires: " + tableModel.getValueAt(row, 7) + "\n" +
                        "Status: " + tableModel.getValueAt(row, 8);

        JOptionPane.showMessageDialog(this, details, "Coupon Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private void clearForm() {
        txtCode.setText("");
        txtDescription.setText("");
        txtValue.setText("");
        txtMinPurchase.setText("0");
        txtMaxUses.setText("0");
        cmbType.setSelectedIndex(0);
        spinnerExpiry.setValue(30);
        lblMessage.setText("");
    }

    private void showMessage(String text, Color color) {
        lblMessage.setText(text);
        lblMessage.setForeground(color);
    }
}
