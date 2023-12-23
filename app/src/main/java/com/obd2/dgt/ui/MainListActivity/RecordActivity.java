package com.obd2.dgt.ui.MainListActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.obd2.dgt.R;
import com.obd2.dgt.network.WebHttpConnect;
import com.obd2.dgt.ui.AppBaseActivity;
import com.obd2.dgt.ui.ListAdapter.DrivingList.DrivingAdapter;
import com.obd2.dgt.ui.ListAdapter.DrivingList.DrivingItem;
import com.obd2.dgt.ui.MainActivity;
import com.obd2.dgt.utils.CommonFunc;
import com.obd2.dgt.utils.MyUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class RecordActivity extends AppBaseActivity {

    ImageView record_prev_btn;
    RecyclerView record_recycle_view;
    TextView mileage_month_text;
    TextView driving_month_time;
    TextView mileage_today_text;
    TextView driving_today_time;
    LinkedHashMap<String, ArrayList<String[]>> driving_info = new LinkedHashMap<>();
    ArrayList<DrivingItem> drivingItems = new ArrayList<>();
    DrivingAdapter drivingAdapter;

    float total_m_distance = 0;
    float total_d_distance = 0;
    int total_m_time = 0;
    int total_d_time = 0;

    private static RecordActivity instance;
    public static RecordActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        instance = this;

        requestReadDrivingInfo();
    }

    private void requestReadDrivingInfo() {
        String msg = getString(R.string.check_network_error);
        String btnText = getString(R.string.confirm_text);
        boolean isNetwork = CommonFunc.checkNetworkStatus(RecordActivity.this, msg, btnText);
        if (isNetwork) {
            String driving_date = CommonFunc.getDate();
            //서버에 등록
            String[][] params = new String[][]{
                    {"car_id", String.valueOf(MyUtils.car_id)},
                    {"user_id", String.valueOf(MyUtils.my_id)},
                    {"driving_date", driving_date}
            };
            CommonFunc.sendParamData(params);
            WebHttpConnect.onReadDrivingInfoRequest();
        }
    }

    public void onSuccessDrivingInfo(Map<String, ArrayList<JSONObject>> info) {
        //현재 날짜
        String current_date = CommonFunc.getDate();
        float m_distance = 0;
        float d_distance = 0;

        if (info.size() > 0) {
            driving_info = new LinkedHashMap<>();
            ArrayList<String[]> detail_info;
            Set<String> keys = info.keySet();
            for (String key : keys) {
                detail_info = new ArrayList<>();
                ArrayList<JSONObject> drv_detail = info.get(key);
                try {
                    for (JSONObject object : drv_detail) {
                        if (current_date.equals(key)) {
                            d_distance += object.getDouble("mileage");
                            total_d_time += CommonFunc.calculateTime(object.getString("driving_time"));
                        }
                        m_distance += object.getDouble("mileage");
                        total_m_time += CommonFunc.calculateTime(object.getString("driving_time"));

                        String[] detail = new String[6];
                        detail[0] = object.getString("start_time");
                        detail[1] = object.getString("end_time");
                        detail[2] = object.getString("driving_time");
                        detail[3] = object.getString("start_place");
                        detail[4] = object.getString("end_place");
                        detail[5] = object.getString("mileage");
                        detail_info.add(detail);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                driving_info.put(key, detail_info);
            }
        }

        total_d_distance = Math.round(d_distance * 10) / (float) 10 ;
        total_m_distance = Math.round(m_distance * 10) / (float) 10 ;

        initLayout();
    }

    @SuppressLint("SetTextI18n")
    private void initLayout() {
        record_recycle_view = findViewById(R.id.record_recycle_view);
        LinearLayoutManager verticalLayoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        record_recycle_view.setLayoutManager(verticalLayoutManager);
        DrivingItem item;
        Set<String> keys = driving_info.keySet();
        for (String key : keys) {
            ArrayList<String[]> detail = driving_info.get(key);
            int idx = 0;
            for (String[] val : detail) {
                if (idx == 0) {
                    item = new DrivingItem(key, val);
                } else {
                    item = new DrivingItem("", val);
                }
                drivingItems.add(item);
                idx++;
            }

        }
        drivingAdapter = new DrivingAdapter(getContext(), drivingItems);
        record_recycle_view.setAdapter(drivingAdapter);

        mileage_month_text = findViewById(R.id.mileage_month_text);
        String mileage_month = total_m_distance + getString(R.string.unit_4);
        mileage_month_text.setText(mileage_month);

        driving_month_time = findViewById(R.id.driving_month_time);
        String m_hTime = CommonFunc.getHour(total_m_time, getString(R.string.unit_hour));
        String m_mTime = CommonFunc.getMinuteAndSecond(total_m_time % 3600, getString(R.string.unit_minute), getString(R.string.unit_second));
        driving_month_time.setText(m_hTime + m_mTime);

        mileage_today_text = findViewById(R.id.mileage_today_text);
        String mileage_today = total_d_distance + getString(R.string.unit_4);
        mileage_today_text.setText(mileage_today);

        driving_today_time = findViewById(R.id.driving_today_time);
        String d_hTime = CommonFunc.getHour(total_d_time, getString(R.string.unit_hour));
        String d_mTime = CommonFunc.getMinuteAndSecond(total_d_time % 3600, getString(R.string.unit_minute), getString(R.string.unit_second));
        driving_today_time.setText(d_hTime + d_mTime);

        record_prev_btn = findViewById(R.id.record_prev_btn);
        record_prev_btn.setOnClickListener(view -> onRecordPrevClick());
    }

    private void onRecordPrevClick(){
        onLRChangeLayount(RecordActivity.this, MainActivity.class);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onLRChangeLayount(RecordActivity.this, MainActivity.class);
        finish();
    }
}