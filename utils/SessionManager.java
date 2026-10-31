package com.example.airlinemanagementsystem.utils;

import com.example.airlinemanagementsystem.models.Flight;

public class SessionManager {

    private static SessionManager instance;
    private int    userId;
    private String username;
    private String name;
    private String role;
    private Flight selectedFlight;
    private String selectedClass;
    private String selectedSeatNumber;
    private double discountAmount;
    private double totalAmount;
    private int    lastBookingId;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) instance = new SessionManager();
        return instance;
    }

    public void login(int userId, String username, String name, String role) {
        this.userId   = userId;
        this.username = username;
        this.name     = name;
        this.role     = role;
    }

    public void logout() {
        userId             = 0;
        username           = null;
        name               = null;
        role               = null;
        selectedFlight     = null;
        selectedClass      = null;
        selectedSeatNumber = null;
        discountAmount     = 0;
        totalAmount        = 0;
        lastBookingId      = 0;
    }

    public int     getUserId()    { return userId; }
    public String  getUsername()  { return username; }
    public String  getName()      { return name; }
    public String  getRole()      { return role; }
    public boolean isLoggedIn()   { return username != null; }

    public Flight getSelectedFlight()                      { return selectedFlight; }
    public void   setSelectedFlight(Flight f)              { this.selectedFlight = f; }

    public String getSelectedClass()                       { return selectedClass; }
    public void   setSelectedClass(String s)               { this.selectedClass = s; }

    public String getSelectedSeatNumber()                  { return selectedSeatNumber; }
    public void   setSelectedSeatNumber(String s)          { this.selectedSeatNumber = s; }

    public double getDiscountAmount()                      { return discountAmount; }
    public void   setDiscountAmount(double d)              { this.discountAmount = d; }

    public double getTotalAmount()                         { return totalAmount; }
    public void   setTotalAmount(double t)                 { this.totalAmount = t; }

    public int    getLastBookingId()                       { return lastBookingId; }
    public void   setLastBookingId(int id)                 { this.lastBookingId = id; }
}