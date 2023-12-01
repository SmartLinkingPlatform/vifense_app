package com.obd2.dgt.btManage.PIDsFormulas;

public class MAF_AIR_FLOW {
    public static String read(String firstHex, String secondHex) {
        String response = null;

        int firstDecimal = Integer.parseInt(firstHex, 16);
        int secondDecimal = Integer.parseInt(secondHex, 16);

        float maf = ((256 * firstDecimal) + secondDecimal) / (float)100;

        response = Float.toString(maf);
        return response;
    }
}
