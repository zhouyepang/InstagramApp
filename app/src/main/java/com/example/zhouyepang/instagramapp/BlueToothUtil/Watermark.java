package com.example.zhouyepang.instagramapp.BlueToothUtil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

class Watermark  {
    private static String file_path;
    private static String file_name;

    void SetName(String file_name){
        Watermark.file_name = file_name;
    }

    void SetPath(String file_path){
        Watermark.file_path = file_path;
    }


    void merge() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                File zhang = new File(file_path+file_name);

                try {
                    Bitmap bitmap1 = BitmapFactory.decodeStream(new FileInputStream(zhang));

                    File zhangphil = new File(file_path+"new"+file_name);
                    if (!zhangphil.exists())
                        zhangphil.createNewFile();

                    int textSize = 180;

                    Bitmap bitmap2 = addTextWatermark(bitmap1, "NEARBY", textSize, Color.RED, 30, 180, true);
                    save(bitmap2, zhangphil, Bitmap.CompressFormat.JPEG, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    private static Bitmap addTextWatermark(Bitmap src, String content, int textSize, int color, float x, float y, boolean recycle) {
        if (isEmptyBitmap(src) || content == null)
            return null;
        Bitmap ret = src.copy(src.getConfig(), true);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Canvas canvas = new Canvas(ret);
        paint.setColor(color);
        paint.setTextSize(textSize);
        Rect bounds = new Rect();
        paint.getTextBounds(content, 0, content.length(), bounds);
        canvas.drawText(content, x, y, paint);
        if (recycle && !src.isRecycled())
            src.recycle();
        return ret;
    }



    private static boolean save(Bitmap src, File file, Bitmap.CompressFormat format, boolean recycle) {
        if (isEmptyBitmap(src))
            return false;

        OutputStream os;
        boolean ret = false;
        try {
            os = new BufferedOutputStream(new FileOutputStream(file));
            ret = src.compress(format, 100, os);
            if (recycle && !src.isRecycled())
                src.recycle();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ret;
    }

    private static boolean isEmptyBitmap(Bitmap src) {
        return src == null || src.getWidth() == 0 || src.getHeight() == 0;
    }
}


