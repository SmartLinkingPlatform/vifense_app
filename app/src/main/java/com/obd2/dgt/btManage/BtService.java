package com.obd2.dgt.btManage;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.os.SystemClock;

import com.obd2.dgt.ui.MainActivity;
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

    public BtService() {
    }

    @SuppressLint("MissingPermission")
    public void connectDevice(BluetoothDevice bluetoothDevice) {
        MyUtils.isSocketError = false;
        // Rfcomm 채널을 통해 블루투스 디바이스와 통신하는 소켓 생성
        try {
            socket = bluetoothDevice.createRfcommSocketToServiceRecord(MyUtils.uuid);
            socket.connect();
            MyUtils.isObdSocket = true;
        } catch (IOException e) {
            MyUtils.isObdSocket = false;
            MyUtils.isSocketError = true;
            MyUtils.isPaired = false;
            e.printStackTrace();
        }
        if (MyUtils.isObdSocket) {
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
                while (MyUtils.isObdSocket) {
                    if (Thread.currentThread().isInterrupted()) {
                        break;
                    }
                    try {
                        // 데이터를 수신했는지 확인합니다.
                        int byteAvailable = inputStream.available();
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
                        }
                    } catch (IOException e) {
                        MyUtils.isObdSocket = false;
                        MyUtils.isSocketError = true;
                        MyUtils.isPaired = false;
                        e.printStackTrace();
                    }

                    setOutStream();
                }
            }
        });
        workerThread.start();
    }

    private void getHeaderData() {
        try {
            String[] msgs = {
                    "ATD",
                    "ATZ"
            };
            for (String msg : msgs) {
                outputStream.write(msg.getBytes());
                //SystemClock.sleep(100);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void setOutStream() {
        for (String[] info : MyUtils.enum_info) {
            String msg = "01" + info[1];
            msg += "\r";
            try {
                if (MyUtils.isObdSocket)
                    outputStream.write(msg.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
            SystemClock.sleep(100);
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

/*
    //고장진단 코드 읽기
    public void getFaultCodes() {
        new FaultCodeTask().execute();
    }
    private class FaultCodeTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            readFaultCodes(socket);
            return null;
        }
        private void readFaultCodes(BluetoothSocket socket) {
            try {
                // OBD-II 명령을 사용하여 고장 코드 읽기
                // "03"은 현재 발생 중인 고장 코드를 의미합니다.
                String obdCommand = "03";
                outputStream.write(obdCommand.getBytes());

                // 응답 데이터를 읽습니다.
                byte[] buffer = new byte[1024];
                int bytesRead = inputStream.read(buffer);

                // 읽은 데이터를 문자열로 변환하여 표시
                String obdData = new String(buffer, 0, bytesRead);

                // 고장 코드를 추출하여 표시 또는 다른 작업 수행
                String faultCodes = extractFaultCodes(obdData);
                handleFaultCodes(faultCodes);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        private String extractFaultCodes(String obdData) {
            return obdData.replace(">", "").trim();
        }
        private void handleFaultCodes(String faultCodes) {
            // 고장 코드를 처리하는 로직 추가
            TroubleCodes tcoc = new TroubleCodes();
            String result = tcoc.getFormattedResult(faultCodes);
            if(result==null)
                result = "";
            if(!result.equals("")) {
                //String[] dtcArray = result.split("\\n");
                //for (String code : dtcArray) {
                //    String explanation = getFaultCodeExplanation(code);
                //    System.out.println("Fault Code: " + code + ", Explanation: " + explanation);
                //}

            }else{
                //addListItem("No error codes received.");
            }
        }
    }
*/
    public void closeSocket(){
        try {
            if (socket.isConnected()) {
                socket.close();
                socket = null;
                MyUtils.isSocketError = false;
                MyUtils.isPaired = false;
                MyUtils.isObdSocket = false;
                workerThread.interrupt();
                workerThread = null;
                outputStream = null;
                inputStream = null;
                MyUtils.btSocket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
