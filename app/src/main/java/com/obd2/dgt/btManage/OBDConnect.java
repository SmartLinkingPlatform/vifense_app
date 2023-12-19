package com.obd2.dgt.btManage;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.SystemClock;

import com.obd2.dgt.ui.MainActivity;
import com.obd2.dgt.utils.CommonFunc;
import com.obd2.dgt.utils.MyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class OBDConnect {
    private BluetoothSocket socket = null;
    private OutputStream outputStream = null; // 블루투스에 데이터를 출력하기 위한 출력 스트림
    private InputStream inputStream = null; // 블루투스에 데이터를 입력하기 위한 입력 스트림
    private Thread workerThread = null; // 문자열 수신에 사용되는 쓰레드
    private final String MOD_PREFIX1 = "41";
    private final String MOD_PREFIX2 = " 41 ";
    private final String MOD_PREFIX3 = "7E8";
    boolean running = false;

    public OBDConnect() {
    }

    @SuppressLint("MissingPermission")
    public void setConnectingOBD(BluetoothDevice obdDevice) throws IOException {
        try {
            MyUtils.mBluetoothAdapter.cancelDiscovery();
            socket = obdDevice.createRfcommSocketToServiceRecord(MyUtils.uuid);
            socket.connect();
            Method m = obdDevice.getClass().getMethod("isConnected", (Class[]) null);
            MyUtils.con_OBD = (boolean) m.invoke(obdDevice, (Object[]) null);
        } catch (Exception e) {
            socket.close();
            CommonFunc.showToastOnUIThread("OBD 연결 실패. 재 연결 중...");
            e.printStackTrace();
        }
    }
    @SuppressLint("MissingPermission")
    public void setConnectingECU(BluetoothDevice bluetoothDevice) {
        //btDevice = bluetoothDevice;
        // Rfcomm 채널을 통해 블루투스 디바이스와 통신하는 소켓 생성
        try {
            MyUtils.mBluetoothAdapter.cancelDiscovery();
            socket = bluetoothDevice.createRfcommSocketToServiceRecord(MyUtils.uuid);
            socket.connect();
            if (socket.isConnected()) {
                running = true;
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (running) {
            try {
                outputStream = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                inputStream = socket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            MyUtils.btSocket = socket;

            getHeaderData();
            // 데이터 수신 함수 호출
            receiveData();
        }
    }
    /*@SuppressLint("MissingPermission")
    public void setConnectingECU(BluetoothDevice obdDevice) throws IOException {
        try {
            if (socket.isConnected()) {
                running = true;
            } else {
                running = false;
                socket.close();
                MyUtils.mBluetoothAdapter.cancelDiscovery();
                socket = obdDevice.createRfcommSocketToServiceRecord(MyUtils.uuid);
                socket.connect();
            }
        } catch (Exception e) {
            socket.close();
            CommonFunc.showToastOnUIThread("ECU 연결 실패. 재 연결 중...");
            e.printStackTrace();
        }
        if (running) {
            try {
                outputStream = socket.getOutputStream();
                inputStream = socket.getInputStream();
                MyUtils.btSocket = socket;

                getHeaderData();
                // 데이터 수신 함수 호출
                SystemClock.sleep(1000);
                receiveData();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }*/

    public void receiveData() {
        workerThread = new Thread(() -> {
            while (running) {
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }
                try {
                    int byteAvailable = 0;
                    if (inputStream != null) {
                        byteAvailable = inputStream.available();

                        // 데이터가 수신 된 경우
                        if (byteAvailable > 0) {
                            // 입력 스트림에서 바이트 단위로 읽어 옵니다.
                            byte[] rawBytes = new byte[byteAvailable];
                            inputStream.read(rawBytes);
                            final String rawResponse = new String(rawBytes, "UTF-8");
                            ArrayList<String> responses = getResponses(rawResponse);
                            for (String response : responses) {
                                ResponseCalculator.ResponseCalculator(response);
                            }
                        } else {
                            MyUtils.loaded_data = false;
                        }
                        sendStreamData();
                        if (Float.parseFloat(MyUtils.ecu_vehicle_speed) > 0 ||
                                Float.parseFloat(MyUtils.ecu_engine_load) > 0 ||
                                Float.parseFloat(MyUtils.ecu_engine_rpm) > 0 ||
                                Float.parseFloat(MyUtils.ecu_coolant_temp) > 0) {
                            MyUtils.con_ECU = true;
                            MyUtils.loaded_data = true;
                        }
                    }
                } catch (Exception e) {
                    CommonFunc.showToastOnUIThread("ECU 데이터 수신 오류 !!!");
                    closeSocket();
                    e.printStackTrace();
                }
            }
        });
        workerThread.start();
    }

    private void getHeaderData() {
        try {
            String[] msgs = {
                    "AT",
                    "ATD",
                    "ATZ"
            };
            if (outputStream == null)
                return;
            for (String msg : msgs) {
                outputStream.write(msg.getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendStreamData() {
        try {
            for (String[] info : MyUtils.enum_info) {
                String msg = "01" + info[1];
                try {
                    if (outputStream != null)
                        outputStream.write(msg.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (running) {
                    SystemClock.sleep(100);
                } else {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private ArrayList<String> getResponses(String rawResponse) {
        ArrayList<String> resVal = new ArrayList<>();
        String[] responses = rawResponse.split(" \r\r>");
        if (responses.length == 1) {
            responses = rawResponse.split("\r\n\r\n>");
        }
        if (responses.length == 1) {
            responses = rawResponse.split("\r\r>");
        }
        for (String res : responses) {
            String val = "";
            if (res.contains(MOD_PREFIX3)) {
                String[] values = res.split(MOD_PREFIX2);
                for (int i = 0; i < values.length; i++) {
                    if (i != 0) {
                        if (i == 1)
                            val += values[i];
                        else
                            val += " " + values[i];
                    }
                }
            } else if (res.contains(MOD_PREFIX1)) {
                String[] values = res.split(MOD_PREFIX1);
                for (int i = 0; i < values.length; i++) {
                    if (i != 0) {
                        if (i == 1)
                            val += values[i];
                        else
                            val += " " + values[i];
                    }
                }
            } else {
                String[] values = res.split(" ");
                for (int i = 0; i < values.length; i++) {
                    if (i != 0) {
                        if (i == 1)
                            val += values[i];
                        else
                            val += " " + values[i];
                    }
                }
            }
            resVal.add(val);
        }
        return resVal;
    }

    public void closeSocket(){
        try {
            running = false;
            if (socket != null && socket.isConnected()) {
                socket.close();
                if (workerThread != null) {
                    workerThread.interrupt();
                    workerThread = null;
                }
            }
            outputStream = null;
            inputStream = null;
            MyUtils.con_OBD = false;
            MyUtils.con_ECU = false;
            MyUtils.loaded_data = false;
            MyUtils.btSocket = null;
            MainActivity.getInstance().isConnecting = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
