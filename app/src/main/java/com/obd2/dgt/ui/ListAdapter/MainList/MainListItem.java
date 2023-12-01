package com.obd2.dgt.ui.ListAdapter.MainList;

public class MainListItem {
    public boolean selected;
    public int resId;
    public String text;

    public MainListItem(boolean selected, int resId, String text){
        this.selected = selected;
        this.resId = resId;
        this.text = text;
    }
}
