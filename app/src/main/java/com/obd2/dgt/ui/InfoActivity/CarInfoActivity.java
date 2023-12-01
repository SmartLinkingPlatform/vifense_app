package com.obd2.dgt.ui.InfoActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.obd2.dgt.R;
import com.obd2.dgt.dbManage.TableInfo.CarInfoTable;
import com.obd2.dgt.ui.AppBaseActivity;
import com.obd2.dgt.ui.FindPwdActivity;
import com.obd2.dgt.ui.MainActivity;
import com.obd2.dgt.utils.MyUtils;

import java.time.LocalDate;
import java.util.ArrayList;

public class CarInfoActivity extends AppBaseActivity {
    EditText reg_car_number_text;
    EditText reg_car_displacement_text;
    Spinner car_manufacturer_spinner;
    Spinner car_model_spinner;
    Spinner reg_car_date_spinner;
    Spinner car_fuel_type_spinner;
    ImageView register_car_btn;
    ImageView reg_car_prev_btn;

    int manufacturer_idx = 0;
    int model_idx = 0;
    int year_idx = 0;
    int fuel_idx = 0;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_info);

        initLayout();
    }

    private void initLayout() {
        reg_car_number_text = findViewById(R.id.reg_car_number_text);

        reg_car_displacement_text = findViewById(R.id.reg_car_displacement_text);

        car_manufacturer_spinner = findViewById(R.id.car_manufacturer_spinner);
        String[] mf_names = new String[MyUtils.company_names.length];
        for (int i = 0; i < MyUtils.company_names.length; i++) {
            mf_names[i] = getString(MyUtils.company_names[i]);
        }
        ArrayAdapter<String> mf_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, mf_names);
        car_manufacturer_spinner.setAdapter(mf_adapter);
        car_manufacturer_spinner.setSelection(manufacturer_idx);

        car_model_spinner = findViewById(R.id.car_model_spinner);
        String[] md_names = new String[MyUtils.model_names.length];
        for (int i = 0; i < MyUtils.model_names.length; i++) {
            md_names[i] = getString(MyUtils.model_names[i]);
        }
        ArrayAdapter<String> md_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, md_names);
        car_model_spinner.setAdapter(md_adapter);
        car_model_spinner.setSelection(model_idx);

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
        reg_car_date_spinner = findViewById(R.id.reg_car_date_spinner);
        ArrayAdapter<String> dt_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, cYears);
        reg_car_date_spinner.setAdapter(dt_adapter);
        reg_car_date_spinner.setSelection(year_idx);

        car_fuel_type_spinner = findViewById(R.id.car_fuel_type_spinner);
        String[] fuel_types = new String[MyUtils.fuel_types.length];
        for (int i = 0; i < MyUtils.fuel_types.length; i++) {
            fuel_types[i] = getString(MyUtils.fuel_types[i]);
        }
        ArrayAdapter<String> ft_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, fuel_types);
        car_fuel_type_spinner.setAdapter(ft_adapter);
        car_fuel_type_spinner.setSelection(fuel_idx);

        register_car_btn = findViewById(R.id.register_car_btn);
        register_car_btn.setOnClickListener(view -> onRegisterCarClick());

        reg_car_prev_btn = findViewById(R.id.reg_car_prev_btn);
        reg_car_prev_btn.setOnClickListener(view -> onCarInfoPrevClick());
    }

    private void onRegisterCarClick() {
        String[][] fields = new String[][]{
                {"manufacturer", String.valueOf(car_manufacturer_spinner.getSelectedItemPosition())},
                {"model", String.valueOf(car_model_spinner.getSelectedItemPosition())},
                {"create_date", String.valueOf(reg_car_date_spinner.getSelectedItemPosition())},
                {"number", reg_car_number_text.getText().toString()},
                {"fuel_type", String.valueOf(car_fuel_type_spinner.getSelectedItemPosition())},
                {"displacement", reg_car_displacement_text.getText().toString()}
        };
        CarInfoTable.insertCarInfoTable(fields);
        CarInfoTable.getCarInfoTable();

        dialog = new Dialog(CarInfoActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        showConfirmDialog();
    }
    @SuppressLint({"ResourceAsColor", "ResourceType"})
    public void showConfirmDialog() {
        dialog.setContentView(R.layout.dlg_normal);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView dialog_normal_text = dialog.findViewById(R.id.dialog_normal_text);
        dialog_normal_text.setText(R.string.confirm_reg_message);
        ImageView dialog_normal_btn = dialog.findViewById(R.id.dialog_normal_btn);
        dialog_normal_btn.setOnClickListener(view -> {
            onLRChangeLayount(CarInfoActivity.this, MyInfoActivity.class);
            finish();
            dialog.dismiss();
        });
        dialog.show();
    }
    private void onCarInfoPrevClick(){
        onLRChangeLayount(CarInfoActivity.this, MyInfoActivity.class);
        finish();
    }

    @Override
    public void onBackPressed() {
        onLRChangeLayount(CarInfoActivity.this, MyInfoActivity.class);
        finish();
    }
}