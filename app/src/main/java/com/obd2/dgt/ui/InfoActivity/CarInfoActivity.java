package com.obd2.dgt.ui.InfoActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.obd2.dgt.R;
import com.obd2.dgt.dbManage.TableInfo.CarInfoTable;
import com.obd2.dgt.network.WebHttpConnect;
import com.obd2.dgt.ui.AppBaseActivity;
import com.obd2.dgt.utils.CommonFunc;
import com.obd2.dgt.utils.MyUtils;


public class CarInfoActivity extends AppBaseActivity {
    EditText reg_car_number_text;
    EditText reg_car_gas_text;
    Spinner car_manufacturer_spinner;
    Spinner car_model_spinner;
    Spinner reg_car_date_spinner;
    Spinner car_fuel_type_spinner;
    ImageView register_car_btn;
    ImageView reg_car_prev_btn;
    FrameLayout car_progress_layout;

    int manufacturer_idx = 0;
    int model_idx = 0;
    int year_idx = 0;
    int fuel_idx = 0;
    boolean isMode = false;
    Dialog dialog;

    private static CarInfoActivity instance;
    public static CarInfoActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_info);
        instance = this;
        initLayout();
    }

    private void initLayout() {
        reg_car_number_text = findViewById(R.id.reg_car_number_text);

        reg_car_gas_text = findViewById(R.id.reg_car_gas_text);

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

        reg_car_date_spinner = findViewById(R.id.reg_car_date_spinner);
        ArrayAdapter<String> dt_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, MyUtils.create_years);
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

        car_progress_layout = findViewById(R.id.car_progress_layout);
        car_progress_layout.setVisibility(View.GONE);
    }

    private void onRegisterCarClick() {
        String msg = getString(R.string.check_network_error);
        String btnText = getString(R.string.confirm_text);
        boolean isNetwork = CommonFunc.checkNetworkStatus(CarInfoActivity.this, msg, btnText);
        if (isNetwork) {
            isMode = true;
            car_progress_layout.setVisibility(View.VISIBLE);
            //서버에 등록
            String[][] params = new String[][]{
                    {"number", reg_car_number_text.getText().toString()},
                    {"manufacturer", car_manufacturer_spinner.getSelectedItem().toString()},
                    {"car_model", car_model_spinner.getSelectedItem().toString()},
                    {"car_date", reg_car_date_spinner.getSelectedItem().toString()},
                    {"car_fuel", car_fuel_type_spinner.getSelectedItem().toString()},
                    {"car_gas", reg_car_gas_text.getText().toString()},
                    {"user_id", String.valueOf(MyUtils.my_id)},
                    {"admin_id", String.valueOf(MyUtils.admin_id)}
            };
            WebHttpConnect.onCarRegisterRequest(params);
        }

    }

    public void onSuccessRegisterCar(String car_id) {
        String[][] fields = new String[][]{
                {"car_id", car_id},
                {"manufacturer", String.valueOf(car_manufacturer_spinner.getSelectedItemPosition())},
                {"model", String.valueOf(car_model_spinner.getSelectedItemPosition())},
                {"create_date", String.valueOf(reg_car_date_spinner.getSelectedItemPosition())},
                {"number", reg_car_number_text.getText().toString()},
                {"fuel_type", String.valueOf(car_fuel_type_spinner.getSelectedItemPosition())},
                {"gas", reg_car_gas_text.getText().toString()}
        };
        CarInfoTable.insertCarInfoTable(fields);

        isMode = false;
        car_progress_layout.setVisibility(View.GONE);

        dialog = new Dialog(CarInfoActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        showConfirmDialog();
    }
    public void onDuplicationRegisterCar() {
        isMode = false;
        car_progress_layout.setVisibility(View.GONE);
        Toast.makeText(getApplicationContext(), R.string.error_reg_car_duplicate, Toast.LENGTH_SHORT).show();
    }

    public void onFailedRegisterCar() {
        isMode = false;
        car_progress_layout.setVisibility(View.GONE);
        Toast.makeText(getApplicationContext(), R.string.error_reg_car_fail, Toast.LENGTH_SHORT).show();
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
        if (!isMode) {
            onLRChangeLayount(CarInfoActivity.this, MyInfoActivity.class);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}