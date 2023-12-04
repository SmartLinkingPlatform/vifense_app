package com.obd2.dgt.dbManage.TableInfo;

import android.database.Cursor;

import com.obd2.dgt.utils.GaugeViewInfo;
import com.obd2.dgt.utils.MyUtils;

import java.util.ArrayList;

public class GaugeInfoTable {
    static String table_name = "tb_gaugeOrder";
    public static void getGaugeOrderTable() {
        try {
            String[] order_fields = new String[]{
                    "status", "orderVal"
            };
            Cursor cursor = MyUtils.db_connect.sqlSelect(table_name, order_fields);
            if (cursor != null && cursor.getCount() > 0) {
                MyUtils.gaugeInfo = new ArrayList<>();
                int record_cnt = cursor.getCount();
                for (int i = 0; i < record_cnt; i++) {
                    if (i == 0)
                        cursor.moveToFirst();
                    else
                        cursor.moveToNext();
                    String[] info = new String[5];
                    info[0] = cursor.getString(0); //계기번호
                    info[1] = cursor.getString(1); //계기측정값
                    info[2] = cursor.getString(2); //계기보임상태
                    info[3] = cursor.getString(3); //계기순서
                    info[4] = cursor.getString(4); //계기이름
                    MyUtils.gaugeInfo.add(info);
                }
            } else {
                MyUtils.gaugeInfo = new ArrayList<>();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static int getGaugeEnableCount() {
        try {
            String field = "id";
            String where = "status = 1";
            Cursor cursor = MyUtils.db_connect.sqlSelect(table_name, field, where);
            if (cursor != null && cursor.getCount() > 0) {
                return cursor.getCount();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return 0;
    }
    public static int getGaugeDisableCount() {
        try {
            String field = "id";
            String where = "status = 2";
            Cursor cursor = MyUtils.db_connect.sqlSelect(table_name, field, where);
            if (cursor != null && cursor.getCount() > 0) {
                return cursor.getCount();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return 0;
    }
    public static void updateGaugeTable(GaugeViewInfo gaugeViewInfo) {
        try {
            String[][] fields = new String[][]{
                    {"showVal", gaugeViewInfo.gaugeVal},
                    {"status", String.valueOf(gaugeViewInfo.gaugeStatus)},
                    {"orderVal", String.valueOf(gaugeViewInfo.orderIndex)}
            };
            String where = "id = " + gaugeViewInfo.id;
            MyUtils.db_connect.sqlUpdate(table_name, fields, where);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void updateGaugeTable(int index, int state) {
        try {
            String[][] fields = new String[][]{
                    {"status", String.valueOf(state)}
            };
            String where = "id = " + index;
            MyUtils.db_connect.sqlUpdate(table_name, fields, where);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
