package com.example.zhouyepang.instagramapp.BlueToothUtil;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;

import com.example.zhouyepang.instagramapp.R;

public class BtServerActivity extends Activity implements com.example.zhouyepang.instagramapp.BlueToothUtil.BtBase.Listener {
    private TextView mTips;
    private EditText mInputMsg;
    private TextView mLogs;
    private com.example.zhouyepang.instagramapp.BlueToothUtil.BtServer mServer;
    public static final int RC_IMAGE_GALLERY = 2;
    private Uri imageUri;
    private String path_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_btserver);
        mTips = findViewById(R.id.tv_tips);
        mInputMsg = findViewById(R.id.input_msg);
        mLogs = findViewById(R.id.tv_log);
        mServer = new com.example.zhouyepang.instagramapp.BlueToothUtil.BtServer(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mServer.unListener();
        mServer.close();
    }

    public void sendMsg(View view) {
        if (mServer.isConnected(null)) {
            String msg = mInputMsg.getText().toString();
            if (TextUtils.isEmpty(msg))
                APP.toast("error: empty message", 0);
            else
                mServer.sendMsg(msg);
        }
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
                mServer.listen();
                msg = "connection is broken and is being re-listened...";
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
        if (ContextCompat.checkSelfPermission(BtServerActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(BtServerActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
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


//            path to uri

//            Uri uri1= Uri.fromFile(new File(path_name));
//            if (uri1.getScheme().equals("file")) {
//                String path = uri1.getEncodedPath();
//                if (path != null) {
//                    path = Uri.decode(path);
//                    ContentResolver cr = this.getContentResolver();
//                    StringBuffer buff = new StringBuffer();
//                    buff.append("(")
//                            .append(MediaStore.Images.ImageColumns.DATA)
//                            .append("=")
//                            .append("'" + path + "'")
//                            .append(")");
//                    Cursor cur = cr.query(
//                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                            new String[]{MediaStore.Images.ImageColumns._ID},
//                            buff.toString(), null, null);
//                    int index = 0;
//                    for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
//                        index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID);
//                        index = cur.getInt(index);
//                    }
//                    if (index == 0) {
//                        //do nothing
//                    } else {
//                        Uri uri_temp = Uri
//                                .parse("content://media/external/images/media/"
//                                        + index);
//                        if (uri_temp != null) {
//                            uri1 = uri_temp;
//                        }
//                    }
//                }
//            }
//            newuri =uri1;



//            System.out.println("uri is:        " + imageUri.toString());
//            System.out.println("uri is to path:" + path_name);
//            System.out.println("uri is to uri :" + newuri.toString());



            if (mServer.isConnected(null)) {
                if (!new File(path_name).isFile())
                    APP.toast("invalid file", 0);
                else
                    mServer.sendFile(path_name);
            } else
                APP.toast("no connection", 0);
        }

    }


}