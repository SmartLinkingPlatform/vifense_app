package com.obd2.dgt.network.http;

import android.os.AsyncTask;

import com.obd2.dgt.utils.MyUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpBodyRequest extends AsyncTask<HttpCall, String, String> {
    private static final String UTF_8 = "UTF-8";

    @Override
    protected String doInBackground(HttpCall... params) {
        HttpURLConnection urlConnection = null;
        HttpCall httpCall = params[0];
        StringBuilder response = new StringBuilder();
        try{
            URL url = new URL(httpCall.getUrl());
            urlConnection = (HttpURLConnection) url.openConnection();
            String method = "";
            switch (httpCall.getMethodtype()) {
                case HttpCall.GET:
                    method = "GET";
                    break;
                case HttpCall.DELETE:
                    method = "DELETE";
                    break;
                case HttpCall.POST:
                    method = "POST";
                    break;
                case HttpCall.PUT:
                    method = "PUT";
                    break;
            }
            urlConnection.setRequestMethod(method);
            urlConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            urlConnection.setRequestProperty("Accept", "application/json;");
            String Authorization = "Bearer " + MyUtils.ACCESS_TOKEN;
            urlConnection.setRequestProperty("Authorization", Authorization);
            urlConnection.setReadTimeout(60000); //milliseconds
            urlConnection.setConnectTimeout(60000); //milliseconds
            if(httpCall.getMethodtype() != HttpCall.GET){
                OutputStream os = urlConnection.getOutputStream();
                os.write(MyUtils.sendRequestData.getBytes(StandardCharsets.UTF_8));
                os.close();
            }
            int responseCode = urlConnection.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED){
                String line ;
                BufferedReader br = new BufferedReader( new InputStreamReader(urlConnection.getInputStream()));
                while ((line = br.readLine()) != null){
                    response.append(line);
                }
            }
            return response.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            urlConnection.disconnect();
        }
        return response.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        onResponse(s);
    }

    public void onResponse(String response){

    }

    private String getDataString(HashMap<String, String> params, int methodType) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean isFirst = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (isFirst){
                isFirst = false;
                if(methodType == HttpCall.GET){
                    result.append("?");
                }
            }else{
                result.append("&");
            }
            result.append(URLEncoder.encode(entry.getKey(), UTF_8));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), UTF_8));
        }
        return result.toString();
    }
}
