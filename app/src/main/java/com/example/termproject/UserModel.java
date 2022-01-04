package com.example.termproject;

public class UserModel {

    private long lat;
    private long longi;
    private String location;
    private String uid;
    private String username;
    public UserModel() {

    }

    public long getLat() {
        return lat;
    }

    public void setLat(long lat) {
        this.lat = lat;
    }

    public long getLongi() {
        return longi;
    }

    public void setLongi(long longi) {
        this.longi = longi;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserModel(long lat, long longi, String username, String location, String uid) {
        this.lat = lat;
        this.uid = uid;
        this.location = location;
        this.longi = longi;
        this.username = username;
    }

}
