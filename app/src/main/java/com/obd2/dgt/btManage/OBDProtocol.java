package com.obd2.dgt.btManage;

import android.util.Log;

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
        custom_protocol = new ArrayList<>();
        for (int i = 2; i <= 6; i++) {
            custom_protocol.add(Protocol.PROTOCOL_CUSTOM[i]);
        }
    }

    public boolean setComProtocol() {
        String response = "";
        boolean isProtocol = false;
        String auto = Protocol.PROTOCOL_AUTOMATIC;
        try {
            sendCommand("AT");
            Log.d("OBD-II", "Selected AT: " + readResponse());

            sendCommand(auto);
            response = readResponse();
            if (response.contains("OK")) {
                isProtocol = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!isProtocol) {
            for (String p : custom_protocol) {
                try {
                    sendCommand(p);
                    response = readResponse();
                    if (response.contains("OK")) {
                        Log.d("OBD-II", "Selected protocol: " + p);
                        isProtocol = true;
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (!isProtocol) {
            isProtocol = resetComProtocol();
        }

        return isProtocol;
    }

    public boolean resetComProtocol() {
        try {
            sendCommand(Protocol.ENABLE_DISPLAY_HEADERS);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String response = "";
        boolean isProtocol = false;

        String auto = Protocol.PROTOCOL_AUTOMATIC.replace(" ", "");
        try {
            sendCommand(auto);
            response = readResponse();
            if (response.contains("OK")) {
                isProtocol = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!isProtocol) {
            for (String p : custom_protocol) {
                try {
                    p = p.replace(" ", "");
                    sendCommand(p);
                    response = readResponse();
                    if (response.contains("OK")) {
                        Log.d("OBD-II", "Selected protocol: " + p);
                        isProtocol = true;
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
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
