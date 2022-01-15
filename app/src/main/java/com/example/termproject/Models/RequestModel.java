package com.example.termproject.Models;

public class RequestModel {
    private String requestedto;
    private String requestedby;
    private String status;
    public RequestModel()
    {

    }
    public RequestModel(String reqid,String usrid,String status) {
        this.requestedto = reqid;
        this.requestedby = usrid;
        this.status = status;
    }

    public String getRequestedto() {
        return requestedto;
    }

    public void setRequestedto(String requestedto) {
        this.requestedto = requestedto;
    }

    public String getRequestedby() {
        return requestedby;
    }

    public void setRequestedby(String requestedby) {
        this.requestedby = requestedby;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
