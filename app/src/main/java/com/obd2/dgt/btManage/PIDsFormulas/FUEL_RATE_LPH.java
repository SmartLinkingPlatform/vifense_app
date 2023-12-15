package com.obd2.dgt.btManage.PIDsFormulas;

public class FUEL_RATE_LPH {
    public static String read(String firstHex, String secondHex) {
        String response = null;

        int firstDecimal = Integer.parseInt(firstHex, 16);
        int secondDecimal = Integer.parseInt(secondHex, 16);

        float fuel = ((256 * firstDecimal) + secondDecimal) / (float)20;

        response = Float.toString(fuel);
        return response;
    }
}
