package com.example.anew;

public class Appointment {
    private String name;
    private String location;
    private String date;
    private String hour ;
    private String minute ;
    private boolean isChecked;


    public Appointment(String name, String location , String date, String hour, String minute, boolean isChecked) {

        this.name = name;
        this.location = location;
        this.date = date;
        this.hour = hour;
        this.minute = minute;
        this.isChecked = isChecked;
    }
    public String getName(){
        return name;
    }
    public String getLocation(){
        return location;
    }

    public String getDate(){
        return date;
    }

    public String getHour() {
        return hour;
    }

    public String getMinute() {
        return minute;
    }
    public boolean isChecked(){
        return isChecked;
    }
    public void setChecked(boolean isChecked){
        this.isChecked=isChecked;
    }


}

