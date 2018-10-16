package com.example.zhouyepang.instagramapp.BlueToothUtil;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

class BtClient extends com.example.zhouyepang.instagramapp.BlueToothUtil.BtBase {
    BtClient(Listener listener) {
        super(listener);
    }

    void connect(BluetoothDevice dev) {
        close();
        try {
            final BluetoothSocket socket = dev.createInsecureRfcommSocketToServiceRecord(SPP_UUID);
            Util.EXECUTOR.execute(new Runnable() {
                @Override
                public void run() {
                    loopRead(socket);
                }
            });
        } catch (Throwable e) {
            close();
        }
    }
}