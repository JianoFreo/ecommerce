package src.ui;

import src.dao.ReviewDAO;
import src.model.Review;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class ReviewManagementPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private ReviewDAO reviewDAO;

    public ReviewManagementPanel() {
        reviewDAO = new ReviewDAO();
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel header = new JLabel("Review Management");
        header.setFont(new Font("Arial", Font.BOLD, 20));
        add(header, BorderLayout.NORTH);

        model = new DefaultTableModel(
            new String[]{"Review ID", "Product", "Customer", "Rating", "Comment", "Date"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        loadReviews();
        
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout());
        JButton btnDelete = new JButton("Delete Review");
        JButton btnRefresh = new JButton("Refresh");
        JButton btnViewDetails = new JButton("View Details");

        btnDelete.addActionListener(e -> deleteReview());
        btnRefresh.addActionListener(e -> loadReviews());
        btnViewDetails.addActionListener(e -> viewReviewDetails());

        actionPanel.add(btnDelete);
        actionPanel.add(btnViewDetails);
        actionPanel.add(btnRefresh);

        add(actionPanel, BorderLayout.SOUTH);
    }

    private void loadReviews() {
        model.setRowCount(0);
        List<Review> reviews = reviewDAO.getAllReviews();
        for (Review r : reviews) {
            model.addRow(new Object[]{
                r.getReviewID(),
                r.getProductName(),
                r.getUserName(),
                r.getStarDisplay() + " (" + r.getRating() + "/5)",
                r.getComment().length() > 50 ? 
                    r.getComment().substring(0, 47) + "..." : r.getComment(),
                r.getReviewDate()
            });
        }
    }

    private void deleteReview() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a review to delete!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Delete this review?", "Confirm", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int reviewID = (int) model.getValueAt(row, 0);
            if (reviewDAO.deleteReview(reviewID)) {
                JOptionPane.showMessageDialog(this, "Review deleted!");
                loadReviews();
            }
        }
    }

    private void viewReviewDetails() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a review!");
            return;
        }

        int reviewID = (int) model.getValueAt(row, 0);
        Review review = reviewDAO.getReviewById(reviewID);

        String details = String.format(
            "Review ID: %d\n" +
            "Product: %s\n" +
            "Customer: %s\n" +
            "Rating: %s (%d/5)\n" +
            "Date: %s\n\n" +
            "Comment:\n%s",
            review.getReviewID(),
            review.getProductName(),
            review.getUserName(),
            review.getStarDisplay(),
            review.getRating(),
            review.getReviewDate(),
            review.getComment()
        );

        JOptionPane.showMessageDialog(this, details, 
            "Review Details", JOptionPane.INFORMATION_MESSAGE);
    }
}
