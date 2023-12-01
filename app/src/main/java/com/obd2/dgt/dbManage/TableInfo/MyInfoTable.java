package com.obd2.dgt.dbManage.TableInfo;

import android.database.Cursor;

import com.obd2.dgt.utils.MyUtils;

public class MyInfoTable {
    static String table_name = "tb_myInfo";

    public static void getMyInfoTable() {
        try {
            Cursor cursor = MyUtils.db_connect.sqlSelect(table_name, "*", "");
            if (cursor != null && cursor.getCount() > 0) {
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    MyUtils.my_name = cursor.getString(0); //나의 이름
                    MyUtils.my_id = cursor.getString(1); //나의 폰 번호
                    MyUtils.my_pwd = cursor.getString(2); //비밀 번호
                    MyUtils.my_company = cursor.getString(3); //소속 회사
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void updateMyInfoTable(String d_name, String d_mac, String paired, String socketed) {
        try {
            String[][] fields = new String[][]{
                    {"name", d_name},
                    {"mac_address", d_mac},
                    {"paired_status", paired},
                    {"socket_status", socketed},
                    {"connected_method", MyUtils.con_method}
            };
            MyUtils.db_connect.sqlUpdate(table_name, fields, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
