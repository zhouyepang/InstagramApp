package com.example.zhouyepang.instagramapp;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.ContextThemeWrapper;
import android.widget.Toast;

import com.example.zhouyepang.instagramapp.UploadOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SendData {

    public static void uploadImage(final Context context, final FirebaseUser fbUser, Uri imageUri, final DatabaseReference database, final String databaseCategory) {

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imagesRef = storageRef.child("images").child(databaseCategory);
        StorageReference userRef = imagesRef.child(fbUser.getUid());
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String filename = fbUser.getUid() + "_" + timeStamp;
        StorageReference fileRef = userRef.child(filename);

        UploadTask uploadTask = fileRef.putFile(imageUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(context, "Upload failed!\n" + exception.getMessage(), Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                Toast.makeText(context, "Upload finished!", Toast.LENGTH_SHORT).show();
                // save image to database
                String key = database.child("images").child(databaseCategory).push().getKey();
                com.example.zhouyepang.instagramapp.Image image = new com.example.zhouyepang.instagramapp.Image(key, fbUser.getUid(), downloadUrl.toString());
                database.child("images").child(databaseCategory).child(key).setValue(image);
            }
        });
    }

    public static void uploadText(final Context context, final FirebaseUser fbUser, Uri imageUri, final DatabaseReference database, final String databaseCategory) {

    }
    public static void sendPost(final Context context, final FirebaseUser fbUser, Uri imageUri, final DatabaseReference database, final String databaseCategory) {
        uploadImage(context, fbUser, imageUri, database, databaseCategory);
    }
}
