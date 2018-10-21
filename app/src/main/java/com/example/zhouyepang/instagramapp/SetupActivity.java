package com.example.zhouyepang.instagramapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
    static final int RC_PERMISSION_READ_EXTERNAL_STORAGE = 1;
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

        //select photo by gallery
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery(view);
            }
        });

        /*usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {
                    String image = dataSnapshot.child("profileimage").getValue().toString();

                    Picasso.get().load(image).placeholder(R.drawable.ic_account_circle_black_24dp).into(profileImage);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });  */

    }

    private void uploadAva() {
        com.example.zhouyepang.instagramapp.SendData.uploadImage(SetupActivity.this, fbUser, imageUri, database, "avatars", false, "");
    }

    //intent to system gallery to choose photo
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

    // method for this fragment to request and get permission for opening gallery from user
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == RC_PERMISSION_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                choosePhoto();
            }
        }
    }

    //open gallery method with check and request permission
    public void openGallery(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, RC_PERMISSION_READ_EXTERNAL_STORAGE);
        } else {
            choosePhoto();
        }
    }
/*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==gallery_pick && requestCode==RESULT_OK && data!=null) {
            imageUri = data.getData();
            System.out.println("image uri "+data.getData());
            profileImage.setImageURI(imageUri);

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    //.setAspectRatio(1,1)
                    .start(this);


        }

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode==RESULT_OK) {
                Uri resultUri = result.getUri();

                StorageReference filePath = userRrofileImageRef.child(currentUserID + ".jpg");
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(SetupActivity.this,"Profile Image stored to Firebase storage!", Toast.LENGTH_SHORT).show();

                            final String downloadUrl = task.getResult().getDownloadUrl().toString();
                            usersRef.child("profileimage").setValue(downloadUrl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()) {
                                                Intent selfIntent = new Intent(SetupActivity.this, SetupActivity.class);
                                                startActivity(selfIntent);
                                                Toast.makeText(SetupActivity.this, "Profile Image stored to Firebase database!", Toast.LENGTH_SHORT).show();
                                            }
                                            else {
                                                String message = task.getException().getMessage();
                                                Toast.makeText(SetupActivity.this, "Error occurred: "+message, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }
                });

            } else {
                Toast.makeText(this, "Error occurred: image cannot be cropped.", Toast.LENGTH_SHORT).show();
            }
        }
    }
*/
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

               /* myAuth.addAuthStateListener (new FirebaseAuth.AuthStateListener() {
                    @Override
                    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if(user!=null){
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(fullName.getText().toString()).build();
                            user.updateProfile(profileUpdates);

                        }
                    }
                }); */
                sendUserToProfilePage();
                Toast.makeText(this,"Your profile is updated successfully!",Toast.LENGTH_SHORT).show();



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

        /*StorageReference filePath = userRrofileImageRef.child(currentUserID + ".jpg");
        filePath.putFile(imageUri);

        if (imageUri != null) {
            usersRef.child("profileimage").setValue(imageUri);
        } */

        FirebaseDatabase.getInstance().getReference().child("users").child(currentUserID).child("displayName").setValue(a);
        uploadAva();
    }
}
