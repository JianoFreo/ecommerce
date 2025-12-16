package src;

import java.sql.Timestamp;
import src.dao.ReviewDAO;
import src.model.Review;

public class AddSampleReviews {
    public static void main(String[] args) {
        ReviewDAO reviewDAO = new ReviewDAO();
        
        // Sample reviews with made-up data
        String[][] reviewsData = {
            {"1", "1", "5", "Excellent quality! Very satisfied with this product."},
            {"1", "2", "4", "Good product, arrived on time. Recommend!"},
            {"2", "3", "5", "Best laptop case ever! Protective and stylish."},
            {"2", "1", "3", "Decent product but could be better."},
            {"3", "4", "5", "Amazing headphones! Crystal clear sound quality."},
            {"4", "2", "4", "Great phone, works perfectly. Happy customer here."},
            {"4", "3", "5", "Fantastic device! Using it daily without issues."},
            {"5", "4", "4", "Good tablet, reliable and fast. Worth the money."},
            {"6", "1", "5", "Excellent monitor, colors are vibrant and clear."},
            {"7", "5", "5", "This keyboard is fantastic! Very comfortable to use."},
            {"1", "5", "4", "Great value for money, highly satisfied."},
            {"3", "5", "5", "Outstanding product quality! Will buy again."},
        };
        
        int added = 0;
        for (String[] data : reviewsData) {
            try {
                Review review = new Review(
                    0,  // reviewID - auto-generated
                    Integer.parseInt(data[0]),  // productID
                    Integer.parseInt(data[1]),  // userID
                    Integer.parseInt(data[2]),  // rating
                    data[3],  // comment
                    new Timestamp(System.currentTimeMillis())  // current timestamp
                );
                
                if (reviewDAO.addReview(review)) {
                    added++;
                    System.out.println("Added review: " + data[3].substring(0, Math.min(30, data[3].length())));
                }
            } catch (Exception e) {
                System.out.println("Error adding review: " + e.getMessage());
            }
        }
        
        System.out.println("\nTotal reviews added: " + added);
    }
}
