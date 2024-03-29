package com.obd2.dgt.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.obd2.dgt.R;
import com.obd2.dgt.dbManage.TableInfo.MyInfoTable;
import com.obd2.dgt.network.WebHttpConnect;
import com.obd2.dgt.ui.InfoActivity.AuthActivity;
import com.obd2.dgt.utils.CommonFunc;
import com.obd2.dgt.utils.Crypt;
import com.obd2.dgt.utils.MyUtils;

public class FindPwdActivity extends AppBaseActivity {

    EditText find_name_text, find_id_text;
    ImageView auth_code_btn, complete_btn;
    EditText auth_code_text;
    TextView auth_time_text;
    LinearLayout auth_code_layout;
    LinearLayout setting_new_pwd_layout;
    Dialog dialog;
    TextView dialog_normal_text;
    boolean recv_flag = false;
    boolean auth_flag = false;
    EditText find_new_pwd;
    EditText find_new_pwd_conf;
    ImageView find_pwd_complete_btn;
    boolean isRun = false;
    int auth_time_count = 60;
    String user_phone = "";
    String encode_pwd = "";
    private GestureDetector gestureDetector;

    private static FindPwdActivity instance;
    public static FindPwdActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findpwd);
        instance = this;
        isRun = false;
        auth_time_count = 60;

        initLayout();
        String result = getIntent().getStringExtra("result");
        if (result != null) {
            if (result.equals("ok")) {
                user_phone = getIntent().getStringExtra("user_phone");
                String user_name = getIntent().getStringExtra("user_name");
                find_name_text.setText(user_name);
                find_id_text.setText(user_phone);
                setting_new_pwd_layout.setVisibility(View.VISIBLE);

            } else {
                setting_new_pwd_layout.setVisibility(View.GONE);
            }
        }

        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return true;
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event);
    }

    private void initLayout() {
        find_name_text = findViewById(R.id.find_name_text);
        find_id_text = findViewById(R.id.find_id_text);
        auth_code_btn = findViewById(R.id.auth_code_btn);
        auth_code_btn.setBackgroundResource(R.drawable.button2);
        auth_code_btn.setOnClickListener(view -> onGetAuthCodeClick());

        //auth_code_layout = findViewById(R.id.auth_code_layout);
        //auth_code_layout.setVisibility(View.GONE);

        setting_new_pwd_layout = findViewById(R.id.setting_new_pwd_layout);
        setting_new_pwd_layout.setVisibility(View.GONE);

        //auth_code_text = findViewById(R.id.auth_code_text);
        //auth_time_text = findViewById(R.id.auth_time_text);

        //complete_btn = findViewById(R.id.complete_btn);
        //complete_btn.setOnClickListener(view -> onAuthCompleteClick());

        find_new_pwd = findViewById(R.id.find_new_pwd);
        find_new_pwd_conf = findViewById(R.id.find_new_pwd_conf);
        find_pwd_complete_btn = findViewById(R.id.find_pwd_complete_btn);
        find_pwd_complete_btn.setOnClickListener(view -> onFindPasswordCompleteClick());

        dialog = new Dialog(FindPwdActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);

    }

    //인증번호 받기 버튼 클릭
    private void onGetAuthCodeClick() {
        int exist_int = MyInfoTable.getExistMyInfoTable(find_id_text.getText().toString());
        if (exist_int > 0) {
            Intent intent = new Intent(FindPwdActivity.this, AuthActivity.class);
            intent.putExtra("dataKey", "find");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
            finish();
        } else {
            Toast.makeText(getApplicationContext(), R.string.error_equal_name_id, Toast.LENGTH_LONG).show();
        }
    }
    //새 비밀번호 설정 확인 버튼 클릭
    private void onFindPasswordCompleteClick() {
        String pwd = find_new_pwd.getText().toString();
        String pwd_conf = find_new_pwd_conf.getText().toString();
        if (pwd.equals(pwd_conf)) {
            String msg = getString(R.string.check_network_error);
            String btnText = getString(R.string.confirm_text);
            boolean isNetwork = CommonFunc.checkNetworkStatus(FindPwdActivity.this, msg, btnText);
            if (isNetwork) {
                encode_pwd = Crypt.encrypt(pwd);
                String update_date = CommonFunc.getDateTime();
                //서버에 등록
                String[][] params = new String[][]{
                        {"user_id", user_phone},
                        {"user_pwd", encode_pwd},
                        {"update_date", update_date}
                };
                CommonFunc.sendParamData(params);
                WebHttpConnect.onNewPasswordRequest();
            }
        } else {
            showConfirmDialog();
        }
    }

    @SuppressLint({"ResourceAsColor", "ResourceType"})
    public void showConfirmDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.setContentView(R.layout.dlg_normal);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog_normal_text = dialog.findViewById(R.id.dialog_normal_text);
                dialog_normal_text.setText(R.string.error_input_pwd);
                ImageView dialog_normal_btn = dialog.findViewById(R.id.dialog_normal_btn);
                dialog_normal_btn.setImageResource(R.drawable.confirm_press);
                dialog_normal_btn.setOnClickListener(view -> {
                    dialog.dismiss();
                });
                dialog.show();
            }
        });
    }

    public void onSuccessSetting() {
        String[][] fields = new String[][]{
                {"password", encode_pwd}
        };
        MyInfoTable.updateMyInfoTable(fields);

        onLRChangeLayout(FindPwdActivity.this, LoginActivity.class);
        finish();
    }

    public void onFailedSetting() {
        Toast.makeText(getApplicationContext(), R.string.error_modify_pwd, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onLRChangeLayout(FindPwdActivity.this, LoginActivity.class);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

}