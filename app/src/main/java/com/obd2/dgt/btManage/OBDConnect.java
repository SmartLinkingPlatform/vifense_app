package com.obd2.dgt.btManage;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.SystemClock;

import com.obd2.dgt.ui.MainActivity;
import com.obd2.dgt.utils.CommonFunc;
import com.obd2.dgt.utils.MyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class OBDConnect {
    private BluetoothSocket socket = null;
    private OutputStream outputStream = null; // 블루투스에 데이터를 출력하기 위한 출력 스트림
    private InputStream inputStream = null; // 블루투스에 데이터를 입력하기 위한 입력 스트림
    private Thread commandThread = null; // 문자열 수신에 사용되는 쓰레드
    private final String MOD_PREFIX = "41";
    private final String MOD_PREFIX2 = "7E8";
    boolean running = false;

    BluetoothDevice obdDevice;

    public OBDConnect() {
    }

    @SuppressLint("MissingPermission")
    public void setConnectingOBD(BluetoothDevice obdDevice) {
        try {
            this.obdDevice = obdDevice;
            MyUtils.mBluetoothAdapter.cancelDiscovery();
            socket = BtManager.connect(obdDevice, true);
            if (socket.isConnected()) {
                MyUtils.con_OBD = true;
                running = true;
            } else {
                MyUtils.con_OBD = false;
            }
        } catch (Exception e) {
            finishSocket();
            e.printStackTrace();
        }
    }

    @SuppressLint("MissingPermission")
    public void setConnectingECU() {
        if (running) {
            try {
                outputStream = socket.getOutputStream();
                inputStream = socket.getInputStream();
                MyUtils.btSocket = socket;
                OBDProtocol protocol = new OBDProtocol(inputStream, outputStream);

                if (protocol.setComProtocol()) {
                    MainActivity.getInstance().setECULinkStatus(true);
                    // 데이터 수신 함수 호출
                    sendDataOBDtoECU();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void sendCommand(String command) {
        // Send command to OBD-II adapter
        try {
            outputStream.write((command).getBytes());
            outputStream.flush();

            Thread.sleep(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readResponse() {
        try {
            byte[] buffer = new byte[4096];
            int bytesRead = inputStream.read(buffer);
            final String rawResponse = new String(buffer, 0, bytesRead);

            /*String content = CommonFunc.getDateTimeMilliseconds() + " --- readResponse2 --- " + rawResponse + "\r\n";
            CommonFunc.writeFile(MyUtils.StorageFilePath, "Vifense_Log.txt", content);*/

            String response = getResponse(rawResponse);
            String resVal = CommonFunc.checkInputOnlyNumberAndAlphabet(response);
            if (!resVal.isEmpty()) {
                OBDResponse.ResponseCalculator(resVal);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (Float.parseFloat(MyUtils.ecu_vehicle_speed) > 0 ||
                Float.parseFloat(MyUtils.ecu_engine_load) > 0 ||
                Float.parseFloat(MyUtils.ecu_engine_rpm) > 0 ||
                Float.parseFloat(MyUtils.ecu_coolant_temp) > 0) {
            MyUtils.con_ECU = true;
            MyUtils.loaded_data = true;
        }
    }

    private String getResponse(String rawResponse) {
        String result = "";
        String res = rawResponse.replaceAll("(\r\n|\r|\n|\n\r)", "");
        res = res.replace("null", "");
        res = res.replaceAll("\\s", "");
        res = res.replaceAll(">", "");
        res = res.replaceAll("SEARCHING...", "");
        if (res.contains(MOD_PREFIX)) {
            int index = res.indexOf(MOD_PREFIX);
            res = res.substring(index, res.length());
            result = CommonFunc.getResponseValue(res);
        }
        if (result.contains(" ")) {
            String[] values = result.split(" ");
            StringBuilder sub_str = new StringBuilder();
            for (int i = 0; i < values.length; i++) {
                if (i > 0) {
                    sub_str.append(values[i]);
                }
            }
            result = sub_str.toString();
        }
        if (result.contains("ERROR") || result.contains("NODATA")) {
            result = "no";
        }
        return result;
    }

    private void sendDataOBDtoECU() {
        commandThread = new Thread(() -> {
            while (running) {
                try {
                    if (commandThread != null && !commandThread.isInterrupted()) {
                        if (MyUtils.isDiagnosis) {
                            continue;
                        }

                        for (String[] info : MyUtils.pid_speed) {
                            String msg = "01" + info[1];
                            String command = CommonFunc.checkInputOnlyNumberAndAlphabet(msg);
                            if (outputStream != null)
                                sendCommand(command);
                            if (inputStream != null)
                                readResponse();
                            //SystemClock.sleep(1);
                        }
                        if (MyUtils.isEnumSec) {
                            for (String[] info : MyUtils.pid_second) {
                                String msg = "01" + info[1];
                                String command = CommonFunc.checkInputOnlyNumberAndAlphabet(msg);
                                if (outputStream != null)
                                    sendCommand(command);
                                if (inputStream != null)
                                    readResponse();
                                //SystemClock.sleep(1);
                            }
                            MyUtils.isEnumSec = false;
                        }
                        if (MyUtils.isEnumInfo) {
                            for (String[] info : MyUtils.pid_info) {
                                String msg = "01" + info[1];
                                String command = CommonFunc.checkInputOnlyNumberAndAlphabet(msg);
                                if (outputStream != null)
                                    sendCommand(command);
                                if (inputStream != null)
                                    readResponse();
                                //SystemClock.sleep(1);
                            }
                            MyUtils.isEnumInfo = false;
                        }
                    } else {
                        running = false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        commandThread.setPriority(Thread.MAX_PRIORITY);
        commandThread.start();
    }

    public void finishSocket(){
        try {
            running = false;
            if (socket != null && socket.isConnected()) {
                socket.close();
                if (commandThread != null) {
                    commandThread.interrupt();
                    commandThread = null;
                }
                /*if (workerThread != null) {
                    workerThread.interrupt();
                    workerThread = null;
                }*/
            }
            if (outputStream != null) {
                outputStream.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            if (MyUtils.btSocket != null) {
                MyUtils.btSocket.close();
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
