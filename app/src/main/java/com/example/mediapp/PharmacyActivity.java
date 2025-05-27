package com.example.mediapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class PharmacyActivity extends AppCompatActivity {

    private RecyclerView productRecyclerView;
    private ProductAdapter productAdapter;
    private List<Product> fullProductList = new ArrayList<>();
    private EditText searchBar;
    private ImageView cartIcon;
    private View cartNotificationDot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacy); // Ensure this file is named correctly

        // Initialize views
        productRecyclerView = findViewById(R.id.productRecyclerView);
        searchBar = findViewById(R.id.searchBar);
        cartIcon = findViewById(R.id.cartIcon);
        cartNotificationDot = findViewById(R.id.cartNotificationDot);

        // Setup RecyclerView
        productRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        productAdapter = new ProductAdapter(this, fullProductList);
        productRecyclerView.setAdapter(productAdapter);

        // Fetch product data from Firestore
        fetchProductsFromFirestore();

        // Check if there are items in the cart
        checkCartSize();

        // Add text listener for search functionality
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProductList(s.toString());
            }
        });

        cartIcon.setOnClickListener(v -> {
            Intent intent = new Intent(PharmacyActivity.this, CartActivity.class);
            startActivity(intent);
        });

    }

    // Function to show/hide the red dot based on cart size
    public void checkCartSize() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("cart").get().addOnSuccessListener(querySnapshot -> {
            if (!querySnapshot.isEmpty()) {
                cartNotificationDot.setVisibility(View.VISIBLE);  // Show the red dot
            } else {
                cartNotificationDot.setVisibility(View.GONE);  // Hide the red dot
            }
        });
    }


    private void fetchProductsFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("products")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    fullProductList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Product product = doc.toObject(Product.class);
                        fullProductList.add(product);
                    }
                    productAdapter.notifyDataSetChanged();
                });
    }

    private void filterProductList(String query) {
        List<Product> filteredList = new ArrayList<>();
        for (Product product : fullProductList) {
            if (product.getName() != null && product.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(product);
            }
        }

        // Ensure RecyclerView updates properly
        productAdapter.updateList(filteredList);
        productAdapter.notifyDataSetChanged();
    }


}


