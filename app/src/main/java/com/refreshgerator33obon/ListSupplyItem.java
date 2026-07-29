package com.refreshgerator33obon;

public class ListSupplyItem {

        private int id;
        private String itemName;
        private String shelfLife;

    private boolean toBuy;  // Add this field to track "To Buy" status


        public ListSupplyItem(int id, String itemName, String shelfLife) {
            this.id = id;
            this.itemName = itemName;
            this.shelfLife = shelfLife;
            this.toBuy = false;

        }


    // Getter and setter for the new 'toBuy' field
    public boolean isToBuy() {
        return toBuy;
    }

    public void setToBuy(boolean toBuy) {
        this.toBuy = toBuy;
    }


    public ListSupplyItem(String name, int shelfLife) {
    }

    public int getId() {
            return id;
        }

        public String getItemName() {
            return itemName;
        }

        public String getShelfLife() {
            return shelfLife;
        }
}