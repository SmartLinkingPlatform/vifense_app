package com.obd2.dgt.ui.InfoActivity;

import android.annotation.SuppressLint;
import android.content.Context;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        String dataKey = getIntent().getStringExtra("dataKey");

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
        //CustomWebChromeClient chromeClient = new CustomWebChromeClient();
        progressBar = (ProgressBar) findViewById(R.id.wv_progressBar);
        progressBar.setMax(100);
        progressBar.setProgress(1);

        webview_frame = (FrameLayout) findViewById(R.id.webview_frame);
        auth_web_view = findViewById(R.id.auth_web_view);
        //auth_web_view.setWebChromeClient(chromeClient);

        WebSettings webSettings = auth_web_view.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        auth_web_view.setWebChromeClient(new CustomWebChromeClient());
        auth_web_view.loadUrl("https://dgt.vifense.com/mok/mokAuth.html");

        // Add JS interface to allow calls from webview to Android
        // code. See below for WebAppInterface class implementation
        auth_web_view.addJavascriptInterface(new WebAppInterface(this), "DGTApp");
    }

    // Interface b/w JS and Android code
    private class WebAppInterface {
        Context mContext;

        WebAppInterface(Context c) {
            mContext = c;
        }

        // This function can be called in our JS script now
        @JavascriptInterface
        public void receivedAuthInfo(String received_data) {
            try {
                JSONObject receiveObj = new JSONObject(received_data);
                String user_name = receiveObj.getString("userName");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void closeWebView() {
        onLRChangeLayount(AuthActivity.this, SignupActivity.class);
        finish();
    }

    @Override
    public void onBackPressed() {
        onLRChangeLayount(AuthActivity.this, SignupActivity.class);
        finish();
    }
}