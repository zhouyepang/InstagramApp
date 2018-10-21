package com.example.zhouyepang.instagramapp;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    private EditText userName, fullName, countryName;
    private Button saveInforButton;
    private CircleImageView profileImage;
    FirebaseUser fbUser;
    DatabaseReference database;

    static final int RC_IMAGE_GALLERY = 2;

    private FirebaseAuth myAuth;
    private DatabaseReference usersRef;

    private StorageReference userRrofileImageRef;

    private String displayName;
    private FirebaseUser currentUser;

    private String currentUserID;

    final static int gallery_pick = 1;
    Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        fbUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fbUser == null) {
            finish();
        }

        database = FirebaseDatabase.getInstance().getReference();

        myAuth = FirebaseAuth.getInstance();
        currentUser = myAuth.getCurrentUser();
        currentUserID = myAuth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("usersProfile").child(currentUserID);
        userRrofileImageRef = FirebaseStorage.getInstance().getReference().child("profile Images");
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
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePhoto();
            }
        });

    }

    private void uploadAva() {
        com.example.zhouyepang.instagramapp.SendData.uploadImage(SetupActivity.this, fbUser, imageUri, database, "avatars", false, "");
    }

    private void choosePhoto(){
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, RC_IMAGE_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_IMAGE_GALLERY && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            profileImage.setImageURI(imageUri);
        }
    }
    private void saveAccountSetupInfor() {

        String username = userName.getText().toString();
        String fullname = fullName.getText().toString();
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
                if(currentUser!=null) {
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(fullName.getText().toString()).build();
                    currentUser.updateProfile(profileUpdates);
                }
                sendUserToProfilePage();
                Toast.makeText(this,"Your profile is updated successfully!",Toast.LENGTH_SHORT).show();



            } catch (Exception e){

                Log.d("Error","Fail to update profile");

            }
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
        uploadAva();
    }
}