package com.coderpage.codelab.ble;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lc. 2017-09-03
 * @since 0.1.0
 */

public class BleHelper {
    public static final int REQUEST_ENABLE_BT = 110;

    private Context mContext;

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;

    private List<BluetoothDevice> mDevicesList = new ArrayList<>();

    public BleHelper(Context context) {
        mContext = context;
        // 获取 bluetoothManager
        mBluetoothManager = (BluetoothManager) context.getSystemService(
                Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    }

    public boolean openBleIfNeed(Activity activity) {
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
//            activity.startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),
//                    REQUEST_ENABLE_BT);
            return mBluetoothAdapter.enable();
        }
        return true;
    }

    public void scanDevices(DeviceScanListener listener) {
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Toast.makeText(mContext, "蓝牙不可用", Toast.LENGTH_SHORT).show();
            return;
        }
        mBluetoothAdapter.startLeScan(new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                if (addDevice(device)) {
                    listener.onScanNewDevice(device);
                }
            }
        });
    }

    public void stopScanDevice() {
        mBluetoothAdapter.stopLeScan(null);
    }

    private synchronized boolean addDevice(BluetoothDevice device) {
        for (BluetoothDevice bd : mDevicesList) {
            if (bd.getAddress().equals(device.getAddress())) {
                return false;
            }
        }
        mDevicesList.add(device);
        return true;
    }


    public interface DeviceScanListener {
        void onScanNewDevice(BluetoothDevice device);
    }
}
