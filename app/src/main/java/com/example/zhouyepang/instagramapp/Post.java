package com.example.zhouyepang.instagramapp;

import com.google.firebase.database.Exclude;

public class Post {
    public String key;
    public String userId;
    public String downloadUrl;
    public String comment;


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

    public Post(String key, String userId, String downloadUrl) {
        this.key = key;
        this.userId = userId;
        this.downloadUrl = downloadUrl;
    }

    public void addLike() {
        this.likes++;
    }

    public void removeLike() {
        this.likes--;
    }
}
