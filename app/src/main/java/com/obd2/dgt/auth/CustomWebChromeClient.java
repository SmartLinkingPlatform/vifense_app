package com.obd2.dgt.auth;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.obd2.dgt.ui.InfoActivity.AuthActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class CustomWebChromeClient extends WebChromeClient {
    String user_birthday = "";
    String user_name = "";
    String user_phone = "";
    String result_code = "";
    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        Log.d("WebView Message", "JavaScript Console: " + consoleMessage.message());
        return true;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
        WebView newWebView = new WebView(view.getContext());
        WebSettings settings = newWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setSupportMultipleWindows(true);

        newWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onCloseWindow(WebView window) {
                window.setVisibility(View.GONE);
                window.destroy();
                AuthActivity.getInstance().closeWebView(user_name, user_phone, user_birthday, result_code);
            }
        });

        newWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                AuthActivity.getInstance().progressBar.setVisibility(View.VISIBLE);
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                AuthActivity.getInstance().progressBar.setVisibility(View.GONE);
            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }
        });
        AuthActivity.getInstance().webview_frame.addView(newWebView);

        ((WebView.WebViewTransport)resultMsg.obj).setWebView(newWebView);
        resultMsg.sendToTarget();

        return true;
    }

    @Override
    public void onCloseWindow(WebView window) {
        Log.i(getClass().getName(), "onCloseWindow");
        window.setVisibility(View.GONE);
        window.destroy();
        super.onCloseWindow(window);
    }

    @Override
    public void onProgressChanged(WebView view, int progress) {
        AuthActivity.getInstance().progressBar.setProgress(progress);
    }

    @Override
    public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
        try {
            message = message.replace("\n\n", "");
            message = message.replace("\n", "");
            if (message != null && !message.isEmpty()) {
                JSONObject receiveObj = new JSONObject(message);
                user_name = receiveObj.getString("userName");
                user_phone = receiveObj.getString("userPhone");
                user_birthday = receiveObj.getString("userBirthday");
                result_code = receiveObj.getString("resultCode");
            }
            AuthActivity.getInstance().closeWebView(user_name, user_phone, user_birthday, result_code);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
        Log.i(getClass().getName(), "onJsConfirm() url:"+url+", message"+message);


        return true;
    }
}
