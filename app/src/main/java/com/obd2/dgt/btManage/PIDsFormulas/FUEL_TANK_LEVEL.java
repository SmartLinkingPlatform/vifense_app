package com.obd2.dgt.btManage.PIDsFormulas;

public class FUEL_TANK_LEVEL {
    public static String read(String firstHex) {
        String response = null;

        int firstDecimal = Integer.parseInt(firstHex, 16);

        float fuel_level = (100 * firstDecimal) / (float)255;

        response = Float.toString(fuel_level);
        return response;
    }
}
