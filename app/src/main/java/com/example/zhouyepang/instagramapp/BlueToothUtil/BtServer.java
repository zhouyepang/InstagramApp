package com.example.zhouyepang.instagramapp.BlueToothUtil;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;


public class BtServer extends com.example.zhouyepang.instagramapp.BlueToothUtil.BtBase {
    private static final String TAG = BtServer.class.getSimpleName();
    private BluetoothServerSocket mSSocket;

    BtServer(Listener listener) {
        super(listener);
        listen();
    }


    void listen() {
        try {
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            mSSocket = adapter.listenUsingInsecureRfcommWithServiceRecord(TAG, SPP_UUID);

            Util.EXECUTOR.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        BluetoothSocket socket = mSSocket.accept();
                        mSSocket.close();
                        loopRead(socket);
                    } catch (Throwable e) {
                        close();
                    }
                }
            });
        } catch (Throwable e) {
            close();
        }
    }

    @Override
    public void close() {
        super.close();
        try {
            mSSocket.close();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}