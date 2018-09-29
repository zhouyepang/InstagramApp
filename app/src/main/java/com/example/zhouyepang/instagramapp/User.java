package com.example.zhouyepang.instagramapp;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {
    public String uid;
    public String displayName;
    public String token;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String uid, String displayName, String token) {
        this.uid = uid;
        this.displayName = displayName;
        this.token = token;
    }

    public String getUid(){
        return uid;
    }

    public String getDisplayName(){
        return displayName;
    }

    public String getToken(){
        return token;
    }

}
