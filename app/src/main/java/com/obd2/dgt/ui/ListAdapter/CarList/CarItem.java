package com.obd2.dgt.ui.ListAdapter.CarList;

public class CarItem {
    String id;
    String c_num;
    String manufacturer;
    String model;
    String number;
    String cYear;
    String fuel;
    String gas;

    public CarItem(String id, String c_num, String manufacturer, String model, String number, String cYear, String fuel, String gas) {
        this.id = id;
        this.c_num = c_num;
        this.manufacturer = manufacturer;
        this.model = model;
        this.number = number;
        this.cYear = cYear;
        this.fuel = fuel;
        this.gas = gas;
    }
}
