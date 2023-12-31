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
            } else {
                MyUtils.companyInfo = new ArrayList<>();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getCompanyName(int cid) {
        String cname = "";
        try {
            String where = "cid=" + cid;
            Cursor cursor = MyUtils.db_connect.sqlSelect(table_name, "name", where);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                cname = cursor.getString(0); //소속 회사 이름
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return cname;
    }

    public static long insertCompanyInfoTable(String[][] fields) {
        try {
            return MyUtils.db_connect.sqlInsert(table_name, fields);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static void deleteAllCompanyInfoTable() {
        try {
            MyUtils.db_connect.sqlDeleteAll(table_name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
