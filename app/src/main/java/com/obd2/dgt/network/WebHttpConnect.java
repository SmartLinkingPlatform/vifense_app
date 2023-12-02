package com.obd2.dgt.network;

import com.obd2.dgt.dbManage.TableInfo.CompanyTable;
import com.obd2.dgt.network.http.HttpCall;
import com.obd2.dgt.network.http.HttpUrlRequest;
import com.obd2.dgt.ui.InfoActivity.MyInfoModifyActivity;
import com.obd2.dgt.ui.LoginActivity;
import com.obd2.dgt.utils.CommonFunc;
import com.obd2.dgt.utils.MyUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public class WebHttpConnect {
    public static void onCompanyInfoRequest() {
        HttpCall httpCallPost = new HttpCall();
        httpCallPost.setMethodtype(HttpCall.POST);
        String url = MyUtils.server_url + MyUtils.call_company;
        httpCallPost.setUrl(url);
        HashMap<String,String> paramsPost = new HashMap<>();
        //paramsPost.put("request", "company");
        httpCallPost.setParams(paramsPost);

        new HttpUrlRequest(){
            @Override
            public void onResponse(String response) {
                super.onResponse(response);
                if (!response.isEmpty()) {
                    JSONArray data = CommonFunc.AnalysisResponse(response);
                    for (int i = 0; i < data.length(); i++) {
                        int index = i + 1;
                        try {
                            JSONObject object = data.getJSONObject(i);
                            String[][] fields = new String[][]{
                                    {"id", String.valueOf(index)},
                                    {"cid", object.getString("user_num")},
                                    {"name", object.getString("company_name")}
                            };
                            CompanyTable.insertCompanyInfoTable(fields);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    CompanyTable.getCompanyInfoTable();
                }
            }
        }.execute(httpCallPost);
    }

    public static void onSignUpRequest(String[][] values) {
        HttpCall httpCallPost = new HttpCall();
        httpCallPost.setMethodtype(HttpCall.POST);
        String url = MyUtils.server_url + MyUtils.user_signup;
        httpCallPost.setUrl(url);
        HashMap<String,String> paramsPost = new HashMap<>();
        for (int i = 0; i < values.length; i++) {
            paramsPost.put(values[i][0], values[i][1]);
        }
        httpCallPost.setParams(paramsPost);

        new HttpUrlRequest(){
            @Override
            public void onResponse(String response) {
                super.onResponse(response);
            }
        }.execute(httpCallPost);
    }

    public static void onLoginRequest(String[][] values) {
        HttpCall httpCallPost = new HttpCall();
        httpCallPost.setMethodtype(HttpCall.POST);
        String url = MyUtils.server_url + MyUtils.user_login;
        httpCallPost.setUrl(url);
        HashMap<String,String> paramsPost = new HashMap<>();
        for (int i = 0; i < values.length; i++) {
            paramsPost.put(values[i][0], values[i][1]);
        }
        httpCallPost.setParams(paramsPost);

        new HttpUrlRequest(){
            @Override
            public void onResponse(String response) {
                super.onResponse(response);
                if (!response.isEmpty()) {
                    try {
                        JSONObject res = new JSONObject(response);
                        String msg = res.getString("msg");
                        if (msg.equals("ok")) {
                            LoginActivity.getInstance().onSuccessStart();
                        } else {
                            LoginActivity.getInstance().onFailedStart();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.execute(httpCallPost);
    }

    public static void onModifyUserRequest(String[][] values) {
        HttpCall httpCallPost = new HttpCall();
        httpCallPost.setMethodtype(HttpCall.POST);
        String url = MyUtils.server_url + MyUtils.user_modify;
        httpCallPost.setUrl(url);
        HashMap<String,String> paramsPost = new HashMap<>();
        for (int i = 0; i < values.length; i++) {
            paramsPost.put(values[i][0], values[i][1]);
        }
        httpCallPost.setParams(paramsPost);

        new HttpUrlRequest(){
            @Override
            public void onResponse(String response) {
                super.onResponse(response);
                if (!response.isEmpty()) {
                    try {
                        JSONObject res = new JSONObject(response);
                        String msg = res.getString("msg");
                        if (msg.equals("ok")) {
                            MyInfoModifyActivity.getInstance().onSuccessModify();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.execute(httpCallPost);
    }
}
