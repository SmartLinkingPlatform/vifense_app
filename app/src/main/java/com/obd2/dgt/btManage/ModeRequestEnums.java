package com.obd2.dgt.btManage;

import java.util.HashMap;
import java.util.Map;

public enum ModeRequestEnums {
    ENGINE_LOAD("04"),
    COOLANT_TEMPERATURE("05"),
    //MANIFOLD_PRESSURE("0B"),
    ENGINE_RPM("0C"),
    VEHICLE_SPEED("0D"),
    //TIMING_ADVANCE("0E"),
    //INTAKE_TEMPERATURE("0F"),
    MAF_AIR_FLOW("10"),
    THROTTLE_POSITION("11"),
    FUEL_TANK_LEVEL("2F"),
    BATTERY_VOLTAGE("42"),
    FUEL_RATE_LITER("5E"),
    FUEL_RATE_GAL("9D"),
    //MILEAGE("A6"),
    //RUN_TIME("7F")
    ;

    private static final Map methodMap = new HashMap();

    static {
        for (ModeRequestEnums enums : ModeRequestEnums.values())
            methodMap.put(enums.getValue(), enums);
    }

    private final String value;

    ModeRequestEnums(String value) {this.value = value;}

    public final String getValue() {
        return value;
    }

    public static String getEnum( String value){
        return String.valueOf(methodMap.get(value));
    }

}
