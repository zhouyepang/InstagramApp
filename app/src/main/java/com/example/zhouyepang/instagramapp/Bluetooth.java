package com.example.zhouyepang.instagramapp;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.example.zhouyepang.instagramapp.BlueToothUtil.APP;
import com.example.zhouyepang.instagramapp.BlueToothUtil.BtClientActivity;
import com.example.zhouyepang.instagramapp.BlueToothUtil.BtServerActivity;

public class Bluetooth extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth);

        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) {
            APP.toast("This machine does not find Bluetooth hardware or drivers！", 0);
            finish();
            return;
        } else {
            if (!adapter.isEnabled()) {
                adapter.enable();
            }
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE
                    , Manifest.permission.READ_EXTERNAL_STORAGE
                    , Manifest.permission.ACCESS_COARSE_LOCATION};
            for (String str : permissions) {
                if (checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(permissions, 111);
                    break;
                }
            }
        }
    }

    public void btClient(View view) {
        startActivity(new Intent(this, BtClientActivity.class));
    }

    public void btServer(View view) {
        startActivity(new Intent(this, BtServerActivity.class));
    }


}
