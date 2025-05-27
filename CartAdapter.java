package com.example.mediapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private List<Product> cartList;

    public CartAdapter(Context context, List<Product> cartList) {
        this.context = context;
        this.cartList = cartList;
    }

    public void updateList(List<Product> newList) {
        this.cartList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Product product = cartList.get(position);
        holder.name.setText(product.getName());
        holder.price.setText("$" + product.getPrice());
        holder.quantity.setText(String.valueOf(product.getQuantity()));

        String imagePath = "file:///android_asset/" + product.getImage();
        Glide.with(context)
                .load(imagePath)
                .placeholder(R.drawable.placeholder_image)
                .into(holder.image);

        // Handle increase quantity
        holder.btnIncrease.setOnClickListener(v -> {
            product.setQuantity(product.getQuantity() + 1);
            notifyItemChanged(position);
        });

        // Handle decrease quantity
        holder.btnDecrease.setOnClickListener(v -> {
            if (product.getQuantity() > 1) {
                product.setQuantity(product.getQuantity() - 1);
                notifyItemChanged(position);
            }
        });

        // Handle remove item
        holder.btnRemove.setOnClickListener(v -> {
            cartList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, cartList.size());
        });
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView image, btnIncrease, btnDecrease, btnRemove;
        TextView name, price, quantity;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.cartProductImage);
            name = itemView.findViewById(R.id.cartProductName);
            price = itemView.findViewById(R.id.cartProductPrice);
            quantity = itemView.findViewById(R.id.cartProductQuantity);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }
    }
}

