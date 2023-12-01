package com.obd2.dgt.btManage.PIDsFormulas;

public class MILEAGE {
    public static String read(String firstHex, String secondHex,
                              String thirdHex, String fourthHex) {
        String response = null;

        int firstDecimal = Integer.parseInt(firstHex, 16);
        int secondDecimal = Integer.parseInt(secondHex, 16);
        int thirdDecimal = Integer.parseInt(secondHex, 16);
        int fourthDecimal = Integer.parseInt(secondHex, 16);

        double mileage = (firstDecimal * Math.pow(2, 24) + secondDecimal * Math.pow(2, 16) + thirdDecimal * Math.pow(2, 8) + fourthDecimal) / 10;

        response = Double.toString(mileage);
        return response;
    }
}
