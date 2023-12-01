package com.obd2.dgt.btManage.PIDsFormulas;

public class TIMING_ADVANCE {
    public static String read(String firstHex){
        String response = null;

        float firstDecimal = Integer.parseInt(firstHex, 16);
        float timingAdvance = (firstDecimal / 2) - 64;

        response = Float.toString(timingAdvance);

        return response;
    }
}
