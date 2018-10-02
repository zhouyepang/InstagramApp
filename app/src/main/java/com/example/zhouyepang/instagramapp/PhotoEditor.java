package com.example.zhouyepang.instagramapp;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.BitmapDrawable;
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

public class PhotoEditor extends AppCompatActivity {
    ImageView tempImageView;
    Drawable tempDrawable;
    Bitmap bitmapImage;
    Bitmap preview1;
    Bitmap preview2;
    Bitmap preview3;
    Bitmap preview4;
    SeekBar contrastBar;
    SeekBar brightnessSeekbar;
    Bitmap finalImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_editor);

        tempDrawable = getResources().getDrawable(R.drawable.jiangzemin);
        tempImageView = (ImageView) findViewById(R.id.showImage);
        bitmapImage = ((BitmapDrawable) tempDrawable).getBitmap();
        preview1 = origin(bitmapImage);
        preview2 = warm(bitmapImage);
        preview3 = blackAndWhite(bitmapImage);
        preview4 = invertColor(bitmapImage);
        tempImageView.setImageBitmap(preview1);
        ImageButton b1 = (ImageButton) findViewById(R.id.effect1);
        ImageButton b2 = (ImageButton) findViewById(R.id.effect2);
        ImageButton b3 = (ImageButton) findViewById(R.id.effect3);
        ImageButton b4 = (ImageButton) findViewById(R.id.effect4);
        b1.setImageBitmap(preview1);
        b2.setImageBitmap(preview2);
        b3.setImageBitmap(preview3);
        b4.setImageBitmap(preview4);

        brightnessSeekbar = (SeekBar) findViewById(R.id.brightnessSeekBar);
        brightnessSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                finalImage = changeBitmapContrastBrightness(BitmapFactory.decodeResource(getResources(), R.drawable.jiangzemin),
                        1, (float) progress / 1f);
                tempImageView.setImageBitmap(finalImage);
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
                finalImage = changeBitmapContrastBrightness(BitmapFactory.decodeResource(getResources(), R.drawable.jiangzemin),
                        (float)progress / 100f, 1);
                tempImageView.setImageBitmap(finalImage);
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
                tempImageView.setImageBitmap(finalImage);
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalImage = bitmapImage;
                finalImage = preview2;
                brightnessSeekbar.setProgress(0);
                contrastBar.setProgress(100);
                tempImageView.setImageBitmap(finalImage);
            }
        });

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalImage = bitmapImage;
                finalImage = preview3;
                brightnessSeekbar.setProgress(0);
                contrastBar.setProgress(100);
                tempImageView.setImageBitmap(finalImage);
            }
        });


        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalImage = bitmapImage;
                finalImage = preview4;
                brightnessSeekbar.setProgress(0);
                contrastBar.setProgress(100);
                tempImageView.setImageBitmap(finalImage);
            }
        });
    }

    public Bitmap getFinalImage() {
        return finalImage;
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

}
