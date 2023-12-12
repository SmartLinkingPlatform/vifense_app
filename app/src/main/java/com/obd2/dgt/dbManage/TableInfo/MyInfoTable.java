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
                    MyUtils.my_phone = cursor.getString(1); //나의 폰 번호
                    MyUtils.my_pwd = cursor.getString(2); //비밀 번호
                    MyUtils.admin_id = cursor.getInt(3); //소속 회사 아이디
                    MyUtils.my_company = CompanyTable.getCompanyName(MyUtils.admin_id); //소속 회사 이름
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static int getExistMyInfoTable(String[] fields) {
        try {
            String where = "name='" + fields[0] + "' and phone='" + fields[1] + "'";
            Cursor cursor = MyUtils.db_connect.sqlSelect(table_name, "name", where);
            if (cursor != null && cursor.getCount() > 0) {
                return cursor.getCount();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return 0;
    }

    public static int getMyInfoTableCount() {
        try {
            Cursor cursor = MyUtils.db_connect.sqlSelect(table_name, "*", "");
            if (cursor != null && cursor.getCount() > 0) {
                return cursor.getCount();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return 0;
    }

    public static void insertMyInfoTable(String[][] fields) {
        try {
            if (getMyInfoTableCount() == 0) {
                MyUtils.db_connect.sqlInsert(table_name, fields);
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
