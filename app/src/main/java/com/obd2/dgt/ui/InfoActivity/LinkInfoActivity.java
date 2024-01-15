package com.obd2.dgt.ui.InfoActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.obd2.dgt.R;
import com.obd2.dgt.dbManage.TableInfo.DeviceInfoTable;
import com.obd2.dgt.ui.AppBaseActivity;
import com.obd2.dgt.ui.ListAdapter.LinkDevice.DeviceAdapter;
import com.obd2.dgt.ui.ListAdapter.LinkDevice.DeviceItem;
import com.obd2.dgt.ui.ListAdapter.LinkDevice.PairedAdapter;
import com.obd2.dgt.ui.ListAdapter.LinkDevice.PairedItem;
import com.obd2.dgt.ui.ListAdapter.LinkMethod.MethodAdapter;
import com.obd2.dgt.ui.ListAdapter.LinkMethod.MethodItem;
import com.obd2.dgt.ui.MainActivity;
import com.obd2.dgt.utils.MyUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LinkInfoActivity extends AppBaseActivity {
    RecyclerView link_method_recycle_view;
    RecyclerView link_paired_recycle_view;
    RecyclerView link_enable_recycle_view;
    TextView paired_device_title;
    FrameLayout enabled_frame_layout;
    ImageView link_prev_btn;
    ImageView refresh_btn;
    ProgressBar refresh_cycle;
    MethodAdapter methodAdapter;
    PairedAdapter pairedAdapter;
    DeviceAdapter deviceAdapter;
    ArrayList<MethodItem> methodItems = new ArrayList<>();
    ArrayList<PairedItem> pairedItems = new ArrayList<>();
    ArrayList<DeviceItem> deviceItems = new ArrayList<>();
    ArrayList<BluetoothDevice> enableDevices = new ArrayList<>();
    Set<BluetoothDevice> pairedDevices = new HashSet<>();
    Dialog dialog;


    private static LinkInfoActivity instance;

    public static LinkInfoActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link);
        instance = this;

        loadView();
        initLayout();
        setPairedList();
    }

    private void loadView() {
        if (MyUtils.con_method == 0) {
            //페어링 된 장치 검색
            onSearchPairedBtDevices();
            //주변 블루투스 장치 검색
            onSearchEnableBtDevices();
        } else if (MyUtils.con_method == 1) {
            onSearchBleDevices();
        }
    }

    @SuppressLint("MissingPermission")
    private void initLayout() {
        link_method_recycle_view = findViewById(R.id.link_method_recycle_view);
        LinearLayoutManager verticalLayoutManager1
                = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        link_method_recycle_view.setLayoutManager(verticalLayoutManager1);
        MethodItem item;
        for (int i = 0; i < MyUtils.link_methods.length; i++) {
            boolean isCheck = false;
            if (i == MyUtils.con_method) {
                isCheck = true;
            }
            item = new MethodItem(isCheck, MyUtils.link_methods[i]);
            methodItems.add(item);
        }
        methodAdapter = new MethodAdapter(getContext(), methodItems, methodListListener);
        link_method_recycle_view.setAdapter(methodAdapter);

        //페어링 된 장치 목록
        paired_device_title = findViewById(R.id.paired_device_title);

        link_paired_recycle_view = findViewById(R.id.link_paired_recycle_view);
        LinearLayoutManager verticalLayoutManager2
                = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        link_paired_recycle_view.setLayoutManager(verticalLayoutManager2);
        for (BluetoothDevice pairedDevice : pairedDevices) {
            PairedItem pitem = new PairedItem(false, pairedDevice);
            pairedItems.add(pitem);
        }
        pairedAdapter = new PairedAdapter(getContext(), pairedItems, pairedListListener);
        link_paired_recycle_view.setAdapter(pairedAdapter);

        enabled_frame_layout = findViewById(R.id.enabled_frame_layout);
        hiddenDeviceList();

        //검색된 블루투스 장치 목록
        link_enable_recycle_view = findViewById(R.id.link_enable_recycle_view);
        LinearLayoutManager verticalLayoutManager3
                = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        link_enable_recycle_view.setLayoutManager(verticalLayoutManager3);
        for (BluetoothDevice device : enableDevices) {
            DeviceItem eitem = new DeviceItem(false, device);
            deviceItems.add(eitem);
        }
        deviceAdapter = new DeviceAdapter(getContext(), deviceItems, deviceListListener);
        link_enable_recycle_view.setAdapter(deviceAdapter);

        //뒤로가기
        link_prev_btn = findViewById(R.id.link_prev_btn);
        link_prev_btn.setOnClickListener(view -> onLinkInfoPrevClick());

        refresh_btn = findViewById(R.id.refresh_btn);
        refresh_btn.setOnClickListener(view -> onStartSearch());
        refresh_btn.setVisibility(View.GONE);

        refresh_cycle = findViewById(R.id.refresh_cycle);
        refresh_cycle.setVisibility(View.VISIBLE);
    }

    private void hiddenDeviceList() {
        if (MyUtils.con_method == 0) {
            enabled_frame_layout.setVisibility(View.VISIBLE);
            paired_device_title.setText(R.string.link_paired_text);
        } else if (MyUtils.con_method == 1) {
            enabled_frame_layout.setVisibility(View.GONE);
            paired_device_title.setText(R.string.link_nopaired_text);
        }
    }

    private MethodAdapter.ItemClickListener methodListListener = new MethodAdapter.ItemClickListener() {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onItemClick(View v, int position) {
            if (MyUtils.con_ECU) {
                Toast.makeText(getApplicationContext(), R.string.fail_select_method, Toast.LENGTH_SHORT).show();
                return;
            }
            for (int i = 0; i < methodItems.size(); i++) {
                if (position == i) {
                    methodItems.get(i).selected = true;
                    MyUtils.con_method = position;
                    hiddenDeviceList();
                    onStopSearch();
                    onStartSearch();
                } else {
                    methodItems.get(i).selected = false;
                }
            }
            methodAdapter.notifyDataSetChanged();
        }
    };
    int select_item = 0;
    private PairedAdapter.ItemClickListener pairedListListener = new PairedAdapter.ItemClickListener() {
        @SuppressLint({"NotifyDataSetChanged", "MissingPermission"})
        @Override
        public void onItemClick(View v, int position) {
            for (int i = 0; i < pairedItems.size(); i++) {
                if (position == i) {
                    select_item = position;
                    MyUtils.obd2_name = pairedItems.get(i).device.getName();
                    MyUtils.obd2_address = pairedItems.get(i).device.getAddress();
                    if (!MyUtils.con_ECU) {
                        pairedItems.get(i).selected = true;
                        //CommonFunc.setUnPairedDevice();
                        showConnectDialog();
                    } else {
                        //OBD2 연결 끊기
                        showDisconnectDialog();
                    }
                } else {
                    pairedItems.get(i).selected = false;
                }
            }
            pairedAdapter.notifyDataSetChanged();
        }
    };

    private DeviceAdapter.ItemClickListener deviceListListener = new DeviceAdapter.ItemClickListener() {
        @SuppressLint({"NotifyDataSetChanged", "MissingPermission"})
        @Override
        public void onItemClick(View v, int position) {
            for (int i = 0; i < deviceItems.size(); i++) {
                if (position == i) {
                    deviceItems.get(i).selected = true;
                    enableDevices.get(i).createBond();
                } else {
                    deviceItems.get(i).selected = false;
                }
            }
            pairedAdapter.notifyDataSetChanged();
        }
    };

    @SuppressLint("MissingPermission")
    private void onSearchEnableBtDevices() {
        onBluetoothReceiver();
        MyUtils.mBluetoothAdapter.startDiscovery();
    }

    @SuppressLint("MissingPermission")
    private void onSearchBleDevices() {
        if (MyUtils.mBluetoothAdapter != null) {
            MyUtils.mBluetoothAdapter.stopLeScan(mLeScanCallBack);
            MyUtils.mBluetoothAdapter.startLeScan(mLeScanCallBack);
        }
    }
    private BluetoothAdapter.LeScanCallback mLeScanCallBack = new BluetoothAdapter.LeScanCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            if (device != null && !TextUtils.isEmpty(device.getName())) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        boolean isExist = false;
                        try {
                            for (BluetoothDevice paired : pairedDevices) {
                                if (paired.getName().equals(device.getName())) {
                                    isExist = true;
                                    break;
                                }
                            }
                            if (!isExist) {
                                List<BluetoothDevice> arraylist = new ArrayList<>(pairedDevices);
                                arraylist.add(device);
                                pairedDevices = new HashSet<>(arraylist);
                                setPairedList();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    };

    @SuppressLint("MissingPermission")
    public void onChangedScanningDeviceList(BluetoothDevice device) {
        boolean isExist = false;
        for (BluetoothDevice paired : pairedDevices) {
            if (paired.getName().equals(device.getName())) {
                isExist = true;
                break;
            }
        }
        if (!isExist) {
            try {
                if (enableDevices.size() == 0) {
                    enableDevices.add(device);
                } else {
                    List<BluetoothDevice> arraylist = new ArrayList<>(enableDevices);
                    boolean isD = false;
                    for (BluetoothDevice enable : arraylist) {
                        if (enable.getName().equals(device.getName())) {
                            isD = true;
                        }
                    }
                    if (!isD) {
                        arraylist.add(device);
                        enableDevices = new ArrayList<>(arraylist);
                    }
                }
                setDeviceList();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void setDeviceList() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                deviceItems.clear();
                for (BluetoothDevice device : enableDevices) {
                    DeviceItem eitem = new DeviceItem(false, device);
                    deviceItems.add(eitem);
                }
                deviceAdapter.setData(deviceItems);
            }
        });
    }

    private void removeEnableList(BluetoothDevice device) {
        List<BluetoothDevice> arraylist = new ArrayList<>(enableDevices);
        arraylist.remove(device);
        enableDevices = new ArrayList<>(arraylist);
    }

    @SuppressLint("MissingPermission")
    private void onSearchPairedBtDevices() {
        pairedDevices = MyUtils.mBluetoothAdapter.getBondedDevices();
    }

    @SuppressLint("MissingPermission")
    public void onChangedPairedDeviceList(BluetoothDevice device) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                boolean isExist = false;
                try {
                    for (BluetoothDevice paired : pairedDevices) {
                        if (paired.getName().equals(device.getName())) {
                            isExist = true;
                            break;
                        }
                    }
                    if (!isExist) {
                        List<BluetoothDevice> arraylist = new ArrayList<>(pairedDevices);
                        arraylist.add(device);
                        pairedDevices = new HashSet<>(arraylist);
                        setPairedList();
                    }
                    removeEnableList(device);
                    setDeviceList();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void setPairedList() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pairedItems.clear();
                for (BluetoothDevice device : pairedDevices) {
                    PairedItem pitem;
                    if (MyUtils.obd2_address.equals(device.getAddress()) && MyUtils.con_ECU) {
                        pitem = new PairedItem(true, device);
                    } else {
                        pitem = new PairedItem(false, device);
                    }
                    pairedItems.add(pitem);
                }
                pairedAdapter.setData(pairedItems);
            }
        });
    }

    private void onLinkInfoPrevClick() {
        onLRChangeLayout(LinkInfoActivity.this, MainActivity.class);
        finish();
    }

    private void showConnectDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog = new Dialog(LinkInfoActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.dlg_two_button);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                TextView dialog_two_title_text = dialog.findViewById(R.id.dialog_two_title_text);
                dialog_two_title_text.setText(R.string.bt_link_title);
                TextView dialog_two_button_text = dialog.findViewById(R.id.dialog_two_content_text);
                dialog_two_button_text.setText(R.string.bt_link_content);
                TextView dialog_two_question_text = dialog.findViewById(R.id.dialog_two_question_text);
                dialog_two_question_text.setText(R.string.bt_link_question);
                ImageView dialog_two_no_btn = dialog.findViewById(R.id.dialog_two_no_btn);
                dialog_two_no_btn.setOnClickListener(view -> {
                    pairedItems.get(select_item).selected = false;
                    pairedAdapter.setData(pairedItems);
                    dialog.dismiss();
                });
                ImageView dialog_two_ok_btn = dialog.findViewById(R.id.dialog_two_ok_btn);
                dialog_two_ok_btn.setOnClickListener(view -> {
                    DeviceInfoTable.updateDeviceInfoTable(MyUtils.obd2_name, MyUtils.obd2_address, "1", "1");
                    SystemClock.sleep(300);
                    MainActivity.getInstance().obdConnectDevice(pairedItems.get(select_item).device);
                    finish();
                    dialog.dismiss();
                });

                dialog.show();
            }
        });
    }

    private void showDisconnectDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog = new Dialog(LinkInfoActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.dlg_finish);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                TextView dialog_two_title_text = dialog.findViewById(R.id.dialog_two_title_text);
                dialog_two_title_text.setText(R.string.bt_unlink_title);
                TextView dialog_two_button_text = dialog.findViewById(R.id.dialog_two_content_text);
                dialog_two_button_text.setText(R.string.bt_unlink_question);
                ImageView dialog_two_no_btn = dialog.findViewById(R.id.dialog_two_no_btn);
                dialog_two_no_btn.setOnClickListener(view -> {
                    dialog.dismiss();
                });
                ImageView dialog_two_ok_btn = dialog.findViewById(R.id.dialog_two_ok_btn);
                dialog_two_ok_btn.setOnClickListener(view -> {
                    pairedItems.get(select_item).selected = false;
                    pairedAdapter.setData(pairedItems);
                    MyUtils.obdConnect.finishSocket();
                    MainActivity.getInstance().showDisconnectedStatus(0);
                    //DB 저장
                    DeviceInfoTable.updateDeviceInfoTable(MyUtils.obd2_name, MyUtils.obd2_address, "1", "0");
                    finish();
                    dialog.dismiss();
                });

                dialog.show();
            }
        });
    }
    private void onStartSearch() {
        enableDevices.clear();
        pairedDevices = new HashSet<>();
        refresh_btn.setVisibility(View.GONE);
        refresh_cycle.setVisibility(View.VISIBLE);
        loadView();
        if (MyUtils.con_method == 0) {
            setPairedList();
        }
    }

    @SuppressLint("MissingPermission")
    public void onStopSearch() {
        refresh_btn.setVisibility(View.VISIBLE);
        refresh_cycle.setVisibility(View.GONE);
        MyUtils.mBluetoothAdapter.stopLeScan(mLeScanCallBack);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onLRChangeLayout(LinkInfoActivity.this, MainActivity.class);
        finish();
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyUtils.mBluetoothAdapter.cancelDiscovery();
    }

}