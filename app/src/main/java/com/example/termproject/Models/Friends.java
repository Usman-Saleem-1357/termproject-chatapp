package com.example.termproject.Models;

public class Friends {
    private String id;
    private String reverseid;
    public Friends(){

    }

    public Friends(String id,String reverseid) {
        this.id = id;
        this.reverseid = reverseid;
    }

    public String getReverseid() {
        return reverseid;
    }

    public void setReverseid(String reverseid) {
        this.reverseid = reverseid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
