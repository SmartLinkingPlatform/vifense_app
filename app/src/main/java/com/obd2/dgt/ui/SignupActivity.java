package com.obd2.dgt.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.obd2.dgt.R;
import com.obd2.dgt.dbManage.TableInfo.MyInfoTable;
import com.obd2.dgt.network.WebHttpConnect;
import com.obd2.dgt.utils.CommonFunc;
import com.obd2.dgt.utils.Crypt;
import com.obd2.dgt.utils.MyUtils;

public class SignupActivity extends AppBaseActivity {
    TextView reg_name_text, reg_id_text;
    EditText reg_pwd_text, reg_pwd_confirm;
    ImageView reg_auth_real_btn, reg_view_condition_btn;
    ImageView check_box, reg_user_btn;
    Spinner reg_company_spinner;
    boolean isChecked = false;
    boolean isRegister = false;

    String name_txt = "";
    String phone_txt = "";
    String password_txt = "";
    private static SignupActivity instance;
    public static SignupActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        instance = this;
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

        reg_company_spinner = findViewById(R.id.reg_company_spinner);
        String[] companys = new String[MyUtils.companyInfo.size()];
        for (int i = 0; i < MyUtils.companyInfo.size(); i++) {
            String c_name = MyUtils.companyInfo.get(i)[2];
            companys[i] = c_name;
        }
        ArrayAdapter<String> mf_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, companys);
        reg_company_spinner.setAdapter(mf_adapter);
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
    String encode_pwd = "";
    private void onRegisterUserClick(){
        pageStatus(false);
        name_txt = reg_name_text.getText().toString();
        name_txt = "ksi";
        phone_txt = reg_id_text.getText().toString();
        phone_txt = "15524206580";
        password_txt = reg_pwd_text.getText().toString();
        if (name_txt.isEmpty() || phone_txt.isEmpty()) {
            Toast.makeText(getApplicationContext(), R.string.error_auth, Toast.LENGTH_SHORT).show();
            pageStatus(true);
            return;
        }
        if (password_txt.isEmpty()) {
            Toast.makeText(getApplicationContext(), R.string.error_pwd_message, Toast.LENGTH_SHORT).show();
            pageStatus(true);
            return;
        }
        if (!password_txt.equals(reg_pwd_confirm.getText().toString())) {
            Toast.makeText(getApplicationContext(), R.string.error_input_pwd, Toast.LENGTH_SHORT).show();
            pageStatus(true);
            return;
        }
        if (!isChecked) {
            Toast.makeText(getApplicationContext(), R.string.error_view_conditions, Toast.LENGTH_SHORT).show();
            pageStatus(true);
            return;
        }

        String msg = getString(R.string.check_network_error);
        String btnText = getString(R.string.confirm_text);
        boolean isNetwork = CommonFunc.checkNetworkStatus(SignupActivity.this, msg, btnText);
        if (isNetwork) {
            if (MyInfoTable.getMyInfoTableCount() > 0) {
                Toast.makeText(getApplicationContext(), R.string.error_register_user, Toast.LENGTH_SHORT).show();
            } else {
                encode_pwd = Crypt.encrypt(password_txt);
                //서버에 등록
                String[][] params = new String[][]{
                        {"user_name", name_txt},
                        {"user_id", phone_txt},
                        {"user_pwd", encode_pwd},
                        {"user_type", "0"},
                        {"user_class", "1"},
                        {"company_name", reg_company_spinner.getSelectedItem().toString()}
                };
                WebHttpConnect.onSignUpRequest(params);
            }
        }
    }
    private void pageStatus(boolean status) {
        if (!status) {
            reg_user_btn.setBackgroundResource(R.drawable.button_press);
        } else {
            reg_user_btn.setBackgroundResource(R.drawable.button);
        }
        reg_name_text.setEnabled(status);
        reg_id_text.setEnabled(status);
        reg_pwd_text.setEnabled(status);
        reg_pwd_confirm.setEnabled(status);
        reg_company_spinner.setEnabled(status);
        check_box.setEnabled(status);
    }
    public void onSuccessSignup() {
        String[][] fields = new String[][]{
                {"name", name_txt},
                {"phone", phone_txt},
                {"password", encode_pwd},
                {"company", reg_company_spinner.getSelectedItem().toString()},
                {"condition", "1"}
        };
        MyInfoTable.insertMyInfoTable(fields);

        onLRChangeLayount(SignupActivity.this, LoginActivity.class);
        finish();
    }
    public void onDuplicateSignup() {
        Toast.makeText(getApplicationContext(), R.string.error_signup_duplicate, Toast.LENGTH_SHORT).show();
    }
    public void onFailedSignup() {
        Toast.makeText(getApplicationContext(), R.string.error_signup_fail, Toast.LENGTH_SHORT).show();
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