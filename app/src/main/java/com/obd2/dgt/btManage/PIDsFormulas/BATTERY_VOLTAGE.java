package com.obd2.dgt.btManage.PIDsFormulas;

public class BATTERY_VOLTAGE {
    public static String read(String firstHex, String secondHex) {
        String response = null;

        int firstDecimal = Integer.parseInt(firstHex, 16);
        int secondDecimal = Integer.parseInt(secondHex, 16);

        int fuel = ((256 * firstDecimal) + secondDecimal) / 1000;

        response = Integer.toString(fuel);
        return response;
    }
}
