package com.obd2.dgt.dbManage.TableInfo;

import android.database.Cursor;

import com.obd2.dgt.utils.MyUtils;


public class DeviceInfoTable {
    static String table_name = "tb_deviceInfo";

    public static void getDeviceInfoTable() {
        try {
            Cursor cursor = MyUtils.db_connect.sqlSelect(table_name, "*", "");
            if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    MyUtils.obd2_name = cursor.getString(0); //OBD2 장치 이름
                    MyUtils.obd2_address = cursor.getString(1); //OBD2 장치 MAC 주소
                    //OBD2 장치 페어링 상태
                    if (cursor.getString(2).equals("1")) {
                        MyUtils.isPaired = true;
                    }
                    //OBD2 장치 소켓 연결 상태
                    if (cursor.getString(3).equals("1")) {
                        MyUtils.savedSocketStatus = true;
                    }
                    MyUtils.con_method = cursor.getString(4); //Bluetooth 연결 방식
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void updateDeviceInfoTable(String d_name, String d_mac, String paired, String socketed) {
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
