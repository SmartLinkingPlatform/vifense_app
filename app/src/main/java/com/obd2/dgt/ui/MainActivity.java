package com.obd2.dgt.ui;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.obd2.dgt.R;
import com.obd2.dgt.dbManage.TableInfo.DeviceInfoTable;
import com.obd2.dgt.ui.InfoActivity.LinkInfoActivity;
import com.obd2.dgt.ui.InfoActivity.MessageActivity;
import com.obd2.dgt.ui.InfoActivity.MyInfoActivity;
import com.obd2.dgt.ui.InfoActivity.RankingInfoActivity;
import com.obd2.dgt.ui.MainListActivity.DashboardActivity;
import com.obd2.dgt.ui.MainListActivity.DiagnosisActivity;
import com.obd2.dgt.ui.MainListActivity.RecordActivity;
import com.obd2.dgt.ui.ListAdapter.MainList.MainListAdapter;
import com.obd2.dgt.ui.ListAdapter.MainList.MainListItem;
import com.obd2.dgt.utils.MyUtils;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppBaseActivity {
    ImageView user_img_btn, con_img_1, con_img_2, con_img_3;
    ImageView dot_img_1, dot_img_2, dot_img_3, dot_img_4, dot_img_5, dot_img_6;
    ImageView connect_btn, mail_btn, ranking_view_btn;
    TextView connect_device_text, ranking_text;
    RecyclerView mainmenu_recycleView;
    ArrayList<MainListItem> mainListItems = new ArrayList<>();
    MainListAdapter mainListAdapter;
    int link_index = 0;
    boolean isRun = false;
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
        MyUtils.currentActivity = this;
        isRun = true;
        initLayout();

        if (MyUtils.isPaired && MyUtils.savedSocketStatus) {
            obdConnectDevice();
        }
    }

    private void initLayout() {
        con_img_1 = findViewById(R.id.con_img_1);
        con_img_1.setImageResource(R.drawable.icon_on);
        con_img_2 = findViewById(R.id.con_img_2);
        con_img_3 = findViewById(R.id.con_img_3);
        dot_img_1 = findViewById(R.id.dot_img_1);
        dot_img_2 = findViewById(R.id.dot_img_2);
        dot_img_3 = findViewById(R.id.dot_img_3);
        dot_img_4 = findViewById(R.id.dot_img_4);
        dot_img_5 = findViewById(R.id.dot_img_5);
        dot_img_6 = findViewById(R.id.dot_img_6);

        connect_device_text = findViewById(R.id.connect_device_text);
        ranking_text = findViewById(R.id.ranking_text);

        user_img_btn = findViewById(R.id.user_img_btn);
        user_img_btn.setOnClickListener(view -> onUserClick());

        connect_btn = findViewById(R.id.connect_btn);
        connect_btn.setOnClickListener(view -> onConnectDeviceClick());

        mail_btn = findViewById(R.id.mail_btn);
        mail_btn.setOnClickListener(view -> onShowMailClick());

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
    }

    private MainListAdapter.ItemClickListener mainListListener = new MainListAdapter.ItemClickListener() {
        @SuppressLint({"ResourceAsColor", "NotifyDataSetChanged"})
        @Override
        public void onItemClick(View v, int position) {
            for (int i = 0; i < mainListItems.size(); i++) {
                if (position == i) {
                    mainListItems.get(i).selected = true;
                } else {
                    mainListItems.get(i).selected = false;
                }
            }
            mainListAdapter.notifyDataSetChanged();

            if (position == 0) {
                onRLChangeLayount(MainActivity.this, DashboardActivity.class);
            } else if (position == 1) {
                onRLChangeLayount(MainActivity.this, DiagnosisActivity.class);
            } else if (position == 2) {
                onRLChangeLayount(MainActivity.this, RecordActivity.class);
            } else if (position == 3) {
                //onRLChangeLayount(MainActivity.this, UserSettingCommunication.class);
            }
        }
    };

    private void onUserClick() {
        onRLChangeLayount(MainActivity.this, MyInfoActivity.class);
    }

    boolean isConClick = true;
    private void onConnectDeviceClick() {
        if(isConClick) {
            link_index = 0;
            onRLChangeLayount(MainActivity.this, LinkInfoActivity.class);
        }
    }

    private void onShowMailClick() {
        onRLChangeLayount(MainActivity.this, MessageActivity.class);
    }

    private void onShowRankingClick() {
        onRLChangeLayount(MainActivity.this, RankingInfoActivity.class);
    }

    private void showConnectingLink(int index) {
        connect_btn.setImageResource(R.drawable.connect_btn_on);
        if (index == 0) {
            dot_img_1.setImageResource(R.drawable.dot_off);
            dot_img_2.setImageResource(R.drawable.dot_off);
            dot_img_3.setImageResource(R.drawable.dot_off);
            con_img_2.setImageResource(R.drawable.bluetooth_off);
            dot_img_4.setImageResource(R.drawable.dot_off);
            dot_img_5.setImageResource(R.drawable.dot_off);
            dot_img_6.setImageResource(R.drawable.dot_off);
            con_img_3.setImageResource(R.drawable.ecu_off);
            connect_btn.setImageResource(R.drawable.connect_btn_off);
        } else if (index == 1) {
            dot_img_1.setImageResource(R.drawable.dot_off);
            dot_img_2.setImageResource(R.drawable.dot_off);
            dot_img_3.setImageResource(R.drawable.dot_off);
        } else if (index == 2) {
            dot_img_1.setImageResource(R.drawable.dot_on);
            dot_img_2.setImageResource(R.drawable.dot_off);
            dot_img_3.setImageResource(R.drawable.dot_off);
        } else if (index == 3) {
            dot_img_1.setImageResource(R.drawable.dot_on);
            dot_img_2.setImageResource(R.drawable.dot_on);
            dot_img_3.setImageResource(R.drawable.dot_off);
        } else if (index == 4) {
            dot_img_1.setImageResource(R.drawable.dot_on);
            dot_img_2.setImageResource(R.drawable.dot_on);
            dot_img_3.setImageResource(R.drawable.dot_on);
        } else if (index == 5) {
            dot_img_1.setImageResource(R.drawable.dot_on);
            dot_img_2.setImageResource(R.drawable.dot_on);
            dot_img_3.setImageResource(R.drawable.dot_on);
            con_img_2.setImageResource(R.drawable.bluetooth_on);
        } else if (index == 6) {
            dot_img_4.setImageResource(R.drawable.dot_off);
            dot_img_5.setImageResource(R.drawable.dot_off);
            dot_img_6.setImageResource(R.drawable.dot_off);
        } else if (index == 7) {
            dot_img_4.setImageResource(R.drawable.dot_on);
            dot_img_5.setImageResource(R.drawable.dot_off);
            dot_img_6.setImageResource(R.drawable.dot_off);
        } else if (index == 8) {
            dot_img_4.setImageResource(R.drawable.dot_on);
            dot_img_5.setImageResource(R.drawable.dot_on);
            dot_img_6.setImageResource(R.drawable.dot_off);
        } else if (index == 9) {
            dot_img_4.setImageResource(R.drawable.dot_on);
            dot_img_5.setImageResource(R.drawable.dot_on);
            dot_img_6.setImageResource(R.drawable.dot_on);
        } else if (index == 10) {
            dot_img_1.setImageResource(R.drawable.dot_on);
            dot_img_2.setImageResource(R.drawable.dot_on);
            dot_img_3.setImageResource(R.drawable.dot_on);
            con_img_2.setImageResource(R.drawable.bluetooth_on);
            dot_img_4.setImageResource(R.drawable.dot_on);
            dot_img_5.setImageResource(R.drawable.dot_on);
            dot_img_6.setImageResource(R.drawable.dot_on);
            con_img_3.setImageResource(R.drawable.ecu_on);
            connect_btn.setImageResource(R.drawable.connect_btn_press);
        }
    }

    //장치 연결 스레드
    boolean obd2link = false;
    class ConnectDeviceAsyncTask extends AsyncTask<String, Integer, Boolean> {
        @SuppressLint("MissingPermission")
        protected Boolean doInBackground(String... str) {
            while (isRun) {
                try {
                    if (!MyUtils.isObdSocket) {
                        connect_device_text.setText(R.string.connecting_obd2_text);
                        isConClick = false;
                        link_index++;
                        if (!MyUtils.isPaired) {
                            if (link_index > 4)
                                link_index = 1;
                        } else {
                            if (link_index > 9) {
                                link_index = 6;
                                if (!obd2link) {
                                    obd2link = true;
                                    Handler handler = new Handler(Looper.getMainLooper());
                                    handler.postDelayed(() -> {
                                        MyUtils.btService.connectDevice(pairedDevice);
                                        //DB 저장
                                        DeviceInfoTable.updateDeviceInfoTable(pairedDevice.getName(), pairedDevice.getAddress(), "1", "1");
                                        handler.removeMessages(0);
                                    }, 2000);
                                }
                            }
                        }

                        if (MyUtils.isSocketError) {
                            link_index = 0;
                            isRun = false;
                            isConClick = true;
                            connect_device_text.setText(R.string.connecting_error_text);
                        }
                        showConnectingLink(link_index);
                        Thread.sleep(300);
                    } else {
                        link_index = 10;
                        connect_device_text.setText(R.string.connected_obd2_text);
                        showConnectingLink(link_index);
                        isRun = false;
                        isConClick = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            ConnectDeviceAsyncTask.this.cancel(true);
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            super.onDestroy();
        }
    }

    BluetoothDevice pairedDevice = null;
    @SuppressLint("MissingPermission")
    public void obdConnectDevice() {
        con_img_1.setImageResource(R.drawable.icon_on);
        Set<BluetoothDevice> pairedDevices = MyUtils.mBluetoothAdapter.getBondedDevices();
        for (BluetoothDevice paired : pairedDevices) {
            if (paired.getName().equals(MyUtils.obd2_name)) {
                pairedDevice = paired;
                break;
            }
        }
        if (pairedDevice != null) {
            isRun = true;
            obd2link = false;
            link_index = 0;
            MyUtils.isObdSocket = false;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new ConnectDeviceAsyncTask().execute("BtConnectInfo");
                }
            });
        }
    }

    public void showDisconnectedStatus() {
        dot_img_1.setImageResource(R.drawable.dot_off);
        dot_img_2.setImageResource(R.drawable.dot_off);
        dot_img_3.setImageResource(R.drawable.dot_off);
        con_img_2.setImageResource(R.drawable.bluetooth_off);
        dot_img_4.setImageResource(R.drawable.dot_off);
        dot_img_5.setImageResource(R.drawable.dot_off);
        dot_img_6.setImageResource(R.drawable.dot_off);
        con_img_3.setImageResource(R.drawable.ecu_off);
        connect_btn.setImageResource(R.drawable.connect_btn_off);
        connect_device_text.setText("");
        //DB 저장
        DeviceInfoTable.updateDeviceInfoTable(MyUtils.obd2_name, MyUtils.obd2_address, "1", "0");
    }

    long waitTime = 0;

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - waitTime >= 1500) {
            waitTime = System.currentTimeMillis();
            Toast.makeText(this, R.string.finish_app, Toast.LENGTH_SHORT).show();
        } else {
            MyUtils.btService.closeSocket();
            ServiceStop();
            finishAffinity();
        }
    }
}