package com.example.dory.userDatabase;

import android.net.Uri;

import java.io.Serializable;

/**
 * A class to create User objects, differs from the User class in that this one does not have a
 * password value but instead a salt and a hash value and also a user_id value. This class is meant
 * to contain values obtained by the database, not meant to be created with user inputted values.
 */
public class UserHashed implements Serializable {
    private String name;
    private String email;
    private String salt;
    private String hash;
    private String role;
    private String profilePhoto;
    private String organizationName;
    private String contactInfo;
    private int user_id;

    /**
     * Constructor to create a UserHashed object. Again, this is meant to receive values retrieved
     * from the database, not values obtained from a user's input.
     *
     * @param name             the user's name
     * @param email            the user's email
     * @param salt             the user's salt
     * @param hash             the user's password hashed with the salt
     * @param role             the user's role (organizer or attendee)
     * @param photo            the user's profile photo (can be null)
     * @param organizationName the user's organization name (can be null)
     * @param contactInfo      the user's contact info (can be null)
     * @param user_id          the user's id
     */
    public UserHashed(String name, String email, String hash, String salt, String role, String photo, String organizationName, String contactInfo, int user_id) {
        this.name = name;
        this.email = email;
        this.hash = hash;
        this.salt = salt;
        this.role = role;
        this.profilePhoto = photo;
        this.organizationName = organizationName;
        this.contactInfo = contactInfo;
        this.user_id = user_id;
    }

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
}
