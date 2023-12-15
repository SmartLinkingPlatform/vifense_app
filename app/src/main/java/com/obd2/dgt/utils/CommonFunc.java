package com.obd2.dgt.utils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;

import com.obd2.dgt.R;
import com.obd2.dgt.network.NetworkStatus;
import com.obd2.dgt.ui.LoginActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Arrays;
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
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, btnText,
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
}
