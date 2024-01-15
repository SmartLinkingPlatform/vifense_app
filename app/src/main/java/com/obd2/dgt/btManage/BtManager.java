package com.obd2.dgt.btManage;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.obd2.dgt.ui.MainActivity;
import com.obd2.dgt.utils.CommonFunc;
import com.obd2.dgt.utils.MyUtils;

import java.lang.reflect.Method;

public class BtManager {
    private static final String TAG = BtManager.class.getName();

    @SuppressLint("MissingPermission")
    public static BluetoothSocket connect(BluetoothDevice dev, boolean secure) {
        BluetoothSocket sock = null;
        BluetoothSocket sockFallback = null;

        Log.d(TAG, "Starting Bluetooth connection..");
        try {
            if (secure) {
                sock = dev.createRfcommSocketToServiceRecord(MyUtils.uuid);
            } else {
                sock = dev.createInsecureRfcommSocketToServiceRecord(MyUtils.uuid);
            }
            sock.connect();
        } catch (Exception e1) {
            Class<?> clazz = sock.getRemoteDevice().getClass();
            Class<?>[] paramTypes = new Class<?>[]{Integer.TYPE};
            try {
                Method m = clazz.getMethod("createRfcommSocket", paramTypes);
                Object[] params = new Object[]{Integer.valueOf(1)};
                sockFallback = (BluetoothSocket) m.invoke(sock.getRemoteDevice(), params);
                sockFallback.connect();
                sock = sockFallback;
            } catch (Exception e2) {
                MyUtils.obdConnect.finishSocket();
                MainActivity.getInstance().showDisconnectedStatus(0);
            }
        }
        return sock;
    }
}
