package com.example.zhouyepang.instagramapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.io.FileNotFoundException;


public class UploadOptions extends AppCompatActivity {
    private Uri imageUri;
    private Bitmap edittedImg;
    private ImageView editedPreview;
    private Button btnEditor, btnPost;
    private EditText description;
    FirebaseUser fbUser;
    DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_options);
        fbUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fbUser == null) {
            finish();
        }

        database = FirebaseDatabase.getInstance().getReference();

        initialingImage();

        description = (EditText) findViewById(R.id.description);
        btnEditor = (Button) findViewById(R.id.btnEditor);
        btnPost = (Button) findViewById(R.id.btnPost);


        btnEditor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editing();
            }
        });

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadPost();
                returnToMain();
            }
        });
    }

    private void initialingImage() {
        Intent intent = getIntent();
        imageUri = Uri.parse(intent.getStringExtra("imageUri"));
        System.out.println("img uri : "+imageUri);
        editedPreview = (ImageView) findViewById(R.id.editedPreview);
        editedPreview.setImageURI(imageUri);
    }

    private void uploadPost() {
        String inputText = description.getText().toString();
        com.example.zhouyepang.instagramapp.SendData.uploadImage(UploadOptions.this, fbUser, imageUri, database, "postImages", true, inputText);
    }

    private void editing(){
        Intent editorIntent = new Intent(this, PhotoEditor.class);
        editorIntent.putExtra("imageUri", imageUri.toString());
        startActivityForResult(editorIntent, 0);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bundle bundle = data.getExtras();
        Uri uri =  Uri.parse(bundle.getString("imageUri"));
        imageUri = uri;
        editedPreview.setImageURI(imageUri);
    }


    private void returnToMain() {
        Intent intent = new Intent(this, MainPage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

}
