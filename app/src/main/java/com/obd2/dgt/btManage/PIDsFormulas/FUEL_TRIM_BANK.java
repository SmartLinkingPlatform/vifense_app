package com.obd2.dgt.btManage.PIDsFormulas;

public class FUEL_TRIM_BANK {
    public static String read(String firstHex) {
        String response = null;

        int firstDecimal = Integer.parseInt(firstHex, 16);

        int fuel_trim_bank = ((100 * firstDecimal) / 128) - 100;

        response = Integer.toString(fuel_trim_bank);
        return response;
    }
}
