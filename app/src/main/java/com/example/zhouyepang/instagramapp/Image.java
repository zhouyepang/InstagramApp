package com.example.zhouyepang.instagramapp;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Image {
    public String key;
    public String userId;
    public String downloadUrl;
    public String imageType;

    @Exclude
    public User user;

    @Exclude
    public int likes = 0;

    @Exclude
    public boolean hasLiked = false;

    @Exclude
    public String userLike;

    public Image() {
    }

    public Image(String key, String userId, String downloadUrl, String imageType) {
        this.key = key;
        this.userId = userId;
        this.downloadUrl = downloadUrl;
        this.imageType = imageType;
    }

}