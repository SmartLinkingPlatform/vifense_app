package com.obd2.dgt.network;

import android.graphics.Bitmap;
import android.widget.Toast;

import com.obd2.dgt.R;
import com.obd2.dgt.dbManage.TableInfo.CompanyTable;
import com.obd2.dgt.dbManage.TableInfo.DrivingTable;
import com.obd2.dgt.dbManage.TableInfo.MessageInfoTable;
import com.obd2.dgt.network.http.HttpBodyRequest;
import com.obd2.dgt.network.http.HttpCall;
import com.obd2.dgt.network.http.HttpUrlRequest;
import com.obd2.dgt.service.RealService;
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
    private static void serverCallFunc(String ajx) {
        httpCallPost.setMethodtype(HttpCall.POST);
        String url = MyUtils.server_url + ajx;
        httpCallPost.setUrl(url);
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
                    if (data.length() == 0) {
                        SplashActivity.getInstance().noCompanyList();
                        return;
                    }
                    for (int i = 0; i < data.length(); i++) {
                        int index = i + 1;
                        try {
                            JSONObject object = data.getJSONObject(i);
                            /*String[][] fields = new String[][]{
                                    {"id", String.valueOf(index)},
                                    {"cid", object.getString("admin_id")},
                                    {"name", object.getString("company_name")}
                            };*/

                            String[] info = new String[3];
                            info[0] = String.valueOf(index); //ID
                            info[1] = object.getString("admin_id"); //company id
                            info[2] = object.getString("company_name"); //company name
                            MyUtils.companyInfo.add(info);
                        } catch (Exception e) {
                            e.printStackTrace();
                            SplashActivity.getInstance().gotoFail();
                        }
                    }
                    SplashActivity.getInstance().gotoSuccess();
                    //CompanyTable.getCompanyInfoTable();
                } else {
                    SplashActivity.getInstance().gotoFail();
                }
            }
        }.execute(httpCallPost);
    }

    //약관 웹파일 경로 요청
    public static void onTermsUrlRequest() {
        serverCallHttpFunc(MyUtils.terms_url);
        new HttpUrlRequest(){
            @Override
            public void onResponse(String response) {
                super.onResponse(response);
                if (!response.isEmpty()) {
                    try {
                        JSONObject res = new JSONObject(response);
                        String msg = res.getString("msg");
                        if (msg.equals("ok")) {
                            MyUtils.terms_file_path = res.getString("path");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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

    //서버에 토큰 정보 요청
    public static void onTokenRequest(String[][] values) {
        serverCallHttpFunc(values, MyUtils.auth_token);
        new HttpUrlRequest(){
            @Override
            public void onResponse(String response) {
                super.onResponse(response);
                if (!response.isEmpty()) {
                    try {
                        JSONObject res = new JSONObject(response);
                        String msg = res.getString("msg");
                        if (msg.equals("ok")) {
                            MyUtils.ACCESS_TOKEN = res.getString("access_token");

                            LoginActivity.getInstance().onSuccessGetToken();
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

    //서버에 로그인 정보 보내기
    public static void onLoginRequest() {
        serverCallFunc(MyUtils.user_login);
        new HttpBodyRequest(){
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
                            LoginActivity.getInstance().onSuccessLogin(user_info);
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
    public static void onModifyUserRequest() {
        serverCallFunc(MyUtils.user_modify);
        new HttpBodyRequest(){
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
    public static void onCarRegisterRequest() {
        serverCallFunc(MyUtils.reg_car);
        new HttpBodyRequest(){
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

    //서버에 차량 정보 조회 보내기
    public static void onCarListRequest() {
        serverCallFunc(MyUtils.list_car);
        new HttpBodyRequest(){
            @Override
            public void onResponse(String response) {
                super.onResponse(response);
                if (!response.isEmpty()) {
                    try {
                        JSONObject res = new JSONObject(response);
                        String msg = res.getString("msg");
                        if (msg.equals("ok")) {
                            ArrayList<String> car_info = new ArrayList<>();
                            car_info.add(res.getString("car_id"));
                            car_info.add(res.getString("number"));
                            car_info.add(res.getString("manufacturer"));
                            car_info.add(res.getString("car_model"));
                            car_info.add(res.getString("car_date"));
                            car_info.add(res.getString("car_fuel"));
                            car_info.add(res.getString("car_gas"));

                            LoginActivity.getInstance().onSuccessCarList(car_info);
                        } else {
                            LoginActivity.getInstance().onFailedCarList();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.execute(httpCallPost);
    }

    //서버에 차량 정보 수정 보내기
    public static void onCarModifyRequest() {
        serverCallFunc(MyUtils.mod_car);
        new HttpBodyRequest(){
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
    public static void onCarDeleteRequest() {
        serverCallFunc(MyUtils.del_car);
        new HttpBodyRequest(){
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
    public static void onReadDrivingInfoRequest() {
        serverCallFunc(MyUtils.read_driving);
        new HttpBodyRequest(){
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
    public static void onSaveDrivingInfoRequest() {
        serverCallFunc(MyUtils.save_driving);
        new HttpBodyRequest(){
            @Override
            public void onResponse(String response) {
                super.onResponse(response);
                if (!response.isEmpty()) {
                    try {
                        JSONObject res = new JSONObject(response);
                        String msg = res.getString("msg");
                        if (msg.equals("ok")) {
                            MainActivity.getInstance().showEndDriving();
                            MyUtils.max_speed = 0;
                            //MyUtils.ecu_total_distance = 0;
                            //MyUtils.ecu_mileage = "0";
                            MyUtils.fast_speed_cnt = 0;
                            MyUtils.quick_speed_cnt = 0;
                            MyUtils.brake_speed_cnt = 0;
                            MyUtils.idling_time = 0;
                            //MyUtils.ecu_driving_time = "00:00";
                            MyUtils.is_driving = false;
                            RealService.getInstance().stopParameters();
                            if (MainActivity.getInstance().isFinish) {
                                MainActivity.getInstance().FinishApp();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.execute(httpCallPost);
    }

    public static void onNotSentDrivingInfoRequest() {
        serverCallFunc(MyUtils.notSent_driving);
        new HttpBodyRequest(){
            @Override
            public void onResponse(String response) {
                super.onResponse(response);
                if (!response.isEmpty()) {
                    try {
                        JSONObject res = new JSONObject(response);
                        String msg = res.getString("msg");
                        if (msg.equals("ok")) {
                            DrivingTable.updateDrivingInfoTable();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.execute(httpCallPost);
    }

    //새로운 메세지 리스트 요청
    public static void onMessageInfoRequest() {
        serverCallFunc(MyUtils.mgs_list);
        new HttpBodyRequest(){
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
                    MainActivity.getInstance().setMessageStatus();
                }
            }
        }.execute(httpCallPost);
    }

    //랭킹 정보 요청
    public static void onDrivingRankingRequest() {
        serverCallFunc(MyUtils.driving_ranking);
        new HttpBodyRequest(){
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
    public static void onRankingRequest() {
        serverCallFunc(MyUtils.ranking);
        new HttpBodyRequest(){
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
    public static void onNewPasswordRequest() {
        serverCallFunc(MyUtils.new_pwd);
        new HttpBodyRequest(){
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
