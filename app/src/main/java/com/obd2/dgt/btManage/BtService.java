package com.obd2.dgt.btManage;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.View;

import com.obd2.dgt.R;
import com.obd2.dgt.ui.LoginActivity;
import com.obd2.dgt.ui.MainActivity;
import com.obd2.dgt.ui.SplashActivity;
import com.obd2.dgt.utils.MyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class BtService {
    private BluetoothSocket socket = null;
    private OutputStream outputStream = null; // 블루투스에 데이터를 출력하기 위한 출력 스트림
    private InputStream inputStream = null; // 블루투스에 데이터를 입력하기 위한 입력 스트림
    private Thread workerThread = null; // 문자열 수신에 사용되는 쓰레드
    private final String MOD_PREFIX1 = "41";
    private final String MOD_PREFIX2 = " 41 ";
    private final String MOD_PREFIX3 = "7E8";
    private boolean startedEngine = false;
    private BluetoothDevice btDevice;
    boolean running = false;

    public BtService() {
    }

    @SuppressLint("MissingPermission")
    public void connectOBD2Device(BluetoothDevice bluetoothDevice) {
        btDevice = bluetoothDevice;
        // Rfcomm 채널을 통해 블루투스 디바이스와 통신하는 소켓 생성
        Handler handler = new Handler(Looper.getMainLooper());
        try {
            if (socket == null) {
                socket = btDevice.createRfcommSocketToServiceRecord(MyUtils.uuid);
                socket.connect();
                if (socket.isConnected()) {
                    running = true;
                    Thread.sleep(1000);
                }
            }
        } catch (Exception e) {
            closeSocket();
            MainActivity.getInstance().showDisconnectedStatus(1);
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

    public void receiveData() {
        // 데이터를 수신하기 위한 쓰레드 생성
        workerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (running) {
                    if (Thread.currentThread().isInterrupted()) {
                        break;
                    }
                    try {
                        // 데이터를 수신했는지 확인합니다.
                        int byteAvailable = 0;
                        if (inputStream != null) {
                            byteAvailable = inputStream.available();
                        }
                        // 데이터가 수신 된 경우
                        if (byteAvailable > 0) {
                            // 입력 스트림에서 바이트 단위로 읽어 옵니다.
                            byte[] rawBytes = new byte[byteAvailable];
                            inputStream.read(rawBytes);
                            final String rawResponse = new String(rawBytes, "UTF-8");
                            String res = rawResponse.toLowerCase();
                            if (res.contains("elm") || res.contains("ok")) {
                                getIgnitionMonitor();
                                continue;
                            }
                            ArrayList<String> responses = getResponses(rawResponse);
                            for (String response : responses) {
                                ResponseCalculator.ResponseCalculator(response);
                            }
                        }

                        if (startedEngine) {
                            MyUtils.isObdSocket = true;
                            MyUtils.loading_obd_data = true;
                            sendStreamData();
                        }
                    } catch (Exception e) {
                        closeSocket();
                        e.printStackTrace();
                    }
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
            if (outputStream == null)
                return;
            for (String[] info : MyUtils.enum_info) {
                String msg = "01" + info[1];
                try {
                    if (MyUtils.isObdSocket)
                        outputStream.write(msg.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Thread.sleep(100);
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

    int repeat = 0;
    private void getIgnitionMonitor() {
        OBD2ApiCommand command = new OBD2ApiCommand(socket);
        String ignition_monitor = command.getIgnitionMonitorStatus();
        if (ignition_monitor.equalsIgnoreCase("on")) {
            startedEngine = true;
        } else {
            repeat++;
            if (repeat < 3) {
                getIgnitionMonitor();
            }
            closeSocket();
            MainActivity.getInstance().showDisconnectedStatus(1);
        }
    }
    public void closeSocket(){
        try {
            if (socket != null && socket.isConnected()) {
                socket.close();
                if (workerThread != null) {
                    workerThread.interrupt();
                    workerThread = null;
                }
            }
            socket = null;
            running = false;
            outputStream = null;
            inputStream = null;
            startedEngine = false;
            MyUtils.isPaired = false;
            MyUtils.isObdSocket = false;
            MyUtils.loading_obd_data = false;
            MyUtils.btSocket = null;
            MainActivity.getInstance().isConnecting = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
