package com.obd2.dgt.btManage.PIDsFormulas;

public class TOTAL_DISTANCE_CODE {
    public static int read(String firstHex, String secondHex) {
        int firstDecimal = Integer.parseInt(firstHex, 16);
        int secondDecimal = Integer.parseInt(secondHex, 16);

        int distance = (256 * firstDecimal) + secondDecimal;

        return distance;
    }
}
