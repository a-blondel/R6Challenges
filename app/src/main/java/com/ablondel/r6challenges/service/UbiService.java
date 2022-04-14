package com.ablondel.r6challenges.service;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class UbiService {


    private static final String AUTH_URL = "https://public-ubiservices.ubi.com/v3/profiles/sessions";
    public static final String APP_ID = "39baebad-39e5-4552-8c25-2c9b919064e2";
    public static final String EXCEPTION_PATTERN = "EXCEPTION: ";
    public static final String CHARSET_UTF8 = "UTF-8";
    private static final String POST_METHOD = "POST";
    private static final String HEADER_UBI_APPID = "Ubi-AppId";
    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_TYPE_JSON = "application/json";
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String AUTHORIZATION_BASIC = "Basic ";
    private static final String REMEMBER_BE = "{rememberMe: true}";

    public String authenticate (String encodedKey){
        String response;
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = prepareRequest(AUTH_URL, AUTHORIZATION_BASIC + encodedKey, POST_METHOD);
            urlConnection.connect();
            response = getResponse(urlConnection);
            Log.d("Debug---ConnResponse", response);
        } catch (MalformedURLException e) {
            response = EXCEPTION_PATTERN + e.getMessage();
        } catch (IOException e) {
            response = EXCEPTION_PATTERN + e.getMessage();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return response;
    }


    private HttpURLConnection prepareRequest(String connectionUrl, String authorization, String method) throws IOException {
        HttpURLConnection urlConnection = null;
        URL url;
        url = new URL(connectionUrl);
        urlConnection = (HttpURLConnection) url.openConnection();
        if(method.equals(POST_METHOD)) {
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod(POST_METHOD);
        }
        urlConnection.setRequestProperty(HEADER_UBI_APPID, APP_ID);
        urlConnection.setRequestProperty(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON);
        urlConnection.setRequestProperty(HEADER_AUTHORIZATION, authorization);
        if(method.equals(POST_METHOD)){
            OutputStream os = urlConnection.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os, CHARSET_UTF8);
            osw.write(REMEMBER_BE);
            osw.flush();
            osw.close();
            os.close();
        }
        return urlConnection;
    }

    private String getResponse(HttpURLConnection urlConnection) throws IOException {
        String response = null;
        BufferedReader br = null;
        if (100 <= urlConnection.getResponseCode() && urlConnection.getResponseCode() <= 399) {
            br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        } else {
            br = new BufferedReader(new InputStreamReader(urlConnection.getErrorStream()));
        }
        String strCurrentLine;
        while ((strCurrentLine = br.readLine()) != null) {
            response += strCurrentLine;
        }
        return response;
    }

}
