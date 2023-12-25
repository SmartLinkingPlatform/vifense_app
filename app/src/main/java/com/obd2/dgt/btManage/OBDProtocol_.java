package com.obd2.dgt.btManage;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class OBDProtocol_ {
    private InputStream inputStream;
    private OutputStream outputStream;

    public OBDProtocol_(InputStream inputStream, OutputStream outputStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    public boolean setComProtocol() {
        boolean isProtocol = false;
        try {
            sendCommand("AT");
            Log.d("OBD-II", "Selected AT: " + readResponse());

            sendCommand("ATSP0");
            String response = readResponse();

            Log.d("OBD-II", "Selected protocol: " + response);
            // Check if the response indicates a successful protocol setup
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
            outputStream.write((command + "\r").getBytes());
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
