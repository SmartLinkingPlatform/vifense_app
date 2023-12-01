package com.obd2.dgt.ui.InfoActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.obd2.dgt.R;
import com.obd2.dgt.ui.AppBaseActivity;
import com.obd2.dgt.ui.MainActivity;
import com.obd2.dgt.utils.MyUtils;

import java.time.LocalDate;
import java.util.ArrayList;

public class MyInfoModifyActivity extends AppBaseActivity {
    TextView my_info_mod_id;
    TextView my_info_mod_name;
    EditText myinfo_mod_pwd;
    EditText myinfo_mod_confirm_pwd;
    ImageView my_info_mod_prev_btn;
    ImageView my_info_mod_btn;
    Spinner myinfo_mod_company_spinner;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info_modify);

        initLayout();
    }

    private void initLayout() {
        my_info_mod_id = findViewById(R.id.my_info_mod_id);
        my_info_mod_id.setText(MyUtils.my_id);

        my_info_mod_name = findViewById(R.id.my_info_mod_name);
        my_info_mod_name.setText(MyUtils.my_name);

        myinfo_mod_pwd = findViewById(R.id.myinfo_mod_pwd);
        myinfo_mod_confirm_pwd = findViewById(R.id.myinfo_mod_confirm_pwd);

        myinfo_mod_company_spinner = findViewById(R.id.myinfo_mod_company_spinner);


        my_info_mod_prev_btn = findViewById(R.id.my_info_mod_prev_btn);
        my_info_mod_prev_btn.setOnClickListener(view -> onMyInfoModPrevClick());

        my_info_mod_btn = findViewById(R.id.my_info_mod_btn);
        my_info_mod_btn.setOnClickListener(view -> onMyInfoModifyClick());
    }

    private void onMyInfoModifyClick() {
        boolean is_mod = false;
        String pwd = myinfo_mod_pwd.getText().toString();
        String pwd_conf = myinfo_mod_confirm_pwd.getText().toString();
        if (pwd.equals(pwd_conf)) {
            is_mod = true;
        }

        dialog = new Dialog(MyInfoModifyActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        showConfirmDialog(is_mod);
    }
    @SuppressLint({"ResourceAsColor", "ResourceType"})
    public void showConfirmDialog(boolean b_mod) {
        dialog.setContentView(R.layout.dlg_normal);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView dialog_normal_text = dialog.findViewById(R.id.dialog_normal_text);
        if (b_mod) {
            dialog_normal_text.setText(R.string.confirm_mod_message);
        } else {
            dialog_normal_text.setText(R.string.error_pwd_message);
        }

        ImageView dialog_normal_btn = dialog.findViewById(R.id.dialog_normal_btn);
        dialog_normal_btn.setOnClickListener(view -> {
            if (b_mod) {
                onLRChangeLayount(MyInfoModifyActivity.this, MyInfoActivity.class);
                finish();
            }
            dialog.dismiss();
        });
        dialog.show();
    }

    private void onMyInfoModPrevClick() {
        onLRChangeLayount(MyInfoModifyActivity.this, MyInfoActivity.class);
        finish();
    }

    @Override
    public void onBackPressed() {
        onLRChangeLayount(MyInfoModifyActivity.this, MyInfoActivity.class);
        finish();
    }
}