package com.obd2.dgt.network;

import com.obd2.dgt.dbManage.TableInfo.CompanyTable;
import com.obd2.dgt.network.http.HttpCall;
import com.obd2.dgt.network.http.HttpUrlRequest;
import com.obd2.dgt.ui.InfoActivity.CarInfoActivity;
import com.obd2.dgt.ui.InfoActivity.MyInfoModifyActivity;
import com.obd2.dgt.ui.LoginActivity;
import com.obd2.dgt.ui.SignupActivity;
import com.obd2.dgt.utils.CommonFunc;
import com.obd2.dgt.utils.MyUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public class WebHttpConnect {
    static HttpCall httpCallPost = new HttpCall();
    private static void serverCallHttpFunc(String ajx) {
        httpCallPost.setMethodtype(HttpCall.POST);
        String url = MyUtils.server_url + ajx;
        httpCallPost.setUrl(url);
        HashMap<String,String> paramsPost = new HashMap<>();
        httpCallPost.setParams(paramsPost);
    }
    private static void serverCallHttpFunc(String[][] values, String ajx) {
        httpCallPost.setMethodtype(HttpCall.POST);
        String url = MyUtils.server_url + ajx;
        httpCallPost.setUrl(url);
        HashMap<String,String> paramsPost = new HashMap<>();
        for (int i = 0; i < values.length; i++) {
            paramsPost.put(values[i][0], values[i][1]);
        }
        httpCallPost.setParams(paramsPost);
    }

    public static void onCompanyInfoRequest() {
        serverCallHttpFunc(MyUtils.call_company);
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
        serverCallHttpFunc(values, MyUtils.user_signup);
        new HttpUrlRequest(){
            @Override
            public void onResponse(String response) {
                super.onResponse(response);
                if (!response.isEmpty()) {
                    try {
                        JSONObject res = new JSONObject(response);
                        String msg = res.getString("msg");
                        if (msg.equals("ok")) {
                            SignupActivity.getInstance().onSuccessSignup();
                        } else if (msg.equals("du")) {
                            SignupActivity.getInstance().onDuplicateSignup();
                        } else {
                            SignupActivity.getInstance().onFailedSignup();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.execute(httpCallPost);
    }

    public static void onLoginRequest(String[][] values) {
        serverCallHttpFunc(values, MyUtils.user_login);
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
        serverCallHttpFunc(values, MyUtils.user_modify);
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

    public static void onCarRegisterRequest(String[][] values) {
        serverCallHttpFunc(values, MyUtils.reg_car);
        new HttpUrlRequest(){
            @Override
            public void onResponse(String response) {
                super.onResponse(response);
                if (!response.isEmpty()) {
                    try {
                        JSONObject res = new JSONObject(response);
                        String msg = res.getString("msg");
                        if (msg.equals("ok")) {
                            CarInfoActivity.getInstance().onSuccessRegisterCar();
                        } else if (msg.equals("du")){
                            CarInfoActivity.getInstance().onDuplicationRegisterCar();
                        } else {
                            CarInfoActivity.getInstance().onFailedRegisterCar();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.execute(httpCallPost);
    }

    public static void onFindPasswordRequest(String[][] values) {
        serverCallHttpFunc(values, MyUtils.find_password);
        new HttpUrlRequest(){
            @Override
            public void onResponse(String response) {
                super.onResponse(response);
                if (!response.isEmpty()) {
                    try {
                        JSONObject res = new JSONObject(response);
                        String msg = res.getString("msg");
                        if (msg.equals("ok")) {

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.execute(httpCallPost);
    }
}
