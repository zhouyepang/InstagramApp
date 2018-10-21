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
                if (imageType == post) {
                    postToDB(context, fbUser, imageUri, database, imageType, databaseCategory, downloadUrl, inputText);
                } else {
                    avatarToDB(context, fbUser, imageUri, database, imageType, databaseCategory, downloadUrl);
                }
            }
        });
    }

    private static void postToDB(final Context context, final FirebaseUser fbUser, Uri imageUri, final DatabaseReference database, boolean imageType, final String databaseCategory, Uri downloadUrl, String inputText){
        String imagekey = database.child("images").child(databaseCategory).push().getKey();
        String postkey = database.child("posts").push().getKey();
        String postTime = getTimeStamp();
        com.example.zhouyepang.instagramapp.Image image = new com.example.zhouyepang.instagramapp.Image(imagekey, fbUser.getUid(), downloadUrl.toString(), databaseCategory, postTime);
        com.example.zhouyepang.instagramapp.Post post = new com.example.zhouyepang.instagramapp.Post(postkey, fbUser.getUid(), imagekey, downloadUrl.toString(), inputText, postTime);

        database.child("images").child(databaseCategory).child(imagekey).setValue(image);
        database.child("posts").child(postkey).setValue(post);
    }

    private static void avatarToDB(final Context context, final FirebaseUser fbUser, Uri imageUri, final DatabaseReference database, boolean imageType, final String databaseCategory, Uri downloadUrl) {
        String key = database.child("images").child(databaseCategory).push().getKey();
        String avaTime = getTimeStamp();
        com.example.zhouyepang.instagramapp.Image image = new com.example.zhouyepang.instagramapp.Image(key, fbUser.getUid(), downloadUrl.toString(), databaseCategory, avaTime);
        database.child("images").child(databaseCategory).child(key).setValue(image);
        database.child("users").child(fbUser.getUid()).child("avaURL").setValue(downloadUrl);
    }

    public static Uri getImageContentUri(Context context,File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID }, MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);
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

    public static String getTimeStamp() {
        Long ts = System.currentTimeMillis();
        return ts.toString();
    }

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

