package com.obd2.dgt.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.obd2.dgt.R;
import com.obd2.dgt.btManage.OBDConnect;
import com.obd2.dgt.dbManage.TableInfo.DrivingTable;
import com.obd2.dgt.network.NetworkStatus;
import com.obd2.dgt.network.WebHttpConnect;
import com.obd2.dgt.ui.InfoActivity.LinkInfoActivity;
import com.obd2.dgt.ui.MainActivity;
import com.obd2.dgt.ui.MainListActivity.DashboardActivity;
import com.obd2.dgt.utils.CommonFunc;
import com.obd2.dgt.utils.MyUtils;

import org.json.JSONObject;

import java.util.ArrayList;


public class RealService extends Service {
    public static Thread mainThread;
    public static Intent serviceIntent = null;

    static int idling_time = 0; //공회전 시간
    boolean speed_fast = false;
    boolean speed_quick = false;
    boolean speed_brake = false;
    float prev_speed = 0;
    public static boolean running = true;
    float fuel_consumption = 0;
    int err_cnt = 0;

    private static RealService instance = null;

    public static RealService getInstance() {
        return instance;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        instance = this;
        if(intent == null){
            return START_STICKY;
        }
        serviceIntent = intent;
        createNotification();
        onMainThread();
        return super.onStartCommand(intent, flags, startId);
    }

    int threadCnt = 0;
    int secCnt = 0;
    int pidCnt = 0;
    private void onMainThread() {
        mainThread = new Thread(() -> {
            while (running) {
                try {
                    if (!mainThread.isInterrupted()) {
                        if (threadCnt == 10) { //1초 간격
                            if (MyUtils.obdConnect == null) {
                                MyUtils.obdConnect = new OBDConnect();
                            } else {
                                if (mainThread != null && MyUtils.con_ECU) {
                                    err_cnt = 0;
                                    getDrivingStatus();
                                    getFuelConsumption();
                                    showWarningDialog();
                                }

                                if (!MyUtils.con_ECU || !MyUtils.loaded_data) {
                                    if (time > 0 && Float.parseFloat(MyUtils.ecu_mileage) > 0) {
                                        stopEngineStatus();
                                        time = 0;
                                    }
                                }
                            }
                            threadCnt = 0;
                            sendNotSentDrivingInfo();
                        }
                        Thread.sleep(100); //100ms 주기
                        if (MyUtils.con_OBD) {
                            if (secCnt == 2) { //200ms 간격
                                MyUtils.isEnumSec = true;
                                secCnt = 0;
                            }
                            if (pidCnt == 20) { //2초 간격
                                MyUtils.isEnumInfo = true;
                                pidCnt = 0;
                            }
                            secCnt++;
                            pidCnt++;
                        }
                        threadCnt++;
                    } else {
                        boolean interrupted = Thread.interrupted();
                        if (interrupted) {
                            running = false;
                        }
                        String content = CommonFunc.getDateTime() + " --- Stop MainThread --- " + "\r\n";
                        CommonFunc.writeFile(MyUtils.StorageFilePath, "Vifense_Log.txt", content);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        mainThread.setPriority(Thread.NORM_PRIORITY);
        mainThread.start();
    }

    static int time = 0;
    static double driving_distance = 0;
    static double average_speed = 0;
    static String start_time = "";
    static String end_time = "";
    static int down_score_fast = 0;
    static int down_score_quick = 0;
    static int down_score_brake = 0;

    //차량의 움직임 상태
    private void getDrivingStatus() {
        speed_fast = false;
        speed_quick = false;
        speed_brake = false;
        int speed = Integer.parseInt(MyUtils.ecu_vehicle_speed);
        int load = Integer.parseInt(MyUtils.ecu_engine_load);
        int rpm = Integer.parseInt(MyUtils.ecu_engine_rpm);

        //속도가 0이상, 엔진부하가 0보다 크면 시동상태
        if (speed >= 0 && load > 0) { //차량이 엔진이 작동 된 상태
            if (MyUtils.showGauge) {
                DashboardActivity.getInstance().startDashboardGauge();
            }

            if (speed == 0) { //시동을 켠 상태에서 이동하지 않는 상태
                idling_time++;
                MyUtils.idling_time = idling_time;
            } else { //차량이 이동하는 중
                idling_time = 0;
                if (MyUtils.max_speed < speed) {
                    MyUtils.max_speed = speed;
                }
                if (start_time.isEmpty()) {
                    start_time = CommonFunc.getTime();
                }
            }

            if (speed > 110) { //속도가 110 km을 초과한 경우
                speed_fast = true;
                MyUtils.fast_speed_time++;
                if (prev_speed < 110) {
                    MyUtils.fast_speed_cnt++;
                }
                if (down_score_fast > -4)
                    down_score_fast -= 2;
            }
            if (speed - prev_speed > 15) { // 차량 속도가 1초내에 15km 이상 급가속 경우
                speed_quick = true;
                MyUtils.quick_speed_cnt++;
                if (MyUtils.quick_speed_cnt >= 30) {
                    int cnt = MyUtils.quick_speed_cnt / 30;
                    if (down_score_quick > -2)
                        down_score_quick -= cnt;
                }
            }
            if (prev_speed - speed > 15) { // 차량 속도가 1초내에 15km 이상 급제동 경우
                speed_brake = true;
                MyUtils.brake_speed_cnt++;
                if (MyUtils.brake_speed_cnt >= 30) {
                    int cnt = MyUtils.brake_speed_cnt / 30;
                    if (down_score_brake > -2)
                        down_score_brake -= cnt;
                }
            }
            time++;
            String hour = CommonFunc.getHour(time, "");
            String min = CommonFunc.getMinuteAndSecond(time % 3600, "", "");
            MyUtils.ecu_driving_time = hour + min;

            double distance = speed / (double)3600;
            driving_distance += distance;
            average_speed = driving_distance / (time / (float)3600);
            MyUtils.ecu_mileage = String.valueOf(Math.round(driving_distance * 10) / 10.0);
        }
        prev_speed = speed;
    }
    private void getFuelConsumption() {
        if (Integer.parseInt(MyUtils.ecu_vehicle_speed) > 0) {
            //순간 연료소모량
            float rpm = Float.parseFloat(MyUtils.ecu_engine_rpm);
            float speed = Float.parseFloat(MyUtils.ecu_vehicle_speed);
            double fuel_rate = (rpm / 1000) * speed / 20;
            MyUtils.ecu_fuel_rate = String.valueOf(Math.round(fuel_rate * 10) / (float) 10);

            //연료 소모량
            fuel_consumption += Double.parseDouble(MyUtils.ecu_fuel_rate) / 3600;
            MyUtils.ecu_fuel_consume = String.valueOf(Math.round(fuel_consumption * 10) / (float) 10.0);
        }
    }

    @SuppressLint({"LaunchActivityFromNotification", "MissingPermission"})
    private void createNotification() {
        String CHANNEL_ID = "com.obd2.dgt";
        createNotificationChannel(CHANNEL_ID);

        Intent notificationIntent = new Intent(MyUtils.mContext, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(MyUtils.mContext, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(MyUtils.mContext, CHANNEL_ID)
                .setContentText(getString(R.string.app_running))
                .setSmallIcon(R.drawable.app_icon)
                .setContentIntent(pendingIntent)
                .setFullScreenIntent(pendingIntent, false)
                .setAutoCancel(true)
                .setPriority (Notification.PRIORITY_DEFAULT)
                .setOngoing(true) //알림 삭제 방지
                .build();

        startForeground(101, notification);
    }

    NotificationManager notificationManager;
    private void createNotificationChannel(String CHANNEL_ID) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            String description = getString(R.string.app_running);
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    boolean is_idling = false;
    int cnt_quick = 0;
    int cnt_brake = 0;

    private void showWarningDialog() {
        if (MyUtils.is_error_dlg) {
            return;
        }
        MyUtils.err_idx = 0;
        if (idling_time > 180 && !is_idling) {
            MyUtils.err_idx = 1;
            is_idling = true;
        }
        if (speed_fast) {
            MyUtils.err_idx = 2;
        }
        if (speed_quick) {
            //if (cnt_quick == 1) {
                MyUtils.err_idx = 3;
            //}
            //cnt_quick++;
        }
        if (speed_brake) {
            //if (cnt_brake == 1) {
                MyUtils.err_idx = 4;
            //}
            //cnt_brake++;
        }
        if (!MyUtils.ecu_trouble_code.isEmpty()) {
            MyUtils.is_trouble = true;
            MyUtils.err_idx = 5;
        }
        if (!MyUtils.ecu_consume_warning.isEmpty()) {
            MyUtils.is_consume = true;
            MyUtils.err_idx = 6;
        }

        if (MyUtils.err_idx > 1) {
            is_idling = false;
        }

        if (MyUtils.err_idx > 0) {
            cnt_brake = 0;
            cnt_quick = 0;
            MyUtils.appBase.gotoDashboard();
        }
    }

    @SuppressLint("MissingPermission")
    public void StopService() {
        running = false;
        if (mainThread != null) {
            mainThread.interrupt();
            mainThread = null;
        }
        Thread.currentThread().interrupt();
    }

    public String[][] setParam(int status) {
        int driving_score = 100 + down_score_fast + down_score_quick + down_score_brake;
        String driving_date = CommonFunc.getDate();
        end_time = CommonFunc.getTime();
        //서버에 등록
        String[][] params = new String[][]{
                {"driving_date", driving_date},
                {"start_time", start_time},
                {"start_place", ""},
                {"end_time", end_time},
                {"end_place", ""},
                {"car_id", String.valueOf(MyUtils.car_id)},
                {"user_id", String.valueOf(MyUtils.my_id)},
                {"max_speed", String.valueOf(MyUtils.max_speed)},
                {"average_speed", String.valueOf(Math.round(average_speed))},
                {"mileage", MyUtils.ecu_mileage},
                {"driving_time", MyUtils.ecu_driving_time},
                {"idling_time", String.valueOf(MyUtils.idling_time)},
                {"driving_score", String.valueOf(driving_score)},
                {"fast_time", String.valueOf(MyUtils.fast_speed_time)},
                {"fast_cnt", String.valueOf(MyUtils.fast_speed_cnt)},
                {"quick_cnt", String.valueOf(MyUtils.quick_speed_cnt)},
                {"brake_cnt", String.valueOf(MyUtils.brake_speed_cnt)},
                {"send_status", String.valueOf(status)}
        };
        return params;
    }

    private void stopEngineStatus() {
        //내부 디비에 보관 된 주행 정보 에서 한달 이전 자료 들은 삭제
        DrivingTable.deletePrevDrivingInfo(CommonFunc.getPrevMonthDate());
        if (NetworkStatus.getNetworkConnect()) {
            if (DrivingTable.insertDrivingInfoTable(setParam(1)) != -1) {
                CommonFunc.sendParamData(setParam(1));
                WebHttpConnect.onSaveDrivingInfoRequest();
            }
        } else {
            if (DrivingTable.insertDrivingInfoTable(setParam(0)) != -1) {
                DrivingTable.getNotSentDrivingInfoTable();
                MyUtils.max_speed = 0;
                MyUtils.fast_speed_cnt = 0;
                MyUtils.quick_speed_cnt = 0;
                MyUtils.brake_speed_cnt = 0;
                MyUtils.idling_time = 0;
                MyUtils.is_driving = false;
                MainActivity.getInstance().showEndDriving();
                stopParameters();
                if (MainActivity.getInstance().isFinish) {
                    MainActivity.getInstance().FinishApp();
                }
            }
        }
    }

    private void sendNotSentDrivingInfo() {
        if (NetworkStatus.getNetworkConnect()) {
            if (MyUtils.not_sent_driving_info.size() > 0) {
                //서버로 전송 안된 주행 정보 전송 하기
                ArrayList<JSONObject> driving_info = new ArrayList<>(MyUtils.not_sent_driving_info);
                for (JSONObject object : driving_info) {
                    try {
                        String[][] params = new String[][]{
                                {"driving_date", object.getString("driving_date")},
                                {"start_time", object.getString("start_time")},
                                {"start_place", object.getString("start_place")},
                                {"end_time", object.getString("end_time")},
                                {"end_place", object.getString("end_place")},
                                {"car_id", object.getString("car_id")},
                                {"user_id", object.getString("user_id")},
                                {"max_speed", object.getString("max_speed")},
                                {"average_speed", object.getString("average_speed")},
                                {"mileage", object.getString("mileage")},
                                {"driving_time", object.getString("driving_time")},
                                {"idling_time", object.getString("idling_time")},
                                {"driving_score", object.getString("driving_score")},
                                {"fast_time", object.getString("fast_time")},
                                {"fast_cnt", object.getString("fast_cnt")},
                                {"quick_cnt", object.getString("quick_cnt")},
                                {"brake_cnt", object.getString("brake_cnt")}
                        };
                        CommonFunc.sendParamData(params);
                        WebHttpConnect.onNotSentDrivingInfoRequest();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                MyUtils.not_sent_driving_info.clear();
            }
        }
    }

    public void stopParameters() {
        prev_speed = 0;
        fuel_consumption = 0;
        idling_time = 0;
        average_speed = 0;
        driving_distance = 0;
        down_score_fast = 0;
        down_score_quick = 0;
        down_score_brake = 0;
        start_time = "";
        end_time = "";
        MyUtils.isEnumSec = false;
        MyUtils.isEnumInfo = false;
        secCnt = 0;
        pidCnt = 0;
        threadCnt = 0;
        if (MyUtils.showGauge) {
            DashboardActivity.getInstance().stopDashboardGauge();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        StopService();
        running = false;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        running = false;
        stopSelf();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

}
