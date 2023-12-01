package com.obd2.dgt.btManage.PIDsFormulas;

public class ENGINE_LOAD {
    public static String read(String firstHex) {
        String response = null;

        float firstDecimal = Integer.parseInt(firstHex, 16);
        float engineLoad = 100 * firstDecimal / (float) 255;

        response = Float.toString(engineLoad);
        return response;
    }
}
