package com.example.zhouyepang.instagramapp;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class LookUpUserID {

    public String id;
    public User user;
    public String name;
    public ArrayList <User> users;
    LookUpUserID(){
        users = new ArrayList <User>();
    }

    public void updateUserLists(User user){
        if(!users.contains(user))
            users.add(user);
    }
    public int getUserListSize(){
        return users.size();
    }

    public String lookUpUserNamebyId(String id){
        for (User user : users){
            if(user.getUid().equals(id)){
                return user.getDisplayName();
            }
        }
        return "Anonymous User";
    }
    public String getName(){
        return name;
    }
}
