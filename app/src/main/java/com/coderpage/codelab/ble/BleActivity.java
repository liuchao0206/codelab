package com.coderpage.codelab.ble;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.coderpage.codelab.BaseActivity;
import com.coderpage.codelab.codelab.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class BleActivity extends BaseActivity {

    private final static int PREMISSION_REQUEST_COARSE_LOCATION = -1;

    @BindView(R.id.recyclerBle)
    RecyclerView mBleRecycler;

    BleDeviceAdapter mAdapter;
    BleHelper mBleHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble);
        ButterKnife.bind(this);

        setTitle("Bluetooth");
        setToolbarAsBack(v -> finish());

        mBleHelper = new BleHelper(this);

        mAdapter = new BleDeviceAdapter();
        mBleRecycler.setAdapter(mAdapter);
        mBleRecycler.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.VERTICAL, false));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        PREMISSION_REQUEST_COARSE_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case PREMISSION_REQUEST_COARSE_LOCATION:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "权限请求失败", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @OnClick({R.id.btnSearch,R.id.btnStopSearch})
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btnSearch:
                if (!mBleHelper.openBleIfNeed(this)) {
                    Toast.makeText(this, "打开蓝牙失败", Toast.LENGTH_SHORT).show();
                    return;
                }
                // 搜索蓝牙
                mBleHelper.scanDevices(new BleHelper.DeviceScanListener() {
                    @Override
                    public void onScanNewDevice(BluetoothDevice device) {
                        mAdapter.addDevice(device);
                    }
                });
                break;
            case R.id.btnStopSearch:
                mBleHelper.stopScanDevice();
                break;
        }
    }

    class BleDeviceAdapter extends RecyclerView.Adapter<BleDeviceAdapter.BleVH> {

        private List<BluetoothDevice> mDeviceList = new ArrayList<>();

        @Override
        public int getItemCount() {
            return mDeviceList.size();
        }

        @Override
        public BleVH onCreateViewHolder(ViewGroup parent, int viewType) {
            return new BleVH(getLayoutInflater().inflate(
                    R.layout.recycler_item_bledevice, parent, false));
        }

        @Override
        public void onBindViewHolder(BleVH holder, int position) {
            holder.setDevice(mDeviceList.get(position));
        }

        class BleVH extends RecyclerView.ViewHolder {

            BluetoothDevice mDevice;

            TextView mDeviceNameTv;

            BleVH(View view) {
                super(view);
                mDeviceNameTv = (TextView) view.findViewById(R.id.tvBleName);
            }

            public void setDevice(BluetoothDevice device) {
                mDevice = device;
                mDeviceNameTv.setText(device.getName());
            }
        }

        public void addDevice(BluetoothDevice device) {
            mDeviceList.add(device);
            notifyDataSetChanged();
        }

        public void refreshData(List<BluetoothDevice> list) {
            mDeviceList.clear();
            mDeviceList.addAll(list);
            notifyDataSetChanged();
        }
    }

}
