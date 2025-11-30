package src.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import src.dao.OrderDAO;
import src.model.Order;
import src.model.User;

public class OrderHistoryPanel extends JPanel {
    private User currentUser;
    private JTable orderTable;
    private DefaultTableModel tableModel;
    private OrderDAO orderDAO;

    public OrderHistoryPanel(User user) {
        this.currentUser = user;
        this.orderDAO = new OrderDAO();
        
        setLayout(new BorderLayout());

        // Table
        String[] columns = {"Order ID", "Date", "Total", "Status", "Shipping Address"};
        tableModel = new DefaultTableModel(columns, 0);
        orderTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(orderTable);
        add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton btnRefresh = new JButton("Refresh");
        JButton btnViewDetails = new JButton("View Details");
        
        btnRefresh.addActionListener(e -> loadOrders());
        btnViewDetails.addActionListener(e -> viewOrderDetails());
        
        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnViewDetails);
        add(buttonPanel, BorderLayout.SOUTH);

        loadOrders();
    }

    private void loadOrders() {
        tableModel.setRowCount(0);
        List<Order> orders = orderDAO.getOrdersByUserId(currentUser.getUserID());
        
        for (Order order : orders) {
            tableModel.addRow(new Object[]{
                order.getOrderID(),
                order.getOrderDate(),
                String.format("$%.2f", order.getTotalAmount()),
                order.getStatus(),
                order.getShippingAddress()
            });
        }
    }

    private void viewOrderDetails() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an order to view details.");
            return;
        }

        int orderID = (int) tableModel.getValueAt(selectedRow, 0);
        Order order = orderDAO.getOrderById(orderID);
        
        if (order != null) {
            StringBuilder details = new StringBuilder();
            details.append("Order ID: ").append(order.getOrderID()).append("\n");
            details.append("Date: ").append(order.getOrderDate()).append("\n");
            details.append("Status: ").append(order.getStatus()).append("\n");
            details.append("Shipping Address: ").append(order.getShippingAddress()).append("\n\n");
            details.append("Items:\n");
            
            order.getItems().forEach(item -> {
                details.append(String.format("- %s (x%d) - $%.2f\n", 
                    item.getProductName(), 
                    item.getQuantity(), 
                    item.getSubtotal()));
            });
            
            details.append("\nTotal: $").append(String.format("%.2f", order.getTotalAmount()));
            
            JOptionPane.showMessageDialog(this, details.toString(), 
                "Order Details", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
