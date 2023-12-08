package com.obd2.dgt.ui.InfoActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.obd2.dgt.R;
import com.obd2.dgt.dbManage.TableInfo.MessageInfoTable;
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
    ImageView link_prev_btn;
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
        MyUtils.currentActivity = this;

        //페어링 된 장치 검색
        onSearchPairedBtDevices();
        //주변 블루투스 장치 검색
        onSearchEnableBtDevices();

        initLayout();
        setPairedList();
    }

    @SuppressLint("MissingPermission")
    private void initLayout() {
        //
        link_method_recycle_view = findViewById(R.id.link_method_recycle_view);
        LinearLayoutManager verticalLayoutManager1
                = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        link_method_recycle_view.setLayoutManager(verticalLayoutManager1);
        MethodItem item;
        for (int i = 0; i < MyUtils.link_methods.length; i ++) {
            boolean isCheck = false;
            if(i == Integer.parseInt(MyUtils.con_method)) {
                isCheck = true;
            }
            item = new MethodItem(isCheck, MyUtils.link_methods[i]);
            methodItems.add(item);
        }
        methodAdapter = new MethodAdapter(getContext(), methodItems, methodListListener);
        link_method_recycle_view.setAdapter(methodAdapter);

        //페어링 된 장치 목록
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
    }

    private MethodAdapter.ItemClickListener methodListListener = new MethodAdapter.ItemClickListener() {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onItemClick(View v, int position) {
            for (int i = 0; i < methodItems.size(); i++) {
                if(position == i) {
                    methodItems.get(i).selected = true;
                } else {
                    methodItems.get(i).selected = false;
                }
                MyUtils.con_method = String.valueOf(i);
            }
            methodAdapter.notifyDataSetChanged();
        }
    };
    private PairedAdapter.ItemClickListener pairedListListener = new PairedAdapter.ItemClickListener() {
        @SuppressLint({"NotifyDataSetChanged", "MissingPermission"})
        @Override
        public void onItemClick(View v, int position) {
            for (int i = 0; i < pairedItems.size(); i++) {
                if(position == i) {
                    MyUtils.obd2_name = pairedItems.get(i).device.getName();
                    MyUtils.obd2_address = pairedItems.get(i).device.getAddress();
                    if (!MyUtils.isObdSocket) {
                        pairedItems.get(i).selected = true;
                        MyUtils.isSocketError = false;
                        MyUtils.isPaired = true;
                        MainActivity.getInstance().obdConnectDevice();
                    } else {
                        //OBD2 연결 끊기
                        MyUtils.isSocketError = false;
                        pairedItems.get(i).selected = false;
                        MyUtils.btService.closeSocket();
                        MainActivity.getInstance().showDisconnectedStatus();
                    }
                } else {
                    pairedItems.get(i).selected = false;
                }
            }
            pairedAdapter.notifyDataSetChanged();

            finish();
        }
    };

    private DeviceAdapter.ItemClickListener deviceListListener = new DeviceAdapter.ItemClickListener() {
        @SuppressLint({"NotifyDataSetChanged", "MissingPermission"})
        @Override
        public void onItemClick(View v, int position) {
            for (int i = 0; i < deviceItems.size(); i++) {
                if(position == i) {
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
        deviceItems.clear();
        for (BluetoothDevice device : enableDevices) {
            DeviceItem eitem = new DeviceItem(false, device);
            deviceItems.add(eitem);
        }
        deviceAdapter.setData(deviceItems);
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
    @SuppressLint("MissingPermission")
    private void setPairedList() {
        pairedItems.clear();
        for (BluetoothDevice device : pairedDevices) {
            PairedItem pitem;
            if (MyUtils.obd2_address.equals(device.getAddress()) && MyUtils.isObdSocket) {
                pitem = new PairedItem(true, device);
            } else {
                pitem = new PairedItem(false, device);
            }
            pairedItems.add(pitem);
        }
        pairedAdapter.setData(pairedItems);
    }

    private void onLinkInfoPrevClick(){
        onLRChangeLayount(LinkInfoActivity.this, MainActivity.class);
        finish();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
    @SuppressLint("MissingPermission")
    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyUtils.mBluetoothAdapter.cancelDiscovery();
    }
}