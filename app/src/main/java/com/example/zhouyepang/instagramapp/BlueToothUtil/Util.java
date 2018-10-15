package com.example.zhouyepang.instagramapp.BlueToothUtil;

import android.util.Log;

import java.io.File;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

class Util {
    private static final String TAG = Util.class.getSimpleName();
    static final Executor EXECUTOR = Executors.newCachedThreadPool();

    static void mkdirs(String filePath) {
        boolean mk = new File(filePath).mkdirs();
        Log.d(TAG, "mkdirs: " + mk);
    }
}
