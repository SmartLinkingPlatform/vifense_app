package com.obd2.dgt.btManage;

import com.obd2.dgt.btManage.PIDsFormulas.BATTERY_VOLTAGE;
import com.obd2.dgt.btManage.PIDsFormulas.COOLANT_TEMPERATURE;
import com.obd2.dgt.btManage.PIDsFormulas.MONITOR_STATUS;
import com.obd2.dgt.btManage.PIDsFormulas.TOTAL_DISTANCE_CODE;
import com.obd2.dgt.btManage.PIDsFormulas.ENGINE_LOAD;
import com.obd2.dgt.btManage.PIDsFormulas.ENGINE_RPM;
import com.obd2.dgt.btManage.PIDsFormulas.FUEL_RATE_GAL;
import com.obd2.dgt.btManage.PIDsFormulas.FUEL_RATE_LPH;
import com.obd2.dgt.btManage.PIDsFormulas.FUEL_TANK_LEVEL;
import com.obd2.dgt.btManage.PIDsFormulas.MAF_AIR_FLOW;
import com.obd2.dgt.btManage.PIDsFormulas.THROTTLE_POSITION;
import com.obd2.dgt.btManage.PIDsFormulas.VEHICLE_SPEED;
import com.obd2.dgt.utils.MyUtils;

public class ResponseCalculator {
    private final static String MOD_ONE_PREFIX = "41";
    private static String firstHex = null;
    private static String secondHex = null;
    private static String thirdHex = null;
    private static String fourthHex = null;

    static void ResponseCalculator(String response) {
        String responseWithPIDOnly = removeModPrefixAndWhitespace(response);
        if (responseWithPIDOnly.length() < 2)
            return;
        String PID = verifyPID(responseWithPIDOnly);
        getSeparateHexValues(responseWithPIDOnly);
        String calculationMethod = ModeRequestEnums.getEnum(PID);

        switch (calculationMethod) {
            case "MONITOR_STATUS":
                MyUtils.ecu_monitor_status = MONITOR_STATUS.read(firstHex, secondHex, thirdHex, fourthHex);
                break;
            case "ENGINE_LOAD":
                MyUtils.ecu_engine_load = ENGINE_LOAD.read(firstHex);
                break;
            case "COOLANT_TEMPERATURE":
                MyUtils.ecu_coolant_temp = COOLANT_TEMPERATURE.read(firstHex);
                break;
            case "ENGINE_RPM":
                MyUtils.ecu_engine_rpm = ENGINE_RPM.read(firstHex, secondHex);
                break;
            case "VEHICLE_SPEED":
                MyUtils.ecu_vehicle_speed = VEHICLE_SPEED.read(firstHex);
                break;
            case "TIMING_ADVANCE":
                //MyUtils.ecu_timing_advance = TIMING_ADVANCE.read(firstHex);
                break;
            case  "MAF_AIR_FLOW":
                MyUtils.ecu_maf = MAF_AIR_FLOW.read(firstHex, secondHex);
                break;
            case  "THROTTLE_POSITION":
                MyUtils.ecu_throttle_position = THROTTLE_POSITION.read(firstHex);
                break;
            case  "FUEL_TANK_LEVEL":
                MyUtils.ecu_fuel_tank_level = FUEL_TANK_LEVEL.read(firstHex);
                break;
            case  "FUEL_RATE_LPH":
                MyUtils.ecu_fuel_rate = FUEL_RATE_LPH.read(firstHex, secondHex);
                break;
            case  "FUEL_RATE_GAL":
                MyUtils.ecu_fuel_gal = FUEL_RATE_GAL.read(firstHex);
                break;
            case  "TOTAL_DISTANCE_CODE":
                MyUtils.ecu_total_distance = TOTAL_DISTANCE_CODE.read(firstHex, secondHex);
                break;
            case  "BATTERY_VOLTAGE":
                MyUtils.ecu_battery_voltage = BATTERY_VOLTAGE.read(firstHex, secondHex);
                break;
        }
    }

    private static void getSeparateHexValues(String responseWithPIDOnly) {
        String pureResponse = responseWithPIDOnly.substring(2);
        if (pureResponse.length() == 2) {
            firstHex = pureResponse.substring(0, 2);
        } else if (pureResponse.length() == 4) {
            firstHex = pureResponse.substring(0, 2);
            secondHex = pureResponse.substring(2, 4);
        } else if (pureResponse.length() == 6) {
            firstHex = pureResponse.substring(0, 2);
            secondHex = pureResponse.substring(2, 4);
            thirdHex = pureResponse.substring(4, 6);
        } else if (pureResponse.length() == 8) {
            firstHex = pureResponse.substring(0, 2);
            secondHex = pureResponse.substring(2, 4);
            thirdHex = pureResponse.substring(4, 6);
            fourthHex = pureResponse.substring(6, 8);
        }
    }

    private static String verifyPID(String responseWithPIDOnly) {
        return responseWithPIDOnly.substring(0, 2);
    }

    private static String removeModPrefixAndWhitespace(String response) {
        String result = null;
        String clearResponse = response.replaceAll(" ", "")
                .replaceAll("\r", "")
                .replaceAll("\n", "")
                .replaceAll(">", "");
        if (response.startsWith(MOD_ONE_PREFIX)) {
            result = clearResponse.substring(2);
        } else result = clearResponse;
        return result;
    }

}
