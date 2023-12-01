package com.obd2.dgt.btManage.PIDsFormulas;

public class INTAKE_TEMPERATURE {
    public static String read(String firstHex){
        String response = null;

        int firstDecimal = Integer.parseInt(firstHex, 16);
        int temperature =  firstDecimal - 40;

        response = Integer.toString(temperature);

        return response;
    }
}
