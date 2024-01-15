package com.obd2.dgt.ui.InfoActivity;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.obd2.dgt.R;
import com.obd2.dgt.btManage.Protocol;
import com.obd2.dgt.dbManage.TableInfo.CarInfoTable;
import com.obd2.dgt.dbManage.TableInfo.ProtocolTable;
import com.obd2.dgt.network.WebHttpConnect;
import com.obd2.dgt.ui.AppBaseActivity;
import com.obd2.dgt.utils.CommonFunc;
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
    FrameLayout car_mod_progress_layout;
    Spinner mod_protocol_spinner;
    int manufacturer_idx = 0;
    int model_idx = 0;
    int year_idx = 0;
    int fuel_idx = 0;
    String car_id = "";
    String car_number = "";
    String car_gas = "";
    boolean isMode = false;
    int protocol_idx = 0;
    ArrayList<String> md_names = new ArrayList<>();
    private static CarInfoModifyActivity instance;
    public static CarInfoModifyActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_info_modify);
        instance = this;
        md_names = new ArrayList<>();
        protocol_idx = 0;
        getSelectedCarInfo();
        initLayout();
    }

    private void setCarModelList(int index) {
        ArrayAdapter<String> md_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, md_names);
        mod_model_spinner.setAdapter(md_adapter);
        mod_model_spinner.setSelection(index);
    }
    private void initLayout() {
        mod_number_text = findViewById(R.id.mod_number_text);
        mod_number_text.setText(car_number);

        mod_gas_text = findViewById(R.id.mod_gas_text);
        mod_gas_text.setText(car_gas);

        mod_manufacturer_spinner = findViewById(R.id.mod_manufacturer_spinner);
        String[] mf_names = new String[MyUtils.manufacturer_names.length];
        for (int i = 0; i < MyUtils.manufacturer_names.length; i++) {
            mf_names[i] = getString(MyUtils.manufacturer_names[i]);
        }

        mod_model_spinner = findViewById(R.id.mod_model_spinner);
        //setCarModelList(model_idx);

        ArrayAdapter<String> mf_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, mf_names);
        mod_manufacturer_spinner.setAdapter(mf_adapter);
        mod_manufacturer_spinner.setSelection(manufacturer_idx);
        mod_manufacturer_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                md_names = new ArrayList<>();
                for (int n = 0; n < MyUtils.model_names.length; n++) {
                    if (mod_manufacturer_spinner.getSelectedItemPosition() == MyUtils.model_names[n][1]) {
                        md_names.add(getString(MyUtils.model_names[n][0]));
                    }
                }
                if (mod_manufacturer_spinner.getSelectedItemPosition() == manufacturer_idx) {
                    if (model_idx >= md_names.size()) {
                        model_idx = 0;
                    }
                    setCarModelList(model_idx);
                } else {
                    setCarModelList(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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

        car_mod_progress_layout = findViewById(R.id.car_mod_progress_layout);
        car_mod_progress_layout.setVisibility(View.GONE);

        mod_protocol_spinner = findViewById(R.id.mod_protocol_spinner);
        String[] protocol_types = new String[6];
        protocol_types[0] = MyUtils.PROTOCOL_CUSTOM[0][1];
        protocol_types[1] = MyUtils.PROTOCOL_CUSTOM[1][1];
        protocol_types[2] = MyUtils.PROTOCOL_CUSTOM[2][1];
        protocol_types[3] = MyUtils.PROTOCOL_CUSTOM[3][1];
        protocol_types[4] = MyUtils.PROTOCOL_CUSTOM[4][1];
        protocol_types[5] = MyUtils.PROTOCOL_CUSTOM[5][1];
        ArrayAdapter<String> protocol_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, protocol_types);
        mod_protocol_spinner.setAdapter(protocol_adapter);
        mod_protocol_spinner.setSelection(protocol_idx);
    }

    private void getSelectedCarInfo() {
        for (String[] infos : MyUtils.carInfo) {
            if (MyUtils.sel_car_id == Integer.parseInt(infos[0])) {
                car_id = infos[1];
                manufacturer_idx = Integer.parseInt(infos[2]);
                model_idx = Integer.parseInt(infos[3]);
                year_idx = Integer.parseInt(infos[4]);
                car_number = infos[5];
                fuel_idx = Integer.parseInt(infos[6]);
                car_gas = infos[7];
                for (int n = 0; n < MyUtils.model_names.length; n++) {
                    if (manufacturer_idx == MyUtils.model_names[n][1]) {
                        md_names.add(getString(MyUtils.model_names[n][0]));
                    }
                }
            }
        }

        for (String[] protocol : MyUtils.PROTOCOL_CUSTOM) {
            if (protocol[0].equals(MyUtils.SEL_PROTOCOL)) {
                break;
            }
            protocol_idx ++;
        }
    }

    private void onModifyCarClick() {
        String msg = getString(R.string.check_network_error);
        String btnText = getString(R.string.confirm_text);
        boolean isNetwork = CommonFunc.checkNetworkStatus(CarInfoModifyActivity.this, msg, btnText);
        if (isNetwork) {
            isMode = true;
            car_mod_progress_layout.setVisibility(View.VISIBLE);
            //서버에 등록
            String[][] params = new String[][]{
                    {"car_id", car_id},
                    {"number", mod_number_text.getText().toString()},
                    {"manufacturer", mod_manufacturer_spinner.getSelectedItem().toString()},
                    {"car_model", mod_model_spinner.getSelectedItem().toString()},
                    {"car_date", mod_date_spinner.getSelectedItem().toString()},
                    {"car_fuel", mod_fuel_type_spinner.getSelectedItem().toString()},
                    {"car_gas", mod_gas_text.getText().toString()},
            };
            CommonFunc.sendParamData(params);
            WebHttpConnect.onCarModifyRequest();

            MyUtils.SEL_PROTOCOL = MyUtils.PROTOCOL_CUSTOM[mod_protocol_spinner.getSelectedItemPosition()][0];
        }
    }

    public void onSuccessModifyCar() {
        try {
            isMode = false;
            String[][] fields = new String[][]{
                    {"manufacturer", String.valueOf(mod_manufacturer_spinner.getSelectedItemPosition())},
                    {"model", String.valueOf(mod_model_spinner.getSelectedItemPosition())},
                    {"create_date", String.valueOf(mod_date_spinner.getSelectedItemPosition())},
                    {"number", mod_number_text.getText().toString()},
                    {"fuel_type", String.valueOf(mod_fuel_type_spinner.getSelectedItemPosition())},
                    {"gas", mod_gas_text.getText().toString()}
            };

            CarInfoTable.updateCarInfoTable(Integer.parseInt(car_id), fields);
            SystemClock.sleep(100);
            CarInfoTable.getCarInfoTable();

            String[][] p_field = new String[][]{
                    {"protocol", MyUtils.PROTOCOL_CUSTOM[mod_protocol_spinner.getSelectedItemPosition()][0]}
            };
            ProtocolTable.updateMyInfoTable(p_field);
            SystemClock.sleep(100);
            ProtocolTable.getProtocolTable();

            car_mod_progress_layout.setVisibility(View.GONE);
            onLRChangeLayout(CarInfoModifyActivity.this, MyInfoActivity.class);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onFailedModifyCar() {
        isMode = false;
        car_mod_progress_layout.setVisibility(View.GONE);
        Toast.makeText(getApplicationContext(), R.string.error_mod_car_fail, Toast.LENGTH_SHORT).show();
    }

    private void onDeleteCarClick() {
        String msg = getString(R.string.check_network_error);
        String btnText = getString(R.string.confirm_text);
        boolean isNetwork = CommonFunc.checkNetworkStatus(CarInfoModifyActivity.this, msg, btnText);
        if (isNetwork) {
            isMode = true;
            car_mod_progress_layout.setVisibility(View.VISIBLE);
            //서버에 등록
            String[][] params = new String[][]{
                    {"car_id", car_id},
                    {"number", mod_number_text.getText().toString()},
                    {"user_id", String.valueOf(MyUtils.my_id)}
            };
            CommonFunc.sendParamData(params);
            WebHttpConnect.onCarDeleteRequest();
        }
    }
    public void onSuccessDeleteCar() {
        isMode = false;
        car_mod_progress_layout.setVisibility(View.GONE);
        CarInfoTable.deleteCarInfoTable(MyUtils.sel_car_id);
        CarInfoTable.getCarInfoTable();

        onLRChangeLayout(CarInfoModifyActivity.this, MyInfoActivity.class);
        finish();
    }
    public void onFailedDeleteCar() {
        isMode = false;
        car_mod_progress_layout.setVisibility(View.GONE);
        Toast.makeText(getApplicationContext(), R.string.error_del_car_fail, Toast.LENGTH_SHORT).show();
    }

    private void onModCarInfoPrevClick(){
        if (!isMode) {
            onLRChangeLayout(CarInfoModifyActivity.this, MyInfoActivity.class);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (!isMode) {
            onLRChangeLayout(CarInfoModifyActivity.this, MyInfoActivity.class);
            finish();
        } else {
            return;
        }
        super.onBackPressed();
    }
}