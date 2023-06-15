package com.example.anew;

public class Location {
    private String nameAddress;

    private String address;

    private String type;


    public Location(String name, String address, String type ) {

        this.nameAddress = name;
        this.address = address;
        this.type = type;
    }
    public String getNameAddress(){
        return nameAddress;
    }
    public String getAddress(){
        return address;
    }

    public String getType() {
        return type;
    }

}
