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
import com.obd2.dgt.dbManage.TableInfo.MyInfoTable;
import com.obd2.dgt.ui.AppBaseActivity;
import com.obd2.dgt.ui.ListAdapter.CarList.CarAdapter;
import com.obd2.dgt.ui.ListAdapter.CarList.CarItem;
import com.obd2.dgt.ui.ListAdapter.MainList.MainListAdapter;
import com.obd2.dgt.ui.ListAdapter.MainList.MainListItem;
import com.obd2.dgt.ui.ListAdapter.MessageList.MessageAdapter;
import com.obd2.dgt.ui.MainActivity;
import com.obd2.dgt.utils.MyUtils;

import java.util.ArrayList;

public class MyInfoActivity extends AppBaseActivity {

    TextView my_info_id, my_info_name, my_info_company;
    ImageView user_info_mod_btn, my_info_prev_btn;
    ImageView user_info_img;
    FrameLayout my_info_framelayout, my_info_add_layout;
    ImageView my_info_reg_btn, my_info_car_add_btn;
    RecyclerView my_info_car_recycle_view;
    CarAdapter carAdapter;
    ArrayList<CarItem> carItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);

        MyInfoTable.getMyInfoTable();
        if (MyUtils.carInfo.size() == 0) {
            CarInfoTable.getCarInfoTable();
        }

        initLayout();
    }

    private void initLayout() {
        my_info_id = findViewById(R.id.my_info_id);
        my_info_id.setText(MyUtils.my_id);

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
                item = new CarItem(MyUtils.carInfo.get(i)[0], MyUtils.carInfo.get(i)[2], MyUtils.carInfo.get(i)[4], MyUtils.carInfo.get(i)[3], MyUtils.carInfo.get(i)[6]);
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
    //차량추가
    private void onMyInfoAddCarClick(){
        onRLChangeLayount(MyInfoActivity.this, CarInfoActivity.class);
        finish();
    }

    @Override
    public void onBackPressed() {
        onLRChangeLayount(MyInfoActivity.this, MainActivity.class);
        finish();
    }

}