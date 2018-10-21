package com.example.zhouyepang.instagramapp;

/**
 * Suggestion score class for suggestion algorithm
 */
public class SuggestUserScore {

    public String userId;
    public int score;

    SuggestUserScore(String userId){
        this.userId = userId;
        this.score = 1;
    }

    public String getUserId(){
        return this.userId;
    }
    public void updateScore(){
        score = score + 1;
    }

    public int getScore(){
        return score;
    }

    public boolean isSameUser(String userId){
        return this.userId == userId;
    }
}
