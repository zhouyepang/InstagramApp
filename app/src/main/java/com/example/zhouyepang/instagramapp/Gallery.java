package com.example.zhouyepang.instagramapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import static android.app.Activity.RESULT_OK;

public class Gallery extends Fragment {
    private Button btnSelect, btnNext;
    private ImageView photoPreview;
    private static final int CROP_PHOTO = 2;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 6;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE2 = 7;
    static final int RC_PERMISSION_READ_EXTERNAL_STORAGE = 1;
    static final int RC_IMAGE_GALLERY = 2;
    Uri imageUri;
    Context applicationContext;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.gallery, container, false);

        //bind image view
        photoPreview = (ImageView) v.findViewById(R.id.photoPreview);
        //bind button
        btnSelect = (Button) v.findViewById(R.id.btn_click_select);
        btnNext = (Button) v.findViewById(R.id.btn_click_next);
        applicationContext = UploadSelect.getContextOfApplication();
        btnSelect.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                openGallery(view);
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (imageUri != null) {
                    Intent uploadOptionsIntent = new Intent(applicationContext, UploadOptions.class);
                    uploadOptionsIntent.putExtra("imageUri", imageUri.toString());
                    startActivity(uploadOptionsIntent);
                }
            }
        });
        return v;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == RC_PERMISSION_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                choosePhoto();
            }
        }
    }

    public void openGallery(View view) {
        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, RC_PERMISSION_READ_EXTERNAL_STORAGE);
        } else {
            choosePhoto();
        }
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
            photoPreview.setImageURI(imageUri);
        }
    }
}
