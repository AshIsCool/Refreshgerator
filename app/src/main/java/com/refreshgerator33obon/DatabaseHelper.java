package com.refreshgerator33obon;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(@Nullable Context context) {
        super(context, "inventory_database", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create categories table
        db.execSQL("CREATE TABLE categories (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "category_name TEXT, " +
                "is_predefined INTEGER DEFAULT 0)");

        // Create products table
        db.execSQL("CREATE TABLE products (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "category_id INTEGER, " +
                "storage INTEGER, " +
                "shelf_life INTEGER, " +
                "poday INTEGER, " +
                "pohour INTEGER, " +
                "pomin INTEGER, " +
                "im_date INTEGER, " +
                "note TEXT, " +
                "FOREIGN KEY (category_id) REFERENCES categories(id))");


        db.execSQL("CREATE TABLE supply (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "item_id INTEGER, " +
                "category_id INTEGER,"+
                "shelf_life INTEGER, " +
                "follow INTEGER DEFAULT 0, " +
                "open INTEGER DEFAULT 0, " +
                "deadline_start TEXT, " +
                "deadline_end TEXT, " +
                "storage INTEGER, " +  // Add storage column here
                "poday INTEGER, " +  // Add storage column here
                "is_to_buy INTEGER DEFAULT 0, " +
                "FOREIGN KEY(item_id) REFERENCES products(id))");


        db.execSQL("CREATE TABLE buy (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "end_date TEXT)");


        db.execSQL("CREATE TABLE window (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "end_date TEXT)");



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS categories");
        db.execSQL("DROP TABLE IF EXISTS products");
        db.execSQL("DROP TABLE IF EXISTS supply");
        db.execSQL("DROP TABLE IF EXISTS buy");
        db.execSQL("DROP TABLE IF EXISTS window");
        onCreate(db);
    }

    // Add a category
    public void addCategory(String categoryName, boolean isPredefined) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("category_name", categoryName);
        values.put("is_predefined", isPredefined ? 1 : 0);

        db.insert("categories", null, values);
        db.close();
    }

    // Fetch all categories from the database
    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM categories", null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex("id");
                int nameIndex = cursor.getColumnIndex("category_name");
                int predefinedIndex = cursor.getColumnIndex("is_predefined");

                if (idIndex != -1 && nameIndex != -1 && predefinedIndex != -1) {
                    int categoryId = cursor.getInt(idIndex); // Database ID
                    String name = cursor.getString(nameIndex); // Category name
                    boolean isPredefined = cursor.getInt(predefinedIndex) == 1; // Predefined status

                    int iconResId = isPredefined ? getIconForCategory(name) : 0; // Handle predefined icons
                    categories.add(new Category(categoryId, name, iconResId, isPredefined));
                } else {
                    Log.e("Database Error", "Column not found in the cursor.");
                }
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return categories;
    }

    // Add a product
    public void addProduct(String name, int categoryId, int storageType, int shelfLife, int poDay, int poHour, int poMin, int importDate, String notes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("category_id", categoryId);
        values.put("storage", storageType);
        values.put("shelf_life", shelfLife);
        values.put("poday", poDay);
        values.put("pohour", poHour);
        values.put("pomin", poMin);
        values.put("im_date", importDate);
        values.put("note", notes);

        long result = db.insert("products", null, values);

        if (result == -1) {
            Log.e("Database Error", "Failed to insert product");
        } else {
            Log.d("Database Info", "Product added successfully");
        }

        db.close();
    }

    // Delete a category
    public void deleteCategory(String categoryName) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete("categories", "category_name = ?", new String[]{categoryName});
        if (rowsAffected > 0) {
            Log.d("Database", "Category deleted successfully: " + categoryName);
        } else {
            Log.d("Database", "Failed to delete category: " + categoryName);
        }

        db.close();
    }

    // Helper method to return icons for predefined categories
    private int getIconForCategory(String categoryName) {
        switch (categoryName) {
            case "Grocery":
                return R.drawable.grocery_img;
            case "Meat and Fish":
                return R.drawable.fish_img;
            case "Eggs and Dairy Products":
                return R.drawable.egg_img;
            case "Drinks":
                return R.drawable.drink_img;
            case "Cereals and Pasta":
                return R.drawable.cereal_img;
            case "Frozen Food":
                return R.drawable.ice_img;
            case "Canned Food":
                return R.drawable.canned_img;
            case "Condiments":
                return R.drawable.condiment_img;
            case "Bread and Confectionery":
                return R.drawable.bread_img;
            default:
                return 0;
        }
    }



    public List<Product> getProductsByCategoryId(int categoryId) {
        List<Product> products = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM products WHERE category_id = ?",
                new String[]{String.valueOf(categoryId)});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Check for column indices to avoid red lines
                int idIndex = cursor.getColumnIndex("id");
                int nameIndex = cursor.getColumnIndex("name");
                int storageIndex = cursor.getColumnIndex("storage");
                int shelfLifeIndex = cursor.getColumnIndex("shelf_life");
                int poDayIndex = cursor.getColumnIndex("poday");
                int poHourIndex = cursor.getColumnIndex("pohour");
                int poMinIndex = cursor.getColumnIndex("pomin");
                int importDateIndex = cursor.getColumnIndex("im_date");
                int notesIndex = cursor.getColumnIndex("note");

                if (idIndex != -1 && nameIndex != -1 && storageIndex != -1 &&
                        shelfLifeIndex != -1 && poDayIndex != -1 && poHourIndex != -1 &&
                        poMinIndex != -1 && importDateIndex != -1 && notesIndex != -1) {

                    int id = cursor.getInt(idIndex);
                    String name = cursor.getString(nameIndex);
                    int storageType = cursor.getInt(storageIndex);
                    int shelfLife = cursor.getInt(shelfLifeIndex);
                    int poDay = cursor.getInt(poDayIndex);
                    int poHour = cursor.getInt(poHourIndex);
                    int poMin = cursor.getInt(poMinIndex);
                    int importDate = cursor.getInt(importDateIndex);
                    String notes = cursor.getString(notesIndex);

                    products.add(new Product(id, name, categoryId, storageType, shelfLife,
                            poDay, poHour, poMin, importDate, notes));
                } else {
                    Log.e("Database Error", "One or more columns not found in the cursor.");
                }
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        return products;
    }





    public Product getProductById(int productId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM products WHERE id = ?", new String[]{String.valueOf(productId)});
        Product product = null;

        if (cursor != null && cursor.moveToFirst()) {
            // Check for column indices to avoid errors
            int idIndex = cursor.getColumnIndex("id");
            int nameIndex = cursor.getColumnIndex("name");
            int categoryIdIndex = cursor.getColumnIndex("category_id");
            int storageIndex = cursor.getColumnIndex("storage");
            int shelfLifeIndex = cursor.getColumnIndex("shelf_life");
            int poDayIndex = cursor.getColumnIndex("poday");
            int poHourIndex = cursor.getColumnIndex("pohour");
            int poMinIndex = cursor.getColumnIndex("pomin");
            int importDateIndex = cursor.getColumnIndex("im_date");
            int notesIndex = cursor.getColumnIndex("note");

            if (idIndex != -1 && nameIndex != -1 && categoryIdIndex != -1 &&
                    storageIndex != -1 && shelfLifeIndex != -1 &&
                    poDayIndex != -1 && poHourIndex != -1 &&
                    poMinIndex != -1 && importDateIndex != -1 && notesIndex != -1) {
                int id = cursor.getInt(idIndex);
                String name = cursor.getString(nameIndex);
                int categoryId = cursor.getInt(categoryIdIndex);
                int storageType = cursor.getInt(storageIndex);
                int shelfLife = cursor.getInt(shelfLifeIndex);
                int poDay = cursor.getInt(poDayIndex);
                int poHour = cursor.getInt(poHourIndex);
                int poMin = cursor.getInt(poMinIndex);
                int importDate = cursor.getInt(importDateIndex);
                String notes = cursor.getString(notesIndex);

                product = new Product(id, name, categoryId, storageType, shelfLife,
                        poDay, poHour, poMin, importDate, notes);
            }
        }

        if (cursor != null) {
            cursor.close();
        }

        return product;
    }

    // Update a product
    public void updateProduct(int productId, String name, int storageType, int shelfLife,
                              int poDay, int poHour, int poMinute, int importDate, String notes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("name", name);
        values.put("storage", storageType);
        values.put("shelf_life", shelfLife);
        values.put("poday", poDay);
        values.put("pohour", poHour);
        values.put("pomin", poMinute);
        values.put("im_date", importDate);
        values.put("note", notes);

        int rowsAffected = db.update("products", values, "id = ?", new String[]{String.valueOf(productId)});
        if (rowsAffected > 0) {
            Log.d("Database", "Product updated successfully: " + name);
        } else {
            Log.e("Database", "Failed to update product with ID: " + productId);
        }

        db.close();
    }

    // Delete a product
    public void deleteProduct(int productId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete("products", "id = ?", new String[]{String.valueOf(productId)});
        if (rowsAffected > 0) {
            Log.d("Database", "Product deleted successfully with ID: " + productId);
        } else {
            Log.e("Database", "Failed to delete product with ID: " + productId);
        }

        db.close();
    }




    // Fetch supply items by category ID
    public List<SupplyItem> getSupplyItemsByCategoryId(int categoryId) {
        List<SupplyItem> supplyItems = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Adjusted query to match table schema
        String query = "SELECT s.id, s.item_id, s.shelf_life, s.follow, s.open, s.deadline_start, s.deadline_end " +
                "FROM supply s " +
                "INNER JOIN products p ON s.item_id = p.id " +
                "WHERE p.category_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(categoryId)});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                int itemId = cursor.getInt(cursor.getColumnIndexOrThrow("item_id"));
                int shelfLife = cursor.getInt(cursor.getColumnIndexOrThrow("shelf_life"));
                boolean isFollowEnabled = cursor.getInt(cursor.getColumnIndexOrThrow("follow")) == 1;
                boolean isOpenEnabled = cursor.getInt(cursor.getColumnIndexOrThrow("open")) == 1;
                String date1 = cursor.getString(cursor.getColumnIndexOrThrow("deadline_start"));
                String date2 = cursor.getString(cursor.getColumnIndexOrThrow("deadline_end"));

                supplyItems.add(new SupplyItem(id, itemId, shelfLife, isFollowEnabled, isOpenEnabled, date1, date2));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return supplyItems;
    }





//    public List<WindowItem> getWindowsItemsByCategoryId(int categoryId) {
//        List<WindowItem> windowsItems = new ArrayList<>();
//        SQLiteDatabase db = this.getReadableDatabase();
//
//        String query = "SELECT p.name AS item_name, s.deadline_end " +
//                "FROM supply s " +
//                "INNER JOIN products p ON s.item_id = p.id " +
//                "WHERE p.category_id = ?";
//        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(categoryId)});
//
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // Adjust format to match your dates
//        String currentDateStr = sdf.format(new Date()); // Get current date
//
//        if (cursor.moveToFirst()) {
//            do {
//                String itemName = cursor.getString(cursor.getColumnIndexOrThrow("item_name"));
//                String deadlineEnd = cursor.getString(cursor.getColumnIndexOrThrow("deadline_end"));
//
//                // Calculate the date difference
//                String dateDifference = "N/A"; // Default value
//                try {
//                    Date endDate = sdf.parse(deadlineEnd);
//                    Date currentDate = sdf.parse(currentDateStr);
//                    long diffMillis = endDate.getTime() - currentDate.getTime();
//                    long diffDays = diffMillis / (1000 * 60 * 60 * 24); // Convert millis to days
//                    dateDifference = diffDays + " days";
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//
//                // Add the item to the list
//                windowsItems.add(new WindowItem(itemName, deadlineEnd, dateDifference));
//            } while (cursor.moveToNext());
//        }
//
//        cursor.close();
//        return windowsItems;
//    }


    public String getProductNameById(int itemId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String productName = null;
        Cursor cursor = db.rawQuery("SELECT name FROM products WHERE id = ?", new String[]{String.valueOf(itemId)});
        if (cursor != null && cursor.moveToFirst()) {
            // Get column index for "name"
            int nameIndex = cursor.getColumnIndex("name");

            // Check if the column index is valid and within range
            if (nameIndex >= 0 && nameIndex < cursor.getColumnCount()) {
                productName = cursor.getString(nameIndex);
            } else {
                Log.e("CursorError", "Invalid column index for 'name'.");
            }

            // Close the cursor
            cursor.close();
        }
        return productName;
    }



    public void addToBuy(String name, String endDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("end_date", endDate);

        long result = db.insert("buy", null, values);

        if (result == -1) {
            Log.e("DatabaseHelper", "Failed to insert into buy table.");
        } else {
            Log.d("DatabaseHelper", "Item added to buy table: " + name);
        }
    }




    public int getStorageValueById(int itemId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT storage FROM products WHERE id = ?", new String[]{String.valueOf(itemId)});

        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex("storage");
            if (columnIndex != -1) {
                // If the column exists, retrieve the value
                int storage = cursor.getInt(columnIndex);
                cursor.close();
                return storage;
            } else {
                // Column not found, handle error
                cursor.close();
                return -1;  // Return -1 if column is not found
            }
        }
        cursor.close();
        return -1;  // Return -1 if no row is found
    }


    public int getPodayValueById(int itemId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT poday FROM products WHERE id = ?", new String[]{String.valueOf(itemId)});

        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex("poday");
            if (columnIndex != -1) {
                // If the column exists, retrieve the value
                int poday = cursor.getInt(columnIndex);
                cursor.close();
                return poday;
            } else {
                // Column not found, handle error
                cursor.close();
                return -1;  // Return -1 if column is not found
            }
        }
        cursor.close();
        return -1;  // Return -1 if no row is found
    }






}