package com.example.anew;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import android.widget.SearchView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
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



public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        Button addButton = view.findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddAppointment.class);
                startActivity(intent);
            }
        });


        //RecyclerView
      List<Appointment> appointmentsList = new ArrayList<>();
        File directory = getActivity().getApplicationContext().getFilesDir();
        File file = new File(directory, "appointments.json");
         try{
            FileReader fileReader = new FileReader(file);
            Type type = new TypeToken<ArrayList<Appointment>>(){}.getType();
            Gson gson = new Gson();
            appointmentsList = gson.fromJson(fileReader, type);
            fileReader.close();
        } catch (FileNotFoundException e) {
            System.err.println("Loi fileReader");
        } catch (IOException e) {
            System.err.println("Loi close fileReader");
        }
        // Khởi tạo adapter
      final AppointmentAdapter adapter = new AppointmentAdapter(getActivity(), appointmentsList);
        // Gán adapter cho ListView
        RecyclerView appointmentRV = view.findViewById(R.id.rv_Appointment);
        appointmentRV.setAdapter(adapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        appointmentRV.setLayoutManager(layoutManager);
        adapter.notifyDataSetChanged();

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        appointmentRV.addItemDecoration(itemDecoration);
        //RecyclerView
        List<Appointment> finalAppointment = appointmentsList;
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                // Xóa item khi được vuốt sang trái
                if (direction == ItemTouchHelper.LEFT) {
                    int position = viewHolder.getAdapterPosition();
                    finalAppointment.remove(position);
                    adapter.notifyItemRemoved(position);
                    // Lưu danh sách mới xuống file
                    Gson gson = new Gson();
                    String json = gson.toJson(finalAppointment);
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
                    String text = finalAppointment.get(position).getLocation();

                    ((MainActivity)getActivity()).SearchAddress = text;
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frameLayout, new MapFragment()).addToBackStack(null).commit();

                }

            }
        };
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(appointmentRV);

        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Sau khi load xong, gọi phương thức setRefreshing(false) để đóng `SwipeRefreshLayout`
                swipeRefreshLayout.setRefreshing(false);
                // Cập nhật lại danh sách dữ liệu
                List<Appointment> updatedList = new ArrayList<>();
                File directory = getActivity().getApplicationContext().getFilesDir();
                File file = new File(directory, "appointments.json");
                try {
                    FileReader fileReader = new FileReader(file);
                    Type type = new TypeToken<ArrayList<Appointment>>(){}.getType();
                    Gson gson = new Gson();
                    updatedList = gson.fromJson(fileReader, type);
                    fileReader.close();
                } catch (FileNotFoundException e) {
                    System.err.println("Loi fileReader");
                } catch (IOException e) {
                    System.err.println("Loi close fileReader");
                }
                adapter.updateData(updatedList);
                adapter.notifyDataSetChanged();
            }

        });
        //SearchView
        SearchView searchView = view.findViewById(R.id.searchView);
        final List<Appointment> searchList = appointmentsList;
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String query) {
                AppointmentAdapter adapter;
                List<Appointment> filteredList = new ArrayList<Appointment>();
                if (query.length() != 0) { // Nếu query rỗng thì hiển thị lại toàn bộ danh sách
                    for (Appointment appointment : searchList) {
                        if (appointment.getName().toLowerCase().contains(query.toLowerCase())) {
                            filteredList.add(appointment);
                            System.out.println("1 cái");

                        }
                    }
                    adapter = new AppointmentAdapter(getActivity(), filteredList);
                    appointmentRV.setAdapter(adapter);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    appointmentRV.setLayoutManager(layoutManager);
                    adapter.notifyDataSetChanged();
                } else {
                    filteredList.addAll(searchList);
                    adapter = new AppointmentAdapter(getActivity(), filteredList);
                    appointmentRV.setAdapter(adapter);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    appointmentRV.setLayoutManager(layoutManager);
                    adapter.notifyDataSetChanged();
                }
                return true;
            }
        });


        return view;
    }
}

