package com.obd2.dgt.dbManage.TableInfo;

import android.database.Cursor;

import com.obd2.dgt.utils.GaugeViewInfo;
import com.obd2.dgt.utils.MyUtils;

import java.util.ArrayList;

public class CarInfoTable {
    static String table_name = "tb_carInfo";
    public static void getCarInfoTable() {
        try {
            Cursor cursor = MyUtils.db_connect.sqlSelect(table_name);
            if (cursor != null && cursor.getCount() > 0) {
                MyUtils.carInfo = new ArrayList<>();
                int record_cnt = cursor.getCount();
                for (int i = 0; i < record_cnt; i++) {
                    if (i == 0)
                        cursor.moveToFirst();
                    else
                        cursor.moveToNext();

                    MyUtils.car_id = cursor.getInt(1); //차량 유일 번호

                    String[] info = new String[8];
                    info[0] = cursor.getString(0); //id
                    info[1] = cursor.getString(1); //차량 유일 번호
                    info[2] = cursor.getString(2); //제조사
                    info[3] = cursor.getString(3); //모델
                    info[4] = cursor.getString(4); //연식
                    info[5] = cursor.getString(5); //차량번호
                    info[6] = cursor.getString(6); //연료종류
                    info[7] = cursor.getString(7); //배기량
                    MyUtils.carInfo.add(info);
                }
            } else {
                MyUtils.carInfo = new ArrayList<>();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateCarInfoTable(int car_id, String[][] fields) {
        try {
            String where = "car_id = " + car_id;
            MyUtils.db_connect.sqlUpdate(table_name, fields, where);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void insertCarInfoTable(String[][] fields) {
        try {
            MyUtils.db_connect.sqlInsert(table_name, fields);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteCarInfoTable(int id) {
        try {
            String where = "id = " + id;
            MyUtils.db_connect.sqlDelete(table_name, where);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
