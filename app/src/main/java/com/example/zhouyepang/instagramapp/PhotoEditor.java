package com.example.zhouyepang.instagramapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.graphics.ColorMatrix;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.widget.*;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import java.io.FileNotFoundException;
import com.fenchtose.nocropper.CropperView;
import java.io.ByteArrayOutputStream;
import android.content.ContextWrapper;
import java.io.File;
import java.io.FileOutputStream;
import android.content.Context;
import java.io.IOException;
import android.os.Environment;
import java.util.UUID;

public class PhotoEditor extends AppCompatActivity {
    Bitmap bitmapImage;
    Bitmap preview1;
    Bitmap preview2;
    Bitmap preview3;
    Bitmap preview4;
    SeekBar contrastBar;
    SeekBar brightnessSeekbar;
    Bitmap finalImage;
    private Uri imageUri;
    CropperView croppedView;
    boolean croppedCentre = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_editor);

        Intent intent = getIntent();
        imageUri = Uri.parse(intent.getStringExtra("imageUri"));
        croppedView = (CropperView) findViewById(R.id.showImage);
        bitmapImage = uriToBitMap(imageUri);
        preview1 = origin(bitmapImage);
        preview2 = warm(bitmapImage);
        preview3 = blackAndWhite(bitmapImage);
        preview4 = invertColor(bitmapImage);
        croppedView.setImageBitmap(preview1);
        ImageButton b1 = (ImageButton) findViewById(R.id.effect1);
        ImageButton b2 = (ImageButton) findViewById(R.id.effect2);
        ImageButton b3 = (ImageButton) findViewById(R.id.effect3);
        ImageButton b4 = (ImageButton) findViewById(R.id.effect4);
        ImageButton snap = (ImageButton) findViewById(R.id.snap);
        ImageButton crop = (ImageButton) findViewById(R.id.cropImage);
        ImageButton saveButton =  (ImageButton) findViewById(R.id.saveImage);
        b1.setImageBitmap(preview1);
        b2.setImageBitmap(preview2);
        b3.setImageBitmap(preview3);
        b4.setImageBitmap(preview4);

        brightnessSeekbar = (SeekBar) findViewById(R.id.brightnessSeekBar);
        brightnessSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                finalImage = changeBitmapContrastBrightness(bitmapImage,
                        1, (float) progress / 1f);
                croppedView.setImageBitmap(finalImage);
                // textView.setText("Contrast: "+(float) progress / 100f);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        brightnessSeekbar.setMax(100);
        brightnessSeekbar.setProgress(0);

        contrastBar = (SeekBar) findViewById(R.id.seekBar);
        contrastBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                finalImage = changeBitmapContrastBrightness(bitmapImage,
                        (float)progress / 100f, 1);
                croppedView.setImageBitmap(finalImage);
                // textView.setText("Contrast: "+(float) progress / 100f);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        contrastBar.setMax(200);
        contrastBar.setProgress(100);


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalImage = preview1;
                brightnessSeekbar.setProgress(0);
                contrastBar.setProgress(100);
                croppedView.setImageBitmap(finalImage);
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalImage = bitmapImage;
                finalImage = preview2;
                brightnessSeekbar.setProgress(0);
                contrastBar.setProgress(100);
                croppedView.setImageBitmap(finalImage);
            }
        });

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalImage = bitmapImage;
                finalImage = preview3;
                brightnessSeekbar.setProgress(0);
                contrastBar.setProgress(100);
                croppedView.setImageBitmap(finalImage);
            }
        });


        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalImage = bitmapImage;
                finalImage = preview4;
                brightnessSeekbar.setProgress(0);
                contrastBar.setProgress(100);
                croppedView.setImageBitmap(finalImage);
            }
        });

        snap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (croppedCentre) {
                    croppedView.cropToCenter();
                }else {
                    croppedView.fitToCenter();
                }
                croppedCentre = ! croppedCentre;
            }
        });

        crop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap tempImage = croppedView.getCroppedBitmap();
                finalImage = tempImage;
                croppedView.setImageBitmap(finalImage);
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTheImage(v);
            }
        });
    }

    public void saveTheImage(View v){
        Uri new_uri = saveToInternalStorage(finalImage);
        Intent uploadOptions = new Intent(this, UploadOptions.class);
        uploadOptions.putExtra("imageUri", new_uri.toString());
        setResult(0, uploadOptions);
        PhotoEditor.this.finish();
    }

    private Uri saveToInternalStorage(Bitmap bitmapImage){
        // path to /data/data/yourapp/app_data/imageDir
        File edittedFile =new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath()+"/"+ UUID.randomUUID()+".jpg");
        System.out.println("saved path "+edittedFile.toString());
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(edittedFile);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(edittedFile);
        intent.setData(uri);
        this.sendBroadcast(intent);
        uri = SendData.getImageContentUri(this, edittedFile);
        System.out.println("transferred uri  "+uri);
        return uri;
    }

    public static Bitmap origin (Bitmap Original){
        Bitmap finalImage = Bitmap.createBitmap(Original.getWidth(), Original.getHeight(), Original.getConfig());
        int A,R,G,B;
        int pixelColor;
        int height = Original.getHeight();
        int width = Original.getWidth();

        for (int y = 0; y < height; y++){
            for (int x = 0; x < width; x++){
                pixelColor = Original.getPixel(x,y);
                A = Color.alpha(pixelColor);
                R = (int) Color.red(pixelColor);
                G = (int) Color.green(pixelColor);
                B = Color.blue(pixelColor);
                finalImage.setPixel(x,y,Color.argb(A,R,G,B));
            }
        }
        return  finalImage;
    }
    public static Bitmap warm(Bitmap Original){
        Log.v("invert","called");
        Bitmap finalImage = Bitmap.createBitmap(Original.getWidth(), Original.getHeight(), Original.getConfig());

        int A,R,G,B;
        int pixelColor;
        int height = Original.getHeight();
        int width = Original.getWidth();

        for (int y = 0; y < height; y++){
            for (int x = 0; x < width; x++){
                pixelColor = Original.getPixel(x,y);
                A = Color.alpha(pixelColor);
                R = (int) (Color.red(pixelColor)*0.9);
                G = (int) (Color.green(pixelColor) * 0.8);
                B = Color.blue(pixelColor);
                finalImage.setPixel(x,y,Color.argb(A,R,G,B));
            }
        }
        return  finalImage;
    }

    public static Bitmap blackAndWhite(Bitmap Original){
        Log.v("invert","called");
        Bitmap finalImage = Bitmap.createBitmap(Original.getWidth(), Original.getHeight(), Original.getConfig());
        int A,R,G,B;
        int pixelColor;
        int height = Original.getHeight();
        int width = Original.getWidth();

        for (int y = 0; y < height; y++){
            for (int x = 0; x < width; x++){
                pixelColor = Original.getPixel(x,y);
                A = Color.alpha(pixelColor);
                R = (int) (Color.red(pixelColor));
                G = (int) (Color.green(pixelColor));
                B = Color.blue(pixelColor);
                int intesity = (int) (R+G+B)/3;
                finalImage.setPixel(x,y,Color.argb(A,intesity,intesity,intesity));
            }
        }
        return  finalImage;
    }

    public static Bitmap invertColor(Bitmap Original){
        Log.v("invert","called");
        Bitmap finalImage = Bitmap.createBitmap(Original.getWidth(), Original.getHeight(), Original.getConfig());
        int A,R,G,B;
        int pixelColor;
        int height = Original.getHeight();
        int width = Original.getWidth();

        for (int y = 0; y < height; y++){
            for (int x = 0; x < width; x++){
                pixelColor = Original.getPixel(x,y);
                A = Color.alpha(pixelColor);
                R = (int) 255-Color.red(pixelColor);
                G = (int) 255-Color.green(pixelColor) ;
                B = 255-Color.blue(pixelColor);
                finalImage.setPixel(x,y,Color.argb(A,R,G,B));
            }
        }
        return  finalImage;
    }

    public static Bitmap changeBitmapContrastBrightness(Bitmap bmp, float contrast, float brightness) {
        ColorMatrix cm = new ColorMatrix(new float[] {contrast, 0, 0, 0, brightness, 0, contrast, 0, 0, brightness, 0, 0, contrast, 0, brightness, 0, 0, 0, 1, 0});
        Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());
        Canvas canvas = new Canvas(ret);
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, 0, 0, paint);
        return ret;
    }

    private Bitmap uriToBitMap(Uri uri) {
        Bitmap bit = null;
        try {
            bit = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.d("tag",e.getMessage());
            Toast.makeText(this,"Crash",Toast.LENGTH_SHORT).show();
        }
        return bit;
    }

}
