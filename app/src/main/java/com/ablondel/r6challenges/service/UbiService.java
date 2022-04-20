package com.ablondel.r6challenges.service;

import android.util.Log;

import com.ablondel.r6challenges.model.UserInfos;
import com.ablondel.r6challenges.model.auth.Authentication;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UbiService {

    // Urls
    private static final String AUTH_URL = "https://public-ubiservices.ubi.com/v3/profiles/sessions";
    private static final String PROFILES_URL = "https://public-ubiservices.ubi.com/v2/profiles?userId=";

    // Methods
    private static final String POST_METHOD = "POST";
    private static final String GET_METHOD = "GET";

    // Headers keys
    private static final String HEADER_UBI_APPID = "Ubi-AppId";
    private static final String HEADER_UBI_SESSIONID = "Ubi-SessionId";
    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private static final String HEADER_AUTHORIZATION = "Authorization";

    // Headers values
    public static final String APP_ID = "39baebad-39e5-4552-8c25-2c9b919064e2";
    private static final String CONTENT_TYPE_JSON = "application/json";
    public static final String CHARSET_UTF8 = "UTF-8";
    private static final String AUTHORIZATION_BASIC = "Basic ";
    private static final String UBI_TOKEN_PREFIX = "Ubi_v1 t=";
    private static final String UBI_REFRESH_PREFIX = "rm_v1 t=";

    // Response
    public static final String EXCEPTION_PATTERN = "EXCEPTION: ";

    // Others
    private static final String UBI_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    private static final String UBI_DATE_DELIMITER = "\\.";


    public String authenticate(String encodedKey) {
        return callWebService(AUTH_URL, AUTHORIZATION_BASIC + encodedKey, POST_METHOD, "{rememberMe: true}", null);
    }

    public String updateRefreshToken(UserInfos userInfos) {
        return callWebService(AUTH_URL, UBI_REFRESH_PREFIX + userInfos.getAuthentication().getRememberMeTicket(),
                POST_METHOD, "{rememberMe: true}", null);
    }

    public String getProfiles(UserInfos userInfos) {
        checkExpiration(userInfos);
        String userId = userInfos.getAuthentication().getUserId();
        return callWebService(PROFILES_URL + userId, UBI_TOKEN_PREFIX + userInfos.getAuthentication().getTicket(),
                GET_METHOD, null, userInfos.getAuthentication().getSessionId());
    }

    private String callWebService(String connectionUrl, String authorization, String method, String body, String sessionId) {
        String response;
        URL url;
        HttpURLConnection urlConnection = null;
        try {
            url = new URL(connectionUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            if (method.equals(POST_METHOD)) {
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod(POST_METHOD);
            }
            urlConnection.setRequestProperty(HEADER_UBI_APPID, APP_ID);
            urlConnection.setRequestProperty(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON);
            urlConnection.setRequestProperty(HEADER_AUTHORIZATION, authorization);
            if(null != sessionId) {
                urlConnection.setRequestProperty(HEADER_UBI_SESSIONID, sessionId);
            }
            if (method.equals(POST_METHOD)) {
                OutputStream os = urlConnection.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);
                osw.write(body);
                osw.flush();
                osw.close();
                os.close();
            }
            urlConnection.connect();
            response = getResponse(urlConnection);
        } catch (IOException e) {
            response = EXCEPTION_PATTERN + e.getMessage();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return response;
    }

    private void checkExpiration(UserInfos userInfos) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(UBI_DATE_FORMAT, Locale.getDefault());
            Date expiration = formatter.parse(userInfos.getAuthentication().getExpiration().split(UBI_DATE_DELIMITER)[0]);
            if (System.currentTimeMillis() > expiration.getTime() - (1000 * 60 * 5)) {

                String updatedAuthJson = updateRefreshToken(userInfos);
                Log.d("Refresh token", updatedAuthJson);
                userInfos.setAuthentication(new Gson().fromJson(updatedAuthJson, Authentication.class));
                try {
                    SharedPreferencesService.getEncryptedSharedPreferences().edit().putString("userInfos",new Gson().toJson(userInfos)).apply();
                } catch (GeneralSecurityException | IOException e) {
                    Log.e("Unable to update token", e.getMessage());
                }
            }
        } catch (ParseException e) {
            Log.e("checkExpiration", e.getMessage());
        }
    }

    private String getResponse(HttpURLConnection urlConnection) throws IOException {
        StringBuilder response = new StringBuilder();
        BufferedReader br;
        if (100 <= urlConnection.getResponseCode() && urlConnection.getResponseCode() <= 399) {
            br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        } else {
            br = new BufferedReader(new InputStreamReader(urlConnection.getErrorStream()));
        }
        String strCurrentLine;
        while ((strCurrentLine = br.readLine()) != null) {
            response.append(strCurrentLine);
        }
        return response.toString();
    }

}
