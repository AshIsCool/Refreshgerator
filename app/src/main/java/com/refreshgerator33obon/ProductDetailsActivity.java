package com.refreshgerator33obon;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class ProductDetailsActivity extends AppCompatActivity {

    private EditText etProductName, etShelfLife, etProductionDay,
            etProductionHour, etProductionMinute, etNotes;
    private RadioGroup rgStorageType, rgImportDate; // Changed from EditText to RadioGroup
    private Button btnUpdateProduct, btnDeleteProduct;
    private TextView txt1,txt_poday;
    private int productId; // ID of the product
    private DatabaseHelper dbHelper; // Database Helper instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        // Initialize UI components
        etProductName = findViewById(R.id.etProductName);
        rgStorageType = findViewById(R.id.rgStorageType); // Initialize RadioGroup
        rgImportDate = findViewById(R.id.rgImportDate); // Initialize RadioGroup
        etShelfLife = findViewById(R.id.etShelfLife);
        txt1 = findViewById(R.id.txt_shelf);
        txt_poday = findViewById(R.id.txt_poday);
        etProductionDay = findViewById(R.id.etProductionDay);
        etProductionHour = findViewById(R.id.etProductionHour);
        etProductionMinute = findViewById(R.id.etProductionMinute);
        etNotes = findViewById(R.id.etNotes);
        btnUpdateProduct = findViewById(R.id.btnUpdateProduct);
        btnDeleteProduct = findViewById(R.id.btnDeleteProduct);

        // Initialize database helper
        dbHelper = new DatabaseHelper(this);

        // Get data passed from ProductListActivity
        productId = getIntent().getIntExtra("productId", -1);

        // Populate product details in the fields
        if (productId != -1) {
            populateProductDetails(productId);
        }

        // Set button listeners
        btnUpdateProduct.setOnClickListener(v -> updateProductDetails());
        btnDeleteProduct.setOnClickListener(v -> deleteProduct());

        int storageType = rgStorageType.getCheckedRadioButtonId() == R.id.rbFromPurchase ? 1 : rgStorageType.getCheckedRadioButtonId()== R.id.rbFromMade?2:3;
        if (storageType==3){
            etShelfLife.setVisibility(View.GONE);

            txt1.setVisibility(View.GONE);
        }


        int poDayValue = dbHelper.getPodayValueById(productId);
        if (poDayValue==-1){
            txt_poday.setVisibility(View.GONE);
            etProductionDay.setVisibility(View.GONE);
        } else {
            txt_poday.setVisibility(View.VISIBLE);
            etProductionDay.setVisibility(View.VISIBLE);
        }





    }



    // Method to populate product details in the fields
    private void populateProductDetails(int productId) {
        Product product = dbHelper.getProductById(productId);
        if (product != null) {
            etProductName.setText(product.getName());

            // Set Storage Type based on database value
            if (product.getStorageType() == 1) {
                rgStorageType.check(R.id.rbFromPurchase);
            } else if (product.getStorageType() == 2) {
                rgStorageType.check(R.id.rbFromMade);
            } else if (product.getStorageType() == 3) {
                rgStorageType.check(R.id.rbGoodUntil);
            }
            // Set Import Date based on database value
            if (product.getImportDate() == 1) {
                rgImportDate.check(R.id.rbOption1);
            } else if (product.getImportDate() == 2) {
                rgImportDate.check(R.id.rbOption2);
            } else if (product.getImportDate() == 3) {
                rgImportDate.check(R.id.rbOption3);
            }

            etShelfLife.setText(String.valueOf(product.getShelfLife()));
            etProductionDay.setText(String.valueOf(product.getPoDay()));
            etProductionHour.setText(String.valueOf(product.getPoHour()));
            etProductionMinute.setText(String.valueOf(product.getPoMin()));
            etNotes.setText(product.getNotes());
        }
    }

    // Method to update product details
    private void updateProductDetails() {
        String productName = etProductName.getText().toString();

        // Get selected Storage Type
        int storageType = rgStorageType.getCheckedRadioButtonId() == R.id.rbFromPurchase ? 1 : rgStorageType.getCheckedRadioButtonId()== R.id.rbFromMade?2:3;

        // Get selected Import Date
        int importDate = rgImportDate.getCheckedRadioButtonId() == R.id.rbOption1 ? 1 :
                rgImportDate.getCheckedRadioButtonId() == R.id.rbOption2 ? 2 : 3;

        int shelfLife = parseInteger(etShelfLife.getText().toString());
        int poDay = parseInteger(etProductionDay.getText().toString());
        int poHour = parseInteger(etProductionHour.getText().toString());
        int poMinute = parseInteger(etProductionMinute.getText().toString());
        String notes = etNotes.getText().toString();



        if(storageType==1 || storageType==2){
            if (productName.isEmpty() || shelfLife == -1 || storageType == -1) {
                etShelfLife.setVisibility(View.VISIBLE);
                txt1.setVisibility(View.VISIBLE);

                Toast.makeText(this, "Please fill out all required fields", Toast.LENGTH_SHORT).show();
                return;
            }
        }else if(storageType==3){
            if (productName.isEmpty() || storageType == -1) {
                Toast.makeText(this, "Please fill out all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

        }

        dbHelper.updateProduct(productId, productName, storageType, shelfLife, poDay, poHour, poMinute, importDate, notes);
        Toast.makeText(this, "Product updated successfully", Toast.LENGTH_SHORT).show();

        // Start ProductListActivity again
        Intent intent = new Intent(ProductDetailsActivity.this, ProductListActivity.class);
        intent.putExtra("categoryId", getIntent().getIntExtra("categoryId", -1));
        intent.putExtra("categoryName", getIntent().getStringExtra("categoryName"));
        startActivity(intent);

        finish(); // Close the activity
    }

    // Method to delete product
    private void deleteProduct() {
        dbHelper.deleteProduct(productId);
        Toast.makeText(this, "Product deleted successfully", Toast.LENGTH_SHORT).show();

        // Start ProductListActivity again
        Intent intent = new Intent(ProductDetailsActivity.this, ProductListActivity.class);
        intent.putExtra("categoryId", getIntent().getIntExtra("categoryId", -1));
        intent.putExtra("categoryName", getIntent().getStringExtra("categoryName"));
        startActivity(intent);

        finish(); // Close the activity
    }

    // Helper method to safely parse integers
    private int parseInteger(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return -1; // Return -1 for invalid input
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ProductDetailsActivity.this, ProductListActivity.class);
        intent.putExtra("categoryId", getIntent().getIntExtra("categoryId", -1));
        intent.putExtra("categoryName", getIntent().getStringExtra("categoryName"));
        startActivity(intent);
        finish();
    }
}