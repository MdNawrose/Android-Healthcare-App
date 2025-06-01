package com.example.mediapp;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.FieldValue;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartActivity extends AppCompatActivity {
    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private List<Product> cartList = new ArrayList<>();
    private Button checkoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        checkoutButton = findViewById(R.id.checkoutButton);

        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(this, cartList);
        cartRecyclerView.setAdapter(cartAdapter);

        fetchCartItems();

        checkoutButton.setOnClickListener(v -> processCheckout());
    }

    private void fetchCartItems() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("cart").get().addOnSuccessListener(querySnapshot -> {
            cartList.clear();
            for (QueryDocumentSnapshot doc : querySnapshot) {
                Product product = doc.toObject(Product.class);
                cartList.add(product);
            }
            cartAdapter.notifyDataSetChanged();
        });
    }

    private void processCheckout() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("cart").get().addOnSuccessListener(querySnapshot -> {
            List<Map<String, Object>> purchasedItems = new ArrayList<>();
            for (QueryDocumentSnapshot doc : querySnapshot) {
                Map<String, Object> item = new HashMap<>();
                item.put("name", doc.getString("name"));
                item.put("price", doc.getDouble("price"));
                item.put("image", doc.getString("image"));
                item.put("quantity", doc.getLong("quantity"));
                purchasedItems.add(item);
            }

            Map<String, Object> purchaseData = new HashMap<>();
            purchaseData.put("items", purchasedItems);
            purchaseData.put("timestamp", FieldValue.serverTimestamp());

            db.collection("purchases").add(purchaseData)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Order Placed! Generating Receipt...", Toast.LENGTH_LONG).show();

                        for (QueryDocumentSnapshot doc : querySnapshot) {
                            doc.getReference().delete();
                        }

                        generateReceipt();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Checkout failed!", Toast.LENGTH_SHORT).show());
        });
    }

    private void generateReceipt() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("purchases").orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        List<Map<String, Object>> items = (List<Map<String, Object>>) doc.get("items");
                        showReceipt(items);
                    }
                });
    }

    private void showReceipt(List<Map<String, Object>> items) {
        StringBuilder receiptText = new StringBuilder("Thank you for your order!\n\nItems:\n");
        for (Map<String, Object> item : items) {
            receiptText.append("- ").append(item.get("name"))
                    .append(" x").append(item.get("quantity"))
                    .append(": $").append(item.get("price")).append("\n");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Purchase Receipt");
        builder.setMessage(receiptText.toString());
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}
