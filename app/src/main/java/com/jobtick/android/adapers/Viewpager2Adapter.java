package com.jobtick.android.adapers;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.jobtick.android.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Viewpager2Adapter extends RecyclerView.Adapter<Viewpager2Adapter.ViewHolder> {

    // Array of images
    // Adding images from drawable folder
    private ArrayList<String> images;
    private Context ctx;

    // Constructor of our ViewPager2Adapter class
    public Viewpager2Adapter(Context ctx, ArrayList<String> images) {
        this.ctx = ctx;
        this.images = images;
    }

    // This method returns our layout
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.view_pager_image_holder, parent, false);
        return new ViewHolder(view);
    }

    // This method binds the screen with the view
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // This will set the images in imageview
        Glide.with(holder.images).load(images.get(position)).into(holder.images);
    }

    // This Method returns the size of the Array
    @Override
    public int getItemCount() {
        return images.size();
    }

    // The ViewHolder class holds the view
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView images;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            images = itemView.findViewById(R.id.image);
        }
    }
}