package com.obd2.dgt.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.obd2.dgt.R;
import com.obd2.dgt.ui.InfoActivity.AuthActivity;
import com.obd2.dgt.ui.MainListActivity.DashboardActivity;
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
    }

    private void initLayout() {
        find_name_text = findViewById(R.id.find_name_text);
        find_id_text = findViewById(R.id.find_id_text);
        auth_code_btn = findViewById(R.id.auth_code_btn);
        auth_code_btn.setBackgroundResource(R.drawable.button2);
        auth_code_btn.setOnClickListener(view -> onGetAuthCodeClick());

        auth_code_layout = findViewById(R.id.auth_code_layout);
        auth_code_layout.setVisibility(View.GONE);

        setting_new_pwd_layout = findViewById(R.id.setting_new_pwd_layout);
        setting_new_pwd_layout.setVisibility(View.GONE);

        auth_code_text = findViewById(R.id.auth_code_text);
        auth_time_text = findViewById(R.id.auth_time_text);

        complete_btn = findViewById(R.id.complete_btn);
        complete_btn.setOnClickListener(view -> onAuthCompleteClick());

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
        Intent intent = new Intent(FindPwdActivity.this, AuthActivity.class);
        intent.putExtra("dataKey", "find");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
        finish();
    }

    private void confirmAuthCode() {
        if (!auth_flag) {
            if (MyUtils.my_name.equals(find_name_text.getText().toString()) &&
                    MyUtils.my_phone.equals(find_id_text.getText().toString())) {
                auth_code_btn.setBackgroundResource(R.drawable.button2_disable);
                auth_code_layout.setVisibility(View.VISIBLE);
                auth_flag = true;
                isRun = true;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new ReceiveSMSAsyncTask().execute("receive_sms");
                    }
                });
            } else {
                showWarningDialog();
                auth_flag = false;
            }
        }
    }
    int auth_time = 0;
    String auth_code = "";
    @SuppressLint("SetTextI18n")
    private void setAuthTime(int time) {
        auth_time = time;
        auth_time_text.setText(auth_time + "s");
    }
    public void setAuthCodeReceive(String code) {
        auth_code = code;
    }

    //인증번호 확인 버튼 클릭
    private void onAuthCompleteClick() {
        boolean is_ok = false;
        if (auth_flag && !recv_flag) {
            if (auth_time == 0) {
                Toast.makeText(getApplicationContext(), R.string.error_input_auth_code, Toast.LENGTH_SHORT).show();
                return;
            }
            if (!auth_code.isEmpty()) {
                if (auth_code.equals(auth_code_text.getText().toString())) {
                    complete_btn.setBackgroundResource(R.drawable.button3_disable);
                    setting_new_pwd_layout.setVisibility(View.VISIBLE);
                    is_ok = true;
                    recv_flag = true;
                    isRun = false;
                } else {
                    auth_code_btn.setBackgroundResource(R.drawable.button2);
                    auth_flag = false;
                    recv_flag = false;
                }
            }

            if (!is_ok) {
                showWarningDialog();
            }
        }
    }

    //새 비밀번호 설정 확인 버튼 클릭
    private void onFindPasswordCompleteClick() {
        String pwd = find_new_pwd.getText().toString();
        String pwd_conf = find_new_pwd_conf.getText().toString();
        if (pwd.equals(pwd_conf)) {
            onLRChangeLayount(FindPwdActivity.this, LoginActivity.class);
            finish();
        } else {
            showConfirmDialog();
        }
    }

    @SuppressLint({"ResourceAsColor", "ResourceType"})
    public void showWarningDialog() {
        dialog.setContentView(R.layout.dlg_notification);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView dialog_text_1 = dialog.findViewById(R.id.dialog_text_1);
        dialog_text_1.setText(R.string.alarm_text_1);
        TextView dialog_confirm_text = dialog.findViewById(R.id.dialog_confirm_text);
        dialog_confirm_text.setTextColor(Color.parseColor(getString(R.color.title_color)));
        ImageView dialog_confirm_btn = dialog.findViewById(R.id.dialog_confirm_btn);
        dialog_confirm_btn.setImageResource(R.drawable.confirm_off);
        dialog_confirm_btn.setOnClickListener(view -> {
            dialog.dismiss();
            auth_code_btn.setBackgroundResource(R.drawable.button2);
        });
        dialog.show();
    }
    @SuppressLint({"ResourceAsColor", "ResourceType"})
    public void showConfirmDialog() {
        dialog.setContentView(R.layout.dlg_normal);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog_normal_text = dialog.findViewById(R.id.dialog_normal_text);
        dialog_normal_text.setText(R.string.error_input_pwd);
        ImageView dialog_normal_btn = dialog.findViewById(R.id.dialog_normal_btn);
        dialog_normal_btn.setImageResource(R.drawable.confirm_press);
        dialog_normal_btn.setOnClickListener(view -> {
            dialog.dismiss();
        });
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        onLRChangeLayount(FindPwdActivity.this, LoginActivity.class);
        finish();
    }

    class ReceiveSMSAsyncTask extends AsyncTask<String, Integer, Boolean> {
        @SuppressLint("MissingPermission")
        protected Boolean doInBackground(String... str) {
            while (isRun) {
                try {
                    if (auth_time_count <= 0) {
                        auth_time_count = 0;
                        isRun = false;
                    } else {
                        auth_time_count--;
                        setAuthTime(auth_time_count);
                    }
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            ReceiveSMSAsyncTask.this.cancel(true);
            return false;
        }
    }

}