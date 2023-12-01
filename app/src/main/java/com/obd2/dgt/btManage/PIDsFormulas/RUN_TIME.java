package com.obd2.dgt.btManage.PIDsFormulas;

public class RUN_TIME {
    public static String read(String firstHex, String secondHex) {
        String response = null;

        int firstDecimal = Integer.parseInt(firstHex, 16);
        int secondDecimal = Integer.parseInt(secondHex, 16);

        int run_time = (256 * firstDecimal) + secondDecimal;

        response = Integer.toString(run_time);
        return response;
    }
}
