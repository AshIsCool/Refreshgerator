package com.refreshgerator33obon;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;

public class ProductListActivity extends AppCompatActivity {

    private TextView tvCategoryName;
    public ListView productListView;
    private ImageView btnAddProduct;
    private int categoryId; // ID of the category
    private String categoryName; // Name of the category
    private DatabaseHelper dbHelper; // Database Helper instance
    private List<Product> products; // List of products
    private ProductAdapter productAdapter; // Adapter for the product list

    LinearLayout nb_supply, nb_list, nb_settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        // Initialize UI components
        tvCategoryName = findViewById(R.id.tvCategoryName);
        productListView = findViewById(R.id.productListView);
        btnAddProduct = findViewById(R.id.btnAddProduct);


        nb_supply = findViewById(R.id.nb_supply);
        nb_list = findViewById(R.id.nb_list);
        nb_settings = findViewById(R.id.nb_settings);




        nb_supply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProductListActivity.this, SupplyActivity.class));
                finish();
                overridePendingTransition(R.anim.inright, R.anim.outleft);
            }
        });
        nb_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProductListActivity.this, List_Activity.class));
                finish();
                overridePendingTransition(R.anim.inright, R.anim.outleft);
            }
        });
        nb_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProductListActivity.this, Notification.class));
                finish();
                overridePendingTransition(R.anim.inright, R.anim.outleft);
            }
        });

        // Initialize database helper
        dbHelper = new DatabaseHelper(this);


        // Get data passed from MainActivity
        categoryId = getIntent().getIntExtra("categoryId", -1);
        categoryName = getIntent().getStringExtra("categoryName");

        // Set the category name in the header
        if (categoryName != null) {
            tvCategoryName.setText(categoryName);
        }

        // Fetch products for the category
        loadProducts();

        // Add product functionality
        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddProductDialog();


            }
        });



        // Set item click listener for the ListView
        productListView.setOnItemClickListener((parent, view, position, id) -> {
            Product clickedProduct = products.get(position);
            Intent intent = new Intent(ProductListActivity.this, ProductDetailsActivity.class);
            intent.putExtra("productId", clickedProduct.getId()); // Pass the product ID
            intent.putExtra("categoryId", categoryId);
            intent.putExtra("categoryName", categoryName);
            startActivity(intent);
            finish(); // Close ProductListActivity
        });
    }

    // Load products for the current category and refresh the ListView
    private void loadProducts() {
        products = dbHelper.getProductsByCategoryId(categoryId);

        // Initialize or refresh the adapter
        if (productAdapter == null) {
            productAdapter = new ProductAdapter(this, products);
            productListView.setAdapter(productAdapter);
        } else {
            productAdapter.notifyDataSetChanged();
        }
    }

    // Show the Bottom Sheet Dialog for adding a product
    private void showAddProductDialog() {
        // Create a Bottom Sheet Dialog
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ProductListActivity.this);
        View bottomSheetView = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_add_product, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            bottomSheet.setBackgroundResource(R.drawable.corner_radius); // Apply the rounded background
        }

        // Initialize input fields in the bottom sheet
        EditText etProductName = bottomSheetView.findViewById(R.id.etProductName);
        EditText etShelfLife = bottomSheetView.findViewById(R.id.etShelfLife);
        TextView txt1 = bottomSheetView.findViewById(R.id.txt_shelfi);
        EditText etPoDay = bottomSheetView.findViewById(R.id.etPoDay);
        EditText etPoHour = bottomSheetView.findViewById(R.id.etPoHour);
        EditText etPoMin = bottomSheetView.findViewById(R.id.etPoMin);
        EditText etNotes = bottomSheetView.findViewById(R.id.etNotes);

        // Initialize RadioGroups for Storage Type and Import Date
        RadioGroup rgStorageType = bottomSheetView.findViewById(R.id.rgStorageType);
        RadioGroup rgImportDate = bottomSheetView.findViewById(R.id.rgImportDate);

        rgStorageType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbGoodUntil) {
                etShelfLife.setVisibility(View.GONE); // Hide the EditText
                txt1.setVisibility(View.GONE); // Hide the EditText

            } else if (checkedId == R.id.rbFromPurchase || checkedId == R.id.rbFromMade) {
                etShelfLife.setVisibility(View.VISIBLE); // Show the EditText
                txt1.setVisibility(View.VISIBLE); // Show the EditText

            }
        });


        // Save Button
        Button btnSaveProduct = bottomSheetView.findViewById(R.id.btnSaveProduct);
        btnSaveProduct.setOnClickListener(v -> {
            // Collect data from input fields
            String productName = etProductName.getText().toString();
            int shelfLife = parseInteger(etShelfLife.getText().toString());
            int poDay = parseInteger(etPoDay.getText().toString());
            int poHour = parseInteger(etPoHour.getText().toString());
            int poMin = parseInteger(etPoMin.getText().toString());
            String notes = etNotes.getText().toString();

            // Get selected values from RadioGroups
            int storageType = rgStorageType.getCheckedRadioButtonId() == R.id.rbFromPurchase ? 1 : rgStorageType.getCheckedRadioButtonId()== R.id.rbFromMade?2:3;
            int checkedId = rgImportDate.getCheckedRadioButtonId();
            int importDate;

            if (checkedId == R.id.rbOption1) {
                importDate = 1;

            } else if (checkedId == R.id.rbOption2) {
                importDate = 2;

            } else if (checkedId == R.id.rbOption3) {
                importDate = 3;

            } else {
                importDate = -1; // Default value if no option is selected
            }




            if(storageType==1 || storageType==2){
                if (productName.isEmpty() || shelfLife == -1 || storageType == -1) {
                    Toast.makeText(this, "Please fill out all required fields", Toast.LENGTH_SHORT).show();
                    return;
                }
            }else{
                if (productName.isEmpty() || storageType == -1) {

                    Toast.makeText(this, "Please fill out all required fields", Toast.LENGTH_SHORT).show();
                    return;
                }

            }


            // Save product to database
            dbHelper.addProduct(
                    productName,
                    categoryId,
                    storageType,
                    shelfLife,
                    poDay,
                    poHour,
                    poMin,
                    importDate,
                    notes
            );

            Toast.makeText(this, "Product added successfully", Toast.LENGTH_SHORT).show();

            // Refresh the product list and update the adapter
            refreshProductList();

            // Dismiss the dialog
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }

    // Helper method to safely parse integers
    private int parseInteger(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return -1; // Return -1 for invalid input
        }
    }

    private void refreshProductList() {
        if (products != null) {
            products.clear(); // Clear the current list
            products.addAll(dbHelper.getProductsByCategoryId(categoryId)); // Add updated products
            productAdapter.notifyDataSetChanged(); // Notify the adapter
        } else {
            products = dbHelper.getProductsByCategoryId(categoryId); // Fetch products
            productAdapter = new ProductAdapter(this, products);
            productListView.setAdapter(productAdapter);
        }
    }





}
