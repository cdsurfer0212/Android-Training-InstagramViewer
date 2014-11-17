package com.example.instagramviewer.model;

import java.io.Serializable;

public class Photo implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private String id = "";
    private String caption = "";
    private int commentsCount;
    private int imageHeight;
    private String imageUrl = "";
    private int likesCount;
    private String timestamp;
    
    private User user;
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getCaption() {
        return caption;
    }
    public void setCaption(String caption) {
        this.caption = caption;
    }
    public int getCommentsCount() {
        return commentsCount;
    }
    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }
    public int getImageHeight() {
        return imageHeight;
    }
    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public int getLikesCount() {
        return likesCount;
    }
    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public String getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

 
    //public String toString() {
    //    return "image - " + imageUrl;
    //}
}
