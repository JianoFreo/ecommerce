package src.models;

public class Category {
    private int categoryID;
    private String name;
    private String description;
    private int parentCategoryID; // For subcategories

    public Category() {}

    public Category(int categoryID, String name, String description) {
        this.categoryID = categoryID;
        this.name = name;
        this.description = description;
    }

    // Getters and Setters
    public int getCategoryID() { return categoryID; }
    public void setCategoryID(int categoryID) { this.categoryID = categoryID; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getParentCategoryID() { return parentCategoryID; }
    public void setParentCategoryID(int parentCategoryID) { this.parentCategoryID = parentCategoryID; }

    @Override
    public String toString() {
        return String.format("Category[ID=%d, Name=%s, Description=%s]",
                categoryID, name, description);
    }
}
