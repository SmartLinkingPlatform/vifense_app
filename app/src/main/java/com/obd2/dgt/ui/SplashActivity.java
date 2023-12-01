package com.obd2.dgt.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.obd2.dgt.dbManage.DBConnect;
import com.obd2.dgt.R;
import com.obd2.dgt.utils.MyUtils;

public class SplashActivity extends AppBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //read db - AppStatus table
        DBConnect m_DBCon = new DBConnect(this.getContext());
        m_DBCon.createDatabase();
        MyUtils.db_connect = m_DBCon;
        try {
            m_DBCon.getDatabase();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        }, 2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}