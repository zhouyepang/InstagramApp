package com.example.zhouyepang.instagramapp;

import com.google.firebase.database.Exclude;

public class Post {
    public String key;
    public String userId;
    public String imageId;
    public String downloadUrl;
    public String description;
    public String timeStamp;


    @Exclude
    public User user;

    @Exclude
    public int likes = 0;

    @Exclude
    public boolean hasLiked = false;

    @Exclude
    public String userLike;

    public Post() {
    }

    public Post(String key, String userId, String imageId, String downloadUrl, String description, String timeStamp) {
        this.key = key;
        this.userId = userId;
        this.imageId = imageId;
        this.downloadUrl = downloadUrl;
        this.description = description;
        this.timeStamp = timeStamp;
    }

    public void addLike() {
        this.likes++;
    }

    public void removeLike() {
        this.likes--;
    }
}
