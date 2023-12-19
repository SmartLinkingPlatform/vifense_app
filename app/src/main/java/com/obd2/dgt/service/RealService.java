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
import android.os.SystemClock;

import androidx.core.app.NotificationCompat;

import com.obd2.dgt.R;
import com.obd2.dgt.btManage.OBDConnect;
import com.obd2.dgt.network.WebHttpConnect;
import com.obd2.dgt.ui.MainActivity;
import com.obd2.dgt.ui.MainListActivity.DashboardActivity;
import com.obd2.dgt.utils.CommonFunc;
import com.obd2.dgt.utils.MyUtils;


public class RealService extends Service {
    private static Thread mainThread;
    public static Intent serviceIntent = null;

    static int idling_time = 0; //공회전 시간
    boolean speed_fast = false;
    boolean speed_quick = false;
    boolean speed_brake = false;
    float prev_speed = 0;
    static boolean running = true;
    float fuel_consumption = 0;
    int err_cnt = 0;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent == null){
            return START_STICKY;
        }
        serviceIntent = intent;
        createNotification();

        onMainThread();
        return super.onStartCommand(intent, flags, startId);
    }

    private void onMainThread() {
        mainThread = new Thread(() -> {
            while (running) {
                if (!mainThread.isInterrupted()) {
                    if (MyUtils.obdConnect == null) {
                        MyUtils.obdConnect = new OBDConnect();
                    } else {
                        if (mainThread != null && MyUtils.con_ECU) {
                            err_cnt = 0;
                            getDrivingStatus();
                            getFuelConsumption();
                            showWarningDialog();
                        }

                        if (!MyUtils.con_ECU || !MyUtils.loaded_data ||
                                Float.parseFloat(MyUtils.ecu_engine_load) == 0 ||
                                Float.parseFloat(MyUtils.ecu_engine_rpm) == 0) {
                            if (time > 0 && Float.parseFloat(MyUtils.ecu_mileage) > 0.1) {
                                stopEngineStatus();
                            }
                        }
                    }
                    SystemClock.sleep(1000);
                } else {
                    boolean interrupted = Thread.interrupted();
                    if (interrupted) {
                        running = false;
                    }
                }
            }
        });
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
    static int fast_time_cnt = 0;
    static int quick_time_cnt = 0;
    static int break_time_cnt = 0;

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
            fuel_consumption += Double.parseDouble(MyUtils.ecu_fuel_rate) / 3600;
            MyUtils.ecu_fuel_consume = String.valueOf(Math.round(fuel_consumption * 10) / (float) 10.0);
        }
    }

    @SuppressLint({"LaunchActivityFromNotification", "MissingPermission"})
    private void createNotification() {
        String CHANNEL_ID = "com.obd2.dgt";
        createNotificationChannel();

        Intent notificationIntent = new Intent(MyUtils.mContext, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(MyUtils.mContext, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(MyUtils.mContext, CHANNEL_ID)
                .setContentText(getString(R.string.app_running))
                .setSmallIcon(R.drawable.app_icon)
                .setContentIntent(pendingIntent)
                .setFullScreenIntent(pendingIntent, false)
                .setAutoCancel(true)
                .build();

        startForeground(101, notification);
    }

    private void createNotificationChannel() {
        String CHANNEL_ID = "com.obd2.dgt";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            String description = getString(R.string.app_running);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
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

    boolean is_idling = false;
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
            MyUtils.err_idx = 3;
        }
        if (speed_brake) {
            MyUtils.err_idx = 4;
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
        MyUtils.obdConnect.closeSocket();
        Thread.currentThread().interrupt();
    }

    private void stopEngineStatus() {
        int driving_score = 100 + down_score_fast + down_score_quick + down_score_brake;
        String driving_date = CommonFunc.getDate();
        end_time = CommonFunc.getTime();
        //서버에 등록
        String[][] params = new String[][]{
                {"driving_date", driving_date},
                {"start_time", start_time},
                {"end_time", end_time},
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
                {"brake_cnt", String.valueOf(MyUtils.brake_speed_cnt)}
        };
        WebHttpConnect.onSaveDrivingInfoRequest(params);
        stopParameters();
    }

    private void stopParameters() {
        time = 0;
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
        MyUtils.obdConnect.closeSocket();
        MainActivity.getInstance().showDisconnectedStatus(0);
        if (MyUtils.showGauge) {
            DashboardActivity.getInstance().stopDashboardGauge();
        }
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
