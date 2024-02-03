package com.obd2.dgt.dbManage.TableInfo;

import android.database.Cursor;

import com.obd2.dgt.utils.CommonFunc;
import com.obd2.dgt.utils.MyUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class DrivingTable {
    private static String table_name = "tb_drivingInfo";

    public static int getDrivingInfoCount() {
        int record_cnt = 0;
        try {
            Cursor cursor = MyUtils.db_connect.sqlSelect(table_name, "id", false);
            record_cnt = cursor.getCount();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return record_cnt;
    }
    public static LinkedHashMap<String, ArrayList<JSONObject>> getDrivingInfoTable() {
        LinkedHashMap<String, ArrayList<JSONObject>> driving_info = new LinkedHashMap<>();
        try {
            Cursor cursor = MyUtils.db_connect.sqlSelect(table_name, "id", false);
            if (cursor != null && cursor.getCount() > 0) {
                String temp_date = "";
                ArrayList<JSONObject> infos = new ArrayList<>();
                int record_cnt = cursor.getCount();
                for (int i = 0; i < record_cnt; i++) {
                    if (i == 0)
                        cursor.moveToFirst();
                    else
                        cursor.moveToNext();

                    JSONObject res = new JSONObject();
                    res.put("id", cursor.getString(0));
                    res.put("driving_date", cursor.getString(1));
                    res.put("start_time", cursor.getString(2));
                    res.put("start_place", cursor.getString(3));
                    res.put("end_time", cursor.getString(4));
                    res.put("end_place", cursor.getString(5));
                    res.put("car_id", cursor.getString(6));
                    res.put("user_id", cursor.getString(7));
                    res.put("max_speed", cursor.getString(8));
                    res.put("average_speed", cursor.getString(9));
                    res.put("mileage", cursor.getString(10));
                    res.put("driving_time", cursor.getString(11));
                    res.put("idling_time", cursor.getString(12));
                    res.put("driving_score", cursor.getString(13));
                    res.put("fast_time", cursor.getString(14));
                    res.put("fast_cnt", cursor.getString(15));
                    res.put("quick_cnt", cursor.getString(16));
                    res.put("brake_cnt", cursor.getString(17));
                    res.put("send_status", cursor.getString(18));

                    String driving_date = res.getString("driving_date");
                    if (infos.size() == 0) {
                        infos.add(res);
                    } else {
                        if (driving_date.equals(temp_date)) {
                            infos.add(res);
                        } else {
                            driving_info.put(temp_date, infos);
                            infos = new ArrayList<>();
                            infos.add(res);
                        }
                    }
                    driving_info.put(driving_date, infos);
                    temp_date = driving_date;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return driving_info;
    }

    public static void getNotSentDrivingInfoTable() {
        try {
            MyUtils.not_sent_driving_info = new ArrayList<>();

            String where = "send_status = 0";
            Cursor cursor = MyUtils.db_connect.sqlSelect(table_name, "*", where);
            if (cursor != null && cursor.getCount() > 0) {
                int record_cnt = cursor.getCount();
                for (int i = 0; i < record_cnt; i++) {
                    if (i == 0)
                        cursor.moveToFirst();
                    else
                        cursor.moveToNext();

                    JSONObject res = new JSONObject();
                    res.put("id", cursor.getString(0));
                    res.put("driving_date", cursor.getString(1));
                    res.put("start_time", cursor.getString(2));
                    res.put("start_place", cursor.getString(3));
                    res.put("end_time", cursor.getString(4));
                    res.put("end_place", cursor.getString(5));
                    res.put("car_id", cursor.getString(6));
                    res.put("user_id", cursor.getString(7));
                    res.put("max_speed", cursor.getString(8));
                    res.put("average_speed", cursor.getString(9));
                    res.put("mileage", cursor.getString(10));
                    res.put("driving_time", cursor.getString(11));
                    res.put("idling_time", cursor.getString(12));
                    res.put("driving_score", cursor.getString(13));
                    res.put("fast_time", cursor.getString(14));
                    res.put("fast_cnt", cursor.getString(15));
                    res.put("quick_cnt", cursor.getString(16));
                    res.put("brake_cnt", cursor.getString(17));
                    res.put("send_status", cursor.getString(18));

                    MyUtils.not_sent_driving_info.add(res);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateDrivingInfoTable() {
        try {
            String[][] fields = new String[][]{
                    {"send_status", "1"},
            };
            String where = "send_status = 0";
            MyUtils.db_connect.sqlUpdate(table_name, fields, where);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static long insertDrivingInfoTable(String[][] fields) {
        try {
            return MyUtils.db_connect.sqlInsert(table_name, fields);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static void deletePrevDrivingInfo(String time) {
        try {
            String where = "driving_date < " + time;
            MyUtils.db_connect.sqlDelete(table_name, where);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
