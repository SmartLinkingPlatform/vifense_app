package com.obd2.dgt.btManage.PIDsFormulas;

public class VEHICLE_SPEED {
    public static String read(String firstHex){
        String response = null;

        int speed = Integer.parseInt(firstHex, 16);
        response = Integer.toString(speed);

        return response;
    }
}
