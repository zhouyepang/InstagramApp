package com.example.zhouyepang.instagramapp.BlueToothUtil;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;

import com.example.zhouyepang.instagramapp.R;

public class BtClientActivity extends Activity implements com.example.zhouyepang.instagramapp.BlueToothUtil.BtBase.Listener, BtReceiver.Listener, BtDevAdapter.Listener {
    private TextView mTips;
    private EditText mInputMsg;
    private TextView mLogs;
    private BtReceiver mBtReceiver;
    private final BtDevAdapter mBtDevAdapter = new BtDevAdapter(this);
    private final com.example.zhouyepang.instagramapp.BlueToothUtil.BtClient mClient = new com.example.zhouyepang.instagramapp.BlueToothUtil.BtClient(this);
    public static final int RC_IMAGE_GALLERY = 2;
    private Uri imageUri;
    private String path_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_btclient);
        RecyclerView rv = findViewById(R.id.rv_bt);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(mBtDevAdapter);
        mTips = findViewById(R.id.tv_tips);
        mInputMsg = findViewById(R.id.input_msg);
        mLogs = findViewById(R.id.tv_log);
        mBtReceiver = new BtReceiver(this, this);
        BluetoothAdapter.getDefaultAdapter().startDiscovery();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBtReceiver);
        mClient.unListener();
        mClient.close();
    }

    @Override
    public void onItemClick(BluetoothDevice dev) {
        if (mClient.isConnected(dev)) {
            APP.toast("connected", 0);
            return;
        }
        mClient.connect(dev);
        APP.toast("connecting...", 0);
        mTips.setText("connecting...");
    }

    @Override
    public void foundDev(BluetoothDevice dev) {
        mBtDevAdapter.add(dev);
    }

    public void reScan(View view) {
        mBtDevAdapter.reScan();
    }

    public void sendMsg(View view) {
        if (mClient.isConnected(null)) {
            String msg = mInputMsg.getText().toString();
            if (TextUtils.isEmpty(msg))
                APP.toast("error: empty message", 0);
            else
                mClient.sendMsg(msg);
        } else
            APP.toast("no connection", 0);
    }

    public void sendFile(View view) {
        openGallery();
    }

    @Override
    public void socketNotify(int state, final Object obj) {
        if (isFinishing())
            return;
        String msg = null;
        switch (state) {
            case com.example.zhouyepang.instagramapp.BlueToothUtil.BtBase.Listener.CONNECTED:
                BluetoothDevice dev = (BluetoothDevice) obj;
                msg = String.format("connecting successfully with %s(%s)", dev.getName(), dev.getAddress());
                mTips.setText(msg);
                break;
            case com.example.zhouyepang.instagramapp.BlueToothUtil.BtBase.Listener.DISCONNECTED:
                msg = "end connection";
                mTips.setText(msg);
                break;
            case com.example.zhouyepang.instagramapp.BlueToothUtil.BtBase.Listener.MSG:
                msg = String.format("\n%s", obj);
                mLogs.append(msg);
                break;
        }
        APP.toast(msg, 0);
    }

    public void openGallery() {
        if (ContextCompat.checkSelfPermission(BtClientActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(BtClientActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            choosePhoto();
        }
    }

    private void choosePhoto() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, RC_IMAGE_GALLERY);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_IMAGE_GALLERY && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();

            String[] projection = {MediaStore.Images.Media.DATA};
            CursorLoader loader = new CursorLoader(this, imageUri, projection, null, null, null);
            Cursor cursor = loader.loadInBackground();
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            path_name = cursor.getString(column_index);


            if (mClient.isConnected(null)) {
                if (!new File(path_name).isFile())
                    APP.toast("invalid file", 0);
                else
                    mClient.sendFile(path_name);
            } else
                APP.toast("no connection", 0);
        }
    }
}
