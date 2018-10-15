package com.example.zhouyepang.instagramapp.BlueToothUtil;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.Handler;
import android.widget.Toast;

public class APP extends Application {
    private static final Handler sHandler = new Handler();
    private static Toast sToast;

    @SuppressLint("ShowToast")
    @Override
    public void onCreate() {
        super.onCreate();
        sToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
    }

    public static void toast(String txt, int duration) {
        sToast.setText(txt);
        sToast.setDuration(duration);
        sToast.show();
    }

    public static void runUi(Runnable runnable) {
        sHandler.post(runnable);
    }
}
