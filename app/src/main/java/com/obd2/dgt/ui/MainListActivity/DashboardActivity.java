package com.obd2.dgt.ui.MainListActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.obd2.dgt.dbManage.TableInfo.GaugeInfoTable;
import com.obd2.dgt.R;
import com.obd2.dgt.ui.AppBaseActivity;
import com.obd2.dgt.ui.ListAdapter.AddGauge.GaugeAdapter;
import com.obd2.dgt.ui.ListAdapter.AddGauge.GaugeItem;
import com.obd2.dgt.ui.MainActivity;
import com.obd2.dgt.utils.CommonFunc;
import com.obd2.dgt.utils.GaugeViewInfo;
import com.obd2.dgt.utils.MyUtils;

import java.util.ArrayList;

public class DashboardActivity extends AppBaseActivity {
    ImageView dash_prev_btn, dash_page_navigation_btn;
    FrameLayout gauge_frame_layout;
    RelativeLayout dash_layout;
    FrameLayout gauge_speed_layout, gauge_engine_layout, gauge_rpm_layout, gauge_mileage_layout;
    FrameLayout gauge_real_fuel_layout, gauge_fuel_layout, gauge_temp_layout, gauge_battery_layout, gauge_dtime_layout;
    FrameLayout gauge_add_layout;
    ImageView gauge_speed_img, gauge_engine_img, gauge_rpm_img;
    TextView gauge_speed_text, gauge_engine_text, gauge_rpm_text, gauge_mileage_text;
    TextView gauge_real_fuel_text, gauge_fuel_text, gauge_temp_text, gauge_battery_text, gauge_dtime_text;
    TextView mileage_date;
    ImageView speed_close_btn, engine_close_btn, rpm_close_btn, mileage_close_btn;
    ImageView real_fuel_close_btn, fuel_close_btn, temp_close_btn, battery_close_btn, dtime_close_btn;
    ImageView gauge_add_btn;
    TextView guage_navigation_text;
    Context mContext;
    Dialog addDialog;
    RecyclerView add_gauge_recycle_view;
    ImageView add_gauge_btn;
    ArrayList<GaugeItem> addItems = new ArrayList<>();
    GaugeAdapter gaugeAdapter;
    GaugeViewInfo touchViewInfo = null;
    GaugeViewInfo movingViewInfo = null;
    int touch_index = -1;
    int over_index = -1;
    float dX, dY;
    ArrayList<GaugeViewInfo> viewInfo = new ArrayList<>();
    int[] dash_layout_location = new int[2];
    boolean isShow = false;
    boolean isAddGauge = false;
    boolean isShortClicking = false;
    boolean isLongClicking = false;
    private static final long LONG_CLICK_THRESHOLD = 1000;
    private final Handler longClickHandler = new Handler(Looper.getMainLooper());
    Dialog loadingDialog;
    Dialog errDialog;
    ImageView dlg_warning_img;

    private GestureDetector gestureDetector;

    @SuppressLint("StaticFieldLeak")
    private static DashboardActivity instance;
    public static DashboardActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash);
        instance = this;

        mContext = getContext();
        viewInfo = new ArrayList<>();
        MyUtils.gaugeInfo = new ArrayList<>();
        MyUtils.showGauge = true;

        initLayout();
        RefreshGaugeLayout();

        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                // 뒤로가기 제스처를 막기 위해 항상 true를 반환합니다.
                return true;
            }
        });
    }

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    private void initLayout() {
        gauge_frame_layout = findViewById(R.id.gauge_frame_layout);
        gauge_frame_layout.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                hiddenCloseButton();
            }
            return false;
        });

        dash_prev_btn = findViewById(R.id.dash_prev_btn);
        dash_prev_btn.setOnClickListener(view -> onPrevActivityClick());

        dash_page_navigation_btn = findViewById(R.id.dash_page_navigation_btn);
        dash_page_navigation_btn.setOnClickListener(view -> onDashboardNavigationClick());
        guage_navigation_text = findViewById(R.id.guage_navigation_text);

        dash_layout = findViewById(R.id.dash_layout);

        int dp_width = (int) (170 * 3);
        int dp_height = (int) (180 * 3);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(dp_width, dp_height);

        //속도 계기
        gauge_speed_layout = findViewById(R.id.gauge_speed_layout);
        gauge_speed_layout.setLayoutParams(params);
        gauge_speed_layout.setOnTouchListener(onTouchEventListener);
        gauge_speed_layout.setVisibility(View.GONE);
        gauge_speed_img = findViewById(R.id.gauge_speed_img);
        gauge_speed_img.setRotation(getRotationValueI(Float.parseFloat(MyUtils.ecu_vehicle_speed), 300));
        gauge_speed_text = findViewById(R.id.gauge_speed_text);
        gauge_speed_text.setText("-");
        speed_close_btn = findViewById(R.id.speed_close_btn);
        speed_close_btn.setOnClickListener(view -> onGaugeCloseClick(gauge_speed_layout));
        speed_close_btn.setVisibility(View.GONE);

        //엔진 부하
        gauge_engine_layout = findViewById(R.id.gauge_engine_layout);
        gauge_engine_layout.setLayoutParams(params);
        gauge_engine_layout.setOnTouchListener(onTouchEventListener);
        gauge_engine_layout.setVisibility(View.GONE);
        gauge_engine_img = findViewById(R.id.gauge_engine_img);
        gauge_engine_img.setRotation(getRotationValueI(Integer.parseInt(MyUtils.ecu_engine_load), 100));
        gauge_engine_text = findViewById(R.id.gauge_engine_text);
        gauge_engine_text.setText("-");
        engine_close_btn = findViewById(R.id.engine_close_btn);
        engine_close_btn.setOnClickListener(view -> onGaugeCloseClick(gauge_engine_layout));
        engine_close_btn.setVisibility(View.GONE);

        //RPM
        gauge_rpm_layout = findViewById(R.id.gauge_rpm_layout);
        gauge_rpm_layout.setLayoutParams(params);
        gauge_rpm_layout.setOnTouchListener(onTouchEventListener);
        gauge_rpm_layout.setVisibility(View.GONE);
        gauge_rpm_img = findViewById(R.id.gauge_rpm_img);
        gauge_rpm_img.setRotation(getRotationValueI(Float.parseFloat(MyUtils.ecu_engine_rpm), 16400));
        gauge_rpm_text = findViewById(R.id.gauge_rpm_text);
        gauge_rpm_text.setText("-");
        rpm_close_btn = findViewById(R.id.rpm_close_btn);
        rpm_close_btn.setOnClickListener(view -> onGaugeCloseClick(gauge_rpm_layout));
        rpm_close_btn.setVisibility(View.GONE);

        //주행 거리
        gauge_mileage_layout = findViewById(R.id.gauge_mileage_layout);
        gauge_mileage_layout.setLayoutParams(params);
        gauge_mileage_layout.setOnTouchListener(onTouchEventListener);
        gauge_mileage_layout.setVisibility(View.GONE);
        mileage_date = findViewById(R.id.mileage_date);
        mileage_date.setText(CommonFunc.getCurrentDate() + "(" + getString(CommonFunc.getCurrentWeek()) + ")");
        gauge_mileage_text = findViewById(R.id.gauge_mileage_text);
        gauge_mileage_text.setText("-");
        mileage_close_btn = findViewById(R.id.mileage_close_btn);
        mileage_close_btn.setOnClickListener(view -> onGaugeCloseClick(gauge_mileage_layout));
        mileage_close_btn.setVisibility(View.GONE);

        //순간 연료 소모량
        gauge_real_fuel_layout = findViewById(R.id.gauge_real_fuel_layout);
        gauge_real_fuel_layout.setLayoutParams(params);
        gauge_real_fuel_layout.setOnTouchListener(onTouchEventListener);
        gauge_real_fuel_layout.setVisibility(View.GONE);
        gauge_real_fuel_text = findViewById(R.id.gauge_real_fuel_text);
        gauge_real_fuel_text.setText("-");
        real_fuel_close_btn = findViewById(R.id.real_fuel_close_btn);
        real_fuel_close_btn.setOnClickListener(view -> onGaugeCloseClick(gauge_real_fuel_layout));
        real_fuel_close_btn.setVisibility(View.GONE);

        //연료 소모량
        gauge_fuel_layout = findViewById(R.id.gauge_fuel_layout);
        gauge_fuel_layout.setLayoutParams(params);
        gauge_fuel_layout.setOnTouchListener(onTouchEventListener);
        gauge_fuel_layout.setVisibility(View.GONE);
        gauge_fuel_text = findViewById(R.id.gauge_fuel_text);
        gauge_fuel_text.setText("-");
        fuel_close_btn = findViewById(R.id.fuel_close_btn);
        fuel_close_btn.setOnClickListener(view -> onGaugeCloseClick(gauge_fuel_layout));
        fuel_close_btn.setVisibility(View.GONE);

        //냉각수 온도
        gauge_temp_layout = findViewById(R.id.gauge_temp_layout);
        gauge_temp_layout.setLayoutParams(params);
        gauge_temp_layout.setOnTouchListener(onTouchEventListener);
        gauge_temp_layout.setVisibility(View.GONE);
        gauge_temp_text = findViewById(R.id.gauge_temp_text);
        gauge_temp_text.setText("-");
        temp_close_btn = findViewById(R.id.temp_close_btn);
        temp_close_btn.setOnClickListener(view -> onGaugeCloseClick(gauge_temp_layout));
        temp_close_btn.setVisibility(View.GONE);

        //배터리 전압
        gauge_battery_layout = findViewById(R.id.gauge_battery_layout);
        gauge_battery_layout.setLayoutParams(params);
        gauge_battery_layout.setOnTouchListener(onTouchEventListener);
        gauge_battery_layout.setVisibility(View.GONE);
        gauge_battery_text = findViewById(R.id.gauge_battery_text);
        gauge_battery_text.setText("-");
        battery_close_btn = findViewById(R.id.battery_close_btn);
        battery_close_btn.setOnClickListener(view -> onGaugeCloseClick(gauge_battery_layout));
        battery_close_btn.setVisibility(View.GONE);

        //주행 시간
        gauge_dtime_layout = findViewById(R.id.gauge_dtime_layout);
        gauge_dtime_layout.setLayoutParams(params);
        gauge_dtime_layout.setOnTouchListener(onTouchEventListener);
        gauge_dtime_layout.setVisibility(View.GONE);
        gauge_dtime_text = findViewById(R.id.gauge_dtime_text);
        gauge_dtime_text.setText("--:--");
        dtime_close_btn = findViewById(R.id.dtime_close_btn);
        dtime_close_btn.setOnClickListener(view -> onGaugeCloseClick(gauge_dtime_layout));
        dtime_close_btn.setVisibility(View.GONE);

        //계기 추가 버튼
        gauge_add_layout = findViewById(R.id.gauge_add_layout);
        gauge_add_layout.setLayoutParams(params);
        gauge_add_layout.setVisibility(View.GONE);
        gauge_add_btn = findViewById(R.id.gauge_add_btn);
        gauge_add_btn.setBackgroundResource(R.drawable.gauge_add_state);
        gauge_add_btn.setOnClickListener(view -> onGaugeAddClick());

        startDashboardGauge();

        showErrorDialog();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // GestureDetector를 사용하여 터치 이벤트를 전달합니다.
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event);
    }
    private void initGaugeItemInfo() {
        View gauge_view = null;

        int pos_index = 0;
        int page_status = 0;
        if (is_nav) {
            page_status = nav_index;
        }
        int sIndex = 6 * page_status;
        int endIndex = sIndex + 6;
        for (int i = sIndex; i < endIndex; i++){
            if(i >= MyUtils.gaugeInfo.size())
                break;
            String[] info = MyUtils.gaugeInfo.get(i);

            if (Integer.parseInt(info[0]) == 1) {
                gauge_view = gauge_speed_layout;
            }
            if (Integer.parseInt(info[0]) == 2) {
                gauge_view = gauge_engine_layout;
            }
            if (Integer.parseInt(info[0]) == 3) {
                gauge_view = gauge_rpm_layout;
            }
            if (Integer.parseInt(info[0]) == 4) {
                gauge_view = gauge_mileage_layout;
            }
            if (Integer.parseInt(info[0]) == 5) {
                gauge_view = gauge_real_fuel_layout;
            }
            if (Integer.parseInt(info[0]) == 6) {
                gauge_view = gauge_fuel_layout;
            }
            if (Integer.parseInt(info[0]) == 7) {
                gauge_view = gauge_temp_layout;
            }
            if (Integer.parseInt(info[0]) == 8) {
                gauge_view = gauge_battery_layout;
            }
            if (Integer.parseInt(info[0]) == 9) {
                gauge_view = gauge_dtime_layout;
            }
            if (gauge_view != null) {
                if (Integer.parseInt(info[2]) == 1) {
                    PointF pointF = new PointF(setScaleX(MyUtils.gauge_pos[pos_index].x), setScaleY(MyUtils.gauge_pos[pos_index].y));
                    gauge_view.setX(pointF.x);
                    gauge_view.setY(pointF.y);
                    gauge_view.setVisibility(View.VISIBLE);
                    GaugeViewInfo gaugeViewInfo = new GaugeViewInfo(gauge_view, Integer.parseInt(info[3]), info[1], pointF, Integer.parseInt(info[0]), Integer.parseInt(info[2]));
                    viewInfo.add(gaugeViewInfo);
                } else {
                    gauge_view.setVisibility(View.GONE);
                }
            }
            pos_index++;
        }
        Log.d("Dashboard Gauge", "GaugeCount=" + viewInfo.size());
    }

    //계기 추가 버튼 보기
    private void showAddButton() {
        int show_cnt = viewInfo.size();
        //한 화면에 다 보일때
        if (show_cnt < 6) {
            PointF pointF = new PointF(setScaleX(MyUtils.gauge_pos[show_cnt].x), setScaleY(MyUtils.gauge_pos[show_cnt].y));
            gauge_add_layout.setX(pointF.x);
            gauge_add_layout.setY(pointF.y);
            gauge_add_layout.setVisibility(View.VISIBLE);
            int hiddenGaugeCnt = GaugeInfoTable.getGaugeDisableCount();
            if(hiddenGaugeCnt == 0) {
                gauge_add_btn.setBackgroundResource(R.drawable.gauge_add_disable);
                isAddGauge = false;
            } else {
                gauge_add_btn.setBackgroundResource(R.drawable.gauge_add_state);
                isAddGauge = true;
            }
        } else {
            gauge_add_layout.setVisibility(View.GONE);
        }

    }

    private void clearAllGaugeLayout() {
        if (viewInfo.size() > 0) {
            for (int i = 0; i < viewInfo.size(); i++){
                viewInfo.get(i).layout.setVisibility(View.GONE);
            }
        }
        viewInfo = new ArrayList<>();
    }
    //page refresh
    private void RefreshGaugeLayout() {
        addItems = new ArrayList<>();
        clearAllGaugeLayout();
        int gauge_cnt = GaugeInfoTable.getGaugeEnableCount();
        if (gauge_cnt >= 6) {
            is_nav = true;
        } else {
            is_nav = false;
        }
        GaugeInfoTable.getGaugeOrderTable();
        initGaugeItemInfo();
        showAddButton();
        showPageNavigationButton();
    }
    int nav_index = 0;
    boolean is_nav = false;
    @SuppressLint("SetTextI18n")
    private void showPageNavigationButton() {
        if (is_nav) {
            if (nav_index == 0) {
                dash_page_navigation_btn.setImageResource(R.drawable.button);
                guage_navigation_text.setText(getString(R.string.next) + "   >");
            } else {
                dash_page_navigation_btn.setImageResource(R.drawable.button);
                guage_navigation_text.setText("<   " + getString(R.string.previous));
            }
        } else {
            dash_page_navigation_btn.setImageResource(R.drawable.button_disable);
            guage_navigation_text.setText(R.string.next);
        }
    }
    //page navigation button click
    private void onDashboardNavigationClick() {
        if(is_nav) {
            if(nav_index == 0) {
                nav_index = 1;
            } else {
                nav_index = 0;
            }
            hiddenCloseButton();
            RefreshGaugeLayout();
        } else {
            nav_index = 0;
        }
    }

    //계기 닫기 버튼
    private void onGaugeCloseClick(View view) {
        int i = 0;
        view.setVisibility(View.GONE);
        for (GaugeViewInfo gInfo : viewInfo) {
            if (view.getId() == gInfo.layout.getId()) {
                viewInfo.get(i).setGaugeStatus(2);
                GaugeInfoTable.updateGaugeTable(viewInfo.get(i).id, 2);
                RefreshGaugeLayout();
                break;
            }
            i++;
        }
    }

    private void onLongTouchEvent() {
        speed_close_btn.setVisibility(View.VISIBLE);
        engine_close_btn.setVisibility(View.VISIBLE);
        rpm_close_btn.setVisibility(View.VISIBLE);
        mileage_close_btn.setVisibility(View.VISIBLE);
        real_fuel_close_btn.setVisibility(View.VISIBLE);
        fuel_close_btn.setVisibility(View.VISIBLE);
        temp_close_btn.setVisibility(View.VISIBLE);
        battery_close_btn.setVisibility(View.VISIBLE);
        dtime_close_btn.setVisibility(View.VISIBLE);
    }

    private void hiddenCloseButton() {
        speed_close_btn.setVisibility(View.GONE);
        engine_close_btn.setVisibility(View.GONE);
        rpm_close_btn.setVisibility(View.GONE);
        mileage_close_btn.setVisibility(View.GONE);
        real_fuel_close_btn.setVisibility(View.GONE);
        fuel_close_btn.setVisibility(View.GONE);
        temp_close_btn.setVisibility(View.GONE);
        battery_close_btn.setVisibility(View.GONE);
        dtime_close_btn.setVisibility(View.GONE);
    }

    private final View.OnTouchListener onTouchEventListener = new View.OnTouchListener() {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touch_index = -1;
                    over_index = -1;
                    dX = view.getX() - event.getRawX();
                    dY = view.getY() - event.getRawY();
                    view.setBackgroundColor(mContext.getResources().getColor(R.color.gray_color));
                    view.setAlpha((float) 0.7);
                    view.setZ(10);
                    dash_layout.getLocationOnScreen(dash_layout_location);
                    isShortClicking = true;
                    if (!isLongClicking)
                        longClickHandler.postDelayed(longClickRunnable, LONG_CLICK_THRESHOLD);
                    break;
                case MotionEvent.ACTION_UP:
                    PointF touchPos = new PointF(event.getRawX(), event.getRawY());
                    moveViewPosition(view, touchPos);
                    view.setBackgroundColor(mContext.getResources().getColor(R.color.bg_color));
                    view.setAlpha((float) 1.0);
                    view.setZ(1);
                    setGaugeViewInfo();
                    touchViewInfo = null;
                    movingViewInfo = null;
                    touch_index = -1;
                    over_index = -1;
                    saveGaugeInfo();
                    if (isLongClicking && isShortClicking) {
                        hiddenCloseButton();
                        isLongClicking = false;
                    }
                    longClickHandler.removeCallbacks(longClickRunnable);
                    break;
                case MotionEvent.ACTION_MOVE:
                    PointF pointF = new PointF(event.getRawX() + dX, event.getRawY() + dY);
                    animationView(view, pointF, 0);
                    break;
            }
            return true;
        }
    };

    private final Runnable longClickRunnable = () -> {
        isLongClicking = true;
        isShortClicking = false;
        onLongTouchEvent();
    };

    //터치 무비
    private void animationView(View view, PointF pointF, int duration) {
        view.animate()
                .x(pointF.x)
                .y(pointF.y)
                .setDuration(duration)
                .start();
    }

    //이동한 계기위치
    private void moveViewPosition(View touchView, PointF touchPos) {
        int i = 0;
        int j = 0;
        float tp_x = touchPos.x - dash_layout_location[0];
        float tp_y = touchPos.y - dash_layout_location[1];
        for (GaugeViewInfo gInfo : viewInfo) {
            float x = gInfo.gaugePos.x;
            float y = gInfo.gaugePos.y;
            float w = gInfo.layout.getWidth();
            float h = gInfo.layout.getHeight();
            if (touchView.getId() != gInfo.layout.getId()) {
                if ((tp_x >= x && tp_x < x + w) && (tp_y >= y && tp_y < y + h)) {
                    over_index = i;
                    movingViewInfo = viewInfo.get(i).clone();
                    break;
                }
            }
            i++;
        }
        for (GaugeViewInfo gInfo : viewInfo) {
            if (touchView.getId() == gInfo.layout.getId()) {
                touch_index = j;
                touchViewInfo = viewInfo.get(j).clone();
                break;
            }
            j++;
        }
    }

    //변화된 계기정보 설정 과 이동
    private void setGaugeViewInfo() {
        if (over_index != -1 && touch_index != -1) {
            viewInfo.get(over_index).setGaugePos(touchViewInfo.gaugePos);
            viewInfo.get(over_index).setOrderIndex(touchViewInfo.orderIndex);

            viewInfo.get(touch_index).setGaugePos(movingViewInfo.gaugePos);
            viewInfo.get(touch_index).setOrderIndex(movingViewInfo.orderIndex);
            animationView(movingViewInfo.layout, touchViewInfo.gaugePos, 300);
            animationView(touchViewInfo.layout, movingViewInfo.gaugePos, 100);
        } else {
            animationView(touchViewInfo.layout, touchViewInfo.gaugePos, 100);
        }
    }

    //계기 추가 버튼
    private void onGaugeAddClick() {
        if(isAddGauge) {
            addDialog = new Dialog(DashboardActivity.this);
            addDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            addDialog.setCancelable(false);
            showAddGaugeDialog();
        }
        hiddenCloseButton();
    }
    //계기 추가 Dialog
    public void showAddGaugeDialog() {
        addDialog.setContentView(R.layout.dlg_add_gauge);
        addDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        add_gauge_recycle_view = addDialog.findViewById(R.id.add_gauge_recycle_view);
        LinearLayoutManager verticalLayoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        add_gauge_recycle_view.setLayoutManager(verticalLayoutManager);
        GaugeItem item;
        for (String[] info : MyUtils.gaugeInfo) {
            if (Integer.parseInt(info[2]) == 2) {
                boolean isCheck = false;
                item = new GaugeItem(isCheck, info[0], info[4]);
                addItems.add(item);
            }
        }
        gaugeAdapter = new GaugeAdapter(addDialog.getContext(), addItems, addGaugeListListener);
        add_gauge_recycle_view.setAdapter(gaugeAdapter);

        add_gauge_btn = addDialog.findViewById(R.id.add_gauge_btn);
        add_gauge_btn.setOnClickListener(view -> {
            addDialog.dismiss();
            if(addItemIndex != 0) {
                GaugeInfoTable.updateGaugeTable(addItemIndex, 1);
                hiddenCloseButton();
                RefreshGaugeLayout();
                addItemIndex = 0;
            }
        });

        addDialog.show();
    }
    int addItemIndex = 0;
    private GaugeAdapter.ItemClickListener addGaugeListListener = new GaugeAdapter.ItemClickListener() {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onItemClick(View v, int position) {
            for (int i = 0; i < addItems.size(); i++) {
                if(position == i) {
                    addItems.get(i).selected = true;
                    addItemIndex = Integer.parseInt(addItems.get(i).id);
                } else {
                    addItems.get(i).selected = false;
                }
            }
            gaugeAdapter.notifyDataSetChanged();
        }
    };

    //계기판 정보 디비 저장
    private void saveGaugeInfo() {
        for (GaugeViewInfo gInfo : viewInfo) {
            GaugeInfoTable.updateGaugeTable(gInfo);
        }
    }

    private void onPrevActivityClick() {
        isShow = false;
        MyUtils.showGauge = false;
        onLRChangeLayount(DashboardActivity.this, MainActivity.class);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        isShow = false;
        MyUtils.showGauge = false;
        onLRChangeLayount(DashboardActivity.this, MainActivity.class);
        finish();
    }

    private float getRotationValueF(float val, float max) {
        float angle = 0;
        if (max == 0) {
            angle = (val / 100) * 180;
        } else {
            angle = (val / max) * 180;
        }
        return angle;
    }

    private int getRotationValueI(float val, float max) {
        float angle = 0;
        if (max == 0) {
            angle = (val / 100) * 180;
        } else {
            angle = (val / max) * 180;
        }
        int rot = Math.round(angle);
        return rot;
    }

    public void startDashboardGauge() {
        if (Integer.parseInt(MyUtils.ecu_engine_load) > 0 || Integer.parseInt(MyUtils.ecu_engine_rpm) > 10) {
            if (!isShow) {
                isShow = true;
                runOnUiThread(() -> new GaugeAsyncTask().execute("GaugeInfo"));
            }
        }
    }
    public void stopDashboardGauge() {
        gauge_speed_text.setText("0");
        gauge_engine_text.setText("0");
        gauge_rpm_text.setText("0");
        gauge_real_fuel_text.setText("0.0");
        gauge_battery_text.setText("0");
        isShow = false;
    }

    class GaugeAsyncTask extends AsyncTask<String, Integer, Boolean> {
        @SuppressLint("SetTextI18n")
        protected Boolean doInBackground(String... str) {
            while (isShow) {
                try {
                    if (!MyUtils.con_ECU || !MyUtils.con_OBD) {
                        stopDashboardGauge();
                        isShow = false;
                    }else {
                        runOnUiThread(() -> {
                            if (MyUtils.loaded_data) {
                                //차량 속도
                                gauge_speed_img.setRotation(getRotationValueI(Float.parseFloat(MyUtils.ecu_vehicle_speed), 300));
                                gauge_speed_text.setText(MyUtils.ecu_vehicle_speed);
                                //엔진 부하
                                gauge_engine_img.setRotation(getRotationValueI(Integer.parseInt(MyUtils.ecu_engine_load), 100));
                                gauge_engine_text.setText(MyUtils.ecu_engine_load);
                                //엔진 RPM
                                gauge_rpm_img.setRotation(getRotationValueI(Float.parseFloat(MyUtils.ecu_engine_rpm), 16400));
                                gauge_rpm_text.setText(MyUtils.ecu_engine_rpm);
                                //주행 거리
                                mileage_date.setText(CommonFunc.getCurrentDate() + "(" + getString(CommonFunc.getCurrentWeek()) + ")");
                                gauge_mileage_text.setText(MyUtils.ecu_mileage);
                                //순간 연료 소모량
                                //순간 연료 소모량이 0이면 "PID 0110 - 스로틀 위치" 와 "PID 010D - 연료 압력"으로 계산한다.
                                double consumptionRate = 0;
                                if (Float.parseFloat(MyUtils.ecu_fuel_rate) == 0) {
                                    double mafLPH = Double.parseDouble(MyUtils.ecu_maf);
                                    // Air-Fuel Ratio (가정값, 실제 값으로 대체 가능)
                                    float airFuelRatio = 14.7f;
                                    // Fuel Density (휘발유의 경우, 가정값)
                                    float fuelDensity = 0.74f;  // kg/L
                                    consumptionRate = (mafLPH - Double.parseDouble(MyUtils.ecu_throttle_position)) * fuelDensity / airFuelRatio;
                                    MyUtils.ecu_fuel_rate = String.valueOf(Math.round(consumptionRate * 10) / (float) 10);
                                }
                                gauge_real_fuel_text.setText(MyUtils.ecu_fuel_rate);
                                //연료 소모량
                                gauge_fuel_text.setText(MyUtils.ecu_fuel_consume);
                                //냉각수 온도
                                gauge_temp_text.setText(MyUtils.ecu_coolant_temp);
                                //배터리 전압
                                gauge_battery_text.setText(MyUtils.ecu_battery_voltage);
                                //주행 시간
                                gauge_dtime_text.setText(MyUtils.ecu_driving_time);
                            }
                        });
                        showErrorDialog();
                        SystemClock.sleep(1000);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            GaugeAsyncTask.this.cancel(true);
            return false;
        }
    }

    public void showErrorDialog() {
        if (MyUtils.err_idx > 0) {
            if (!MyUtils.is_error_dlg) {
                runOnUiThread(() -> {
                    MyUtils.is_error_dlg = true;
                    errDialog = new Dialog(DashboardActivity.this);
                    errDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    errDialog.setCancelable(false);
                    errDialog.setContentView(R.layout.dlg_error);
                    errDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    errDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    TextView dlg_error_text = errDialog.findViewById(R.id.dlg_error_text);
                    dlg_warning_img = errDialog.findViewById(R.id.dlg_warning_img);

                    if (MyUtils.err_idx == 1) {
                        dlg_error_text.setText(R.string.show_error_idle);
                    } else if (MyUtils.err_idx == 2) {
                        dlg_error_text.setText(R.string.show_error_fast);
                    } else if (MyUtils.err_idx == 3) {
                        dlg_error_text.setText(R.string.show_error_quick);
                    } else if (MyUtils.err_idx == 4) {
                        dlg_error_text.setText(R.string.show_error_brake);
                    } else if (MyUtils.err_idx == 5) {
                        dlg_error_text.setText(R.string.show_error_system);
                        MyUtils.ecu_trouble_code = "";
                    } else if (MyUtils.err_idx == 6) {
                        dlg_error_text.setText(R.string.show_error_consume);
                        MyUtils.ecu_consume_warning = "";
                    }

                    Handler handler = new Handler();
                    handler.postDelayed(() -> {
                        errDialog.dismiss();
                        MyUtils.is_error_dlg = false;
                        MyUtils.err_idx = 0;
                    }, 2000);

                    errDialog.show();
                });
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyUtils.showGauge = false;
        isShow = false;
        finish();
    }
}