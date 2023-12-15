package com.obd2.dgt.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.obd2.dgt.dbManage.TableInfo.CarInfoTable;
import com.obd2.dgt.dbManage.TableInfo.CompanyTable;
import com.obd2.dgt.dbManage.TableInfo.DeviceInfoTable;
import com.obd2.dgt.R;
import com.obd2.dgt.dbManage.TableInfo.MessageInfoTable;
import com.obd2.dgt.dbManage.TableInfo.MyInfoTable;
import com.obd2.dgt.network.WebHttpConnect;
import com.obd2.dgt.utils.CommonFunc;
import com.obd2.dgt.utils.Crypt;
import com.obd2.dgt.utils.MyUtils;

import java.time.LocalDate;
import java.util.ArrayList;

public class LoginActivity extends AppBaseActivity {
    EditText login_id_text, login_pwd_text;
    TextView find_pwd_btn, register_btn;
    ImageView login_btn;
    String user_phone = "";
    String user_pwd = "";
    FrameLayout progress_layout;

    private static LoginActivity instance;
    public static LoginActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        instance = this;

        permissionCheck();
        resetBluetoothAdapter();

        getDatabaseInfo();
        getWindowsSize();

        if (MyUtils.create_years.size() == 0) {
            //년도
            LocalDate now = null;
            int year = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                now = LocalDate.now();
                year = now.getYear();
            }
            for (int i = 1990; i <= year; i++) {
                MyUtils.create_years.add(String.valueOf(i));
            }
        }

        initLayout();

        if (!MyUtils.my_phone.isEmpty() && !MyUtils.my_pwd.isEmpty()) {
            String encode_pwd = Crypt.decrypt(MyUtils.my_pwd);
            login_id_text.setText(MyUtils.my_phone);
            login_pwd_text.setText(encode_pwd);
        }
    }

    private void initLayout() {
        login_id_text = findViewById(R.id.login_id_text);
        login_id_text.setText(MyUtils.my_phone);
        login_pwd_text = findViewById(R.id.login_pwd_text);
        login_pwd_text.setText(MyUtils.my_pwd);

        find_pwd_btn = findViewById(R.id.find_pwd_btn);
        find_pwd_btn.setOnClickListener(view -> gotoFindPwdActivity());

        register_btn = findViewById(R.id.register_btn);
        register_btn.setOnClickListener(view -> gotoSignupActivity());

        login_btn = findViewById(R.id.login_btn);
        login_btn.setOnClickListener(view -> gotoMainActivity());

        progress_layout = findViewById(R.id.progress_layout);
        progress_layout.setVisibility(View.GONE);
    }
    private void gotoFindPwdActivity(){
        onRLChangeLayount(LoginActivity.this, FindPwdActivity.class);
        finish();
    }
    private void gotoSignupActivity(){
        onRLChangeLayount(LoginActivity.this, SignupActivity.class);
        finish();
    }
    private void gotoMainActivity(){
        user_phone = login_id_text.getText().toString();
        user_pwd = login_pwd_text.getText().toString();
        if (user_phone.isEmpty()) {
            Toast.makeText(getApplicationContext(), R.string.error_login_id, Toast.LENGTH_SHORT).show();
            return;
        }
        if (user_pwd.isEmpty()) {
            Toast.makeText(getApplicationContext(), R.string.error_login_pwd, Toast.LENGTH_SHORT).show();
            return;
        }

        progress_layout.setVisibility(View.VISIBLE);
        String encode_pwd = Crypt.encrypt(user_pwd);

        String[][] msgparams = new String[][]{
                {"msg_id", String.valueOf(MyUtils.lastMsgID)},
                {"user_phone", user_phone}
        };
        WebHttpConnect.onMessageInfoRequest(msgparams);


        String visit_date = CommonFunc.getDateTime();
        String[][] params = new String[][]{
                {"user_phone", user_phone},
                {"user_pwd", encode_pwd},
                {"visit_date", visit_date}
        };
        WebHttpConnect.onLoginRequest(params);
    }

    public void onSuccessStart(ArrayList<String> user_info) {
        DeviceInfoTable.getDeviceInfoTable();
        CarInfoTable.getCarInfoTable();
        
        MyUtils.my_id = Integer.parseInt(user_info.get(0));
        MyUtils.admin_id = Integer.parseInt(user_info.get(4));
        String[][] params = new String[][]{
                {"phone", user_info.get(1)},
                {"name", user_info.get(2)},
                {"password", user_info.get(3)},
                {"cid", user_info.get(4)},
                {"condition", "1"}
        };
        if (MyInfoTable.getExistMyInfoTable(user_phone) == 0) {
            MyInfoTable.insertMyInfoTable(params);
            MyInfoTable.getMyInfoTable();
        } else {
            MyInfoTable.updateMyInfoTable(params);
            MyInfoTable.getMyInfoTable();
        }
        ServiceStart();
        progress_layout.setVisibility(View.GONE);
        onRLChangeLayount(LoginActivity.this, MainActivity.class);
        finish();
    }
    public void onNonUser() {
        progress_layout.setVisibility(View.GONE);
        Toast.makeText(getApplicationContext(), R.string.error_login, Toast.LENGTH_LONG).show();
    }

    public void onFailedPassword() {
        progress_layout.setVisibility(View.GONE);
        Toast.makeText(getApplicationContext(), R.string.error_password, Toast.LENGTH_LONG).show();
    }

    //DataBase setting
    private void getDatabaseInfo() {
        //CompanyTable.deleteAllCompanyInfoTable();
        MyInfoTable.getMyInfoTable();
        MessageInfoTable.getMessageInfoTable();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
        progress_layout.setVisibility(View.GONE);
    }

}