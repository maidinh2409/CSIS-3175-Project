package com.example.dory.userDatabase;

import java.io.Serializable;

/**
 * A class to create User objects which are used by UserDBHandler to add or
 * update users.
 * No methods besides constructors, getters and setters;
 */
public class User implements Serializable {
    private String name;
    private String email;
    private String password;
    private String role;
    private String profilePhoto;
    private String organizationName;
    private String contactInfo;

    /**
     * Creates a User object with a name, email, password, and role. The photo,
     * organization name, and contact info are optional.
     * 
     * @param name             the user's name
     * @param email            the user's email
     * @param password         the user's password
     * @param role             the user's role (attendee or organizer)
     * @param organizationName (optional) the user's organization name
     */

    public User(String name, String email, String password, String role, String organizationName) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.organizationName = organizationName;
        this.contactInfo = null;
        this.profilePhoto = null;
    };

    public User(String name, String email, String organizationName, String contactInfo) {
        this.name = name;
        this.email = email;
        this.organizationName = organizationName;
        this.contactInfo = contactInfo;
        this.profilePhoto = null;
    };

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
}
