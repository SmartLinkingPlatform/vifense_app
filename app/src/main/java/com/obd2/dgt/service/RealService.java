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
import com.obd2.dgt.btManage.BtService;
import com.obd2.dgt.btManage.OBD2ApiCommand;
import com.obd2.dgt.network.WebHttpConnect;
import com.obd2.dgt.ui.FindPwdActivity;
import com.obd2.dgt.ui.InfoActivity.CarInfoActivity;
import com.obd2.dgt.ui.MainActivity;
import com.obd2.dgt.utils.CommonFunc;
import com.obd2.dgt.utils.MyUtils;

import java.util.concurrent.atomic.AtomicBoolean;

public class RealService extends Service {
    private static Thread mainThread;
    public static Intent serviceIntent = null;

    int idling_time = 0; //공회전 시간
    boolean speed_fast = false;
    boolean speed_quick = false;
    boolean speed_brake = false;
    float prev_speed = 0;
    static boolean running = true;
    float fuel_consumption = 0;

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
                    if (MyUtils.btService == null) {
                        MyUtils.btService = new BtService();
                    } else {
                        if (mainThread != null && MyUtils.isObdSocket) {
                            //MyUtils.btService.setOutStreamPID();
                            getDrivingStatus();
                            getFuelConsumption();
                            showErrorDialog();
                        }
                    }

                    SystemClock.sleep(1000); // 1000 milisecond (1 second)
                } else {
                    boolean interrupted = Thread.interrupted();
                    if (interrupted) {
                        running = false;
                        MyUtils.isObdSocket = false;
                    }
                }
            }
        });
        mainThread.start();
    }

    int time = 0;
    double driving_distance = 0;
    //차량의 움직임 상태
    private void getDrivingStatus() {
        float speed = Float.parseFloat(MyUtils.ecu_vehicle_speed);
        float load = Float.parseFloat(MyUtils.ecu_engine_load);
        if(speed >= 0 && load > 0) { //차량이 엔진을 켠 상태
            if (speed == 0) { //시동을 켠 상태에서 이동하지 않는 상태
                idling_time++;
            } else { //차량이 이동하는 중
                idling_time = 0;
            }
            speed_fast = false;
            speed_quick = false;
            speed_brake = false;
            if (speed > 110) { //속도가 110 km을 초과한 경우
                speed_fast = true;
            }
            if (speed - prev_speed > 7) { // 차량 속도가 1초내에 7km 이상 가속된 경우
                speed_quick = true;
            }
            if (prev_speed - speed > 9) { // 차량 속도가 1초내에 9km 이상 감소된 경우
                speed_brake = true;
            }
            time++;
            String hour = getHour(time);
            String min = getMinuteAndSecond(time % 3600);
            MyUtils.ecu_driving_time = hour + min;

            double distance = speed / (double)3600;
            driving_distance += distance;
            MyUtils.ecu_mileage = String.valueOf(Math.round(driving_distance * 10) / 10.0);
        } else { // 차량이 정지(엔진이 꺼진 상태)
            
        }
        prev_speed = speed;
    }
    private String getHour(int time) {
        String str_h = "";
        int hour = Math.round(time / 3600);
        if (hour > 0) {
            if (hour > 9) {
                str_h = hour + ":";
            } else {
                str_h = "0" + hour + ":";
            }
        }
        return str_h;
    }
    private String getMinuteAndSecond(int time) {
        String str_m = "00";
        int min = Math.round(time / 60);
        if (min > 9) {
            str_m = String.valueOf(min);
        } else {
            str_m = "0" + min;
        }
        String str_s = "00";
        int sec = Math.round(time % 60);
        if (sec > 9) {
            str_s = String.valueOf(sec);
        } else {
            str_s = "0" + sec;
        }
        String min_sec = str_m + ":" + str_s;
        return min_sec;
    }
    private void getFuelConsumption() {
        fuel_consumption += Double.parseDouble(MyUtils.ecu_fuel_rate) / 3600;
        MyUtils.ecu_fuel_consume = String.valueOf(Math.round(fuel_consumption * 10) / (float)10.0);
    }

    @SuppressLint("LaunchActivityFromNotification")
    private void createNotification() {
        String CHANNEL_ID = "com.obd2.dgt";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel =
                    new NotificationChannel(CHANNEL_ID, getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(serviceChannel);
        }

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_MUTABLE);
        } else {
            pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        }
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.app_running))
                .setSmallIcon(R.drawable.app_icon)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();


        startForeground(1, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        StopService();
        MyUtils.isObdSocket = false;
        running = false;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        MyUtils.isObdSocket = false;
        running = false;
        stopSelf();
    }

    private void showErrorDialog() {
        int err_idx = 0;
        if (idling_time > 180) {
            err_idx = 1;
        }
        if (speed_fast) {
            err_idx = 2;
        }
        if (speed_quick) {
            err_idx = 3;
        }
        if (speed_brake) {
            err_idx = 4;
        }
        /*if (!MyUtils.ecu_trouble_code.isEmpty()) {
            MyUtils.is_trouble = true;
            err_idx = 5;
        }
        if (!MyUtils.ecu_consume_warning.isEmpty()) {
            MyUtils.is_consume = true;
            err_idx = 6;
        }*/

        if (err_idx > 0) {
            //MyUtils.appBase.addErrorDialog(err_idx);
        }
    }

    @SuppressLint("MissingPermission")
    public static void StopService() {
        if (mainThread != null) {
            mainThread.interrupt();
            mainThread = null;
        }
        Thread.currentThread().interrupt();
        MyUtils.isObdSocket = false;
        running = false;
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
