package com.example.termproject;

import android.location.Location;

public class UserModel {

    public double lat,longi;
    public String location;
    public String uid;
    public String username;
    public UserModel() {

    }

    public UserModel(double lat,double longi, String username,String location,String uid) {
        this.lat = lat;
        this.uid = uid;
        this.location = location;
        this.longi = longi;
        this.username = username;
    }

}
