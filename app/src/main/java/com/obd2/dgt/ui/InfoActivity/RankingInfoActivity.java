package com.obd2.dgt.ui.InfoActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.obd2.dgt.R;
import com.obd2.dgt.network.WebHttpConnect;
import com.obd2.dgt.ui.AppBaseActivity;
import com.obd2.dgt.ui.CustomSpinner.CustomSpinnerAdapter;
import com.obd2.dgt.ui.ListAdapter.LinkMethod.MethodAdapter;
import com.obd2.dgt.ui.ListAdapter.LinkMethod.MethodItem;
import com.obd2.dgt.ui.ListAdapter.ScoringList.ScoringAdapter;
import com.obd2.dgt.ui.ListAdapter.ScoringList.ScoringItem;
import com.obd2.dgt.ui.MainActivity;
import com.obd2.dgt.ui.MainListActivity.RecordActivity;
import com.obd2.dgt.utils.CommonFunc;
import com.obd2.dgt.utils.MyUtils;

import java.time.LocalDate;
import java.util.ArrayList;

public class RankingInfoActivity extends AppBaseActivity {
    Spinner ranking_date_spinner;
    TextView ranking_mileage_text;
    TextView ranking_safety_text;
    TextView ranking_detail_mileage_text;
    TextView ranking_detail_avrspeed_text;
    TextView ranking_detail_drivingtime_text;
    TextView ranking_detail_fastspeed_text;
    TextView ranking_detail_quick_text;
    TextView ranking_detail_braking_text;
    TextView yesterday_value_text;
    TextView today_value_text;
    RecyclerView ranking_safety_recycle_view;
    ImageView ranking_prev_btn;
    ArrayList<ScoringItem> scoringItems = new ArrayList<>();
    ScoringAdapter scoringAdapter;
    int sel_date = 0;
    String mileage_score = "0";
    String safety_score = "0";
    String total_mileage = "0";
    String avr_speed = "0";
    String fast = "0";
    String quick = "0";
    String brake = "0";
    String total_time = "";
    private static RankingInfoActivity instance;
    public static RankingInfoActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);
        instance = this;

        initLayout();
        requestDrivingRankingInfo();
    }

    private void requestDrivingRankingInfo() {
        String msg = getString(R.string.check_network_error);
        String btnText = getString(R.string.confirm_text);
        boolean isNetwork = CommonFunc.checkNetworkStatus(RankingInfoActivity.this, msg, btnText);
        if (isNetwork) {
            String driving_date = CommonFunc.getDate();
            //서버에 등록
            String[][] params = new String[][]{
                    {"car_id", String.valueOf(MyUtils.car_id)},
                    {"user_id", String.valueOf(MyUtils.my_id)},
                    {"driving_date", driving_date}
            };
            CommonFunc.sendParamData(params);
            WebHttpConnect.onDrivingRankingRequest();
        }
    }

    public void setRankingValues(String[] values) {
        mileage_score = values[0] + getString(R.string.unit_score);
        safety_score = values[1] + getString(R.string.unit_score);
        total_mileage = values[2] + getString(R.string.unit_4);
        avr_speed = values[3] + getString(R.string.unit_4);
        fast = values[5] + getString(R.string.unit_count);;
        quick = values[6] + getString(R.string.unit_count);;
        brake = values[7] + getString(R.string.unit_count);;
        int time = Integer.parseInt(values[4]);
        String m_hTime = CommonFunc.getHour(time, getString(R.string.unit_hour));
        String m_mTime = CommonFunc.getMinuteAndSecond(time % 3600, getString(R.string.unit_minute), getString(R.string.unit_second));
        total_time = m_hTime + m_mTime;

        MyUtils.mileage_score = values[0];
        MyUtils.safety_score = values[1];

        ranking_mileage_text.setText(mileage_score);
        ranking_safety_text.setText(safety_score);
        ranking_detail_mileage_text.setText(total_mileage);
        ranking_detail_avrspeed_text.setText(avr_speed);
        ranking_detail_drivingtime_text.setText(total_time);
        ranking_detail_fastspeed_text.setText(fast);
        ranking_detail_quick_text.setText(quick);
        ranking_detail_braking_text.setText(brake);
    }

    private void initLayout() {
        LocalDate now = null;
        int year = 0;
        int month = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            now = LocalDate.now();
            year = now.getYear();
            month = now.getMonthValue();
        }
        ArrayList<String> selDates = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            String nowDate = year + getString(R.string.unit_year) + " " + i + getString(R.string.unit_month);
            selDates.add(nowDate);
            if (i == month) {
                sel_date = i - 1;
            }
        }
        ranking_date_spinner = findViewById(R.id.ranking_date_spinner);
        CustomSpinnerAdapter dt_adapter = new CustomSpinnerAdapter(this, android.R.layout.simple_spinner_dropdown_item, selDates);
        ranking_date_spinner.setAdapter(dt_adapter);
        ranking_date_spinner.setSelection(sel_date);
        ranking_date_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                dt_adapter.setSelectedPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // 아무 것도 선택되지 않았을 때의 동작
            }
        });

        ranking_mileage_text = findViewById(R.id.ranking_mileage_text);
        ranking_safety_text = findViewById(R.id.ranking_safety_text);
        ranking_detail_mileage_text = findViewById(R.id.ranking_detail_mileage_text);
        ranking_detail_avrspeed_text = findViewById(R.id.ranking_detail_avrspeed_text);
        ranking_detail_drivingtime_text = findViewById(R.id.ranking_detail_drivingtime_text);
        ranking_detail_fastspeed_text = findViewById(R.id.ranking_detail_fastspeed_text);
        ranking_detail_quick_text = findViewById(R.id.ranking_detail_quick_text);
        ranking_detail_braking_text = findViewById(R.id.ranking_detail_braking_text);
        yesterday_value_text = findViewById(R.id.yesterday_value_text);
        today_value_text = findViewById(R.id.today_value_text);
        ranking_prev_btn = findViewById(R.id.ranking_prev_btn);
        ranking_prev_btn.setOnClickListener(view -> onRankingPrevClick());

        ranking_safety_recycle_view = findViewById(R.id.ranking_safety_recycle_view);
        LinearLayoutManager verticalLayoutManager1
                = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        ranking_safety_recycle_view.setLayoutManager(verticalLayoutManager1);
        ScoringItem item;
        for (int i = 0; i < MyUtils.safe_driving_scorings.length; i ++) {
            int index = i + 1;
            item = new ScoringItem(String.valueOf(index), getString(MyUtils.safe_driving_scorings[i]));
            scoringItems.add(item);
        }
        scoringAdapter = new ScoringAdapter(getContext(), scoringItems);
        ranking_safety_recycle_view.setAdapter(scoringAdapter);
    }

    private void onRankingPrevClick(){
        MainActivity.getInstance().showRankingInfo();
        onLRChangeLayount(RankingInfoActivity.this, MainActivity.class);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MainActivity.getInstance().showRankingInfo();
        onLRChangeLayount(RankingInfoActivity.this, MainActivity.class);
        finish();
    }
}