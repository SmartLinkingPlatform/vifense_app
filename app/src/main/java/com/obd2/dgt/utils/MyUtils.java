package com.obd2.dgt.utils;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.graphics.PointF;
import android.util.DisplayMetrics;

import com.obd2.dgt.btManage.BtService;
import com.obd2.dgt.dbManage.DBConnect;
import com.obd2.dgt.R;
import com.obd2.dgt.ui.AppBaseActivity;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class MyUtils {
    //public static String server_url = "http://192.168.1.6";
    public static String server_url = "https://dgt.vifense.com";
    public static String signup_url = "https://dgt.vifense.com/mok/auth_signup.html";
    public static String find_url = "https://dgt.vifense.com/mok/auth_findpwd.html";
    public static String call_company = "/mobile.companyInfo";
    public static String user_signup = "/mobile.userSignup";
    public static String user_login = "/mobile.userLogin";
    public static String user_modify = "/mobile.userInfoModify";
    public static String reg_car = "/mobile.regCarInfo";
    public static String mod_car = "/mobile.modCarInfo";
    public static String del_car = "/mobile.delCarInfo";
    public static String read_driving = "/mobile.readDriving";
    public static String save_driving = "/mobile.saveDriving";
    public static String ranking = "/mobile.ranking";
    public static String driving_ranking = "/mobile.drivingRanking";
    public static String mgs_list = "/mobile.messageList";
    public static String new_pwd = "/mobile.newpassword";
    public static AppBaseActivity appBase = null;
    public static Class<?> currentActivity = null;
    public static Context mContext = null;
    public static boolean run_main = false;
    public static boolean loading_obd_data = false;
    public static boolean show_dash_dialog = false;
    public static DisplayMetrics metrics;
    public static float mDpX = 1;
    public static float mDpY = 1;
    public static DBConnect db_connect;
    public static UUID uuid = java.util.UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    public static BluetoothAdapter mBluetoothAdapter = null;
    public static BluetoothSocket btSocket = null;
    public static BtService btService = null;
    public static String PID = "01";
    public static int max_speed = 0; //최대 속도
    public static int idling_time = 0; //공회전 시간
    public static int fast_speed_time = 0; //과속 운전 시간
    public static int fast_speed_cnt = 0; //과속 운전 수
    public static int quick_speed_cnt = 0; //급가속 수
    public static int brake_speed_cnt = 0; //급제동 수
    public static final int REQUEST_ENABLE_BT = 10;
    public static final int REQUEST_PAIRED_BT = 11;
    public static int[] main_list_images = {
            R.drawable.img_1, //0
            R.drawable.img_2, //1
            R.drawable.img_3, //2
            R.drawable.img_4, //3
    };
    public static int[] main_list_texts = {
            R.string.vehicle_gauge, //0
            R.string.vehicle_diagnosis, //1
            R.string.driving_record, //2
            R.string.vehicle_management, //3
    };
    public static int[] safe_driving_scorings = {
            R.string.safe_driving_scoring1, //0
            R.string.safe_driving_scoring2, //1
            R.string.safe_driving_scoring3, //2
            R.string.safe_driving_scoring4, //3
    };

    public static PointF[] gauge_pos = {
            new PointF(25, 25),     // 0
            new PointF(542, 25),    // 1
            new PointF(25, 572),    // 2
            new PointF(542, 572),   // 3
            new PointF(25, 1119),   // 4
            new PointF(542, 1119),  // 5
            new PointF(25, 1666),   // 6
            new PointF(542, 1666),  // 7
            new PointF(25, 2213),   // 8
            new PointF(542, 2213)   // 9
    };

    public static String[][] enum_info = {
            {"ENGINE_LOAD", "04"},          //엔진 부하
            {"COOLANT_TEMPERATURE", "05"},  //냉각수 온도
            {"ENGINE_RPM", "0C"},           //엔진 RPM
            {"VEHICLE_SPEED", "0D"},        //차량 속도
            //{"TIMING_ADVANCE", "0E"},       //주행 시간
            {"MAF_AIR_FLOW", "10"},         //흡입 공기량
            {"THROTTLE_POSITION", "11"},    //스로틀 위치
            {"FUEL_TANK_LEVEL", "2F"},      //연료 탱크 레벨
            {"TOTAL_DISTANCE_CODE", "31"},        //주행 거리
            {"BATTERY_VOLTAGE", "42"},      //배터리 전압
            {"FUEL_RATE_LITER", "5E"},      //순간 연료 소모량 l/h
            {"FUEL_RATE_GAL", "9D"},       //순간 연료 소모량 g/s
    };
    public static String ecu_engine_load = "0";
    public static String ecu_coolant_temp = "0";
    public static String ecu_engine_rpm = "0";
    public static String ecu_vehicle_speed = "0";
    public static String ecu_timing_advance = "0";
    public static String ecu_fuel_tank_level = "0";
    public static String ecu_fuel_rate = "0";
    public static String ecu_fuel_rate_gram = "0";
    public static String ecu_fuel_consume = "0";
    public static String ecu_battery_voltage = "0";
    public static int ecu_total_distance = 0;
    public static String ecu_mileage = "0";
    public static String ecu_driving_time = "00:00";
    public static String ecu_run_time = "0";
    public static String ecu_maf = "0";
    public static String ecu_throttle_position = "0";
    public static String ecu_trouble_code = "";
    public static String ecu_consume_warning = "";
    public static boolean is_trouble = false;
    public static boolean is_consume = false;
    public static boolean is_error_dlg = false;
    public static int err_idx = 0;

    public static String[] link_methods = {
            "Bluetooth",
            "Bluetooth LE 4.0"
    };
    public static ArrayList<String[]> messageInfo = new ArrayList<>();
    public static ArrayList<String[]> troubleCodes = new ArrayList<>();
    public static int lastMsgID = 0;
    public static boolean msg_show = false;
    public static ArrayList<String[]> gaugeInfo = new ArrayList<>();
    public static ArrayList<String[]> carInfo = new ArrayList<>();
    public static ArrayList<String[]> companyInfo = new ArrayList<>();
    public static int admin_id = 0;
    public static int my_id = 0; //사용자 유일 번호
    public static int car_id = 0; //차량 유일 번호
    public static String my_phone = "";
    public static String my_name = "";
    public static String my_pwd = "";
    public static String my_company = "";
    public static String obd2_name = "";
    public static String obd2_address = "";
    public static boolean isPaired = false;
    public static boolean savedSocketStatus = false;
    public static boolean isObdSocket = false;
    public static String con_method = "";
    public static boolean isSocketError = false;

    public static int sel_car_id = 0;
    public static boolean showGauge = false;
    public static String mileage_score = "0";
    public static String safety_score = "0";

    public static int[] company_names = {
            R.string.car_name_1, R.string.car_name_2, R.string.car_name_3, R.string.car_name_4,
            R.string.car_name_5, R.string.car_name_6, R.string.car_name_7, R.string.car_name_8,
            R.string.car_name_9, R.string.car_name_10, R.string.car_name_11, R.string.car_name_12,
            R.string.car_name_13, R.string.car_name_14, R.string.car_name_15, R.string.car_name_16,
            R.string.car_name_17, R.string.car_name_18, R.string.car_name_19, R.string.car_name_20,
            R.string.car_name_21, R.string.car_name_22, R.string.car_name_23, R.string.car_name_24,
            R.string.car_name_25, R.string.car_name_26, R.string.car_name_27, R.string.car_name_28,
            R.string.car_name_29, R.string.car_name_30, R.string.car_name_31, R.string.car_name_31,
            R.string.car_name_33, R.string.car_name_34, R.string.car_name_35, R.string.car_name_36,
            R.string.car_name_37, R.string.car_name_38, R.string.car_name_39, R.string.car_name_40,
            R.string.car_name_41, R.string.car_name_42, R.string.car_name_43, R.string.car_name_44,
            R.string.car_name_45, R.string.car_name_46, R.string.car_name_47, R.string.car_name_48,
            R.string.car_name_49, R.string.car_name_50, R.string.car_name_51, R.string.car_name_52,
            R.string.car_name_53, R.string.car_name_54, R.string.car_name_55, R.string.car_name_56,
            R.string.car_name_57, R.string.car_name_58, R.string.car_name_59, R.string.car_name_60,
            R.string.car_name_61, R.string.car_name_62, R.string.car_name_63, R.string.car_name_64,
            R.string.car_name_65, R.string.car_name_66, R.string.car_name_67, R.string.car_name_68,
            R.string.car_name_69, R.string.car_name_70, R.string.car_name_71, R.string.car_name_72,
            R.string.car_name_73, R.string.car_name_74, R.string.car_name_75, R.string.car_name_76,
            R.string.car_name_77, R.string.car_name_78, R.string.car_name_79, R.string.car_name_80,
            R.string.car_name_81, R.string.car_name_82, R.string.car_name_83, R.string.car_name_84,
            R.string.car_name_85, R.string.car_name_86, R.string.car_name_87, R.string.car_name_88,
            R.string.car_name_89, R.string.car_name_90, R.string.car_name_91, R.string.car_name_92,
            R.string.car_name_93, R.string.car_name_94, R.string.car_name_95, R.string.car_name_96,
            R.string.car_name_97, R.string.car_name_98
    };
    public static int[] model_names = {
            R.string.model_name_1, R.string.model_name_2, R.string.model_name_3, R.string.model_name_4,
            R.string.model_name_5, R.string.model_name_6, R.string.model_name_7, R.string.model_name_8,
            R.string.model_name_9, R.string.model_name_10, R.string.model_name_11, R.string.model_name_12,
            R.string.model_name_13, R.string.model_name_14, R.string.model_name_15, R.string.model_name_16,
            R.string.model_name_17, R.string.model_name_18, R.string.model_name_19, R.string.model_name_20,
            R.string.model_name_21, R.string.model_name_22, R.string.model_name_23, R.string.model_name_24,
            R.string.model_name_25, R.string.model_name_26, R.string.model_name_27, R.string.model_name_28,
            R.string.model_name_29, R.string.model_name_30, R.string.model_name_31, R.string.model_name_31,
            R.string.model_name_33, R.string.model_name_34, R.string.model_name_35, R.string.model_name_36,
            R.string.model_name_37, R.string.model_name_38, R.string.model_name_39, R.string.model_name_40,
            R.string.model_name_41, R.string.model_name_42, R.string.model_name_43, R.string.model_name_44,
            R.string.model_name_45, R.string.model_name_46, R.string.model_name_47, R.string.model_name_48,
            R.string.model_name_49, R.string.model_name_50, R.string.model_name_51, R.string.model_name_52,
            R.string.model_name_53, R.string.model_name_54, R.string.model_name_55, R.string.model_name_56
    };

    public static ArrayList<String> create_years = new ArrayList<>();

    public static int[] fuel_types = {
            R.string.fuel_type_1, R.string.fuel_type_2, R.string.fuel_type_3, R.string.fuel_type_4,
            R.string.fuel_type_5, R.string.fuel_type_6, R.string.fuel_type_7, R.string.fuel_type_8,
            R.string.fuel_type_9, R.string.fuel_type_10, R.string.fuel_type_11, R.string.fuel_type_12,
            R.string.fuel_type_13, R.string.fuel_type_14, R.string.fuel_type_15, R.string.fuel_type_16,
            R.string.fuel_type_17, R.string.fuel_type_18, R.string.fuel_type_19, R.string.fuel_type_20
    };


}
