package com.example.anew;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import androidx.appcompat.app.AppCompatActivity;


import com.google.gson.Gson;

import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DecimalFormat;

import java.util.ArrayList;



public class AddAppointment extends AppCompatActivity {
    EditText nameEditText;
    EditText locationEditText;

    TimePicker timepick;
    private static final String TAG = "MainActivity";

    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_appointment);
        timepick = findViewById(R.id.time_picker);
        timepick.setIs24HourView(true);
        nameEditText = findViewById(R.id.edittext_name);
        locationEditText = findViewById(R.id.otherLocationEditText);


// Lấy tham chiếu đến RadioGroup và thiết lập trình nghe sự kiện khi chọn radio button
        RadioGroup locationRadioGroup = findViewById(R.id.location_radio_group);
        locationRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Kiểm tra xem radio button nào đã được chọn
                if (checkedId == R.id.current_location_radio_button) {
                    // Chọn vị trí hiện tại
                    // Ẩn EditText nếu đang hiển thị
                    locationEditText.setVisibility(View.GONE);
                } else if (checkedId == R.id.other_location_radio_button) {
                    // Chọn vị trí khác
                    // Hiển thị EditText để người dùng nhập địa chỉ vị trí khác
                    locationEditText.setVisibility(View.VISIBLE);
                }
            }
        });

        Button saveButton = findViewById(R.id.button_save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lưu thông tin mới vào cơ sở dữ liệu
                saveData();
                setAlarm(getBaseContext(), timepick.getHour(),timepick.getMinute());


            }
        });
        mDisplayDate = (TextView) findViewById(R.id.tvDate);

        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        AddAppointment.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;


                // định dạng các số tháng và ngày
                DecimalFormat df = new DecimalFormat("00");
                String formattedMonth = df.format(month);
                String formattedDay = df.format(day);

                String date = formattedMonth + "/" + formattedDay + "/" + year;
                mDisplayDate.setText(date);
                Log.d(TAG, "onDateSet: mm/dd/yyy: " + formattedMonth + "/" + formattedDay + "/" + year);

            }
        };



    }

    private void saveData() {
        // Lấy thông tin từ các trường nhập liệu
        String name = nameEditText.getText().toString();
        String location = locationEditText.getText().toString();
        String date = mDisplayDate.getText().toString();
        DecimalFormat df = new DecimalFormat("00");
        String hour = String.valueOf(timepick.getHour());
        String minute = String.valueOf((timepick.getMinute()));
        // Lưu thông tin mới vào cơ sở dữ liệu
        Appointment appointment = new Appointment(name,location,date,hour,minute,true);

        File directory = getApplicationContext().getFilesDir();
        File file = new File(directory, "appointments.json");

        ArrayList<Appointment> AppointmentsList;

        if (file.exists()) {
            try{
                FileReader fileReader = new FileReader(file);
                Type type = new TypeToken<ArrayList<Appointment>>(){}.getType();
                Gson gson = new Gson();
                AppointmentsList = gson.fromJson(fileReader, type);
                fileReader.close();
                AppointmentsList.add(appointment);
                try {
                    FileWriter fileWriter = new FileWriter(file);
                    gson.toJson(AppointmentsList, fileWriter);
                    fileWriter.close();
                    showDialog();
                } catch (IOException e) {
                    System.err.println("Loi viet file");
                }

            } catch (FileNotFoundException e) {
                System.err.println("Loi fileReader");
            } catch (IOException e) {
                System.err.println("Loi close fileReader");
            }
        }else {
            AppointmentsList = new ArrayList<>();
            AppointmentsList.add(appointment);

            try {
                FileWriter fileWriter = new FileWriter(file);
                Gson gson = new Gson();
                gson.toJson(AppointmentsList, fileWriter);
                fileWriter.close();
            } catch (IOException e) {
                System.err.println("Loi viet file");
            }
        }

    }
    private void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thông báo");
        builder.setMessage("Thêm cuộc hẹn thành công");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Trở về màn hình chính
                finish();
            }
        });
        builder.show();
    }
    private void setAlarm(Context context, int hour, int minute) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        long alarmTime = calendar.getTimeInMillis();
        System.out.println(alarmTime);
        if (alarmTime <= System.currentTimeMillis()) {
            // Nếu thời gian báo thức đã qua, thêm một ngày để báo thức được kích hoạt vào thời điểm mong muốn vào ngày hôm sau
            alarmTime += 24 * 60 * 60 * 1000;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
        }
    }
}

