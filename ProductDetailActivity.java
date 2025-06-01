package com.example.mediapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ProductDetailActivity extends AppCompatActivity {

    private TextView productName, productPrice, productDescription;
    private ImageView productImage;
    private Button addToCartButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // Initialize views
        productName = findViewById(R.id.productName);
        productPrice = findViewById(R.id.productPrice);
        productDescription = findViewById(R.id.productDescription);
        productImage = findViewById(R.id.productImage);
        addToCartButton = findViewById(R.id.addToCartButton);

        // Get product data from intent
        String name = getIntent().getStringExtra("name");
        double price = getIntent().getDoubleExtra("price", 0);
        String image = getIntent().getStringExtra("image");
        int quantity = getIntent().getIntExtra("quantity", 1);
        String description = getIntent().getStringExtra("description"); // new

        // Set product details
        productName.setText(name);
        productPrice.setText("$" + String.format("%.2f", price));
        productDescription.setText(description != null ? description : "No description available.");

        try {
            // Load image from assets
            InputStream inputStream = getAssets().open(image);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            productImage.setImageBitmap(bitmap);
        } catch (IOException e) {
            productImage.setImageResource(R.drawable.placeholder_image); // fallback
            e.printStackTrace();
        }

        // Add to Cart Click Listener
        addToCartButton.setOnClickListener(v -> addToCart(name, price, image, quantity));
    }

    private void checkCartSize() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        View cartNotificationDot = findViewById(R.id.cartNotificationDot); // Ensure you have this in layout

        if (cartNotificationDot != null) {
            db.collection("cart").get().addOnSuccessListener(querySnapshot -> {
                cartNotificationDot.setVisibility(querySnapshot.isEmpty() ? View.GONE : View.VISIBLE);
            });
        }
    }

    private void addToCart(String name, double price, String image, int quantity) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> cartItem = new HashMap<>();
        cartItem.put("name", name);
        cartItem.put("price", price);
        cartItem.put("image", image);
        cartItem.put("quantity", quantity);

        db.collection("cart")
                .add(cartItem)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Added to Cart!", Toast.LENGTH_SHORT).show();
                    checkCartSize();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to add to Cart", Toast.LENGTH_SHORT).show());
    }
}
