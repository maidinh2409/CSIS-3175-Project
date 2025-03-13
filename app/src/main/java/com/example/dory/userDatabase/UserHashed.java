package com.example.dory.userDatabase;

public class UserHashed {
    private String name;
    private String email;
    private String salt;
    private String hash;
    private String role;
    private String profilePhoto;
    private String organizationName;
    private String contactInfo;
    private int user_id;

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
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

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public UserHashed(String name, String email, String salt, String hash, String role){
        this.name = name;
        this.email = email;
        this.salt = salt;
        this.hash = hash;
        this.role = role;
    }
    public UserHashed(String name, String email, String salt, String hash, String role, String photo, String organizationName, String contactInfo){
        this.name = name;
        this.email = email;
        this.salt = salt;
        this.hash = hash;
        this.role = role;
        this.profilePhoto = photo;
        this.organizationName = organizationName;
        this.contactInfo = contactInfo;
    }

    public UserHashed(String name, String email, String salt, String hash, String role, String photo, String organizationName, String contactInfo, int user_id){
        this.name = name;
        this.email = email;
        this.salt = salt;
        this.hash = hash;
        this.role = role;
        this.profilePhoto = photo;
        this.organizationName = organizationName;
        this.contactInfo = contactInfo;
        this.user_id = user_id;
    }
}
