package src.models;

import java.sql.Timestamp;

public class Review {
    private int reviewID;
    private int productID;
    private int userID;
    private String userName;
    private int rating; // 1-5 stars
    private String comment;
    private Timestamp reviewDate;

    public Review() {}

    public Review(int productID, int userID, int rating, String comment) {
        this.productID = productID;
        this.userID = userID;
        this.rating = rating;
        this.comment = comment;
    }

    // Getters and Setters
    public int getReviewID() { return reviewID; }
    public void setReviewID(int reviewID) { this.reviewID = reviewID; }

    public int getProductID() { return productID; }
    public void setProductID(int productID) { this.productID = productID; }

    public int getUserID() { return userID; }
    public void setUserID(int userID) { this.userID = userID; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public int getRating() { return rating; }
    public void setRating(int rating) { 
        if (rating >= 1 && rating <= 5) {
            this.rating = rating;
        }
    }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public Timestamp getReviewDate() { return reviewDate; }
    public void setReviewDate(Timestamp reviewDate) { this.reviewDate = reviewDate; }

    @Override
    public String toString() {
        return String.format("Review[ID=%d, Product=%d, User=%s, Rating=%d/5, Comment=%s]",
                reviewID, productID, userName, rating, comment);
    }
}
