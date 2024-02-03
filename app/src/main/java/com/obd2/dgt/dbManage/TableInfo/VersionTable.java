package com.obd2.dgt.dbManage.TableInfo;

import android.database.Cursor;

import com.obd2.dgt.utils.MyUtils;

public class VersionTable {
    static String table_name = "tb_version";

    //sqlite db에 등록된 Database version 얻기
    public static void getVersionTable() {
        try {
            Cursor cursor = MyUtils.db_connect.sqlSelect(table_name);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                MyUtils.DB_VERSION = cursor.getInt(0);
            }
        } catch (Exception e) {
            MyUtils.DB_VERSION = 0;
            e.printStackTrace();
        }
    }

}
