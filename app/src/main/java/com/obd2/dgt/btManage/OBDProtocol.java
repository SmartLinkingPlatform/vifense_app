package com.obd2.dgt.btManage;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.obd2.dgt.utils.MyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class OBDProtocol {
    private InputStream inputStream;
    private OutputStream outputStream;

    public OBDProtocol(InputStream inputStream, OutputStream outputStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    public boolean autoSelectProtocol() {
        // Try sending commands for different OBD-II protocols and check for responses

        boolean isProtocol = false;
        String[] obdProtocols = {"ATSP0", "ATSP1", "ATSP2", "ATSP3", "ATSP4", "ATSP5", "ATSP6", "ATSP7", "ATSP8", "ATSP9"};

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
        /*for (String protocol : obdProtocols) {
            try {
                sendCommand(protocol);
                String response = readResponse();

                Log.d("OBD-II", "Selected protocol: " + protocol);
                // Check if the response indicates a successful protocol setup
                if (response.contains("OK")) {
                    Log.d("OBD-II", "Selected protocol: " + protocol);
                    isProtocol = true;
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
                // Handle communication error
            }
        }*/
        return isProtocol;
    }

    private void sendCommand(String command) throws IOException {
        // Send command to OBD-II adapter
        outputStream.write((command + "\r").getBytes());
        outputStream.flush();
    }

    private String readResponse() throws IOException {
        // Read response from OBD-II adapter
        byte[] buffer = new byte[1024];
        int bytesRead = inputStream.read(buffer);
        return new String(buffer, 0, bytesRead);
    }
}
