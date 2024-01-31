package com.obd2.dgt.utils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.obd2.dgt.R;
import com.obd2.dgt.network.NetworkStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

public class CommonFunc {
    public static String getCurrentDate() {
        Date dateNow = Calendar.getInstance().getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());

        return format.format(dateNow);
    }
    public static String getPrevMonthDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH ,-1);
        java.util.Date prevDate = cal.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());

        return formatter.format(prevDate);
    }
    public static int getCurrentWeek() {
        Date currentDate = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);

        int week_resid = R.string.week_1;
        int dayOfWeekNumber = calendar.get(Calendar.DAY_OF_WEEK);
        switch (dayOfWeekNumber) {
            case 1:
                week_resid = R.string.week_1;
                break;
            case 2:
                week_resid = R.string.week_2;
                break;
            case 3:
                week_resid = R.string.week_3;
                break;
            case 4:
                week_resid = R.string.week_4;
                break;
            case 5:
                week_resid = R.string.week_5;
                break;
            case 6:
                week_resid = R.string.week_6;
                break;
            case 7:
                week_resid = R.string.week_7;
                break;
        }

        return week_resid;
    }

    public static boolean checkNetworkStatus(Context context, String msg, String btnText) {
        if (!NetworkStatus.getNetworkConnect()) {
            AlertDialog alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setMessage(msg);
            alertDialog.setCancelable(false);
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, btnText,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
            return false;
        } else {
            return true;
        }
    }

    @SuppressLint("MissingPermission")
    public static BluetoothDevice getPairedDevice() {
        BluetoothDevice pairedDevice = null;
        Set<BluetoothDevice> pairedDevices = MyUtils.mBluetoothAdapter.getBondedDevices();
        for (BluetoothDevice paired : pairedDevices) {
            if (paired.getName().equals(MyUtils.obd2_name)) {
                pairedDevice = paired;
                break;
            }
        }
        return pairedDevice;
    }

    @SuppressLint("MissingPermission")
    public static void setUnPairedDevice() {
        BluetoothDevice pairedDevice = null;
        Set<BluetoothDevice> pairedDevices = MyUtils.mBluetoothAdapter.getBondedDevices();
        for (BluetoothDevice paired : pairedDevices) {
            if (!paired.getAddress().equals(MyUtils.obd2_address)) {
                try {
                    Method method = paired.getClass().getMethod("removeBond");
                    method.invoke(paired);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static JSONArray AnalysisResponse(String response) {
        JSONArray jobs = new JSONArray();
        try {
            JSONObject res = new JSONObject(response);
            String msg = res.getString("msg");
            if (msg.equals("ok")) {
                String lists = res.getString("lists");
                jobs = new JSONArray(lists);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jobs;
    }

    public static int calculateTime(String time) {
        String[] d_time = time.split(":");
        int sec_time = 0;
        if (d_time.length > 2) {
            sec_time = Integer.parseInt(d_time[0]) * 3600 + Integer.parseInt(d_time[1]) * 60 + Integer.parseInt(d_time[2]);
        } else {
            sec_time = Integer.parseInt(d_time[0]) * 60 + Integer.parseInt(d_time[1]);
        }
        return sec_time;
    }

    public static String getDateTime() {
        long currentMillis = new Date().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return formatter.format(currentMillis);
    }

    public static String getDateTimeMilliseconds() {
        long currentMillis = new Date().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
        return formatter.format(currentMillis);
    }

    public static String getTime() {
        long currentMillis = new Date().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return formatter.format(currentMillis);
    }

    public static String getDate() {
        long currentMillis = new Date().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return formatter.format(currentMillis);
    }

    public static String getHour(int time, String unit) {
        String str_h = "";
        int hour = Math.round(time / 3600);
        if (unit.isEmpty()) {
            if (hour > 0) {
                if (hour > 9) {
                    str_h = hour + ":";
                } else {
                    str_h = "0" + hour + ":";
                }
            }
        } else {
            if (hour > 9) {
                str_h = hour + unit;
            } else {
                str_h = "0" + hour + unit + " ";
            }
        }
        return str_h;
    }

    public static String getMinuteAndSecond(int time, String m_unit, String s_unit) {
        String str_m = "00";
        int min = Math.round(time / 60);
        if (min > 9) {
            str_m = String.valueOf(min);
        } else {
            str_m = "0" + min;
        }
        String str_s = "00";
        int sec = Math.round(time % 60);
        if (sec > 9) {
            str_s = String.valueOf(sec);
        } else {
            str_s = "0" + sec;
        }
        String min_sec = str_m + ":" + str_s;
        if (!m_unit.isEmpty()) {
            min_sec = str_m + m_unit + " " + str_s + s_unit;
        }
        return min_sec;
    }

    public static int getArrayToIndex(int[] arr, String val) {
        int idx = 0;
        for (int res : arr) {
            String txt = MyUtils.mContext.getString(res);
            if (txt.equalsIgnoreCase(val)) {
                return idx;
            }
            idx++;
        }
        return 0;
    }
    public static int getYearsToIndex(String val) {
        int idx = 0;
        for (String y : MyUtils.create_years) {
            if (y.equalsIgnoreCase(val)) {
                return idx;
            }
            idx++;
        }
        return 0;
    }

    public static void showToastOnUIThread(final String message) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            Toast.makeText(MyUtils.mContext, message, Toast.LENGTH_SHORT).show();
            handler.removeMessages(0);
        }, 1000);
    }

    static int m_idx = 0;
    public static String findManufacturer(String val) {
        String index = "0";
        for (int i = 0; i < MyUtils.manufacturer_names.length; i++) {
            if (val.equals(MyUtils.mContext.getString(MyUtils.manufacturer_names[i]))) {
                index = String.valueOf(i);
            }
        }
        m_idx = Integer.parseInt(index);
        return index;
    }

    public static String findModel(String val) {
        int index = 0;
        for (int i = 0; i < MyUtils.model_names.length; i++) {
            if (m_idx == MyUtils.model_names[i][1]) {
                if (val.equals(MyUtils.mContext.getString(MyUtils.model_names[i][0]))) {
                    break;
                }
                index++;
            }
        }
        return String.valueOf(index);
    }
    public static String findCreateYear(String val) {
        String index = "0";
        for (int i = 0; i < MyUtils.create_years.size(); i++) {
            if (val.equals(MyUtils.create_years.get(i))) {
                index = String.valueOf(i);
            }
        }
        return index;
    }
    public static String findFuelType(String val) {
        String index = "0";
        for (int i = 0; i < MyUtils.fuel_types.length; i++) {
            if (val.equals(MyUtils.mContext.getString(MyUtils.fuel_types[i]))) {
                index = String.valueOf(i);
            }
        }
        return index;
    }

    //로그파일 쓰기
    public static void writeFile(String path, String filename, String content) {
        FileWriter writer;
        try {
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir + "/" + filename);
            if (!file.exists()) {
                file.createNewFile();
            }
            writer = new FileWriter(file, true);
            writer.write(content);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendParamData(String[][] values) {
        JSONObject SendDataPackage = new JSONObject();
        try {
            for (int i = 0; i < values.length; i++) {
                SendDataPackage.put(values[i][0], values[i][1]);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MyUtils.sendRequestData = SendDataPackage.toString();
    }

    public static void setInformationToSystem(String keyName, String keyInfo){
        SharedPreferences.Editor editor = MyUtils.sharedPreferences.edit();
        editor.putString(keyName, keyInfo);
        editor.apply();
    }

    public static int getInformationToSystem(String keyName){
        int run = 0;
        String str_run = MyUtils.sharedPreferences.getString(keyName, "");

        if (!str_run.equals(""))
            run = Integer.parseInt(str_run);

        return run;
    }

    public static String checkInputOnlyNumberAndAlphabet(String textInput) {
        StringBuilder val = new StringBuilder();
        for (int i = 0; i < textInput.length(); i++) {
            char chrInput = textInput.charAt(i);
            if (chrInput >= 0x61 && chrInput <= 0x7A) {
                val.append(String.valueOf(chrInput));
            } else if (chrInput >= 0x41 && chrInput <= 0x5A) {
                val.append(String.valueOf(chrInput));
            } else if (chrInput >= 0x30 && chrInput <= 0x39) {
                val.append(String.valueOf(chrInput));
            }
        }
        return val.toString();
    }

    public static String getResponseValue(String res) {
        String sub_res = "";
        String pid = res.substring(2, 4);
        for (int i = 0; i < MyUtils.pid_speed.length; i++) {
            if (MyUtils.pid_speed[i][1].equalsIgnoreCase(pid)) {
                int digit = Integer.parseInt(MyUtils.pid_speed[i][2]) + 4;
                sub_res = res.substring(0, digit);
                break;
            }
        }
        if (MyUtils.isEnumSec) {
            for (int i = 0; i < MyUtils.pid_second.length; i++) {
                if (MyUtils.pid_second[i][1].equalsIgnoreCase(pid)) {
                    int digit = Integer.parseInt(MyUtils.pid_second[i][2]) + 4;
                    sub_res = res.substring(0, digit);
                    break;
                }
            }
        }
        if (MyUtils.isEnumInfo) {
            for (int i = 0; i < MyUtils.pid_info.length; i++) {
                if (MyUtils.pid_info[i][1].equalsIgnoreCase(pid)) {
                    int digit = Integer.parseInt(MyUtils.pid_info[i][2]) + 4;
                    sub_res = res.substring(0, digit);
                    break;
                }
            }
        }
        return sub_res;
    }

}
