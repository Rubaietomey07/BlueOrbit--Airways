package com.example.airlinemanagementsystem.models;

public class User {
    private int    userId;
    private String name;
    private String username;
    private String password;
    private String role;
    private String status;

    public int    getUserId()   { return userId; }
    public String getName()     { return name; }
    public String getUsername() { return username; }
    public String getRole()     { return role; }
    public String getStatus()   { return status; }

    public void setUserId(int userId)    { this.userId = userId; }
    public void setName(String name)     { this.name = name; }
    public void setUsername(String u)    { this.username = u; }
    public void setPassword(String p)    { this.password = p; }
    public void setRole(String role)     { this.role = role; }
    public void setStatus(String status) { this.status = status; }
}