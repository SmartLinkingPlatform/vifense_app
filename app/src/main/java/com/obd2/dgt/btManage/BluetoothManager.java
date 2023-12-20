package com.obd2.dgt.btManage;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.obd2.dgt.utils.CommonFunc;
import com.obd2.dgt.utils.MyUtils;

import java.io.IOException;
import java.lang.reflect.Method;

public class BluetoothManager {
    private static final String TAG = BluetoothManager.class.getName();

    @SuppressLint("MissingPermission")
    public static BluetoothSocket connect(BluetoothDevice dev) throws IOException {
        BluetoothSocket sock = null;
        BluetoothSocket sockFallback = null;

        Log.d(TAG, "Starting Bluetooth connection..");
        try {
            sock = dev.createRfcommSocketToServiceRecord(MyUtils.uuid);
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
                String content = CommonFunc.getDateTime() + " --- Couldn't fallback while establishing Bluetooth connection. --- " + e2.getMessage() + "\r\n";
                CommonFunc.writeFile(MyUtils.StorageFilePath, "Vifense_Log.txt", content);
                throw new IOException(e2.getMessage());
            }
            String content = CommonFunc.getDateTime() + " --- ODB Connect Error --- " + e1.getMessage() + "\r\n";
            CommonFunc.writeFile(MyUtils.StorageFilePath, "Vifense_Log.txt", content);
        }
        return sock;
    }
}
