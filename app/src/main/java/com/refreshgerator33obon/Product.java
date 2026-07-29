package com.refreshgerator33obon;

public class Product {
        private int id;
        private String name;
        private int categoryId;
        private int storageType;
        private int shelfLife;
        private int poDay;
        private int poHour;
        private int poMin;
        private int importDate;
        private String notes;

        public Product(int id, String name, int categoryId, int storageType, int shelfLife,
                       int poDay, int poHour, int poMin, int importDate, String notes) {
            this.id = id;
            this.name = name;
            this.categoryId = categoryId;
            this.storageType = storageType;
            this.shelfLife = shelfLife;
            this.poDay = poDay;
            this.poHour = poHour;
            this.poMin = poMin;
            this.importDate = importDate;
            this.notes = notes;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public int getCategoryId() {
            return categoryId;
        }

        public int getStorageType() {
            return storageType;
        }

        public int getShelfLife() {
            return shelfLife;
        }

        public int getPoDay() {
            return poDay;
        }

        public int getPoHour() {
            return poHour;
        }

        public int getPoMin() {
            return poMin;
        }

        public int getImportDate() {
            return importDate;
        }

        public String getNotes() {
            return notes;
        }
}
