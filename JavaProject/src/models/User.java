package src.models;

public class User {
    private int userID;
    private String name;
    private String email;
    private String password; // Should be hashed in production
    private String address;
    private String phoneNumber;
    private String role; // Customer, Admin

    public User() {}

    public User(int userID, String name, String email, String password, String role) {
        this.userID = userID;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // Getters and Setters
    public int getUserID() { return userID; }
    public void setUserID(int userID) { this.userID = userID; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isAdmin() {
        return "Admin".equalsIgnoreCase(role);
    }

    @Override
    public String toString() {
        return String.format("User[ID=%d, Name=%s, Email=%s, Role=%s]",
                userID, name, email, role);
    }
}
