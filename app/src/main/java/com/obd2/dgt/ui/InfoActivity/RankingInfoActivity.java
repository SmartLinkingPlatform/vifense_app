package com.obd2.dgt.ui.InfoActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.obd2.dgt.R;
import com.obd2.dgt.ui.AppBaseActivity;
import com.obd2.dgt.ui.ListAdapter.LinkMethod.MethodAdapter;
import com.obd2.dgt.ui.ListAdapter.LinkMethod.MethodItem;
import com.obd2.dgt.ui.ListAdapter.ScoringList.ScoringAdapter;
import com.obd2.dgt.ui.ListAdapter.ScoringList.ScoringItem;
import com.obd2.dgt.ui.MainActivity;
import com.obd2.dgt.utils.MyUtils;

import java.util.ArrayList;

public class RankingInfoActivity extends AppBaseActivity {
    Spinner ranking_date_spinner;
    TextView ranking_mileage_text;
    TextView ranking_safety_text;
    TextView ranking_detail_mileage_text;
    TextView ranking_detail_avrspeed_text;
    TextView ranking_detail_drivingtime_text;
    TextView ranking_detail_fastspeed_text;
    TextView ranking_detail_suddenspeed_text;
    TextView ranking_detail_braking_text;
    TextView yesterday_value_text;
    TextView today_value_text;
    RecyclerView ranking_safety_recycle_view;
    ImageView ranking_prev_btn;
    ArrayList<ScoringItem> scoringItems = new ArrayList<>();
    ScoringAdapter scoringAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        initLayout();
    }

    private void initLayout() {
        ranking_date_spinner = findViewById(R.id.ranking_date_spinner);
        ranking_mileage_text = findViewById(R.id.ranking_mileage_text);
        ranking_safety_text = findViewById(R.id.ranking_safety_text);
        ranking_detail_mileage_text = findViewById(R.id.ranking_detail_mileage_text);
        ranking_detail_avrspeed_text = findViewById(R.id.ranking_detail_avrspeed_text);
        ranking_detail_drivingtime_text = findViewById(R.id.ranking_detail_drivingtime_text);
        ranking_detail_fastspeed_text = findViewById(R.id.ranking_detail_fastspeed_text);
        ranking_detail_suddenspeed_text = findViewById(R.id.ranking_detail_suddenspeed_text);
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
        onLRChangeLayount(RankingInfoActivity.this, MainActivity.class);
        finish();
    }

    @Override
    public void onBackPressed() {
        onLRChangeLayount(RankingInfoActivity.this, MainActivity.class);
        finish();
    }
}