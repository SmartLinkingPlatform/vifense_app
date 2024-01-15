package com.obd2.dgt.ui.InfoActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.obd2.dgt.R;
import com.obd2.dgt.btManage.Protocol;
import com.obd2.dgt.dbManage.TableInfo.CarInfoTable;
import com.obd2.dgt.dbManage.TableInfo.ProtocolTable;
import com.obd2.dgt.network.WebHttpConnect;
import com.obd2.dgt.ui.AppBaseActivity;
import com.obd2.dgt.utils.CommonFunc;
import com.obd2.dgt.utils.MyUtils;

import java.util.ArrayList;


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
    Spinner car_protocol_spinner;

    int manufacturer_idx = 0;
    int model_idx = 0;
    int year_idx = 0;
    int fuel_idx = 0;
    boolean isMode = false;
    Dialog dialog;
    ArrayList<String> md_names = new ArrayList<>();

    private static CarInfoActivity instance;
    public static CarInfoActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_info);
        instance = this;
        md_names = new ArrayList<>();
        initLayout();
    }

    private void setCarModelList(int index) {
        ArrayAdapter<String> md_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, md_names);
        car_model_spinner.setAdapter(md_adapter);
        car_model_spinner.setSelection(index);
    }
    private void initLayout() {
        reg_car_number_text = findViewById(R.id.reg_car_number_text);

        reg_car_gas_text = findViewById(R.id.reg_car_gas_text);

        car_model_spinner = findViewById(R.id.car_model_spinner);
        //setCarModelList(model_idx);

        car_manufacturer_spinner = findViewById(R.id.car_manufacturer_spinner);
        String[] mf_names = new String[MyUtils.manufacturer_names.length];
        for (int i = 0; i < MyUtils.manufacturer_names.length; i++) {
            mf_names[i] = getString(MyUtils.manufacturer_names[i]);
        }
        ArrayAdapter<String> mf_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, mf_names);
        car_manufacturer_spinner.setAdapter(mf_adapter);
        car_manufacturer_spinner.setSelection(manufacturer_idx);
        car_manufacturer_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                md_names = new ArrayList<>();
                for (int n = 0; n < MyUtils.model_names.length; n++) {
                    if (car_manufacturer_spinner.getSelectedItemPosition() == MyUtils.model_names[n][1]) {
                        md_names.add(getString(MyUtils.model_names[n][0]));
                    }
                }
                setCarModelList(model_idx);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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

        car_protocol_spinner = findViewById(R.id.car_protocol_spinner);
        String[] protocol_types = new String[6];
        protocol_types[0] = MyUtils.PROTOCOL_CUSTOM[0][1];
        protocol_types[1] = MyUtils.PROTOCOL_CUSTOM[1][1];
        protocol_types[2] = MyUtils.PROTOCOL_CUSTOM[2][1];
        protocol_types[3] = MyUtils.PROTOCOL_CUSTOM[3][1];
        protocol_types[4] = MyUtils.PROTOCOL_CUSTOM[4][1];
        protocol_types[5] = MyUtils.PROTOCOL_CUSTOM[5][1];
        ArrayAdapter<String> protocol_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, protocol_types);
        car_protocol_spinner.setAdapter(protocol_adapter);
        car_protocol_spinner.setSelection(0);
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
            CommonFunc.sendParamData(params);
            WebHttpConnect.onCarRegisterRequest();
            MyUtils.SEL_PROTOCOL = MyUtils.PROTOCOL_CUSTOM[car_protocol_spinner.getSelectedItemPosition()][0];
        }

    }

    public void onSuccessRegisterCar(String car_id) {
        try {
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
            SystemClock.sleep(100);

            String[][] p_field = new String[][]{
                    {"protocol", MyUtils.PROTOCOL_CUSTOM[car_protocol_spinner.getSelectedItemPosition()][0]}
            };
            ProtocolTable.updateMyInfoTable(p_field);
            SystemClock.sleep(100);
            ProtocolTable.getProtocolTable();

            isMode = false;
            car_progress_layout.setVisibility(View.GONE);

            dialog = new Dialog(CarInfoActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            showConfirmDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView dialog_normal_text = dialog.findViewById(R.id.dialog_normal_text);
        dialog_normal_text.setText(R.string.confirm_reg_message);
        ImageView dialog_normal_btn = dialog.findViewById(R.id.dialog_normal_btn);
        dialog_normal_btn.setOnClickListener(view -> {
            onLRChangeLayout(CarInfoActivity.this, MyInfoActivity.class);
            finish();
            dialog.dismiss();
        });
        dialog.show();
    }
    private void onCarInfoPrevClick(){
        if (!isMode) {
            onLRChangeLayout(CarInfoActivity.this, MyInfoActivity.class);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (!isMode) {
            onLRChangeLayout(CarInfoActivity.this, MyInfoActivity.class);
            finish();
        } else {
            return;
        }
        super.onBackPressed();
    }
}