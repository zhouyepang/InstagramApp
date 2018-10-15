package com.example.zhouyepang.instagramapp.BlueToothUtil;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class BtReceiver extends BroadcastReceiver {
    private static final String TAG = BtReceiver.class.getSimpleName();
    private final Listener mListener;

    public BtReceiver(Context cxt, Listener listener) {
        mListener = listener;
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);

        filter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        filter.addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED);
        filter.addAction(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED);
        cxt.registerReceiver(this, filter);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null)
            return;
        Log.i(TAG, "===" + action);
        BluetoothDevice dev = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        if (dev != null)
            Log.i(TAG, "BluetoothDevice: " + dev.getName() + ", " + dev.getAddress());
        switch (action) {
            case BluetoothAdapter.ACTION_STATE_CHANGED:
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                Log.i(TAG, "STATE: " + state);
                break;
            case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                break;
            case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                break;

            case BluetoothDevice.ACTION_FOUND:
                short rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MAX_VALUE);
                Log.i(TAG, "EXTRA_RSSI:" + rssi);
                mListener.foundDev(dev);
                break;
            case BluetoothDevice.ACTION_PAIRING_REQUEST:
                break;
            case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                Log.i(TAG, "BOND_STATE: " + intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, 0));
                break;
            case BluetoothDevice.ACTION_ACL_CONNECTED:
                break;
            case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                break;

            case BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED:
                Log.i(TAG, "CONN_STATE: " + intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE, 0));
                break;
            case BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED:
                Log.i(TAG, "CONN_STATE: " + intent.getIntExtra(BluetoothHeadset.EXTRA_STATE, 0));
                break;
            case BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED:
                Log.i(TAG, "CONN_STATE: " + intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, 0));
                break;
        }
    }

    public interface Listener {
        void foundDev(BluetoothDevice dev);
    }
}