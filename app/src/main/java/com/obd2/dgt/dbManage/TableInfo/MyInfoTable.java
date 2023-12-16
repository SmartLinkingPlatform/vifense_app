package com.obd2.dgt.dbManage.TableInfo;

import android.app.Activity;
import android.database.Cursor;
import android.widget.Toast;

import com.obd2.dgt.ui.InfoActivity.MyInfoActivity;
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
                    //MyUtils.my_company = CompanyTable.getCompanyName(MyUtils.admin_id); //소속 회사 이름
                    for (String[] companyInfo : MyUtils.companyInfo) {
                        if (Integer.parseInt(companyInfo[1]) == MyUtils.admin_id) {
                            MyUtils.my_company = companyInfo[2];
                            break;
                        }
                    }
                }
            }
            else{
                Toast.makeText(MyUtils.mContext, "-" + table_name + "-  생성되지 않았습니다.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(MyUtils.mContext, " 오류 발생 :" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public static int getExistMyInfoTable(String val) {
        try {
            String where = "phone='" + val + "'";
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

    public static long insertMyInfoTable(String[][] fields) {
        try {
            if (getMyInfoTableCount() == 0) {
                long rt = MyUtils.db_connect.sqlInsert(table_name, fields);
                if(rt < 0)
                    Toast.makeText(MyUtils.mContext, "" + table_name + " 에 insert 시에 오류 발생", Toast.LENGTH_SHORT).show();
                else
                    return rt;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(MyUtils.mContext, "" + table_name + " 에 insert 시에 오류 발생 : " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return -1;
    }

    public static void updateMyInfoTable(String[][] fields) {
        try {
            MyUtils.db_connect.sqlUpdate(table_name, fields, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
