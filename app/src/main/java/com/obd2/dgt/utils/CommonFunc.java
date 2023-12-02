package com.obd2.dgt.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.obd2.dgt.R;
import com.obd2.dgt.network.NetworkStatus;
import com.obd2.dgt.ui.LoginActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
}
