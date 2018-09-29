package com.example.zhouyepang.instagramapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class MainActivity extends AppCompatActivity {

    public static final int RC_SIGN_IN = 123;
    public static DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, MainPage.class);
        startActivity(intent);

        database = FirebaseDatabase.getInstance().getReference();

        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
        if(fbUser != null) {
            // User already signed in
            String token = FirebaseInstanceId.getInstance().getToken();

            // save the user info in the database to users/UID/
            // we'll use the UID as part of the path
            User user = new User(fbUser.getUid(), fbUser.getDisplayName(), token);
            database.child("users").child(user.uid).setValue(user);

        }
    }

}
