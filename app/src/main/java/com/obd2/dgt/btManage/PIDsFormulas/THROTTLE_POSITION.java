package com.obd2.dgt.btManage.PIDsFormulas;

public class THROTTLE_POSITION {
    public static String read(String firstHex) {
        String response = null;

        int firstDecimal = Integer.parseInt(firstHex, 16);

        float throttle_position = (100 * firstDecimal) / (float)255;

        response = Float.toString(throttle_position);
        return response;
    }
}
