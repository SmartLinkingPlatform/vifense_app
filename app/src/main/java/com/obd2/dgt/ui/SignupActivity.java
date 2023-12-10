package com.obd2.dgt.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.obd2.dgt.R;
import com.obd2.dgt.dbManage.TableInfo.MyInfoTable;
import com.obd2.dgt.network.WebHttpConnect;
import com.obd2.dgt.ui.InfoActivity.AuthActivity;
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
    String user_birthday = "";
    Dialog dialog;
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

        String result = getIntent().getStringExtra("result");
        if (result != null) {
            if (result.equals("ok")) {
                String name = getIntent().getStringExtra("user_name");
                reg_name_text.setText(name);
                String phone = getIntent().getStringExtra("user_phone");
                reg_id_text.setText(phone);
                user_birthday = getIntent().getStringExtra("user_birthday");
            } else {
                showSignErrorDialog(R.string.error_verify_phone);
            }
        }
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

    private void showSignErrorDialog(int res_id) {
        dialog = new Dialog(SignupActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dlg_normal);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView dialog_normal_text = dialog.findViewById(R.id.dialog_normal_text);
        dialog_normal_text.setText(res_id);
        ImageView dialog_normal_btn = dialog.findViewById(R.id.dialog_normal_btn);
        dialog_normal_btn.setOnClickListener(view -> {
            dialog.dismiss();
        });
        dialog.show();
    }

    //휴대폰 인증하기 버튼
    private void onAuthRealNameClick(){
        Intent intent = new Intent(SignupActivity.this, AuthActivity.class);
        intent.putExtra("dataKey", "sign");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
        finish();
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
        setButtonStatus(false, false);
        name_txt = reg_name_text.getText().toString();
        phone_txt = reg_id_text.getText().toString();
        password_txt = reg_pwd_text.getText().toString();
        if (name_txt.isEmpty() || phone_txt.isEmpty()) {
            Toast.makeText(getApplicationContext(), R.string.error_auth, Toast.LENGTH_SHORT).show();
            setButtonStatus(true, false);
            return;
        }
        if (password_txt.isEmpty()) {
            Toast.makeText(getApplicationContext(), R.string.error_pwd_message, Toast.LENGTH_SHORT).show();
            setButtonStatus(true, false);
            return;
        }
        if (!password_txt.equals(reg_pwd_confirm.getText().toString())) {
            Toast.makeText(getApplicationContext(), R.string.error_input_pwd, Toast.LENGTH_SHORT).show();
            setButtonStatus(true, false);
            return;
        }
        if (!isChecked) {
            Toast.makeText(getApplicationContext(), R.string.error_view_conditions, Toast.LENGTH_SHORT).show();
            setButtonStatus(true, false);
            return;
        }

        String msg = getString(R.string.check_network_error);
        String btnText = getString(R.string.confirm_text);
        boolean isNetwork = CommonFunc.checkNetworkStatus(SignupActivity.this, msg, btnText);
        if (isNetwork) {
            if (MyInfoTable.getMyInfoTableCount() > 0) {
                showSignErrorDialog(R.string.error_signup_duplicate);
                setButtonStatus(false, true);
            } else {
                encode_pwd = Crypt.encrypt(password_txt);
                MyUtils.admin_id = Integer.parseInt(MyUtils.companyInfo.get(reg_company_spinner.getSelectedItemPosition())[1]);
                String create_date = CommonFunc.getDateTime();
                //서버에 등록
                String[][] params = new String[][]{
                        {"user_phone", phone_txt},
                        {"user_name", name_txt},
                        {"user_pwd", encode_pwd},
                        {"user_birthday", user_birthday},
                        {"admin_id", String.valueOf(MyUtils.admin_id)},
                        {"certifice_status", "1"},
                        {"active", "0"},
                        {"create_date", create_date}
                };
                WebHttpConnect.onSignUpRequest(params);
            }
        }
    }
    private void setButtonStatus(boolean status, boolean error) {
        if (!status) {
            if (!error) {
                reg_user_btn.setBackgroundResource(R.drawable.button_press);
            } else {
                reg_user_btn.setBackgroundResource(R.drawable.button_disable);
                reg_user_btn.setEnabled(false);
            }
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
                {"cid", String.valueOf(MyUtils.admin_id)},
                {"condition", "1"}
        };
        MyInfoTable.insertMyInfoTable(fields);

        onLRChangeLayount(SignupActivity.this, LoginActivity.class);
        finish();
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