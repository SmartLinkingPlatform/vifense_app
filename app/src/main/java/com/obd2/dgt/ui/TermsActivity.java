package com.obd2.dgt.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.obd2.dgt.R;
import com.obd2.dgt.auth.CustomWebChromeClient;
import com.obd2.dgt.ui.InfoActivity.MessageActivity;
import com.obd2.dgt.utils.MyUtils;

public class TermsActivity extends AppBaseActivity {

    ImageView terms_prev_btn, terms_btn;
    WebView terms_web_view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        initLayout();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initLayout() {
        terms_prev_btn = findViewById(R.id.terms_prev_btn);
        terms_prev_btn.setOnClickListener(view -> onTermsPrevClick());

        terms_btn = findViewById(R.id.terms_btn);
        if (MyUtils.terms_file_path.isEmpty()) {
            terms_btn.setBackgroundResource(R.drawable.button_disable);
        } else {
            terms_btn.setBackgroundResource(R.drawable.button);
            terms_btn.setOnClickListener(view -> onTermsPrevClick());
        }

        terms_web_view = (WebView) findViewById(R.id.terms_web_view);
        terms_web_view.setWebViewClient(new TermsWebViewClient());
        WebSettings webSettings = terms_web_view.getSettings();
        webSettings.setJavaScriptEnabled(true);
        if (!MyUtils.terms_file_path.isEmpty()) {
            terms_web_view.loadUrl(MyUtils.terms_file_path);
        }
    }

    private void onTermsPrevClick(){
        onLRChangeLayout(TermsActivity.this, SignupActivity.class);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onLRChangeLayout(TermsActivity.this, SignupActivity.class);
        finish();
    }
}

class TermsWebViewClient extends WebViewClient {
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
    }
}