package src.model;

import java.util.Date;

/**
 * DiscountCode - Represents a discount/promotion code
 * Supports both percentage and fixed amount discounts
 */
public class DiscountCode {
    private int discountID;
    private String code;
    private String description;
    private String discountType; // PERCENTAGE or FIXED
    private double discountValue;
    private double minPurchase; // Minimum purchase required
    private Date expiryDate;
    private int maxUses; // 0 = unlimited
    private int timesUsed;
    
    public DiscountCode() {}
    
    public DiscountCode(int discountID, String code, String description, String discountType,
                       double discountValue, double minPurchase, Date expiryDate, int maxUses, int timesUsed) {
        this.discountID = discountID;
        this.code = code;
        this.description = description;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.minPurchase = minPurchase;
        this.expiryDate = expiryDate;
        this.maxUses = maxUses;
        this.timesUsed = timesUsed;
    }
    
    // Calculate discount amount for a total
    public double calculateDiscount(double cartTotal) {
        if (cartTotal < minPurchase) return 0;
        
        if ("PERCENTAGE".equals(discountType)) {
            return (cartTotal * discountValue) / 100;
        } else if ("FIXED".equals(discountType)) {
            return Math.min(discountValue, cartTotal);
        }
        return 0;
    }
    
    // Getters and Setters
    public int getDiscountID() { return discountID; }
    public void setDiscountID(int discountID) { this.discountID = discountID; }
    
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getDiscountType() { return discountType; }
    public void setDiscountType(String discountType) { this.discountType = discountType; }
    
    public double getDiscountValue() { return discountValue; }
    public void setDiscountValue(double discountValue) { this.discountValue = discountValue; }
    
    public double getMinPurchase() { return minPurchase; }
    public void setMinPurchase(double minPurchase) { this.minPurchase = minPurchase; }
    
    public Date getExpiryDate() { return expiryDate; }
    public void setExpiryDate(Date expiryDate) { this.expiryDate = expiryDate; }
    
    public int getMaxUses() { return maxUses; }
    public void setMaxUses(int maxUses) { this.maxUses = maxUses; }
    
    public int getTimesUsed() { return timesUsed; }
    public void setTimesUsed(int timesUsed) { this.timesUsed = timesUsed; }
    
    public boolean isValid() {
        return expiryDate.after(new Date()) && (maxUses == 0 || timesUsed < maxUses);
    }
    
    @Override
    public String toString() {
        return code + " - " + description + " (" + discountValue + (discountType.equals("PERCENTAGE") ? "%" : "₱") + ")";
    }
}
