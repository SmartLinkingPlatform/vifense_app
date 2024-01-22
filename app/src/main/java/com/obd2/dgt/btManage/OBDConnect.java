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
    private Thread workerThread = null; // 문자열 수신에 사용되는 쓰레드
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
            closeedSocket();
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
                    Thread.sleep(500);
                    getDataOBDtoECU();
                }/* else {
                    MainActivity.getInstance().setECULinkStatus(false);
                    CommonFunc.showToastOnUIThread("현재 차량의 ELM327 통신 프로토콜 검색중...");
                }*/
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendCommand(String command) {
        // Send command to OBD-II adapter
        try {
            String content = CommonFunc.getDateTime() + " --- ECU to ODB sendCommand --- " + command + "\r\n";
            CommonFunc.writeFile(MyUtils.StorageFilePath, "Vifense_Log.txt", content);

            outputStream.write((command).getBytes());
            outputStream.flush();
            Thread.sleep(10);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readResponse(String command) {
        try {
            boolean is_read = true;
            //int bytesRead = inputStream.read(buffer);
            //final String rawResponse = new String(buffer, 0, bytesRead);
            while (is_read) {
                if (MyUtils.isDiagnosis) {
                    continue;
                }

                int byteCount = inputStream.available();
                if (byteCount == 0) {
                    SystemClock.sleep(1);
                } else {
                    byte[] rawBytes = new byte[byteCount];
                    inputStream.read(rawBytes);
                    final String rawResponse = new String(rawBytes, "UTF-8");

                    String response = getResponse(rawResponse);
                    if (response.equals(command) || response.isEmpty()) {
                        SystemClock.sleep(1);
                        String content = CommonFunc.getDateTime() + " --- ECU to ODB Response --- " + response + "\r\n";
                        CommonFunc.writeFile(MyUtils.StorageFilePath, "Vifense_Log.txt", content);
                    } else {
                        String content = CommonFunc.getDateTime() + " --- ECU to ODB Response --- " + response + "\r\n";
                        CommonFunc.writeFile(MyUtils.StorageFilePath, "Vifense_Log.txt", content);
                        OBDResponse.ResponseCalculator(response);
                    }
                }
                is_read = false;
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
        String res = rawResponse.replaceAll("(\r\n|\r|\n|\n\r)", "");
        res = res.replace("null", "");
        res = res.replaceAll("\\s", "");
        res = res.replaceAll(">", "");
        res = res.replaceAll("SEARCHING...", "");
        if (res.contains(MOD_PREFIX)) {
            int index = res.indexOf(MOD_PREFIX);
            res = res.substring(index, res.length());
        }

        if (res.contains(" ")) {
            String[] values = res.split(" ");
            StringBuilder sub_str = new StringBuilder();
            for (int i = 0; i < values.length; i++) {
                if (i > 0) {
                    sub_str.append(values[i]);
                }
            }
            res = sub_str.toString();
        }
        if (res.contains("ERROR") || res.contains("NODATA")) {
            res = "no";
        }
        return res;
    }
    private void getDataOBDtoECU() {
        workerThread = new Thread(() -> {
            while (running) {
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }
                if (MyUtils.isDiagnosis) {
                    continue;
                }

                for (String[] info : MyUtils.enum_info) {
                    String msg = "01" + info[1];
                    if (outputStream != null)
                        sendCommand(msg);
                    if (inputStream != null)
                        readResponse(msg);
                }
            }
        });
        workerThread.start();
    }

    public void closeedSocket(){
        try {
            running = false;
            if (socket != null && socket.isConnected()) {
                socket.close();
                if (workerThread != null) {
                    workerThread.interrupt();
                    workerThread = null;
                }
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void finishSocket(){
        try {
            running = false;
            if (socket != null && socket.isConnected()) {
                socket.close();
                if (workerThread != null) {
                    workerThread.interrupt();
                    workerThread = null;
                }
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
