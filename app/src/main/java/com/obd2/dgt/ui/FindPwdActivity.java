package com.obd2.dgt.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.obd2.dgt.R;

public class FindPwdActivity extends AppBaseActivity {

    EditText find_name_text, find_id_text;
    ImageView auth_code_btn, complete_btn;
    EditText auth_code_text;
    TextView auth_time_text;
    LinearLayout auth_code_layout;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findpwd);
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

        auth_code_text = findViewById(R.id.auth_code_text);
        auth_time_text = findViewById(R.id.auth_time_text);

        complete_btn = findViewById(R.id.complete_btn);
        complete_btn.setOnClickListener(view -> onAuthCompleteClick());
    }

    private void onGetAuthCodeClick() {
        auth_code_btn.setBackgroundResource(R.drawable.button2_disable);
        auth_code_layout.setVisibility(View.VISIBLE);

        dialog = new Dialog(FindPwdActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        showWarningDialog();
    }

    private void onAuthCompleteClick() {

        dialog = new Dialog(FindPwdActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        showConfirmDialog();
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
        dialog.setContentView(R.layout.dlg_notification);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView dialog_text_1 = dialog.findViewById(R.id.dialog_text_1);
        dialog_text_1.setText(R.string.alarm_text_2);
        TextView dialog_text_2 = dialog.findViewById(R.id.dialog_text_2);
        dialog_text_2.setText(R.string.alarm_text_3);
        TextView dialog_confirm_text = dialog.findViewById(R.id.dialog_confirm_text);
        dialog_confirm_text.setTextColor(Color.parseColor(getString(R.color.white)));
        ImageView dialog_confirm_btn = dialog.findViewById(R.id.dialog_confirm_btn);
        dialog_confirm_btn.setImageResource(R.drawable.confirm_press);
        dialog_confirm_btn.setOnClickListener(view -> {
            dialog.dismiss();
        });
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        onLRChangeLayount(FindPwdActivity.this, LoginActivity.class);
        finish();
    }

}