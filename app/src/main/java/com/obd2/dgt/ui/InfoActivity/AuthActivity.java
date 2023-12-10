package com.obd2.dgt.ui.InfoActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.obd2.dgt.R;
import com.obd2.dgt.auth.CustomWebChromeClient;
import com.obd2.dgt.ui.AppBaseActivity;
import com.obd2.dgt.ui.FindPwdActivity;
import com.obd2.dgt.ui.LoginActivity;
import com.obd2.dgt.ui.SignupActivity;
import com.obd2.dgt.utils.MyUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class AuthActivity extends AppBaseActivity {
    WebView auth_web_view;
    public FrameLayout webview_frame;
    public ProgressBar progressBar;
    String send_url = "";
    private static AuthActivity instance;
    public static AuthActivity getInstance() {
        return instance;
    }
    String dataKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        dataKey = getIntent().getStringExtra("dataKey");

        if (dataKey.equals("sign")) {
            send_url = MyUtils.signup_url;
        } else if (dataKey.equals("find")) {
            send_url = MyUtils.find_url;
        }

        instance = this;
        initLayout();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initLayout() {
        progressBar = (ProgressBar) findViewById(R.id.wv_progressBar);
        progressBar.setMax(100);
        progressBar.setProgress(1);

        webview_frame = (FrameLayout) findViewById(R.id.webview_frame);
        auth_web_view = findViewById(R.id.auth_web_view);

        WebSettings webSettings = auth_web_view.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        auth_web_view.setWebChromeClient(new CustomWebChromeClient());
        auth_web_view.loadUrl(send_url);
    }

    public void closeWebView(String name, String phone, String birthday, String resultCode) {
        Intent intent = null;
        if (dataKey.equals("sign")) {
            intent = new Intent(AuthActivity.this, SignupActivity.class);
        } else if (dataKey.equals("find")) {
            intent = new Intent(AuthActivity.this, FindPwdActivity.class);
        }
        if (intent != null) {
            if (!resultCode.equals("2000")) {
                intent.putExtra("result", "no");
            } else {
                intent.putExtra("result", "ok");
                intent.putExtra("user_name", name);
                intent.putExtra("user_phone", phone);
                intent.putExtra("user_birthday", birthday);
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);

            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (dataKey.equals("sign")) {
            onLRChangeLayount(AuthActivity.this, SignupActivity.class);
        } else if (dataKey.equals("find")) {
            onLRChangeLayount(AuthActivity.this, FindPwdActivity.class);
        }
        finish();
    }
}