package com.example.anew;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SearchView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class LocationListFragment extends Fragment {
    public LocationListFragment() {
        // Required empty public constructor
    }
    public static LocationListFragment newInstance() {
        LocationListFragment fragment = new LocationListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location_list, container, false);

        List<Location> locationList = new ArrayList<>();
        File directory = getActivity().getApplicationContext().getFilesDir();
        File file = new File(directory, "Location.json");
        try{
            FileReader fileReader = new FileReader(file);
            Type type = new TypeToken<ArrayList<Location>>(){}.getType();
            Gson gson = new Gson();
            locationList = gson.fromJson(fileReader, type);
            fileReader.close();
        } catch (FileNotFoundException e) {
            System.err.println("Loi fileReader");
        } catch (IOException e) {
            System.err.println("Loi close fileReader");
        }
        // Khởi tạo adapter
        LocationAdapter adapter = new LocationAdapter(getActivity(), locationList);
        // Gán adapter cho ListView
        RecyclerView locationRecyclerView = view.findViewById(R.id.rv_address);
        locationRecyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        locationRecyclerView.setLayoutManager(layoutManager);
        adapter.notifyDataSetChanged();

        // Thêm ItemTouchHelper
        List<Location> finalLocationList = locationList;
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT| ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                // Xóa item khi được vuốt sang trái
                if (direction == ItemTouchHelper.LEFT) {

                    int position = viewHolder.getAdapterPosition();
                    finalLocationList.remove(position);
                    adapter.notifyItemRemoved(position);
                    // Lưu danh sách mới xuống file
                    Gson gson = new Gson();
                    String json = gson.toJson(finalLocationList);
                    try {
                        FileWriter fileWriter = new FileWriter(file);
                        fileWriter.write(json);
                        fileWriter.close();
                    } catch (IOException e) {
                        System.err.println("Loi fileWriter");
                    }

                }


                if (direction == ItemTouchHelper.RIGHT) {

                    int position = viewHolder.getAdapterPosition();
                    String text = finalLocationList.get(position).getAddress();

                    ((MainActivity)getActivity()).SearchAddress = text;
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frameLayout, new MapFragment()).addToBackStack(null).commit();
                }
            }
        };
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(locationRecyclerView);

        //SearchView
        SearchView searchView = view.findViewById(R.id.searchView);
        final List<Location> searchList = locationList;
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String query) {
                LocationAdapter adapter;
                List<Location> filteredList = new ArrayList<Location>();
                if (query.length() != 0) { // Nếu query rỗng thì hiển thị lại toàn bộ danh sách
                    for (Location location : searchList) {
                        if (location.getNameAddress().toLowerCase().contains(query.toLowerCase())) {
                            filteredList.add(location);
                            System.out.println("1 cái");

                        }
                    }
                    adapter = new LocationAdapter(getActivity(), filteredList);
                    locationRecyclerView.setAdapter(adapter);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    locationRecyclerView.setLayoutManager(layoutManager);
                    adapter.notifyDataSetChanged();
                } else {
                    filteredList.addAll(searchList);
                    adapter = new LocationAdapter(getActivity(), filteredList);
                    locationRecyclerView.setAdapter(adapter);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    locationRecyclerView.setLayoutManager(layoutManager);
                    adapter.notifyDataSetChanged();
                }
                return true;
            }
        });
        return view;
    }

}