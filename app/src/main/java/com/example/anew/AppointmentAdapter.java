package com.example.anew;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
        Appointment appointment = appointmentList.get(position);
        if (appointment == null) {
            return;
        }
        holder.textViewName.setText(appointment.getName());
        holder.textViewAddress.setText(appointment.getLocation());
        holder.textViewDate.setText(appointment.getDate());
        holder.timeTextView.setText(appointment.getHour()+":"+appointment.getMinute());
        holder.aSwitch.setChecked(appointment.isChecked());
        holder.aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                // Update appointment object with new switch state
                appointment.setChecked(isChecked);
                appointmentList.set(position, appointment);

                // Convert the list to a JSON string
                Gson gson = new Gson();
                String json = gson.toJson(appointmentList);

                // Write the JSON string to the file
                File directory = mContext.getApplicationContext().getFilesDir();
                File file = new File(directory, "appointments.json");
                try {
                    FileWriter fileWriter = new FileWriter(file);
                    fileWriter.write(json);
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if (isChecked){
                setAlarm(appointment);
            }
            else {
                cancelAlarm(appointment);
            }

            }
        });
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
    private void setAlarm(Appointment appointment) {
        if (appointment.isChecked()) {
            // Convert appointment date and time to milliseconds
            long appointmentTimeMillis = convertToMillis(appointment.getDate(), appointment.getHour(), appointment.getMinute());

            // Get a reference to the alarm manager
            AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

            // Create a new intent that will be fired when the alarm goes off
            Intent intent = new Intent(mContext, AlarmReceiver.class);
            intent.putExtra("appointment_name", appointment.getName());

            // Create a pending intent that wraps the intent above
            PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, 0);

            // Set the alarm to go off at the appointment time
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, appointmentTimeMillis, pendingIntent);
        } else {
            // Cancel any existing alarms for this appointment
            Intent intent = new Intent(mContext, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, 0);
            AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
        }
        Toast.makeText(mContext, "Bật báo thức thành công!", Toast.LENGTH_SHORT).show();
    }

    private long convertToMillis(String date, String hour, String minute) {
        // Parse the appointment date and time
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String dateTimeStr = date + " " + hour + ":" + minute;
        Date dateTime;
        try {
            dateTime = dateFormat.parse(dateTimeStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }

        // Convert to milliseconds since epoch
        return dateTime.getTime();
    }
    private void cancelAlarm(Appointment appointment) {
        // Create a PendingIntent for the alarm with the same intent as the original alarm
        Intent intent = new Intent(mContext, AlarmReceiver.class);
        intent.putExtra("name", appointment.getName());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, appointment.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Cancel the alarm
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        Toast.makeText(mContext, "Hủy báo thức thành công!", Toast.LENGTH_SHORT).show();
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
        private List<Appointment> appointments;

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

