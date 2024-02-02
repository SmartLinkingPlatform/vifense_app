package com.obd2.dgt.btManage;

import android.os.SystemClock;
import android.util.Log;

import com.obd2.dgt.utils.CommonFunc;
import com.obd2.dgt.utils.MyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class OBDProtocol {
    private InputStream inputStream;
    private OutputStream outputStream;
    private ArrayList<String> custom_protocol = new ArrayList<>();

    public OBDProtocol(InputStream inputStream, OutputStream outputStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        /*custom_protocol = new ArrayList<>();
        for (int i = 2; i <= 6; i++) {
            custom_protocol.add(Protocol.PROTOCOL_CUSTOM[i]);
        }*/
    }

    public boolean setComProtocol() {
        String response = "";
        boolean isProtocol = false;

        try {
            //ATZ 차량의 컴퓨터나 모듈을 재설정합니다
            //ATDP Describe the current Protocol
            //ATAT0-1-2 Adaptive Timing Off - adaptive Timing Auto1 - adaptive Timing Auto2
            //ATE0-1 Echo Off - Echo On ECHO를 끄거나 켭니다. ECHO가 켜져 있으면 명령을 보내고 받은 응답을 동시에 표시합니다.
            //ATSP0 통신 프로토콜을 선택합니다.
            //ATMA 모든 모니터링이 활성화되도록 설정합니다.
            //ATL1-0 Linefeeds On - Linefeeds Off 라인 피드 문자의 사용 여부를 설정합니다.
            //ATH1-0 Headers On - Headers Off 헤더를 설정합니다.
            //ATI 장치 정보를 표시합니다.
            //ATS1-0 printing of Spaces On - printing of Spaces Off
            //ATAL Allow Long (>7 byte) messages
            //ATRD Read the stored data
            //ATSTFF Set time out to maximum
            //ATSTHH Set timeout to 4ms
            //ATMT 테스트 결과를 모니터합니다.

            String[] initializeCommands = new String[]{"ATZ", "ATI", "ATL1", "ATE0", "ATH1", "ATAL", "ATRD", "ATDP"};
            for (String command : initializeCommands) {
                sendCommand(command);
                readResponse();
            }
            Thread.sleep(1);

            String[] protocol_command = new String[] {MyUtils.SEL_PROTOCOL, MyUtils.SEL_PROTOCOL};
            for (String command : protocol_command) {
                sendCommand(command);
                response = readResponse();

                /*String content = CommonFunc.getDateTimeMilliseconds() + " --- " + MyUtils.SEL_PROTOCOL + " --- " + response + "\r\n";
                CommonFunc.writeFile(MyUtils.StorageFilePath, "Vifense_Log.txt", content);*/

                if (response.contains("OK")) {
                    isProtocol = true;
                    break;
                }
            }
            /*sendCommand(MyUtils.SEL_PROTOCOL);
            response = readResponse();
            Thread.sleep(20);
            if (response.contains("OK")) {
                isProtocol = true;
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }

        return isProtocol;
    }

    private void sendCommand(String command) throws IOException {
        // Send command to OBD-II adapter
        try {
            outputStream.write((command).getBytes());
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String readResponse() throws IOException {
        // Read response from OBD-II adapter
        String response = "";
        try {
            byte[] buffer = new byte[1024];
            int bytesRead = inputStream.read(buffer);
            response = new String(buffer, 0, bytesRead);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

}
