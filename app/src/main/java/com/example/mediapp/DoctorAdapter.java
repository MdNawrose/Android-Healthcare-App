package com.example.mediapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.ViewHolder> {
    private Context context;
    private List<Doctor> doctorList;

    public DoctorAdapter(Context context, List<Doctor> doctorList) {
        this.context = context;
        this.doctorList = doctorList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_doctor, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Doctor doc = doctorList.get(position);
        holder.name.setText(doc.getName());
        holder.specialty.setText(doc.getSpecialty());
        holder.rating.setText("★ " + doc.getRating());
        holder.distance.setText(doc.getDistance());

        try {
            InputStream is = context.getAssets().open(doc.getImage());
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            holder.image.setImageBitmap(bitmap);
        } catch (IOException e) {
            holder.image.setImageResource(R.drawable.placeholder_image);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DoctorDetailActivity.class);
            intent.putExtra("doctorId", doc.getId()); // Make sure ID exists
            intent.putExtra("doctorName", doc.getName()); // Make sure Name exists
            intent.putExtra("doctor", doc); // Pass complete object
            context.startActivity(intent);
        });


    }

    @Override
    public int getItemCount() {
        return doctorList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, specialty, rating, distance;
        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.doctorImage);
            name = itemView.findViewById(R.id.doctorName);
            specialty = itemView.findViewById(R.id.doctorSpecialty);
            rating = itemView.findViewById(R.id.doctorRating);
            distance = itemView.findViewById(R.id.doctorDistance);
        }
    }
}
