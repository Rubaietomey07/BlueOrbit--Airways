package com.example.airlinemanagementsystem.models;

import java.time.LocalDate;

public class Flight {
    private int flightId;
    private String flightNumber;
    private String source;
    private String destination;
    private LocalDate date;
    private double economyPrice;
    private double businessPrice;
    private double firstPrice;

    public int getFlightId()         { return flightId; }
    public String getFlightNumber()  { return flightNumber; }
    public String getSource()        { return source; }
    public String getDestination()   { return destination; }
    public LocalDate getDate()       { return date; }
    public double getEconomyPrice()  { return economyPrice; }
    public double getBusinessPrice() { return businessPrice; }
    public double getFirstPrice()    { return firstPrice; }

    public void setFlightId(int flightId)            { this.flightId = flightId; }
    public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }
    public void setSource(String source)             { this.source = source; }
    public void setDestination(String destination)   { this.destination = destination; }
    public void setDate(LocalDate date)              { this.date = date; }
    public void setEconomyPrice(double economyPrice) { this.economyPrice = economyPrice; }
    public void setBusinessPrice(double businessPrice){ this.businessPrice = businessPrice; }
    public void setFirstPrice(double firstPrice)     { this.firstPrice = firstPrice; }
}