package com.obd2.dgt.dbManage.TableInfo;

import android.database.Cursor;

import com.obd2.dgt.utils.MyUtils;

import java.util.ArrayList;

public class CompanyTable {
    private static String table_name = "tb_companyInfo";

    public static void getCompanyInfoTable() {
        try {
            Cursor cursor = MyUtils.db_connect.sqlSelect(table_name);
            if (cursor != null && cursor.getCount() > 0) {
                MyUtils.companyInfo = new ArrayList<>();
                int record_cnt = cursor.getCount();
                for (int i = 0; i < record_cnt; i++) {
                    if (i == 0)
                        cursor.moveToFirst();
                    else
                        cursor.moveToNext();
                    String[] info = new String[5];
                    info[0] = cursor.getString(0); //msg ID
                    info[1] = cursor.getString(1); //msg date
                    info[2] = cursor.getString(2); //msg user
                    info[3] = cursor.getString(3); //msg title
                    info[4] = cursor.getString(4); //msg content
                    MyUtils.companyInfo.add(info);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteMessageInfoTable(String id) {
        try {
            String where = "id = " + id;
            MyUtils.db_connect.sqlDelete(table_name, where);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteAllMessageInfoTable() {
        try {
            MyUtils.db_connect.sqlDeleteAll(table_name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
