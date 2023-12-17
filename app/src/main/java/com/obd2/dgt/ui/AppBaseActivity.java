package com.obd2.dgt.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.obd2.dgt.R;
import com.obd2.dgt.service.RealService;
import com.obd2.dgt.ui.InfoActivity.LinkInfoActivity;
import com.obd2.dgt.ui.MainListActivity.DashboardActivity;
import com.obd2.dgt.utils.MyUtils;
import com.obd2.dgt.utils.PermissionSupport;


public class AppBaseActivity extends AppCompatActivity {
    Intent foregroundServiceIntent = null;
    private PermissionSupport permission;


    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (MyUtils.appBase == null) {
            MyUtils.appBase = this;
        }
        if (MyUtils.mContext == null) {
            MyUtils.mContext = getContext();
        }
    }

    public void onRLChangeLayount(Context currentLayout, Class<?> changeLayout) {
        MyUtils.currentActivity = changeLayout;
        Intent intent = new Intent(currentLayout, changeLayout);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
    }

    public void onLRChangeLayount(Context currentLayout, Class<?> changeLayout) {
        MyUtils.currentActivity = changeLayout;
        Intent intent = new Intent(currentLayout, changeLayout);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
    }

    public Context getContext() {
        Context context = getBaseContext();
        return context;
    }

    public void permissionCheck() {
        permission = new PermissionSupport(this, this);
        if (!permission.checkPermission()) {
            permission.requestPermission();
        } else {
            checkBluetoothAdapter();
        }
    }

    public void resetBluetoothAdapter() {
        if (MyUtils.mBluetoothAdapter == null) {
            BluetoothManager bluetoothManager = (BluetoothManager) this.getSystemService(Context.BLUETOOTH_SERVICE);
            MyUtils.mBluetoothAdapter = bluetoothManager.getAdapter();
        }
    }

    @SuppressLint("MissingPermission")
    public boolean checkBluetoothAdapter() {
        MyUtils.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (MyUtils.mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), R.string.bluetooth_permission_nodevice, Toast.LENGTH_LONG).show();
            return false;
        } else {
            if (!MyUtils.mBluetoothAdapter.isEnabled()) {
                int state = MyUtils.mBluetoothAdapter.getState();
                if (state == BluetoothAdapter.STATE_OFF) {
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent, MyUtils.REQUEST_ENABLE_BT);
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case MyUtils.REQUEST_ENABLE_BT:
                break;
            case MyUtils.REQUEST_PAIRED_BT:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (!permission.permissionResult(requestCode, permissions, grantResults)) {
            permission.requestPermission();
        } else {
            checkBluetoothAdapter();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    protected void onPause(){
        super.onPause();
    }

    //블루투스 기기 찾기
    public void onBluetoothReceiver() {
        IntentFilter stateFilter = new IntentFilter();
        stateFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED); //BluetoothAdapter.ACTION_STATE_CHANGED : 블루투스 상태변화 액션
        stateFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        stateFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED); //연결 확인
        stateFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED); //연결 끊김 확인
        stateFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        stateFilter.addAction(BluetoothDevice.ACTION_FOUND);    //기기 검색됨
        stateFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);   //기기 검색 시작
        stateFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);  //기기 검색 종료
        stateFilter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
        registerReceiver(mBluetoothStateReceiver, stateFilter);
    }

    BroadcastReceiver mBluetoothStateReceiver = new BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        @Override
        public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();   //입력된 action
        final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        //입력된 action에 따라서 함수를 처리한다
        switch (action) {
            case BluetoothAdapter.ACTION_STATE_CHANGED: //블루투스의 연결 상태 변경
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        checkBluetoothAdapter();
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:

                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d("bluetooth connect status", "STATE_ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d("bluetooth connect status", "STATE_TURNING_ON");

                        break;
                }

                break;
            case BluetoothDevice.ACTION_ACL_CONNECTED:  //블루투스 기기 페어링 하기
                LinkInfoActivity.getInstance().onChangedPairedDeviceList(device);
                break;
            case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                break;
            case BluetoothDevice.ACTION_ACL_DISCONNECTED:   //블루투스 기기 끊어짐
                MyUtils.btService.closeSocket();
                MainActivity.getInstance().showDisconnectedStatus(0);
                break;

            case BluetoothAdapter.ACTION_DISCOVERY_STARTED: //블루투스 기기 검색 시작
                break;
            case BluetoothDevice.ACTION_FOUND:  //블루투스 기기 검색 됨, 블루투스 기기가 근처에서 검색될 때마다 수행됨
                String device_name = device.getName();
                if (device_name != null) {
                    LinkInfoActivity.getInstance().onChangedScanningDeviceList(device);
                }
                break;
            case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:    //블루투스 기기 검색 종료
                MyUtils.mBluetoothAdapter.cancelDiscovery();
                LinkInfoActivity.getInstance().onStopSearch();
                break;
            case BluetoothDevice.ACTION_PAIRING_REQUEST:

                break;
        }

        }
    };

    public void ServiceStart() {
        try {
            //Start Service
            if (RealService.serviceIntent == null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    foregroundServiceIntent = new Intent(this, RealService.class);
                    startForegroundService(foregroundServiceIntent);
                } else {
                    foregroundServiceIntent = RealService.serviceIntent;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void gotoDashboard() {
        onRLChangeLayount(MyUtils.appBase, DashboardActivity.class);
        finish();
    }
    int width = 1080;
    int height = 2115;

    public void getWindowsSize() {
        MyUtils.metrics = getResources().getDisplayMetrics();
        MyUtils.mDpX = (float) (428.625 / (float) MyUtils.metrics.xdpi) * 3;
        MyUtils.mDpY = (float) (424.542 / (float) MyUtils.metrics.ydpi) * 3;
    }

    public float setScaleX(float x) {
        return x * (float) MyUtils.metrics.widthPixels / width;
    }

    public float setScaleY(float y) {
        return y * (float) MyUtils.metrics.heightPixels / height;
    }
}
