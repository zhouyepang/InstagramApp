package com.example.zhouyepang.instagramapp;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
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

import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SendData {


    //this function include uploading to storage and database. Before image save its download url in database, the image should be uploaded to the cloud storage and get the download url from the storage,
    //so that we can save the download url to database and retrieve by another usage directly for downloading the image.
    public static void uploadImage(final Context context, final FirebaseUser fbUser, final Uri imageUri, final DatabaseReference database, String postImages, final boolean imageType, final String inputText) {

        final boolean post = true;
        final boolean avatar = false;
        final String databaseCategory;
        if (imageType == true) {
            databaseCategory = "postImages";
        } else {
            databaseCategory = "avatars";
        }

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imagesRef = storageRef.child("images").child(databaseCategory);
        StorageReference userRef = imagesRef.child(fbUser.getUid());
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String filename = fbUser.getUid() + "_" + timeStamp;
        StorageReference fileRef = userRef.child(filename);

        //create upload task which is a new thread, then it will upload the image to cloud storage by local image uri
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
                // call function to save image to database
                if (imageType == post) {
                    postToDB(context, fbUser, imageUri, database, imageType, databaseCategory, downloadUrl, inputText);
                } else {
                    avatarToDB(context, fbUser, imageUri, database, imageType, databaseCategory, downloadUrl);
                }
            }
        });
    }

    //upload the post with additional information to database, it will upload the image and post as two different category in database and link themselves by post id to match the image and post
    private static void postToDB(final Context context, final FirebaseUser fbUser, Uri imageUri, final DatabaseReference database, boolean imageType, final String databaseCategory, Uri downloadUrl, String inputText){
        //making key for image and post
        String imagekey = database.child("images").child(databaseCategory).push().getKey();
        String postkey = database.child("posts").push().getKey();
        String postTime = getTimeStamp();

        //initial a image and post instance to upload
        com.example.zhouyepang.instagramapp.Image image = new com.example.zhouyepang.instagramapp.Image(imagekey, fbUser.getUid(), downloadUrl.toString(), databaseCategory, postTime);
        com.example.zhouyepang.instagramapp.Post post = new com.example.zhouyepang.instagramapp.Post(postkey, fbUser.getUid(), imagekey, downloadUrl.toString(), inputText, postTime);

        //start to upload image and post to database with different category
        database.child("images").child(databaseCategory).child(imagekey).setValue(image);
        database.child("posts").child(postkey).setValue(post);
    }

    //function to upload avatar image and give the download url to matched user profile category in database
    private static void avatarToDB(final Context context, final FirebaseUser fbUser, Uri imageUri, final DatabaseReference database, boolean imageType, final String databaseCategory, Uri downloadUrl) {
        String key = database.child("images").child(databaseCategory).push().getKey();
        String avaTime = getTimeStamp();
        com.example.zhouyepang.instagramapp.Image image = new com.example.zhouyepang.instagramapp.Image(key, fbUser.getUid(), downloadUrl.toString(), databaseCategory, avaTime);

        //start to upload avatar image and include the avatar image download url to matched user profile in database
        database.child("images").child(databaseCategory).child(key).setValue(image);
        database.child("usersProfile").child(fbUser.getUid()).child("avaURL").setValue(downloadUrl.toString());
    }

    //this function will return the uri for given file path
    public static Uri getImageContentUri(Context context,File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID }, MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);
        //get the newest image uri which just save in local storage, before calling this function this will need to be refreshed the media store
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    //function to generate time stamp
    public static String getTimeStamp() {
        Long ts = System.currentTimeMillis();
        return ts.toString();
    }

    //function to get date by given time stamp
    public static String timeStampToDateTime(String timeStamp) {
        Long ts = Long.parseLong(timeStamp);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(ts);
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);
        int mHour = calendar.get(Calendar.HOUR_OF_DAY);
        int mMin = calendar.get(Calendar.MINUTE);
        int mSec = calendar.get(Calendar.SECOND);
        @SuppressLint("DefaultLocale") String dateTime = String.format("%d-%d-%d %d:%d:%d", mYear, mMonth, mDay, mHour, mMin, mSec);
        return dateTime;
    }
}

