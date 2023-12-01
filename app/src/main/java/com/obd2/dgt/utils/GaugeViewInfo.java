package com.obd2.dgt.utils;

import android.graphics.PointF;
import android.view.View;

import androidx.annotation.NonNull;

public class GaugeViewInfo implements Cloneable{
    public View layout;
    public int id;
    public int orderIndex;
    public String gaugeVal;
    public PointF gaugePos;
    public int gaugeStatus;

    public GaugeViewInfo(View layout, int orderIndex, String gaugeVal, PointF gaugePos, int id, int gaugeStatus) {
        this.layout = layout;
        this.orderIndex = orderIndex;
        this.gaugeVal = gaugeVal;
        this.gaugePos = gaugePos;
        this.id = id;
        this.gaugeStatus = gaugeStatus;
    }

    public void setGaugeView(View layout) {
        this.layout = layout;
    }
    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }
    public void setGaugePos(PointF gaugePos) {
        this.gaugePos = gaugePos;
    }
    public void setGaugeVal(String gaugeVal) {
        this.gaugeVal = gaugeVal;
    }
    public void setGaugeStatus(int gaugeStatus) {
        this.gaugeStatus = gaugeStatus;
    }

    @NonNull
    @Override
    public GaugeViewInfo clone() {
        try {
            return (GaugeViewInfo) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
