package com.obd2.dgt.ui.InfoActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.obd2.dgt.R;
import com.obd2.dgt.dbManage.TableInfo.CarInfoTable;
import com.obd2.dgt.ui.AppBaseActivity;
import com.obd2.dgt.ui.ListAdapter.CarList.CarAdapter;
import com.obd2.dgt.ui.ListAdapter.CarList.CarItem;
import com.obd2.dgt.ui.MainActivity;
import com.obd2.dgt.utils.MyUtils;

import java.util.ArrayList;

public class MyInfoActivity extends AppBaseActivity {

    TextView my_info_phone, my_info_name, my_info_company;
    ImageView user_info_mod_btn, my_info_prev_btn;
    ImageView user_info_img;
    FrameLayout my_info_framelayout, my_info_add_layout;
    ImageView my_info_reg_btn;
    ImageView my_info_car_add_btn;
    RecyclerView my_info_car_recycle_view;
    CarAdapter carAdapter;
    ArrayList<CarItem> carItems = new ArrayList<>();
    private static MyInfoActivity instance;
    public static MyInfoActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);
        instance = this;

        if (MyUtils.carInfo.size() == 0) {
            CarInfoTable.getCarInfoTable();
        }

        initLayout();
    }

    private void initLayout() {
        my_info_phone = findViewById(R.id.my_info_phone);
        my_info_phone.setText(MyUtils.my_phone);

        my_info_name = findViewById(R.id.my_info_name);
        my_info_name.setText(MyUtils.my_name);

        my_info_company = findViewById(R.id.my_info_company);
        my_info_company.setText(MyUtils.my_company);

        user_info_mod_btn = findViewById(R.id.user_info_mod_btn);
        user_info_mod_btn.setOnClickListener(view -> onMyInfoModifyClick());

        my_info_prev_btn = findViewById(R.id.my_info_prev_btn);
        my_info_prev_btn.setOnClickListener(view -> onMyInfoPrevClick());

        user_info_img = findViewById(R.id.user_info_img);

        my_info_framelayout = findViewById(R.id.my_info_framelayout);
        my_info_add_layout = findViewById(R.id.my_info_add_layout);

        if (MyUtils.carInfo.size() == 0) {
            my_info_framelayout.setVisibility(View.VISIBLE);
            my_info_add_layout.setVisibility(View.GONE);

            my_info_reg_btn = findViewById(R.id.my_info_reg_btn);
            my_info_reg_btn.setOnClickListener(view -> onMyInfoRegisterCarClick());
        } else {
            my_info_framelayout.setVisibility(View.GONE);
            my_info_add_layout.setVisibility(View.VISIBLE);

            my_info_car_recycle_view = findViewById(R.id.my_info_car_recycle_view);
            LinearLayoutManager verticalLayoutManager
                    = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            my_info_car_recycle_view.setLayoutManager(verticalLayoutManager);
            CarItem item;
            for (int i = 0; i < MyUtils.carInfo.size(); i++) {
                String id = MyUtils.carInfo.get(i)[0];
                String c_num = MyUtils.carInfo.get(i)[1];
                String manufacturer = getString(MyUtils.company_names[Integer.parseInt(MyUtils.carInfo.get(i)[2])]);
                String model = getString(MyUtils.model_names[Integer.parseInt(MyUtils.carInfo.get(i)[3])]);
                String number = MyUtils.carInfo.get(i)[5];
                String year = MyUtils.create_years.get(Integer.parseInt(MyUtils.carInfo.get(i)[4])) + getString(R.string.year_unit);
                String fuel = getString(MyUtils.fuel_types[Integer.parseInt(MyUtils.carInfo.get(i)[6])]);
                String gas = MyUtils.carInfo.get(i)[7] + getString(R.string.gas_unit);
                item = new CarItem(id, c_num, manufacturer, model, number, year, fuel, gas);
                carItems.add(item);
            }
            carAdapter = new CarAdapter(getContext(), carItems, carListListener);
            my_info_car_recycle_view.setAdapter(carAdapter);

            my_info_car_add_btn = findViewById(R.id.my_info_car_add_btn);
            my_info_car_add_btn.setOnClickListener(view -> onMyInfoAddCarClick());
        }
    }
    private CarAdapter.ItemClickListener carListListener = new CarAdapter.ItemClickListener() {
        @SuppressLint({"ResourceAsColor", "NotifyDataSetChanged"})
        @Override
        public void onItemClick(View v, int position) {
            MyUtils.sel_car_id = position;
        }
    };

    private void onMyInfoPrevClick(){
        onLRChangeLayount(MyInfoActivity.this, MainActivity.class);
        finish();
    }
    //내 정보 수정
    private void onMyInfoModifyClick(){
        onRLChangeLayount(MyInfoActivity.this, MyInfoModifyActivity.class);
        finish();
    }
    //차량등록하기
    private void onMyInfoRegisterCarClick(){
        onRLChangeLayount(MyInfoActivity.this, CarInfoActivity.class);
        finish();
    }
    //차량 추가
    private void onMyInfoAddCarClick(){
        onRLChangeLayount(MyInfoActivity.this, CarInfoActivity.class);
        finish();
    }
    //차량 정보 수정
    public void onCarModifyClick(int id){
        MyUtils.sel_car_id = id;
        onRLChangeLayount(MyInfoActivity.this, CarInfoModifyActivity.class);
        finish();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

}