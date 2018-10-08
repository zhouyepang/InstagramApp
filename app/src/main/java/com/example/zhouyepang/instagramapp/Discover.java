package com.example.zhouyepang.instagramapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Discover extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);
    }

    public void searchUser(View view) {
        //EditText enteredUserName = (EditText)findViewById(R.id.enteredUserName);
        //database.searchUserNameList(enteredUserName.getText().toString());
    }
}
