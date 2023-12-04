package com.obd2.dgt.ui;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.obd2.dgt.dbManage.TableInfo.CompanyTable;
import com.obd2.dgt.dbManage.TableInfo.DeviceInfoTable;
import com.obd2.dgt.R;
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
    boolean isNetwork = false;
    String id_txt = "";
    String pwd_txt = "";

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
        initLayout();

        //인터넷 상태 확인
        String msg = getString(R.string.check_network_error);
        String btnText = getString(R.string.confirm_text);
        isNetwork = CommonFunc.checkNetworkStatus(LoginActivity.this, msg, btnText);
        if (isNetwork) {
            getDatabaseInfo();
            getWindowsSize();
            getCompanyInfoFromServer();
        }
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
        if (isNetwork) {
            onRLChangeLayount(LoginActivity.this, FindPwdActivity.class);
            finish();
        }
    }
    private void gotoRegisterActivity(){
        if (isNetwork) {
            onRLChangeLayount(LoginActivity.this, SignupActivity.class);
            finish();
        }
    }
    private void gotoMainActivity(){
        if (isNetwork) {
            id_txt = login_id_text.getText().toString();
            pwd_txt = login_pwd_text.getText().toString();
            String encode_pwd = Crypt.encrypt(pwd_txt);

            String[][] params = new String[][]{
                    {"user_id", id_txt},
                    {"user_pwd", encode_pwd}
            };
            WebHttpConnect.onLoginRequest(params);
        }
    }
    public void onSuccessStart(ArrayList<String> user_info) {
        MyUtils.user_only_num = Integer.parseInt(user_info.get(0));
        if (MyUtils.my_name.isEmpty()) {
            String[][] params = new String[][]{
                    {"phone", user_info.get(1)},
                    {"password", user_info.get(2)},
                    {"name", user_info.get(3)},
                    {"company", user_info.get(4)},
                    {"condition", "1"}
            };
            MyInfoTable.insertMyInfoTable(params);
            MyInfoTable.getMyInfoTable();
        }
        ServiceStart();
        onRLChangeLayount(LoginActivity.this, MainActivity.class);
        finish();
    }
    public void onFailedStart() {
        Toast.makeText(getApplicationContext(), R.string.error_login, Toast.LENGTH_SHORT).show();
    }

    //DataBase setting
    private void getDatabaseInfo() {
        CompanyTable.deleteAllCompanyInfoTable();
        MyInfoTable.getMyInfoTable();
        DeviceInfoTable.getDeviceInfoTable();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void getCompanyInfoFromServer() {
        WebHttpConnect.onCompanyInfoRequest();
    }
}