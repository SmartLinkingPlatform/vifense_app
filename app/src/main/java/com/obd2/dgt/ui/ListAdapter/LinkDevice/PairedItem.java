package com.obd2.dgt.ui.ListAdapter.LinkDevice;

import android.bluetooth.BluetoothDevice;

public class PairedItem {
    public boolean selected;
    public BluetoothDevice device;

    public PairedItem(boolean selected, BluetoothDevice device){
        this.selected = selected;
        this.device = device;
    }
}
