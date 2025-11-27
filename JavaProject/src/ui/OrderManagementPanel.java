package src.ui;

import src.dao.OrderDAO;
import src.model.Order;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class OrderManagementPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private OrderDAO orderDAO;
    private JComboBox<String> cmbStatus;

    public OrderManagementPanel() {
        orderDAO = new OrderDAO();
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel header = new JLabel("Order Management");
        header.setFont(new Font("Arial", Font.BOLD, 20));
        add(header, BorderLayout.NORTH);

        model = new DefaultTableModel(
            new String[]{"Order ID", "Customer", "Date", "Status", "Total", "Address"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        loadOrders();
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout());
        
        cmbStatus = new JComboBox<>(new String[]{"Pending", "Processing", "Shipped", "Delivered", "Cancelled"});
        JButton btnUpdateStatus = new JButton("Update Status");
        JButton btnViewDetails = new JButton("View Details");
        JButton btnRefresh = new JButton("Refresh");

        btnUpdateStatus.addActionListener(e -> updateOrderStatus());
        btnViewDetails.addActionListener(e -> viewOrderDetails());
        btnRefresh.addActionListener(e -> loadOrders());

        actionPanel.add(new JLabel("Status:"));
        actionPanel.add(cmbStatus);
        actionPanel.add(btnUpdateStatus);
        actionPanel.add(btnViewDetails);
        actionPanel.add(btnRefresh);

        add(actionPanel, BorderLayout.SOUTH);
    }

    private void loadOrders() {
        model.setRowCount(0);
        List<Order> orders = orderDAO.getAllOrders();
        for (Order o : orders) {
            model.addRow(new Object[]{
                o.getOrderID(),
                o.getUserName(),
                o.getOrderDate(),
                o.getStatus(),
                String.format("₱%.2f", o.getTotalAmount()),
                o.getShippingAddress()
            });
        }
    }

    private void updateOrderStatus() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select an order!");
            return;
        }
        int orderID = (int) model.getValueAt(row, 0);
        String newStatus = (String) cmbStatus.getSelectedItem();
        
        if (orderDAO.updateOrderStatus(orderID, newStatus)) {
            JOptionPane.showMessageDialog(this, "Status updated!");
            loadOrders();
        }
    }

    private void viewOrderDetails() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select an order!");
            return;
        }
        int orderID = (int) model.getValueAt(row, 0);
        Order order = orderDAO.getOrderById(orderID);
        
        StringBuilder details = new StringBuilder();
        details.append("Order ID: ").append(order.getOrderID()).append("\n");
        details.append("Customer: ").append(order.getUserName()).append("\n");
        details.append("Date: ").append(order.getOrderDate()).append("\n");
        details.append("Status: ").append(order.getStatus()).append("\n");
        details.append("Shipping Address: ").append(order.getShippingAddress()).append("\n\n");
        details.append("Items:\n");
        
        order.getItems().forEach(item -> {
            details.append(String.format("  • %s - Qty: %d - ₱%.2f\n", 
                item.getProductName(), item.getQuantity(), item.getSubtotal()));
        });
        
        details.append(String.format("\nTotal: ₱%.2f", order.getTotalAmount()));
        
        JOptionPane.showMessageDialog(this, details.toString(), 
            "Order Details", JOptionPane.INFORMATION_MESSAGE);
    }
}
