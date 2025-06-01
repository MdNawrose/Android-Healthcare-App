package com.example.mediapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.io.InputStream;
import java.text.BreakIterator;
import java.text.StringCharacterIterator;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productList;

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    public void updateList(List<Product> newList) {
        this.productList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

  //  @Override
//    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
//        Product product = productList.get(position);
//        holder.name.setText(product.getName());
//        holder.price.setText("$" + product.getPrice());
//
//        // Load image from assets using Glide
//        String imagePath = "file:///android_asset/" + product.getImage();
//        Glide.with(context)
//                .load(Uri.parse(imagePath))
//                .placeholder(R.drawable.placeholder_image)
//                .into(holder.image);
//    }
  @Override
  public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
      Product product = productList.get(position);
      holder.name.setText(product.getName());
      holder.price.setText("$" + product.getPrice());
      holder.description.setText(product.getDescription());

      // Load image from assets using AssetManager
      try {
          InputStream inputStream = context.getAssets().open(product.getImage());
          Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
          holder.image.setImageBitmap(bitmap);
      } catch (IOException e) {
          // Show backup placeholder if the image isn't found
          holder.image.setImageResource(R.drawable.placeholder_image);
          e.printStackTrace();
      }

      // Set click listener to open ProductDetailActivity
      holder.itemView.setOnClickListener(v -> {
          Intent intent = new Intent(context, ProductDetailActivity.class);
          intent.putExtra("name", product.getName());
          intent.putExtra("price", product.getPrice());
          intent.putExtra("image", product.getImage());
          intent.putExtra("description", product.getDescription());
          context.startActivity(intent);
      });
  }



    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView name, price, description;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.productImage);
            name = itemView.findViewById(R.id.productName);
            price = itemView.findViewById(R.id.productPrice);
            description = itemView.findViewById(R.id.productDescription);
        }
    }
}
