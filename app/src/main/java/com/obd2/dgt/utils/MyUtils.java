package com.obd2.dgt.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PointF;
import android.os.Environment;
import android.util.DisplayMetrics;

import com.obd2.dgt.btManage.OBDConnect;
import com.obd2.dgt.dbManage.DBConnect;
import com.obd2.dgt.R;
import com.obd2.dgt.ui.AppBaseActivity;

import java.util.ArrayList;
import java.util.UUID;

public class MyUtils {
    //public static String server_url = "http://192.168.1.4";
    public static String server_url = "https://dgt.vifense.com";
    public static String signup_url = "https://dgt.vifense.com/mok/auth_signup.html";
    public static String find_url = "https://dgt.vifense.com/mok/auth_findpwd.html";
    public static String call_company = "/mobile.companyInfo";
    public static String terms_url = "/mobile.termsUrl";
    public static String user_signup = "/mobile.register";
    public static String auth_token = "/mobile.login";
    public static String user_login = "/mobile.get_user";
    public static String user_modify = "/mobile.userInfoModify";
    public static String reg_car = "/mobile.regCarInfo";
    public static String list_car = "/mobile.listCarInfo";
    public static String mod_car = "/mobile.modCarInfo";
    public static String del_car = "/mobile.delCarInfo";
    public static String read_driving = "/mobile.readDriving";
    public static String save_driving = "/mobile.saveDriving";
    public static String ranking = "/mobile.ranking";
    public static String driving_ranking = "/mobile.drivingRanking";
    public static String mgs_list = "/mobile.messageList";
    public static String new_pwd = "/mobile.newpassword";

    public static String terms_file_path = "";
    public static String ACCESS_TOKEN = "";
    public static String sendRequestData = "";
    public static AppBaseActivity appBase = null;
    public static String StorageFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
    public static Class<?> currentActivity = null;
    public static Context mContext = null;

    public static DisplayMetrics metrics;
    public static float mDpX = 1;
    public static float mDpY = 1;
    public static DBConnect db_connect;
    public static UUID uuid = java.util.UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    public static BluetoothAdapter mBluetoothAdapter = null;
    public static BluetoothSocket btSocket = null;
    public static OBDConnect obdConnect = null;
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

    public static boolean isEnumInfo = false;
    public static String[][] enum_base = {
            {"ENGINE_RPM", "0C", "4"},           //엔진 RPM
            {"VEHICLE_SPEED", "0D", "2"}         //차량 속도
    };
    public static String[][] enum_info = {
            {"ENGINE_LOAD", "04", "2"},          //엔진 부하
            {"COOLANT_TEMPERATURE", "05", "2"},  //냉각수 온도
            {"MAF_AIR_FLOW", "10", "4"},         //흡입 공기량
            {"THROTTLE_POSITION", "11", "2"},    //스로틀 위치
            {"BATTERY_VOLTAGE", "42", "4"},      //배터리 전압
            {"FUEL_RATE_LPH", "5E", "4"}         //순간 연료 소모량 l/h
    };
    public static String ecu_monitor_status = "off";
    public static String ecu_engine_load = "0";
    public static String ecu_coolant_temp = "0";
    public static String ecu_engine_rpm = "0";
    public static String ecu_vehicle_speed = "0";
    public static String ecu_timing_advance = "0";
    public static String ecu_fuel_tank_level = "0";
    public static String ecu_fuel_rate = "0";
    public static String ecu_fuel_gal = "0";
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
    public static String ecu_ignition_monitor = "off";
    public static boolean is_trouble = false;
    public static boolean is_consume = false;
    public static boolean is_error_dlg = false;
    public static int err_idx = 0;
    public static boolean new_login = false;
    public static boolean is_driving = false;

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
    public static boolean con_OBD = false;
    public static boolean con_ECU = false;
    public static boolean run_main = false;
    public static boolean loaded_data = false;
    public static boolean show_dash_dialog = false;
    public static boolean savedSocketStatus = false;

    public static int con_method = 0;

    public static int sel_car_id = 0;
    public static boolean showGauge = false;
    public static String mileage_score = "0";
    public static String safety_score = "0";

    public static int[] manufacturer_names = {
            R.string.car_name_1,
            R.string.car_name_2,
            R.string.car_name_3,
            R.string.car_name_4,
            R.string.car_name_5
    };
    public static int[][] model_names = {
            {R.string.model_name_0, 0},
            {R.string.model_name_1, 0},
            {R.string.model_name_2, 0},
            {R.string.model_name_3, 0},
            {R.string.model_name_4, 0},
            {R.string.model_name_5, 1},
            {R.string.model_name_6, 1},
            {R.string.model_name_7, 1},
            {R.string.model_name_8, 1},
            {R.string.model_name_9, 1},
            {R.string.model_name_10, 2},
            {R.string.model_name_11, 2},
            {R.string.model_name_12, 2},
            {R.string.model_name_13, 2},
            {R.string.model_name_14, 0},
            {R.string.model_name_15, 0},
            {R.string.model_name_16, 0},
            {R.string.model_name_17, 0},
            {R.string.model_name_18, 0},
            {R.string.model_name_19, 3},
            {R.string.model_name_20, 0},
            {R.string.model_name_21, 2},
            {R.string.model_name_22, 2},
            {R.string.model_name_23, 2},
            {R.string.model_name_24, 0},
            {R.string.model_name_25, 2},
            {R.string.model_name_26, 2},
            {R.string.model_name_27, 2},
            {R.string.model_name_28, 3},
            {R.string.model_name_29, 0},
            {R.string.model_name_30, 2},
            {R.string.model_name_31, 4}
    };

    public static ArrayList<String> create_years = new ArrayList<>();

    public static int[] fuel_types = {
            R.string.fuel_type_1, R.string.fuel_type_2, R.string.fuel_type_3, R.string.fuel_type_4,
            R.string.fuel_type_5, R.string.fuel_type_6, R.string.fuel_type_7, R.string.fuel_type_8,
            R.string.fuel_type_9, R.string.fuel_type_10, R.string.fuel_type_11, R.string.fuel_type_12,
            R.string.fuel_type_13, R.string.fuel_type_14, R.string.fuel_type_15, R.string.fuel_type_16,
            R.string.fuel_type_17, R.string.fuel_type_18, R.string.fuel_type_19, R.string.fuel_type_20
    };

    public static String SEL_PROTOCOL = "AT SP0";
    public static String[][] PROTOCOL_CUSTOM = {
            {"AT SP0", "AUTO"},
            {"AT SP3", "ISO 9141-2(5 baud)"},
            {"AT SP4", "ISO 14230-4 KWP(5 baud)"},
            {"AT SP5", "ISO 14230-4 KWP(fast)"},
            {"AT SP6", "ISO 15765-4 CAN(11 bit)"},
            {"AT SP7", "ISO 15765-4 CAN(29 bit)"}
    };

    public static SharedPreferences sharedPreferences;
    public static boolean isDiagnosis = false;

}
