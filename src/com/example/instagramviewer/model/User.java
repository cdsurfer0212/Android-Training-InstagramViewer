package com.example.instagramviewer.model;

import java.io.Serializable;

public class User implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private String fullName;
    private String profilePicture;
    
    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    public String getProfilePicture() {
        return profilePicture;
    }
    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
    
    
}
