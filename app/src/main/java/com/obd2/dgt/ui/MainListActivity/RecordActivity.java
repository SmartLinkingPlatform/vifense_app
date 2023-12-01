package com.obd2.dgt.ui.MainListActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.obd2.dgt.R;
import com.obd2.dgt.ui.AppBaseActivity;
import com.obd2.dgt.ui.InfoActivity.RankingInfoActivity;
import com.obd2.dgt.ui.MainActivity;
import com.obd2.dgt.utils.MyUtils;

public class RecordActivity extends AppBaseActivity {

    ImageView record_prev_btn;
    RecyclerView record_recycle_view;
    TextView mileage_month_text;
    TextView driving_month_time;
    TextView mileage_today_text;
    TextView driving_today_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        MyUtils.currentActivity = this;

        initLayout();
    }

    private void initLayout() {
        record_recycle_view = findViewById(R.id.record_recycle_view);

        mileage_month_text = findViewById(R.id.mileage_month_text);
        driving_month_time = findViewById(R.id.driving_month_time);
        mileage_today_text = findViewById(R.id.mileage_today_text);
        driving_today_time = findViewById(R.id.driving_today_time);

        record_prev_btn = findViewById(R.id.record_prev_btn);
        record_prev_btn.setOnClickListener(view -> onRecordPrevClick());
    }

    private void onRecordPrevClick(){
        onLRChangeLayount(RecordActivity.this, MainActivity.class);
        finish();
    }

    @Override
    public void onBackPressed() {
        onLRChangeLayount(RecordActivity.this, MainActivity.class);
        finish();
    }
}