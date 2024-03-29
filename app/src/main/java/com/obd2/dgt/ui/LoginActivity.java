package com.obd2.dgt.ui;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.obd2.dgt.dbManage.TableInfo.CarInfoTable;
import com.obd2.dgt.dbManage.TableInfo.CompanyTable;
import com.obd2.dgt.dbManage.TableInfo.DeviceInfoTable;
import com.obd2.dgt.R;
import com.obd2.dgt.dbManage.TableInfo.DrivingTable;
import com.obd2.dgt.dbManage.TableInfo.MessageInfoTable;
import com.obd2.dgt.dbManage.TableInfo.MyInfoTable;
import com.obd2.dgt.dbManage.TableInfo.ProtocolTable;
import com.obd2.dgt.network.WebHttpConnect;
import com.obd2.dgt.utils.CommonFunc;
import com.obd2.dgt.utils.Crypt;
import com.obd2.dgt.utils.MyUtils;

import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;

public class LoginActivity extends AppBaseActivity {
    EditText login_id_text, login_pwd_text;
    TextView find_pwd_btn, register_btn;
    ImageView login_btn;
    String user_phone = "";
    String user_pwd = "";
    FrameLayout progress_layout;
    String[] permissions  = {
            READ_EXTERNAL_STORAGE,
            WRITE_EXTERNAL_STORAGE
    };
    private ActivityResultLauncher<Intent> activityResultLauncher;

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
        if (createDatabase()) {
            resetPhoneSetting();
        /*if (!checkPermission()) {
            activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult( ActivityResult result ) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        if (Environment.isExternalStorageManager()) {

                        }
                        else
                            Toast.makeText(LoginActivity.this, getText(R.string.permission_failue), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, getText(R.string.permission_failue), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            requestFilePermission();
        }*/
            getTermsFile();
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
                if (!MyUtils.new_login) {
                    String encode_pwd = Crypt.decrypt(MyUtils.my_pwd);
                    login_id_text.setText(MyUtils.my_phone);
                    login_pwd_text.setText(encode_pwd);
                }
            }
            MyUtils.new_login = false;
        } else {
            Toast.makeText(getApplicationContext(), R.string.fail_database, Toast.LENGTH_LONG).show();
        }
    }

    private void initLayout() {
        login_id_text = findViewById(R.id.login_id_text);
        //login_id_text.setText(MyUtils.my_phone);
        login_pwd_text = findViewById(R.id.login_pwd_text);
        //login_pwd_text.setText(MyUtils.my_pwd);

        find_pwd_btn = findViewById(R.id.find_pwd_btn);
        find_pwd_btn.setOnClickListener(view -> gotoFindPwdActivity());

        register_btn = findViewById(R.id.register_btn);
        register_btn.setOnClickListener(view -> gotoSignupActivity());

        login_btn = findViewById(R.id.login_btn);
        login_btn.setOnClickListener(view -> getAuthToken());

        progress_layout = findViewById(R.id.progress_layout);
        progress_layout.setVisibility(View.GONE);
    }
    private void gotoFindPwdActivity(){
        onRLChangeLayout(LoginActivity.this, FindPwdActivity.class);
        finish();
    }
    private void gotoSignupActivity(){
        onRLChangeLayout(LoginActivity.this, SignupActivity.class);
        finish();
    }
    private void getAuthToken() {
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
        if (!MyUtils.my_phone.isEmpty() && !MyUtils.my_pwd.isEmpty()) {
            if (!user_phone.equalsIgnoreCase(MyUtils.my_phone) ||
                    !encode_pwd.equalsIgnoreCase(MyUtils.my_pwd)) {
                Toast.makeText(getApplicationContext(), R.string.error_other_login, Toast.LENGTH_SHORT).show();
                progress_layout.setVisibility(View.GONE);
                return;
            }
        }

        String[][] params = new String[][]{
                {"user_phone", user_phone},
                {"user_pwd", encode_pwd}
        };
        WebHttpConnect.onTokenRequest(params);
    }
    public void onSuccessGetToken(){
        String visit_date = CommonFunc.getDateTime();
        String[][] params = new String[][]{
                {"user_phone", user_phone},
                {"visit_date", visit_date}
        };
        CommonFunc.sendParamData(params);
        WebHttpConnect.onLoginRequest();
    }

    public void onSuccessLogin(ArrayList<String> user_info) {
        DeviceInfoTable.getDeviceInfoTable();
        CarInfoTable.getCarInfoTable();

        MyUtils.my_id = Integer.parseInt(user_info.get(0));
        MyUtils.admin_id = Integer.parseInt(user_info.get(4));
        String encode_pwd = Crypt.encrypt(user_pwd);

        String[][] dbparams = new String[][]{
                {"phone", user_info.get(1)},
                {"name", user_info.get(2)},
                {"password", encode_pwd},
                {"cid", user_info.get(4)},
                {"condition", "1"}
        };
        if (MyInfoTable.getExistMyInfoTable(user_phone) == 0) {
            MyInfoTable.insertMyInfoTable(dbparams);
            MyInfoTable.getMyInfoTable();
        } else {
            MyInfoTable.updateMyInfoTable(dbparams);
            MyInfoTable.getMyInfoTable();
        }


        //차량정보 조회
        String[][] params = new String[][]{
                {"user_id", String.valueOf(MyUtils.my_id)}
        };
        CommonFunc.sendParamData(params);
        WebHttpConnect.onCarListRequest();
    }
    public void onNonUser() {
        progress_layout.setVisibility(View.GONE);
        Toast.makeText(getApplicationContext(), R.string.error_login, Toast.LENGTH_LONG).show();
    }

    public void onFailedPassword() {
        progress_layout.setVisibility(View.GONE);
        Toast.makeText(getApplicationContext(), R.string.error_password, Toast.LENGTH_LONG).show();
    }

    public void onSuccessCarList(ArrayList<String> car_info) {
        String[][] params = new String[][]{
                {"car_id", car_info.get(0)},
                {"number", car_info.get(1)},
                {"manufacturer", CommonFunc.findManufacturer(car_info.get(2))},
                {"model", CommonFunc.findModel(car_info.get(3))},
                {"create_date", CommonFunc.findCreateYear(car_info.get(4))},
                {"fuel_type", CommonFunc.findFuelType(car_info.get(5))},
                {"gas", car_info.get(6)}
        };
        if (MyUtils.carInfo.size() == 0) {
            CarInfoTable.insertCarInfoTable(params);
            CarInfoTable.getCarInfoTable();
        } else {
            CarInfoTable.updateCarInfoTable(Integer.parseInt(car_info.get(0)), params);
            CarInfoTable.getCarInfoTable();
        }
        gotoMainPage();
    }

    public void onFailedCarList() {
        gotoMainPage();
        progress_layout.setVisibility(View.GONE);
        Toast.makeText(getApplicationContext(), R.string.error_read_car_fail, Toast.LENGTH_LONG).show();
    }

    private void gotoMainPage() {
        BluetoothServiceStart();
        progress_layout.setVisibility(View.GONE);
        onRLChangeLayout(LoginActivity.this, MainActivity.class);
        finish();
    }

    //DataBase setting
    private void getDatabaseInfo() {
        //CompanyTable.deleteAllCompanyInfoTable();
        MyInfoTable.getMyInfoTable();
        ProtocolTable.getProtocolTable();
        MessageInfoTable.getMessageLastID();
        MessageInfoTable.getMessageInfoTable();
        DrivingTable.getNotSentDrivingInfoTable();
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



    /*private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            int readCheck = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
            int writeCheck = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
            return readCheck == PackageManager.PERMISSION_GRANTED && writeCheck == PackageManager.PERMISSION_GRANTED;
        }
    }*/

    private void requestFilePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            new AlertDialog.Builder(LoginActivity.this)
                    .setTitle(getString(R.string.permission_storage_title))
                    .setMessage(getString(R.string.permission_storage_content))
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick( DialogInterface dialog, int which ) {
                            try {
                                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                                intent.addCategory("android.intent.category.DEFAULT");
                                intent.setData(Uri.parse(String.format("package:%s", new Object[]{getApplicationContext().getPackageName()})));
                                activityResultLauncher.launch(intent);
                            } catch (Exception e) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                                activityResultLauncher.launch(intent);
                            }
                        }
                    })
                    .setCancelable(false)
                    .show();

        } else {
            ActivityCompat.requestPermissions(LoginActivity.this, permissions, 30);
        }
    }

    private void getTermsFile() {
        String msg = getString(R.string.check_network_error);
        String btnText = getString(R.string.confirm_text);
        boolean isNetwork = CommonFunc.checkNetworkStatus(LoginActivity.this, msg, btnText);
        if (isNetwork) {
            //약관 url 받기
            WebHttpConnect.onTermsUrlRequest();
        }
    }
}