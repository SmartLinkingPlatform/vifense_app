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
                    String[] info = new String[3];
                    info[0] = cursor.getString(0); //ID
                    info[1] = cursor.getString(1); //company id
                    info[2] = cursor.getString(2); //company name
                    MyUtils.companyInfo.add(info);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void insertCompanyInfoTable(String[][] fields) {
        try {
            MyUtils.db_connect.sqlInsert(table_name, fields);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteAllCompanyInfoTable() {
        try {
            MyUtils.db_connect.sqlDeleteAll(table_name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
