package com.example.anew;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {
        private Context mContext;
        private List<Appointment> appointmentList;
        public AppointmentAdapter(Context context, List<Appointment> appointmentList) {
        this.mContext = context;
        this.appointmentList = appointmentList;
        }
    public void updateData(List<Appointment> newAppointmentList) {
        appointmentList.clear();
        appointmentList.addAll(newAppointmentList);
        notifyDataSetChanged();
    }
    @Override
    public AppointmentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_appointment, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AppointmentAdapter.AppointmentViewHolder holder, int position) {
        Appointment appointment = appointmentList.get(holder.getAdapterPosition());
        if (appointment == null) {
            return;
        }
        holder.textViewName.setText(appointment.getName());
        holder.textViewAddress.setText(appointment.getLocation());
        holder.textViewDate.setText(appointment.getDate());
        holder.timeTextView.setText(appointment.getHour()+":"+appointment.getMinute());
        holder.aSwitch.setChecked(appointment.isChecked());
        // Set up swipe to delete
        holder.itemView.setOnTouchListener(new OnSwipeTouchListener(mContext) {
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
        if(appointmentList != null){
            return appointmentList.size();
        }
            return  0;
    }

    public static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewName;
        public TextView textViewAddress;
        public TextView textViewDate;
        public TextView timeTextView;
        public Switch aSwitch;

        public AppointmentViewHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.name_text_view);
            textViewAddress = itemView.findViewById(R.id.location_text_view);
            textViewDate = itemView.findViewById(R.id.date_text_view);
            timeTextView = itemView.findViewById(R.id.time_text_view);
            aSwitch = itemView.findViewById(R.id.notification_switch);
        }


    }

}

