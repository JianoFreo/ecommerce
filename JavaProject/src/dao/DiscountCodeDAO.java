package src.dao;

import java.sql.*;
import java.util.*;
import src.db.DatabaseConnection;
import src.model.DiscountCode;

/**
 * DiscountCodeDAO - Manages discount codes and promotions
 * Supports percentage and fixed amount discounts with expiry dates
 */
public class DiscountCodeDAO {
    
    // Add a new discount code
    public void addDiscountCode(DiscountCode discount) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO discount_codes (code, description, discountType, discountValue, minPurchase, expiryDate, maxUses, timesUsed, isActive) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, 0, 1)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, discount.getCode());
            ps.setString(2, discount.getDescription());
            ps.setString(3, discount.getDiscountType()); // PERCENTAGE or FIXED
            ps.setDouble(4, discount.getDiscountValue());
            ps.setDouble(5, discount.getMinPurchase());
            ps.setTimestamp(6, new Timestamp(discount.getExpiryDate().getTime()));
            ps.setInt(7, discount.getMaxUses());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Get discount code by code string
    public DiscountCode getDiscountCode(String code) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM discount_codes WHERE code = ? AND isActive = 1 AND expiryDate > NOW() AND (maxUses = 0 OR timesUsed < maxUses)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, code.toUpperCase());
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return new DiscountCode(
                    rs.getInt("discountID"),
                    rs.getString("code"),
                    rs.getString("description"),
                    rs.getString("discountType"),
                    rs.getDouble("discountValue"),
                    rs.getDouble("minPurchase"),
                    rs.getTimestamp("expiryDate"),
                    rs.getInt("maxUses"),
                    rs.getInt("timesUsed")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Get all active discount codes
    public List<DiscountCode> getAllActiveDiscounts() {
        List<DiscountCode> discounts = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM discount_codes WHERE isActive = 1 AND expiryDate > NOW()";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                discounts.add(new DiscountCode(
                    rs.getInt("discountID"),
                    rs.getString("code"),
                    rs.getString("description"),
                    rs.getString("discountType"),
                    rs.getDouble("discountValue"),
                    rs.getDouble("minPurchase"),
                    rs.getTimestamp("expiryDate"),
                    rs.getInt("maxUses"),
                    rs.getInt("timesUsed")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return discounts;
    }
    
    // Increment usage count when code is used
    public void incrementUsageCount(String code) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "UPDATE discount_codes SET timesUsed = timesUsed + 1 WHERE code = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, code.toUpperCase());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Deactivate discount code
    public void deactivateDiscountCode(int discountID) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "UPDATE discount_codes SET isActive = 0 WHERE discountID = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, discountID);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
