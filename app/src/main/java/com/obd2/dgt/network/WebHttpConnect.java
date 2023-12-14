package com.obd2.dgt.network;

import android.graphics.Bitmap;

import com.obd2.dgt.dbManage.TableInfo.CompanyTable;
import com.obd2.dgt.dbManage.TableInfo.MessageInfoTable;
import com.obd2.dgt.network.http.HttpCall;
import com.obd2.dgt.network.http.HttpUrlRequest;
import com.obd2.dgt.ui.FindPwdActivity;
import com.obd2.dgt.ui.InfoActivity.CarInfoActivity;
import com.obd2.dgt.ui.InfoActivity.CarInfoModifyActivity;
import com.obd2.dgt.ui.InfoActivity.MyInfoModifyActivity;
import com.obd2.dgt.ui.InfoActivity.RankingInfoActivity;
import com.obd2.dgt.ui.LoginActivity;
import com.obd2.dgt.ui.MainActivity;
import com.obd2.dgt.ui.MainListActivity.RecordActivity;
import com.obd2.dgt.ui.SignupActivity;
import com.obd2.dgt.ui.SplashActivity;
import com.obd2.dgt.utils.CommonFunc;
import com.obd2.dgt.utils.MyUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

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
                    MyUtils.companyInfo.clear();
                    for (int i = 0; i < data.length(); i++) {
                        int index = i + 1;
                        try {
                            JSONObject object = data.getJSONObject(i);
                            String[][] fields = new String[][]{
                                    {"id", String.valueOf(index)},
                                    {"cid", object.getString("admin_id")},
                                    {"name", object.getString("company_name")}
                            };

                            String[] info = new String[3];
                            info[0] = String.valueOf(index); //ID
                            info[1] = object.getString("admin_id"); //company id
                            info[2] = object.getString("company_name"); //company name
                            MyUtils.companyInfo.add(info);

                            SplashActivity.getInstance().gotoSuccess();
                            //CompanyTable.insertCompanyInfoTable(fields);
                        } catch (Exception e) {
                            e.printStackTrace();
                            SplashActivity.getInstance().gotoFail();
                        }
                    }
                    //CompanyTable.getCompanyInfoTable();
                } else {
                    SplashActivity.getInstance().gotoFail();
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
                            user_info.add(res.getString("user_id"));
                            user_info.add(res.getString("user_phone"));
                            user_info.add(res.getString("user_name"));
                            user_info.add(res.getString("user_pwd"));
                            user_info.add(res.getString("admin_id"));
                            LoginActivity.getInstance().onSuccessStart(user_info);
                        } else if (msg.equals("nonuser")){
                            LoginActivity.getInstance().onNonUser();
                        } else {
                            LoginActivity.getInstance().onFailedPassword();
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
                        } else {
                            MyInfoModifyActivity.getInstance().onFailedModify();
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
                            String car_id = res.getString("car_id");
                            CarInfoActivity.getInstance().onSuccessRegisterCar(car_id);
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

    //차량 주행 정보 요청
    public static void onReadDrivingInfoRequest(String[][] values) {
        serverCallHttpFunc(values, MyUtils.read_driving);
        new HttpUrlRequest(){
            @Override
            public void onResponse(String response) {
                super.onResponse(response);
                if (!response.isEmpty()) {
                    try {
                        JSONArray data = CommonFunc.AnalysisResponse(response);
                        String temp_date = "";
                        LinkedHashMap<String, ArrayList<JSONObject>> driving_info = new LinkedHashMap<>();
                        ArrayList<JSONObject> infos = new ArrayList<>();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject res = data.getJSONObject(i);
                            String driving_date = res.getString("driving_date");
                            if (infos.size() == 0) {
                                infos.add(res);
                            } else {
                                if (driving_date.equals(temp_date)) {
                                    infos.add(res);
                                } else {
                                    driving_info.put(temp_date, infos);
                                    infos = new ArrayList<>();
                                    infos.add(res);
                                }
                            }
                            if (i == data.length() - 1) {
                                driving_info.put(driving_date, infos);
                            }
                            temp_date = driving_date;
                        }

                        RecordActivity.getInstance().onSuccessDrivingInfo(driving_info);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.execute(httpCallPost);
    }

    //차량의 주행 정보 보내기
    public static void onSaveDrivingInfoRequest(String[][] values) {
        serverCallHttpFunc(values, MyUtils.save_driving);
        new HttpUrlRequest(){
            @Override
            public void onResponse(String response) {
                super.onResponse(response);
                if (!response.isEmpty()) {
                    try {
                        JSONObject res = new JSONObject(response);
                        String msg = res.getString("msg");
                        if (msg.equals("ok")) {
                            MyUtils.max_speed = 0;
                            //MyUtils.ecu_total_distance = 0;
                            //MyUtils.ecu_mileage = "0";
                            MyUtils.fast_speed_cnt = 0;
                            MyUtils.quick_speed_cnt = 0;
                            MyUtils.brake_speed_cnt = 0;
                            MyUtils.idling_time = 0;
                            //MyUtils.ecu_driving_time = "00:00";
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.execute(httpCallPost);
    }

    //새로운 메세지 리스트 요청
    public static void onMessageInfoRequest(String[][] values) {
        serverCallHttpFunc(values, MyUtils.mgs_list);
        new HttpUrlRequest(){
            @Override
            public void onResponse(String response) {
                super.onResponse(response);
                if (!response.isEmpty()) {
                    JSONArray data = CommonFunc.AnalysisResponse(response);
                    for (int i = 0; i < data.length(); i++) {
                        try {
                            JSONObject object = data.getJSONObject(i);
                            String[][] fields = new String[][]{
                                    {"id", object.getString("notice_id")},
                                    {"msg_date", object.getString("msg_date")},
                                    {"msg_user", object.getString("msg_user")},
                                    {"msg_title", object.getString("msg_title")},
                                    {"msg_content", object.getString("msg_content")}
                            };
                            if (MyUtils.lastMsgID != object.getInt("notice_id")) {
                                MessageInfoTable.insertMessageTable(fields);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    MessageInfoTable.getMessageInfoTable();
                }
            }
        }.execute(httpCallPost);
    }

    //랭킹 정보 요청
    public static void onDrivingRankingRequest(String[][] values) {
        serverCallHttpFunc(values, MyUtils.driving_ranking);
        new HttpUrlRequest(){
            @Override
            public void onResponse(String response) {
                super.onResponse(response);
                if (!response.isEmpty()) {
                    try {
                        String mileage_score = "";
                        String safety_score = "";
                        double total_mileage = 0.0;
                        float avr_speed = 0;
                        int fast = 0;
                        int quick = 0;
                        int brake = 0;
                        int total_time = 0;
                        JSONObject res = new JSONObject(response);
                        String msg = res.getString("msg");
                        if (msg.equals("ok")) {
                            mileage_score = res.getString("mileage_score");
                            safety_score = res.getString("safety_score");
                            total_mileage = res.getDouble("total_mileage");
                            String lists = res.getString("lists");
                            JSONArray data = new JSONArray(lists);
                            int total_speed = 0;
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject object = data.getJSONObject(i);
                                total_speed += object.getInt("average_speed");
                                fast += object.getInt("fast_speed_cnt");
                                quick += object.getInt("quick_speed_cnt");
                                brake += object.getInt("brake_speed_cnt");
                                total_time += CommonFunc.calculateTime(object.getString("driving_time"));
                            }
                            avr_speed = total_speed / (float) data.length();
                        }
                        String[] ranking_val = {
                                mileage_score,
                                safety_score,
                                String.valueOf(Math.round(total_mileage * 10) / (float)10),
                                String.valueOf(Math.round(avr_speed * 10) / (float)10),
                                String.valueOf(total_time),
                                String.valueOf(fast),
                                String.valueOf(quick),
                                String.valueOf(brake),
                        };
                        RankingInfoActivity.getInstance().setRankingValues(ranking_val);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.execute(httpCallPost);
    }

    //메인화면 랭킹정보
    public static void onRankingRequest(String[][] values) {
        serverCallHttpFunc(values, MyUtils.ranking);
        new HttpUrlRequest(){
            @Override
            public void onResponse(String response) {
                super.onResponse(response);
                if (!response.isEmpty()) {
                    try {
                        String mileage_score = "";
                        String safety_score = "";
                        JSONObject res = new JSONObject(response);
                        String msg = res.getString("msg");
                        if (msg.equals("ok")) {
                            mileage_score = res.getString("mileage_score");
                            safety_score = res.getString("safety_score");
                        }
                        String[] ranking_val = {
                                mileage_score,
                                safety_score
                        };
                        MainActivity.getInstance().setRankingValues(ranking_val);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.execute(httpCallPost);
    }

    //서버에 새 비밀번호 저장
    public static void onNewPasswordRequest(String[][] values) {
        serverCallHttpFunc(values, MyUtils.new_pwd);
        new HttpUrlRequest(){
            @Override
            public void onResponse(String response) {
                super.onResponse(response);
                if (!response.isEmpty()) {
                    try {
                        JSONObject res = new JSONObject(response);
                        String msg = res.getString("msg");
                        if (msg.equals("ok")) {
                            FindPwdActivity.getInstance().onSuccessSetting();
                        } else {
                            FindPwdActivity.getInstance().onFailedSetting();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.execute(httpCallPost);
    }
}
