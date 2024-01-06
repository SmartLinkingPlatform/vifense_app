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
            //ATZ reset all
            //ATDP Describe the current Protocol
            //ATAT0-1-2 Adaptive Timing Off - adaptive Timing Auto1 - adaptive Timing Auto2
            //ATE0-1 Echo Off - Echo On
            //ATSP0 Set Protocol to Auto and save it
            //ATMA Monitor All
            //ATL1-0 Linefeeds On - Linefeeds Off
            //ATH1-0 Headers On - Headers Off
            //ATI Device infomation
            //ATS1-0 printing of Spaces On - printing of Spaces Off
            //ATAL Allow Long (>7 byte) messages
            //ATRD Read the stored data
            //ATSTFF Set time out to maximum
            //ATSTHH Set timeout to 4ms

            String[] initializeCommands = new String[]{"ATZ", "ATL0", "ATE1", "ATH1", "ATAT1", "ATSTFF", "ATI", "ATDP"};
            for (String command : initializeCommands) {
                sendCommand(command);
                readResponse();
            }
            SystemClock.sleep(100);
            sendCommand(MyUtils.SEL_PROTOCOL);
            response = readResponse();
            SystemClock.sleep(100);
            if (response.contains("OK")) {
                isProtocol = true;
            }
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
