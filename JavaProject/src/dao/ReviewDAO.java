package src.dao;

import java.sql.*;
import java.util.*;
import src.db.DatabaseConnection;
import src.model.Review;

public class ReviewDAO {

    // CREATE - Add new review
    public boolean addReview(Review review) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO reviews (productID, userID, rating, comment, reviewDate) " +
                "VALUES (?, ?, ?, ?, NOW())");
            ps.setInt(1, review.getProductID());
            ps.setInt(2, review.getUserID());
            ps.setInt(3, review.getRating());
            ps.setString(4, review.getComment());
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // READ - Get all reviews
    public List<Review> getAllReviews() {
        List<Review> reviews = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT r.*, u.name as userName, p.name as productName " +
                "FROM reviews r " +
                "JOIN users u ON r.userID = u.userID " +
                "JOIN products p ON r.productID = p.id " +
                "ORDER BY r.reviewDate DESC");
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Review review = new Review(
                    rs.getInt("reviewID"),
                    rs.getInt("productID"),
                    rs.getInt("userID"),
                    rs.getInt("rating"),
                    rs.getString("comment"),
                    rs.getTimestamp("reviewDate")
                );
                review.setUserName(rs.getString("userName"));
                review.setProductName(rs.getString("productName"));
                reviews.add(review);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reviews;
    }

    // READ - Get reviews by product ID
    public List<Review> getReviewsByProductId(int productID) {
        List<Review> reviews = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT r.*, u.name as userName FROM reviews r " +
                "JOIN users u ON r.userID = u.userID " +
                "WHERE r.productID=? ORDER BY r.reviewDate DESC");
            ps.setInt(1, productID);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Review review = new Review(
                    rs.getInt("reviewID"),
                    rs.getInt("productID"),
                    rs.getInt("userID"),
                    rs.getInt("rating"),
                    rs.getString("comment"),
                    rs.getTimestamp("reviewDate")
                );
                review.setUserName(rs.getString("userName"));
                reviews.add(review);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reviews;
    }

    // READ - Get reviews by user ID
    public List<Review> getReviewsByUserId(int userID) {
        List<Review> reviews = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT r.*, p.name as productName FROM reviews r " +
                "JOIN products p ON r.productID = p.id " +
                "WHERE r.userID=? ORDER BY r.reviewDate DESC");
            ps.setInt(1, userID);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Review review = new Review(
                    rs.getInt("reviewID"),
                    rs.getInt("productID"),
                    rs.getInt("userID"),
                    rs.getInt("rating"),
                    rs.getString("comment"),
                    rs.getTimestamp("reviewDate")
                );
                review.setProductName(rs.getString("productName"));
                reviews.add(review);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reviews;
    }

    // READ - Get review by ID
    public Review getReviewById(int reviewID) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT r.*, u.name as userName, p.name as productName " +
                "FROM reviews r " +
                "JOIN users u ON r.userID = u.userID " +
                "JOIN products p ON r.productID = p.id " +
                "WHERE r.reviewID=?");
            ps.setInt(1, reviewID);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                Review review = new Review(
                    rs.getInt("reviewID"),
                    rs.getInt("productID"),
                    rs.getInt("userID"),
                    rs.getInt("rating"),
                    rs.getString("comment"),
                    rs.getTimestamp("reviewDate")
                );
                review.setUserName(rs.getString("userName"));
                review.setProductName(rs.getString("productName"));
                return review;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // UPDATE - Update review
    public boolean updateReview(Review review) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "UPDATE reviews SET rating=?, comment=? WHERE reviewID=?");
            ps.setInt(1, review.getRating());
            ps.setString(2, review.getComment());
            ps.setInt(3, review.getReviewID());
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // DELETE - Delete review
    public boolean deleteReview(int reviewID) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM reviews WHERE reviewID=?");
            ps.setInt(1, reviewID);
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get average rating for a product
    public double getAverageRating(int productID) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT AVG(rating) as avgRating FROM reviews WHERE productID=?");
            ps.setInt(1, productID);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("avgRating");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    // Get review count for a product
    public int getReviewCount(int productID) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT COUNT(*) as count FROM reviews WHERE productID=?");
            ps.setInt(1, productID);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
