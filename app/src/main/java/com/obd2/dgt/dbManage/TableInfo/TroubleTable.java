package com.obd2.dgt.dbManage.TableInfo;

import android.database.Cursor;

import com.obd2.dgt.utils.MyUtils;

import java.util.ArrayList;

public class TroubleTable {
    static String table_name = "tb_trouble";
    public static int max_cid = 0;

    public static void getTroubleCodeTable() {
        try {
            Cursor cursor = MyUtils.db_connect.sqlSelect(table_name, "id", false);
            if (cursor != null && cursor.getCount() > 0) {
                MyUtils.troubleCodes = new ArrayList<>();
                int record_cnt = cursor.getCount();
                for (int i = 0; i < record_cnt; i++) {
                    if (i == 0) {
                        cursor.moveToFirst();
                        max_cid = cursor.getInt(0) + 1;
                    }
                    else
                        cursor.moveToNext();
                    String[] info = new String[7];
                    info[0] = cursor.getString(0); //id
                    info[1] = cursor.getString(1); //code
                    info[2] = cursor.getString(2); //description
                    MyUtils.troubleCodes.add(info);
                }
            } else {
                MyUtils.troubleCodes = new ArrayList<>();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void insertTroubleTable(String[][] fields) {
        try {
            MyUtils.db_connect.sqlInsert(table_name, fields);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteTroubleTable(int id) {
        try {
            String where = "id = " + id;
            MyUtils.db_connect.sqlDelete(table_name, where);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
