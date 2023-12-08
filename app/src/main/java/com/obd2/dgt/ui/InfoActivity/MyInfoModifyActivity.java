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
import com.obd2.dgt.dbManage.TableInfo.MyInfoTable;
import com.obd2.dgt.network.WebHttpConnect;
import com.obd2.dgt.ui.AppBaseActivity;
import com.obd2.dgt.ui.LoginActivity;
import com.obd2.dgt.ui.MainActivity;
import com.obd2.dgt.utils.CommonFunc;
import com.obd2.dgt.utils.Crypt;
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
    TextView dialog_normal_text;
    String[] c_ids;
    private static MyInfoModifyActivity instance;
    public static MyInfoModifyActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info_modify);
        instance = this;
        MyUtils.currentActivity = this;

        initLayout();
    }

    private void initLayout() {
        my_info_mod_id = findViewById(R.id.my_info_mod_id);
        my_info_mod_id.setText(MyUtils.my_phone);

        my_info_mod_name = findViewById(R.id.my_info_mod_name);
        my_info_mod_name.setText(MyUtils.my_name);

        myinfo_mod_pwd = findViewById(R.id.myinfo_mod_pwd);
        myinfo_mod_confirm_pwd = findViewById(R.id.myinfo_mod_confirm_pwd);

        myinfo_mod_company_spinner = findViewById(R.id.myinfo_mod_company_spinner);
        int selected_idx = 0;
        c_ids = new String[MyUtils.companyInfo.size()];
        String[] companys = new String[MyUtils.companyInfo.size()];
        for (int i = 0; i < MyUtils.companyInfo.size(); i++) {
            String c_id = MyUtils.companyInfo.get(i)[1];
            c_ids[i] = c_id;
            String c_name = MyUtils.companyInfo.get(i)[2];
            companys[i] = c_name;
            if (MyUtils.admin_id == Integer.parseInt(c_id)) {
                selected_idx = i;
            }
        }
        ArrayAdapter<String> mf_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, companys);
        myinfo_mod_company_spinner.setAdapter(mf_adapter);
        myinfo_mod_company_spinner.setSelection(selected_idx);

        my_info_mod_prev_btn = findViewById(R.id.my_info_mod_prev_btn);
        my_info_mod_prev_btn.setOnClickListener(view -> onMyInfoModPrevClick());

        my_info_mod_btn = findViewById(R.id.my_info_mod_btn);
        my_info_mod_btn.setOnClickListener(view -> onMyInfoModifyClick());

        dialog = new Dialog(MyInfoModifyActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dlg_normal);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog_normal_text = dialog.findViewById(R.id.dialog_normal_text);
    }

    private void onMyInfoModifyClick() {
        String pwd = myinfo_mod_pwd.getText().toString();
        String pwd_conf = myinfo_mod_confirm_pwd.getText().toString();
        if (pwd.equals(pwd_conf)) {
            //인터넷 상태 확인
            String msg = getString(R.string.check_network_error);
            String btnText = getString(R.string.confirm_text);
            boolean isNetwork = CommonFunc.checkNetworkStatus(MyInfoModifyActivity.this, msg, btnText);

            if (isNetwork) {
                String encode_pwd = Crypt.encrypt(pwd);
                String[][] fields = new String[][]{
                        {"password", encode_pwd},
                        {"cid", c_ids[myinfo_mod_company_spinner.getSelectedItemPosition()]}
                };
                MyInfoTable.updateMyInfoTable(fields);
                MyInfoTable.getMyInfoTable();

                String update_date = CommonFunc.getDateTime();
                //서버에 등록
                String[][] params = new String[][]{
                        {"user_id", String.valueOf(MyUtils.my_id)},
                        {"user_pwd", encode_pwd},
                        {"admin_id", c_ids[myinfo_mod_company_spinner.getSelectedItemPosition()]},
                        {"update_date", update_date}
                };
                WebHttpConnect.onModifyUserRequest(params);
            }
        } else {
            showConfirmDialog(false);
        }
    }

    public void onSuccessModify() {
        showConfirmDialog(true);
    }
    @SuppressLint({"ResourceAsColor", "ResourceType"})
    public void showConfirmDialog(boolean b_mod) {
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
        //super.onBackPressed();
    }
}