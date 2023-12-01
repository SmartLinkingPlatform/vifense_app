package com.obd2.dgt.btManage.PIDsFormulas;

public class COOLANT_TEMPERATURE {
    public static String read(String firstHex) {
        String response = null;

        float firstDecimal = Integer.parseInt(firstHex, 16);
        float engineLoad = firstDecimal - 40;

        response = Float.toString(engineLoad);
        return response;
    }
}
