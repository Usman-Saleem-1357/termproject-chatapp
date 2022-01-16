package com.example.termproject.Models;

import com.google.type.DateTime;

import java.sql.Date;
import java.sql.Timestamp;

public class ChatModel {
    private String message,senderid,receiverid;
    private String dateTime;

    public ChatModel() {
    }

    public ChatModel(String message, String senderid, String receiverid, String dateTime) {
        this.message = message;
        this.senderid = senderid;
        this.receiverid = receiverid;
        this.dateTime = dateTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderid() {
        return senderid;
    }

    public void setSenderid(String senderid) {
        this.senderid = senderid;
    }

    public String getReceiverid() {
        return receiverid;
    }

    public void setReceiverid(String receiverid) {
        this.receiverid = receiverid;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
