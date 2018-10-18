package com.example.zhouyepang.instagramapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    private EditText userName, fullName, countryName;
    private Button saveInforButton;
    private CircleImageView profileImage;

    private FirebaseAuth myAuth;
    private DatabaseReference usersRef;
    private String displayName;

    String currentUserID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        myAuth = FirebaseAuth.getInstance();
        currentUserID = myAuth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("usersProfile").child(currentUserID);
        displayName = myAuth.getCurrentUser().getDisplayName();

        userName = (EditText) findViewById(R.id.setup_username);
        fullName = (EditText) findViewById(R.id.setup_full_name);
        fullName.setText(displayName);
        countryName = (EditText) findViewById(R.id.setup_country_name);
        saveInforButton = (Button) findViewById(R.id.setup_information_button);
        profileImage = (CircleImageView) findViewById(R.id.setup_profile_image);

        saveInforButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAccountSetupInfor();
            }
        });
    }

    private void saveAccountSetupInfor() {

        String username = userName.getText().toString();
        final String fullname = fullName.getText().toString();
        String country = countryName.getText().toString();



        if(TextUtils.isEmpty(username)) {
            Toast.makeText(this,"Please write your username...",Toast.LENGTH_SHORT).show();

        }

        else if(TextUtils.isEmpty(fullname)) {
            Toast.makeText(this,"Please write your full name...",Toast.LENGTH_SHORT).show();

        }

        else if(TextUtils.isEmpty(country)) {
            Toast.makeText(this,"Please write your country...",Toast.LENGTH_SHORT).show();

        }
        else {

            try{

                updateInfo(fullname, username, country);

                myAuth.addAuthStateListener (new FirebaseAuth.AuthStateListener() {
                    @Override
                    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if(user!=null){
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(fullname).build();
                            user.updateProfile(profileUpdates);

                        }
                    }
                });
                sendUserToProfilePage();
                Toast.makeText(this,"Your profile is updated successfully!",Toast.LENGTH_SHORT).show();
                //sendUserToProfilePage();


            } catch (Exception e){

                Log.d("Error","Fail to update profile");

            }

            //updateInfo(fullname, username, country)

            //usersRef.child("displayName").setValue(fullname);
            //usersRef.child("username").setValue(username);
            //usersRef.child("country").setValue(country);

            //Toast.makeText(this,"Your profile is updated successfully!",Toast.LENGTH_SHORT).show();


        }
    }

    private void sendUserToProfilePage() {
        Intent profileIntent = new Intent (this, Profile.class);
        profileIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(profileIntent);
        finish();
    }

    private void updateInfo(String a, String b, String c) {
        usersRef.child("displayName").setValue(a);
        usersRef.child("username").setValue(b);
        usersRef.child("country").setValue(c);
        FirebaseDatabase.getInstance().getReference().child("users").child(currentUserID).child("displayName").setValue(a);
    }
}
