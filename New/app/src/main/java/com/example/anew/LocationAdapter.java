package com.example.anew;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder> {
    private Context context;
    private List<Location> locationList;

    public LocationAdapter(Context context, List<Location> locationList) {
        this.context = context;
        this.locationList = locationList;
    }

    @Override
    public LocationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_location, parent, false);
        return new LocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LocationViewHolder holder, int position) {
        Location location = locationList.get(holder.getAdapterPosition());
        holder.textViewName.setText(location.getNameAddress() + "-" + location.getType());
        holder.textViewAddress.setText(location.getAddress());

        // Set up swipe to delete
        holder.itemView.setOnTouchListener(new OnSwipeTouchListener(context) {
            @Override
            public void onSwipeLeft() {

            }

            @Override
            public void onSwipeRight() {

            }
        });

        // Set up delete button
    }


    @Override
    public int getItemCount() {
        return locationList.size();
    }

    public static class LocationViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewName;
        public TextView textViewAddress;


        public LocationViewHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.name_tv);
            textViewAddress = itemView.findViewById(R.id.location_tv);

        }
    }

    // Helper method to save the location list to a JSON file
    private void saveLocationListToJsonFile(Context context, List<Location> locationList) {
        File directory = context.getApplicationContext().getFilesDir();
        File file = new File(directory, "Location.json");
        try {
            FileWriter fileWriter = new FileWriter(file);
            Gson gson = new Gson();
            gson.toJson(locationList, fileWriter);
            fileWriter.close();
        } catch (IOException e) {
            System.err.println("Loi fileWriter");
        }
    }
}

