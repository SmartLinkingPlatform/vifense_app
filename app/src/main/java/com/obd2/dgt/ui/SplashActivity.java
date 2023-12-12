package com.obd2.dgt.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.obd2.dgt.dbManage.DBConnect;
import com.obd2.dgt.R;
import com.obd2.dgt.network.WebHttpConnect;
import com.obd2.dgt.utils.CommonFunc;
import com.obd2.dgt.utils.MyUtils;

public class SplashActivity extends AppBaseActivity {
    private static SplashActivity instance;
    public static SplashActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        instance = this;

        //read db - AppStatus table
        DBConnect m_DBCon = new DBConnect(this.getContext());
        m_DBCon.createDatabase();
        MyUtils.db_connect = m_DBCon;
        try {
            m_DBCon.getDatabase();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        String msg = getString(R.string.check_network_error);
        String btnText = getString(R.string.confirm_text);
        boolean isNetwork = CommonFunc.checkNetworkStatus(SplashActivity.this, msg, btnText);
        if (isNetwork) {
            //서버 에서 회사 자료 받기
            WebHttpConnect.onCompanyInfoRequest();
        }
    }

    public void gotoSuccess() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        }, 1000);
    }

    public void gotoFail() {
        Toast.makeText(getApplicationContext(), R.string.check_network_error, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}