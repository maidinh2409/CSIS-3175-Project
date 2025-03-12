package com.example.dory.userDatabase;

public class User {
    private String name;
    private String email;
    private byte[] salt;
    private byte[] hash;
    private String role;
    private String photo;
    private int id;

    public User(String name, String email, byte[] salt, byte[] hash, String role){
        this.name = name;
        this.email = email;
        this.salt = salt;
        this.hash = hash;
        this.role = role;
    }
    public User(String name, String email, byte[] salt, byte[] hash, String role, String photo){
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

    public byte[] getHash() {
        return hash;
    }

    public void setHash(byte[] hash) {
        this.hash = hash;
    }

    public byte[] getSalt() {
        return salt;
    }

    public void setSalt(byte[] salt) {
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
