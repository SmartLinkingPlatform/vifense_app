package com.obd2.dgt.dbManage.TableInfo;

import android.database.Cursor;

import com.obd2.dgt.utils.GaugeViewInfo;
import com.obd2.dgt.utils.MyUtils;

import java.util.ArrayList;

public class MessageInfoTable {
    static String table_name = "tb_message";

    public static void getMessageInfoTable() {
        try {
            MyUtils.msg_show = false;
            String where = "active=1 ORDER BY id DESC";
            Cursor cursor = MyUtils.db_connect.sqlSelect(table_name, "*", where);
            if (cursor != null && cursor.getCount() > 0) {
                MyUtils.messageInfo = new ArrayList<>();
                int record_cnt = cursor.getCount();
                for (int i = 0; i < record_cnt; i++) {
                    if (i == 0)
                        cursor.moveToFirst();
                    else
                        cursor.moveToNext();
                    String[] info = new String[7];
                    info[0] = cursor.getString(0); //msg ID
                    info[1] = cursor.getString(1); //msg date
                    info[2] = cursor.getString(2); //msg user
                    info[3] = cursor.getString(3); //msg title
                    info[4] = cursor.getString(4); //msg content
                    info[5] = cursor.getString(5); //msg active
                    info[6] = cursor.getString(6); //msg show
                    MyUtils.messageInfo.add(info);
                    if (cursor.getInt(6) == 0) {
                        MyUtils.msg_show = true;
                    }
                    if (i == 0) {
                        MyUtils.lastMsgID = cursor.getInt(0);
                    }
                }
            } else {
                MyUtils.messageInfo = new ArrayList<>();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static long insertMessageTable(String[][] fields) {
        try {
            return MyUtils.db_connect.sqlInsert(table_name, fields);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static void updateMessageTable(String field, String active, String idx) {
        try {
            String[][] fields = new String[][]{
                    {field, active}
            };
            String where = "id = " + idx;
            MyUtils.db_connect.sqlUpdate(table_name, fields, where);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void updateAllMessageTable() {
        try {
            String[][] fields = new String[][]{
                    {"active", "0"}
            };
            MyUtils.db_connect.sqlUpdate(table_name, fields, "");
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
