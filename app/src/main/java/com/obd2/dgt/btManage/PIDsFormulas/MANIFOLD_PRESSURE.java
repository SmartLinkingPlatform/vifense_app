package com.obd2.dgt.btManage.PIDsFormulas;

public class MANIFOLD_PRESSURE {
    public static String read(String firstHex){
        String response = null;

        int pressure = Integer.parseInt(firstHex, 16);
        response = Integer.toString(pressure);

        return response;
    }
}
