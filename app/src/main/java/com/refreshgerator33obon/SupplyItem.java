package com.refreshgerator33obon;

public class SupplyItem {
    private int id;
    private int itemId;            // Foreign key to the "products" table
    private int shelfLife;         // Represents the shelf life in days
    private boolean isFollowEnabled; // Matches the "follow" field
    private boolean isOpenEnabled;   // Matches the "open" field
    private String date1;          // Represents the first date (date1)
    private String date2;          // Represents the second date (date2)

    // Constructor
    public SupplyItem(int id, int itemId, int shelfLife, boolean isFollowEnabled,
                      boolean isOpenEnabled, String date1, String date2) {
        this.id = id;
        this.itemId = itemId;
        this.shelfLife = shelfLife;
        this.isFollowEnabled = isFollowEnabled;
        this.isOpenEnabled = isOpenEnabled;
        this.date1 = date1;
        this.date2 = date2;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getShelfLife() {
        return shelfLife;
    }

    public void setShelfLife(int shelfLife) {
        this.shelfLife = shelfLife;
    }

    public boolean isFollowEnabled() {
        return isFollowEnabled;
    }

    public void setFollowEnabled(boolean followEnabled) {
        isFollowEnabled = followEnabled;
    }

    public boolean isOpenEnabled() {
        return isOpenEnabled;
    }

    public void setOpenEnabled(boolean openEnabled) {
        isOpenEnabled = openEnabled;
    }

    public String getDate1() {
        return date1;
    }

    public void setDate1(String date1) {
        this.date1 = date1;
    }

    public String getDate2() {
        return date2;
    }

    public void setDate2(String date2) {
        this.date2 = date2;
    }
}