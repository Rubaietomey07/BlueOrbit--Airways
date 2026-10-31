package com.example.airlinemanagementsystem.models;

public class Booking {
    private int    bookingId;
    private String flightNumber;
    private String source;
    private String destination;
    private String date;
    private String seatNumber;
    private String seatClass;
    private double amount;
    private String paymentMethod;
    private String status;
    private String bookingDate;

    public int    getBookingId()      { return bookingId; }
    public String getFlightNumber()   { return flightNumber; }
    public String getSource()         { return source; }
    public String getDestination()    { return destination; }
    public String getDate()           { return date; }
    public String getSeatNumber()     { return seatNumber; }
    public String getSeatClass()      { return seatClass; }
    public double getAmount()         { return amount; }
    public String getPaymentMethod()  { return paymentMethod; }
    public String getStatus()         { return status; }
    public String getBookingDate()    { return bookingDate; }

    public void setBookingId(int v)       { this.bookingId = v; }
    public void setFlightNumber(String v) { this.flightNumber = v; }
    public void setSource(String v)       { this.source = v; }
    public void setDestination(String v)  { this.destination = v; }
    public void setDate(String v)         { this.date = v; }
    public void setSeatNumber(String v)   { this.seatNumber = v; }
    public void setSeatClass(String v)    { this.seatClass = v; }
    public void setAmount(double v)       { this.amount = v; }
    public void setPaymentMethod(String v){ this.paymentMethod = v; }
    public void setStatus(String v)       { this.status = v; }
    public void setBookingDate(String v)  { this.bookingDate = v; }
}