package com.obd2.dgt.ui.ListAdapter.LinkDevice;

import android.bluetooth.BluetoothDevice;

public class DeviceItem {
    public boolean selected;
    public BluetoothDevice device;

    public DeviceItem(boolean selected, BluetoothDevice device){
        this.selected = selected;
        this.device = device;
    }
}
