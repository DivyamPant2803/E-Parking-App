package com.example.paytmgateway;

public class Users {
    public String name,contact;
    public String vehicle_number,uniqueId;
    public String time;
    //public int uniqueId;

    public Users(){

    }

    public Users(String name, String contact, String vehicle_number,String uniqueId,String time) {
        this.name = name;
        this.contact = contact;
        this.vehicle_number = vehicle_number;
        this.uniqueId = uniqueId;
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getVehicle_number() {
        return vehicle_number;
    }

    public void setVehicle_number(String vehicle_number) {
        this.vehicle_number = vehicle_number;
    }
}
