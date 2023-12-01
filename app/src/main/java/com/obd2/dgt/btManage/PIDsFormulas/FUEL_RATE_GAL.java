package com.obd2.dgt.btManage.PIDsFormulas;

public class FUEL_RATE_GAL {
    public static String read(String firstHex) {
        String response = null;

        int firstDecimal = Integer.parseInt(firstHex, 16);

        response = Integer.toString(firstDecimal);
        return response;
    }
}
