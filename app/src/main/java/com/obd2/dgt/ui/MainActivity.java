package com.obd2.dgt.ui;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.obd2.dgt.R;
import com.obd2.dgt.dbManage.TableInfo.DeviceInfoTable;
import com.obd2.dgt.dbManage.TableInfo.DrivingTable;
import com.obd2.dgt.network.WebHttpConnect;
import com.obd2.dgt.service.RealService;
import com.obd2.dgt.ui.InfoActivity.CarInfoActivity;
import com.obd2.dgt.ui.InfoActivity.LinkInfoActivity;
import com.obd2.dgt.ui.InfoActivity.MessageActivity;
import com.obd2.dgt.ui.InfoActivity.MyInfoActivity;
import com.obd2.dgt.ui.InfoActivity.RankingInfoActivity;
import com.obd2.dgt.ui.MainListActivity.DashboardActivity;
import com.obd2.dgt.ui.MainListActivity.DiagnosisActivity;
import com.obd2.dgt.ui.MainListActivity.RecordActivity;
import com.obd2.dgt.ui.ListAdapter.MainList.MainListAdapter;
import com.obd2.dgt.ui.ListAdapter.MainList.MainListItem;
import com.obd2.dgt.utils.CommonFunc;
import com.obd2.dgt.utils.MyUtils;

import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppBaseActivity {
    ImageView user_img_btn, con_img_1, con_img_2, con_img_3;
    ImageView dot_img_1, dot_img_2, dot_img_3, dot_img_4, dot_img_5, dot_img_6;
    ImageView connect_btn, message_btn, ranking_view_btn;
    TextView connect_device_text;
    TextView ranking_mileage_text, ranking_safety_text;
    TextView ranking_mileage_unit_text, ranking_safety_unit_text;
    TextView app_and_db_version;
    RecyclerView mainmenu_recycleView;
    FrameLayout main_ranking_view_layout;
    ArrayList<MainListItem> mainListItems = new ArrayList<>();
    MainListAdapter mainListAdapter;
    int link_index = 0;
    public boolean isConnecting = false;
    Dialog dialog;
    Dialog closeDialog;
    FrameLayout progress_layout;
    public boolean isFinish = false;
    private static MainActivity instance;
    public static MainActivity getInstance() {
        return instance;
    }


    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance = this;

        requestRankingInfo();
        initLayout();

        if (!MyUtils.run_main) {
            MyUtils.run_main = true;
            SystemClock.sleep(1000);
            if (MyUtils.con_OBD) {
                if (MyUtils.con_ECU) {
                    link_index = 10;
                    showConnectingLink(link_index);
                }
                /*else {
                    if (MyUtils.savedSocketStatus) {
                        obdConnectDevice();
                    }
                }*/
            }

        }
    }
    private void requestRankingInfo() {
        String msg = getString(R.string.check_network_error);
        String btnText = getString(R.string.confirm_text);
        boolean isNetwork = CommonFunc.checkNetworkStatus(MainActivity.this, msg, btnText);
        if (isNetwork) {
            String driving_date = CommonFunc.getDate();
            //서버에 등록
            String[][] params = new String[][]{
                    {"admin_id", String.valueOf(MyUtils.admin_id)},
                    {"car_id", String.valueOf(MyUtils.car_id)},
                    {"user_id", String.valueOf(MyUtils.my_id)},
                    {"driving_date", driving_date}
            };
            CommonFunc.sendParamData(params);
            WebHttpConnect.onRankingRequest();
        }
    }

    public void setRankingValues(String[] values) {
        MyUtils.mileage_score = values[0];
        MyUtils.safety_score = values[1];
        showRankingInfo();

        //메일 받기
        String[][] msgparams = new String[][]{
                {"msg_id", String.valueOf(MyUtils.lastMsgID)},
                {"user_phone", MyUtils.my_phone}
        };
        CommonFunc.sendParamData(msgparams);
        WebHttpConnect.onMessageInfoRequest();
    }

    public void setMessageStatus() {
        if (MyUtils.msg_show) {
            message_btn.setBackgroundResource(R.drawable.mail_btn_state);
        } else {
            message_btn.setBackgroundResource(R.drawable.mail_off);
        }
        DrivingTable.getNotSentDrivingInfoTable();
        if (MyUtils.not_sent_driving_info.size() > 0) {
            //서버로 전송 안된 주행 정보 전송 하기
            ArrayList<JSONObject> driving_info = new ArrayList<>(MyUtils.not_sent_driving_info);
            for (JSONObject object : driving_info) {
                try {
                    String[][] params = new String[][]{
                            {"admin_id", String.valueOf(MyUtils.admin_id)},
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

    private void initLayout() {
        con_img_1 = findViewById(R.id.con_img_1);
        con_img_1.setBackgroundResource(R.drawable.icon_on);
        con_img_2 = findViewById(R.id.con_img_2);
        con_img_3 = findViewById(R.id.con_img_3);
        dot_img_1 = findViewById(R.id.dot_img_1);
        dot_img_2 = findViewById(R.id.dot_img_2);
        dot_img_3 = findViewById(R.id.dot_img_3);
        dot_img_4 = findViewById(R.id.dot_img_4);
        dot_img_5 = findViewById(R.id.dot_img_5);
        dot_img_6 = findViewById(R.id.dot_img_6);

        connect_device_text = findViewById(R.id.connect_device_text);

        main_ranking_view_layout = findViewById(R.id.main_ranking_view_layout);
        main_ranking_view_layout.setOnClickListener(view -> onShowRankingClick());

        ranking_mileage_text = findViewById(R.id.ranking_mileage_text);
        ranking_mileage_unit_text = findViewById(R.id.ranking_mileage_unit_text);
        ranking_safety_text = findViewById(R.id.ranking_safety_text);
        ranking_safety_unit_text = findViewById(R.id.ranking_safety_unit_text);

        user_img_btn = findViewById(R.id.user_img_btn);
        user_img_btn.setOnClickListener(view -> onUserClick());

        connect_btn = findViewById(R.id.connect_btn);
        connect_btn.setOnClickListener(view -> onConnectDeviceClick());

        message_btn = findViewById(R.id.message_btn);
        message_btn.setOnClickListener(view -> onShowMailClick());

        ranking_view_btn = findViewById(R.id.ranking_view_btn);
        ranking_view_btn.setOnClickListener(view -> onShowRankingClick());

        mainmenu_recycleView = findViewById(R.id.mainmenu_recycleView);
        LinearLayoutManager verticalLayoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mainmenu_recycleView.setLayoutManager(verticalLayoutManager);
        MainListItem item;
        for (int i = 0; i < MyUtils.main_list_texts.length; i++) {
            item = new MainListItem(false, MyUtils.main_list_images[i], getString(MyUtils.main_list_texts[i]));
            mainListItems.add(item);
        }
        mainListAdapter = new MainListAdapter(getContext(), mainListItems, mainListListener);
        mainmenu_recycleView.setAdapter(mainListAdapter);

        dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        progress_layout = findViewById(R.id.finish_progress_layout);
        progress_layout.setVisibility(View.GONE);

        app_and_db_version = findViewById(R.id.app_and_db_version);
        String str_version = getString(R.string.app_version) + "_" + MyUtils.DB_VERSION;
        app_and_db_version.setText(str_version);
    }

    private MainListAdapter.ItemClickListener mainListListener = new MainListAdapter.ItemClickListener() {
        @SuppressLint({"ResourceAsColor", "NotifyDataSetChanged"})
        @Override
        public void onItemClick(View v, int position) {
            if (isFinish)
                return;
            for (int i = 0; i < mainListItems.size(); i++) {
                if (position == i) {
                    mainListItems.get(i).selected = true;
                } else {
                    mainListItems.get(i).selected = false;
                }
            }
            mainListAdapter.notifyDataSetChanged();

            if (position == 0) {
                onRLChangeLayout(MainActivity.this, DashboardActivity.class);
            } else if (position == 1) {
                onRLChangeLayout(MainActivity.this, DiagnosisActivity.class);
            } else if (position == 2) {
                onRLChangeLayout(MainActivity.this, RecordActivity.class);
            } else if (position == 3) {
                //onRLChangeLayout(MainActivity.this, UserSettingCommunication.class);
            }
        }
    };

    private void onUserClick() {
        if (isFinish)
            return;
        onRLChangeLayout(MainActivity.this, MyInfoActivity.class);
    }

    boolean isConClick = true;
    private void onConnectDeviceClick() {
        if (isFinish)
            return;
        if(isConClick) {
            if (MyUtils.carInfo.size() == 0) {
                showAddCarDialog();
                return;
            }
            link_index = 0;
            onRLChangeLayout(MainActivity.this, LinkInfoActivity.class);
        }
    }
    private void showAddCarDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.setContentView(R.layout.dlg_normal);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                TextView dialog_normal_text = dialog.findViewById(R.id.dialog_normal_text);
                dialog_normal_text.setText(R.string.show_reg_car);
                ImageView dialog_normal_btn = dialog.findViewById(R.id.dialog_normal_btn);
                dialog_normal_btn.setOnClickListener(view -> {
                    dialog.dismiss();
                    onRLChangeLayout(MainActivity.this, CarInfoActivity.class);
                });
                dialog.show();
            }
        });
    }
    private void onShowMailClick() {
        if (isFinish)
            return;
        onRLChangeLayout(MainActivity.this, MessageActivity.class);
    }

    private void onShowRankingClick() {
        if (isFinish)
            return;
        onRLChangeLayout(MainActivity.this, RankingInfoActivity.class);
    }

    private void showConnectingLink(int index) {
        connect_btn.setBackgroundResource(R.drawable.connect_btn_on);
        if (index == 0) {
            dot_img_1.setBackgroundResource(R.drawable.dot_off);
            dot_img_2.setBackgroundResource(R.drawable.dot_off);
            dot_img_3.setBackgroundResource(R.drawable.dot_off);
            con_img_2.setBackgroundResource(R.drawable.bluetooth_off);
            dot_img_4.setBackgroundResource(R.drawable.dot_off);
            dot_img_5.setBackgroundResource(R.drawable.dot_off);
            dot_img_6.setBackgroundResource(R.drawable.dot_off);
            con_img_3.setBackgroundResource(R.drawable.ecu_off);
            connect_btn.setBackgroundResource(R.drawable.connect_btn_off);
        } else if (index == 1) {
            dot_img_1.setBackgroundResource(R.drawable.dot_off);
            dot_img_2.setBackgroundResource(R.drawable.dot_off);
            dot_img_3.setBackgroundResource(R.drawable.dot_off);
        } else if (index == 2) {
            dot_img_1.setBackgroundResource(R.drawable.dot_on);
            dot_img_2.setBackgroundResource(R.drawable.dot_off);
            dot_img_3.setBackgroundResource(R.drawable.dot_off);
        } else if (index == 3) {
            dot_img_1.setBackgroundResource(R.drawable.dot_on);
            dot_img_2.setBackgroundResource(R.drawable.dot_on);
            dot_img_3.setBackgroundResource(R.drawable.dot_off);
        } else if (index == 4) {
            dot_img_1.setBackgroundResource(R.drawable.dot_on);
            dot_img_2.setBackgroundResource(R.drawable.dot_on);
            dot_img_3.setBackgroundResource(R.drawable.dot_on);
        } else if (index == 5) {
            dot_img_1.setBackgroundResource(R.drawable.dot_on);
            dot_img_2.setBackgroundResource(R.drawable.dot_on);
            dot_img_3.setBackgroundResource(R.drawable.dot_on);
            con_img_2.setBackgroundResource(R.drawable.bluetooth_on);
        } else if (index == 6) {
            dot_img_4.setBackgroundResource(R.drawable.dot_off);
            dot_img_5.setBackgroundResource(R.drawable.dot_off);
            dot_img_6.setBackgroundResource(R.drawable.dot_off);
        } else if (index == 7) {
            dot_img_4.setBackgroundResource(R.drawable.dot_on);
            dot_img_5.setBackgroundResource(R.drawable.dot_off);
            dot_img_6.setBackgroundResource(R.drawable.dot_off);
        } else if (index == 8) {
            dot_img_4.setBackgroundResource(R.drawable.dot_on);
            dot_img_5.setBackgroundResource(R.drawable.dot_on);
            dot_img_6.setBackgroundResource(R.drawable.dot_off);
        } else if (index == 9) {
            dot_img_4.setBackgroundResource(R.drawable.dot_on);
            dot_img_5.setBackgroundResource(R.drawable.dot_on);
            dot_img_6.setBackgroundResource(R.drawable.dot_on);
        } else if (index == 10) {
            dot_img_1.setBackgroundResource(R.drawable.dot_on);
            dot_img_2.setBackgroundResource(R.drawable.dot_on);
            dot_img_3.setBackgroundResource(R.drawable.dot_on);
            con_img_2.setBackgroundResource(R.drawable.bluetooth_on);
            dot_img_4.setBackgroundResource(R.drawable.dot_on);
            dot_img_5.setBackgroundResource(R.drawable.dot_on);
            dot_img_6.setBackgroundResource(R.drawable.dot_on);
            con_img_3.setBackgroundResource(R.drawable.ecu_on);
            connect_btn.setBackgroundResource(R.drawable.connect_btn_press);
            connect_device_text.setText(R.string.connected_obd2_text);
        }
    }

    public void setECULinkStatus(boolean status) {
        eculink = status;
    }

    private boolean getECULinkStatus() {
        return eculink;
    }

    //장치 연결 스레드
    boolean obdlink = false;
    boolean eculink = false;
    int delay = 0;
    class ConnectDeviceAsyncTask extends AsyncTask<String, Integer, Boolean> {
        @SuppressLint({"MissingPermission", "WrongThread"})
        protected Boolean doInBackground(String... str) {
            while (isConnecting) {
                try {
                    if (!MyUtils.con_ECU) {
                        connect_device_text.setText(R.string.connecting_obd2_text);
                        isConClick = false;
                        if (!MyUtils.con_OBD) {
                            if (link_index > 4) {
                                link_index = 1;
                                if (!obdlink) {
                                    obdlink = true;
                                    MyUtils.obdConnect.setConnectingOBD(pairedDevice);
                                }
                                if (delay > 2) {
                                    delay = 0;
                                    obdlink = false;
                                }
                                delay++;
                            }
                        } else {
                            if (link_index > 9) {
                                link_index = 6;
                                if (!getECULinkStatus()) {
                                    MyUtils.obdConnect.setConnectingECU();
                                }
                            }
                        }
                        showConnectingLink(link_index);
                        SystemClock.sleep(400);
                        link_index++;
                    } else {
                        delay = 0;
                        link_index = 10;
                        showConnectingLink(link_index);
                        isConnecting = false;
                        isConClick = true;
                        MyUtils.is_driving = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            /*if (MyUtils.show_dash_dialog) {
                DashboardActivity.getInstance().showSocketError();
            }*/

            ConnectDeviceAsyncTask.this.cancel(true);
            return false;
        }
    }

    BluetoothDevice pairedDevice = null;
    @SuppressLint("MissingPermission")
    public void obdConnectDevice(BluetoothDevice bt_obd) {
        con_img_1.setBackgroundResource(R.drawable.icon_on);
        pairedDevice = bt_obd;
        if (pairedDevice != null) {
            isConnecting = true;
            obdlink = false;
            eculink = false;
            link_index = 0;
            runOnUiThread(() -> new ConnectDeviceAsyncTask().execute("BtConnectInfo"));
        } else {
            showDisconnectedStatus(0);
        }
    }

    public void showDisconnectedStatus(int idx) {
        isConnecting = true;
        link_index = 0;
        isConClick = true;
        showConnectingLink(link_index);
        if (idx == 0)
            connect_device_text.setText("");
        else
            connect_device_text.setText(R.string.connecting_error_text);
    }

    public void showRankingInfo() {
        ranking_mileage_text.setText(MyUtils.mileage_score);
        ranking_mileage_unit_text.setText(R.string.unit_score);
        ranking_safety_text.setText(MyUtils.safety_score);
        ranking_safety_unit_text.setText(R.string.unit_score);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyUtils.run_main = false;
    }

    @Override
    public void onBackPressed() {
        if (isFinish)
            return;

        closeDialog = new Dialog(MainActivity.this);
        closeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        closeDialog.setCancelable(false);
        closeDialog.setContentView(R.layout.dlg_finish);
        closeDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        closeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView dialog_two_title_text = closeDialog.findViewById(R.id.dialog_two_title_text);
        dialog_two_title_text.setText(R.string.finish_app_title);
        TextView dialog_two_button_text = closeDialog.findViewById(R.id.dialog_two_content_text);
        dialog_two_button_text.setText(R.string.finish_app_content);
        ImageView dialog_two_no_btn = closeDialog.findViewById(R.id.dialog_two_no_btn);
        dialog_two_no_btn.setOnClickListener(view -> {
            isFinish = false;
            closeDialog.dismiss();
        });
        ImageView dialog_two_ok_btn = closeDialog.findViewById(R.id.dialog_two_ok_btn);
        dialog_two_ok_btn.setOnClickListener(view -> {
            isFinish = true;
            if (!MyUtils.con_ECU || !MyUtils.loaded_data || Float.parseFloat(MyUtils.ecu_mileage) == 0)
                FinishApp();
            else {
                MyUtils.obdConnect.finishSocket();
                MainActivity.getInstance().showDisconnectedStatus(0);
                DeviceInfoTable.updateDeviceInfoTable(MyUtils.obd2_name, MyUtils.obd2_address, "1", "0");
                progress_layout.setVisibility(View.VISIBLE);
                closeDialog.dismiss();
            }
        });

        closeDialog.show();
        //super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseWakeLock();
    }

    public void FinishApp() {
        isFinish = false;
        progress_layout.setVisibility(View.GONE);
        //CommonFunc.setInformationToSystem("isRun", "0");
        try {
            closeDatabase();
            RealService.getInstance().onTaskRemoved(RealService.serviceIntent);
            moveTaskToBack(true);
            finish();
            finishAffinity();
            overridePendingTransition(0, 0);
            android.os.Process.killProcess(android.os.Process.myPid());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showEndDriving() {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(MyUtils.mContext, R.string.send_driving_data, Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void showNotEndDriving() {
        isFinish = false;
        progress_layout.setVisibility(View.GONE);

        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(MyUtils.mContext, R.string.not_send_driving_data, Toast.LENGTH_SHORT).show();
            }
        });
    }
}