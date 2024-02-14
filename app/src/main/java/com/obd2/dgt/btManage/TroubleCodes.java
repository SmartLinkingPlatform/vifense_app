package com.obd2.dgt.btManage;

import com.obd2.dgt.utils.CommonFunc;

public class TroubleCodes {
    char[] dtcLetters = {'P', 'C', 'B', 'U'};
    char[] hexArray = "0123456789ABCDEF".toCharArray();
    StringBuilder codes = new StringBuilder();

    public String getFormattedResult(String rawData) {
        String result = rawData.replace("SEARCHING...", "").replace("NODATA", "");
        String workingData = "";
        int startIndex = 0;//Header size.

        StringBuilder canOneFrame = new StringBuilder();
        result = result.replaceAll("7E8", "");
        String[] values = result.split("[\r\r]");
        for (String val : values) {
            val = val.replaceAll(" ", "");
            if (val.contains(":")) {
                int index = val.indexOf(":") + 1;
                val = val.substring(index);
            }
            if (val.length() > 4) {
                canOneFrame.append(val);
            }
        }

        if (canOneFrame.toString().contains("43")) {
            int index = canOneFrame.indexOf("43") + 2;
            String res = canOneFrame.substring(index);
            String c_cnt = res.substring(0, 2);
            index = canOneFrame.indexOf(c_cnt) + 2;
            workingData = canOneFrame.substring(index);
        }
        for (int begin = startIndex; begin < workingData.length(); begin += 4) {
            String dtc = "";
            byte b1 = (byte) ((Character.digit(workingData.charAt(begin), 16) << 4));
            int ch1 = ((b1 & 0xC0) >> 6);
            int ch2 = ((b1 & 0x30) >> 4);
            dtc += dtcLetters[ch1];
            dtc += hexArray[ch2];
            if (workingData.length() < begin + 1)
                break;
            if (workingData.length() < begin + 4)
                break;
            dtc += workingData.substring(begin + 1, begin + 4);
            if (dtc.equals("P0000")) {
                return "";
            }
            codes.append(dtc);
            codes.append('\n');
        }

        return codes.toString();
    }
    /*
    public String getFormattedResult(String rawData) {
        final String result = rawData.replace("SEARCHING...", "").replace("NODATA", "");
        String workingData;
        int startIndex = 0;//Header size.

        String canOneFrame = result.replaceAll("[\r\n]", "");
        canOneFrame = result.replaceAll("7E8", "");
        int canOneFrameLength = canOneFrame.length();
        if (canOneFrameLength <= 16 && canOneFrameLength % 4 == 0) {//CAN(ISO-15765) protocol one frame.
            workingData = canOneFrame;//43yy{codes}
            startIndex = 4;//Header is 43yy, yy showing the number of data items.
        } else if (result.contains(":")) {//CAN(ISO-15765) protocol two and more frames.
            workingData = result.replaceAll("[\r\n].:", "");//Edit: x:43yy{codes}
            startIndex = 4;//Edit: Header is x:43yy, x: showing the frame number beginning at 0, yy showing the number of data items.
        } else {//ISO9141-2, KWP2000 Fast and KWP2000 5Kbps (ISO15031) protocols.
            workingData = result.replaceAll("^43|[\r\n]43|[\r\n]", "");
        }
        for (int begin = startIndex; begin < workingData.length(); begin += 4) {
            String dtc = "";
            byte b1 = (byte) ((Character.digit(workingData.charAt(begin), 16) << 4));
            int ch1 = ((b1 & 0xC0) >> 6);
            int ch2 = ((b1 & 0x30) >> 4);
            dtc += dtcLetters[ch1];
            dtc += hexArray[ch2];
            if (workingData.length() < begin + 1)
                break;
            if (workingData.length() < begin + 4)
                break;
            dtc += workingData.substring(begin + 1, begin + 4);
            if (dtc.equals("P0000")) {
                return "";
            }
            codes.append(dtc);
            codes.append('\n');
        }

        return codes.toString();
    }
     */
}
