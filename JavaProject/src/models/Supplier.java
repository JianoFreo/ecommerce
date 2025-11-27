package src.models;

public class Supplier {
    private int supplierID;
    private String name;
    private String contactPerson;
    private String email;
    private String phone;
    private String address;

    public Supplier() {}

    public Supplier(int supplierID, String name, String contactPerson, String email, String phone) {
        this.supplierID = supplierID;
        this.name = name;
        this.contactPerson = contactPerson;
        this.email = email;
        this.phone = phone;
    }

    // Getters and Setters
    public int getSupplierID() { return supplierID; }
    public void setSupplierID(int supplierID) { this.supplierID = supplierID; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    @Override
    public String toString() {
        return String.format("Supplier[ID=%d, Name=%s, Contact=%s, Email=%s]",
                supplierID, name, contactPerson, email);
    }
}
