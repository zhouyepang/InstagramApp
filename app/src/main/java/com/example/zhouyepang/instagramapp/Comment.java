package com.example.zhouyepang.instagramapp;

public class Comment {

    public String imageId;
    public String userId;
    public String content;

    public Comment() {
    }

    public Comment(String imageId, String userId, String content) {
        this.imageId = imageId;
        this.userId = userId;
        this.content = content;
    }

    public String getImageId(){
        return imageId;
    }

    public String getUserId(){
        return userId;
    }

    public String getContent(){
        return content;
    }
}

