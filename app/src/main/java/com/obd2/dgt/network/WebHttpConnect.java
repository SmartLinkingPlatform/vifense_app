package com.obd2.dgt.network;

import com.obd2.dgt.dbManage.TableInfo.CompanyTable;
import com.obd2.dgt.network.http.HttpCall;
import com.obd2.dgt.network.http.HttpUrlRequest;
import com.obd2.dgt.ui.InfoActivity.CarInfoActivity;
import com.obd2.dgt.ui.InfoActivity.CarInfoModifyActivity;
import com.obd2.dgt.ui.InfoActivity.MyInfoModifyActivity;
import com.obd2.dgt.ui.LoginActivity;
import com.obd2.dgt.ui.SignupActivity;
import com.obd2.dgt.utils.CommonFunc;
import com.obd2.dgt.utils.MyUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
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

    //서버에 회사 리스트 요청
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

    //서버에 회원 가입 정보 보내기
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

    //서버에 로그인 정보 보내기
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
                            ArrayList<String> user_info = new ArrayList<>();
                            user_info.add(res.getString("user_num"));
                            user_info.add(res.getString("user_id"));
                            user_info.add(res.getString("user_pwd"));
                            user_info.add(res.getString("user_name"));
                            user_info.add(res.getString("company_name"));
                            LoginActivity.getInstance().onSuccessStart(user_info);
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

    //서버에 나의 정보 수정 내용 보내기
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

    //서버에 차량 등록 정보 보내기
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
                            String car_num = res.getString("car_num");
                            CarInfoActivity.getInstance().onSuccessRegisterCar(car_num);
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

    //서버에 차량 정보 수정 보내기
    public static void onCarModifyRequest(String[][] values) {
        serverCallHttpFunc(values, MyUtils.mod_car);
        new HttpUrlRequest(){
            @Override
            public void onResponse(String response) {
                super.onResponse(response);
                if (!response.isEmpty()) {
                    try {
                        JSONObject res = new JSONObject(response);
                        String msg = res.getString("msg");
                        if (msg.equals("ok")) {
                            CarInfoModifyActivity.getInstance().onSuccessModifyCar();
                        } else {
                            CarInfoModifyActivity.getInstance().onFailedModifyCar();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.execute(httpCallPost);
    }

    //서버에 차량 정보 삭제 보내기
    public static void onCarDeleteRequest(String[][] values) {
        serverCallHttpFunc(values, MyUtils.del_car);
        new HttpUrlRequest(){
            @Override
            public void onResponse(String response) {
                super.onResponse(response);
                if (!response.isEmpty()) {
                    try {
                        JSONObject res = new JSONObject(response);
                        String msg = res.getString("msg");
                        if (msg.equals("ok")) {
                            CarInfoModifyActivity.getInstance().onSuccessDeleteCar();
                        } else {
                            CarInfoModifyActivity.getInstance().onFailedDeleteCar();
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

    //차량의 주행 정보 보내기
    public static void onDrivingInfoRequest(String[][] values) {
        serverCallHttpFunc(values, MyUtils.driving_info);
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
