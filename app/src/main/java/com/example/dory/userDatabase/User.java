package com.example.dory.userDatabase;

public class User {
    private int id;
    private String name;
    private String email;
    private String salt;
    private String hash;
    private String role;
    private String photo;

    public User(int id, String name, String email, String salt, String hash, String role){
        this.id = id;
        this.name = name;
        this.email = email;
        this.salt = salt;
        this.hash = hash;
        this.role = role;
    }
    public User(int id, String name, String email, String salt, String hash, String role, String photo){
        this.id = id;
        this.name = name;
        this.email = email;
        this.salt = salt;
        this.hash = hash;
        this.role = role;
        this.photo = photo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
