package com.obd2.dgt.ui.ListAdapter.CarList;

public class CarItem {
    String id;
    String model;
    String number;
    String cYear;
    String gas;

    public CarItem(String id, String model, String number, String cYear, String gas) {
        this.id = id;
        this.model = model;
        this.number = number;
        this.cYear = cYear;
        this.gas = gas;
    }
}
