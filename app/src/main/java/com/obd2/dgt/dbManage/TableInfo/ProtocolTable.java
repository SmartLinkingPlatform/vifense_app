package com.obd2.dgt.dbManage.TableInfo;

import android.database.Cursor;

import com.obd2.dgt.utils.MyUtils;

public class ProtocolTable {
    static String table_name = "tb_protocol";

    public static void getProtocolTable() {
        try {
            Cursor cursor = MyUtils.db_connect.sqlSelect(table_name, "*", "");
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                MyUtils.SEL_PROTOCOL = cursor.getString(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateMyInfoTable(String[][] fields) {
        try {
            MyUtils.db_connect.sqlUpdate(table_name, fields, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
