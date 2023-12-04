package com.obd2.dgt.ui.InfoActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.obd2.dgt.R;
import com.obd2.dgt.dbManage.TableInfo.CarInfoTable;
import com.obd2.dgt.ui.AppBaseActivity;
import com.obd2.dgt.utils.MyUtils;

import java.time.LocalDate;
import java.util.ArrayList;

public class CarInfoModifyActivity extends AppBaseActivity {
    EditText mod_number_text;
    EditText mod_gas_text;
    Spinner mod_manufacturer_spinner;
    Spinner mod_model_spinner;
    Spinner mod_date_spinner;
    Spinner mod_fuel_type_spinner;
    ImageView mod_car_btn;
    ImageView del_car_btn;
    ImageView mod_car_prev_btn;
    int manufacturer_idx = 0;
    int model_idx = 0;
    int year_idx = 0;
    int fuel_idx = 0;
    String car_number = "";
    String car_gas = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_info_modify);

        getSelectedCarInfo();
        initLayout();
    }

    private void initLayout() {
        mod_number_text = findViewById(R.id.mod_number_text);
        mod_number_text.setText(car_number);

        mod_gas_text = findViewById(R.id.mod_gas_text);
        mod_gas_text.setText(car_gas);

        mod_manufacturer_spinner = findViewById(R.id.mod_manufacturer_spinner);
        String[] mf_names = new String[MyUtils.company_names.length];
        for (int i = 0; i < MyUtils.company_names.length; i++) {
            mf_names[i] = getString(MyUtils.company_names[i]);
        }
        ArrayAdapter<String> mf_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, mf_names);
        mod_manufacturer_spinner.setAdapter(mf_adapter);
        mod_manufacturer_spinner.setSelection(manufacturer_idx);

        mod_model_spinner = findViewById(R.id.mod_model_spinner);
        String[] md_names = new String[MyUtils.model_names.length];
        for (int i = 0; i < MyUtils.model_names.length; i++) {
            md_names[i] = getString(MyUtils.model_names[i]);
        }
        ArrayAdapter<String> md_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, md_names);
        mod_model_spinner.setAdapter(md_adapter);
        mod_model_spinner.setSelection(model_idx);

        LocalDate now = null;
        int year = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            now = LocalDate.now();
            year = now.getYear();
        }
        ArrayList<String> cYears = new ArrayList<>();
        for (int i = 1990; i <= year; i++) {
            cYears.add(String.valueOf(i));
        }
        mod_date_spinner = findViewById(R.id.mod_date_spinner);
        ArrayAdapter<String> dt_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, cYears);
        mod_date_spinner.setAdapter(dt_adapter);
        mod_date_spinner.setSelection(year_idx);

        mod_fuel_type_spinner = findViewById(R.id.mod_fuel_type_spinner);
        String[] fuel_types = new String[MyUtils.fuel_types.length];
        for (int i = 0; i < MyUtils.fuel_types.length; i++) {
            fuel_types[i] = getString(MyUtils.fuel_types[i]);
        }
        ArrayAdapter<String> ft_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, fuel_types);
        mod_fuel_type_spinner.setAdapter(ft_adapter);
        mod_fuel_type_spinner.setSelection(fuel_idx);

        mod_car_btn = findViewById(R.id.mod_car_btn);
        mod_car_btn.setOnClickListener(view -> onModifyCarClick());

        del_car_btn = findViewById(R.id.del_car_btn);
        del_car_btn.setOnClickListener(view -> onDeleteCarClick());

        mod_car_prev_btn = findViewById(R.id.mod_car_prev_btn);
        mod_car_prev_btn.setOnClickListener(view -> onModCarInfoPrevClick());
    }

    private void getSelectedCarInfo() {
        for (String[] infos : MyUtils.carInfo) {
            if (MyUtils.sel_car_id == Integer.parseInt(infos[0])) {
                manufacturer_idx = Integer.parseInt(infos[1]);
                model_idx = Integer.parseInt(infos[2]);
                year_idx = Integer.parseInt(infos[3]);
                car_number = infos[4];
                fuel_idx = Integer.parseInt(infos[5]);
                car_gas = infos[6];
            }
        }
    }

    private void onModifyCarClick() {
        String[][] fields = new String[][]{
                {"manufacturer", String.valueOf(mod_manufacturer_spinner.getSelectedItemPosition())},
                {"model", String.valueOf(mod_model_spinner.getSelectedItemPosition())},
                {"create_date", String.valueOf(mod_date_spinner.getSelectedItemPosition())},
                {"number", mod_number_text.getText().toString()},
                {"fuel_type", String.valueOf(mod_fuel_type_spinner.getSelectedItemPosition())},
                {"displacement", mod_gas_text.getText().toString()}
        };

        CarInfoTable.updateCarInfoTable(MyUtils.sel_car_id, fields);
        CarInfoTable.getCarInfoTable();

        onLRChangeLayount(CarInfoModifyActivity.this, MyInfoActivity.class);
        finish();
    }

    private void onDeleteCarClick() {
        CarInfoTable.deleteCarInfoTable(MyUtils.sel_car_id);
        CarInfoTable.getCarInfoTable();

        onLRChangeLayount(CarInfoModifyActivity.this, MyInfoActivity.class);
        finish();
    }

    private void onModCarInfoPrevClick(){
        onLRChangeLayount(CarInfoModifyActivity.this, MyInfoActivity.class);
        finish();
    }

    @Override
    public void onBackPressed() {
        onLRChangeLayount(CarInfoModifyActivity.this, MyInfoActivity.class);
        finish();
    }
}