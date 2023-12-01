package com.obd2.dgt.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.obd2.dgt.R;

public class SignupActivity extends AppBaseActivity {
    TextView reg_name_text, reg_id_text;
    EditText reg_pwd_text, reg_pwd_confirm;
    ImageView reg_auth_real_btn, reg_view_condition_btn;
    ImageView check_box, reg_user_btn;
    Spinner reg_belong_company;
    boolean isChecked = false;
    boolean isRegister = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        initLayout();
    }

    @SuppressLint("WrongViewCast")
    private void initLayout() {
        reg_name_text = findViewById(R.id.reg_name_text);
        reg_id_text = findViewById(R.id.reg_id_text);
        reg_pwd_text = findViewById(R.id.reg_pwd_text);
        reg_pwd_confirm = findViewById(R.id.reg_pwd_confirm);

        reg_auth_real_btn = findViewById(R.id.reg_auth_real_btn);
        reg_auth_real_btn.setOnClickListener(view -> onAuthRealNameClick());

        check_box = findViewById(R.id.check_box);
        check_box.setOnClickListener(view -> setEnableCheckBox());

        reg_view_condition_btn = findViewById(R.id.reg_view_condition_btn);
        reg_view_condition_btn.setOnClickListener(view -> onViewConditionClick());

        reg_user_btn = findViewById(R.id.reg_user_btn);
        reg_user_btn.setOnClickListener(view -> onRegisterUserClick());

        reg_belong_company = findViewById(R.id.reg_belong_company);

    }

    //휴대폰 인증하기 버튼
    private void onAuthRealNameClick(){

    }

    //약관 동의 체크
    private void setEnableCheckBox(){
        if(isChecked) {
            check_box.setImageResource(R.drawable.check_normal);
            isChecked = false;
        } else {
            check_box.setImageResource(R.drawable.check_selected);
            isChecked = true;
        }
    }

    //회원 가입하기 버튼
    private void onRegisterUserClick(){
        reg_user_btn.setBackgroundResource(R.drawable.button_press);
        reg_name_text.setEnabled(false);
        reg_id_text.setEnabled(false);
        reg_pwd_text.setEnabled(false);
        reg_pwd_confirm.setEnabled(false);
        reg_belong_company.setEnabled(false);
        check_box.setEnabled(false);
    }

    //이용 약관 보기
    private void onViewConditionClick(){

    }

    @Override
    public void onBackPressed() {
        onLRChangeLayount(SignupActivity.this, LoginActivity.class);
        finish();
    }
}