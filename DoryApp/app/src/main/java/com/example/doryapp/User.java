package com.example.doryapp;

public class User {
    int id;
    String name, email, password, company, role;

    public User(int id, String name, String email, String password, String company, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.company = company;
        this.role = role;
    }

    // Getters (optional)
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getCompany() { return company; }
    public String getRole() { return role; }
}
