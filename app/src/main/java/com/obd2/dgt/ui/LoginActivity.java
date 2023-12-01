package com.obd2.dgt.ui;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.obd2.dgt.dbManage.TableInfo.DeviceInfoTable;
import com.obd2.dgt.R;

public class LoginActivity extends AppBaseActivity {
    EditText login_id_text, login_pwd_text;
    TextView find_pwd_btn, register_btn;
    ImageView login_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        permissionCheck();
        resetBluetoothAdapter();
        initLayout();
        getDatabaseInfo();
        getWindowsSize();
    }

    private void initLayout() {
        login_id_text = findViewById(R.id.login_id_text);
        login_pwd_text = findViewById(R.id.login_pwd_text);

        find_pwd_btn = findViewById(R.id.find_pwd_btn);
        find_pwd_btn.setOnClickListener(view -> gotoFindPwdActivity());

        register_btn = findViewById(R.id.register_btn);
        register_btn.setOnClickListener(view -> gotoRegisterActivity());

        login_btn = findViewById(R.id.login_btn);
        login_btn.setOnClickListener(view -> gotoMainActivity());
    }
    private void gotoFindPwdActivity(){
        onRLChangeLayount(LoginActivity.this, FindPwdActivity.class);
        finish();
    }
    private void gotoRegisterActivity(){
        onRLChangeLayount(LoginActivity.this, SignupActivity.class);
        finish();
    }
    private void gotoMainActivity(){
        ServiceStart();
        onRLChangeLayount(LoginActivity.this, MainActivity.class);
        finish();
    }

    private void getDatabaseInfo() {
        DeviceInfoTable.getDeviceInfoTable();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}