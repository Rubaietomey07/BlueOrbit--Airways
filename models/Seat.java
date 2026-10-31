package com.example.airlinemanagementsystem.models;

public class Seat {
    private int seatId;
    private int flightId;
    private String seatNumber;
    private String seatClass;
    private String status;

    public int    getSeatId()     { return seatId; }
    public int    getFlightId()   { return flightId; }
    public String getSeatNumber() { return seatNumber; }
    public String getSeatClass()  { return seatClass; }
    public String getStatus()     { return status; }

    public void setSeatId(int seatId)         { this.seatId = seatId; }
    public void setFlightId(int flightId)     { this.flightId = flightId; }
    public void setSeatNumber(String s)       { this.seatNumber = s; }
    public void setSeatClass(String s)        { this.seatClass = s; }
    public void setStatus(String status)      { this.status = status; }
}